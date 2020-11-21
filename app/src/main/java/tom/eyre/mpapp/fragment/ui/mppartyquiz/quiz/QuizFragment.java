package tom.eyre.mpapp.fragment.ui.mppartyquiz.quiz;

import android.icu.text.Edits;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.mpapp.R;
import tom.eyre.mpapp.adapter.ExpenseAdapter;
import tom.eyre.mpapp.adapter.QuestionAdapter;
import tom.eyre.mpapp.data.MpResponse;
import tom.eyre.mpapp.entity.QuestionEntity;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static tom.eyre.mpapp.util.DatabaseUtil.mpDB;

public class QuizFragment extends Fragment {

    private QuizViewModel quizViewModel;
    private AmazonDynamoDBClient dbClient;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        quizViewModel =
                ViewModelProviders.of(this).get(QuizViewModel.class);
        View root = inflater.inflate(R.layout.fragment_questions, container, false);

        recyclerView = root.findViewById(R.id.questionRv);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity(), getActivity().getResources().getString(R.string.identityId), Regions.US_WEST_2);

        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        checkForNewQuestions();

        return root;
    }

    private void checkForNewQuestions() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Long time = mpDB.mpDao().getMostRecentQuestionTime();
                if (time == null) {
                    time = 0l;
                }

                Map<String, AttributeValue> expressionAttributeValues =
                        new HashMap<String, AttributeValue>();
                expressionAttributeValues.put(":val", new AttributeValue().withN(String.valueOf(time)));

                ScanRequest scanRequest = new ScanRequest()
                        .withTableName("questions")
                        .withFilterExpression("lastUpdatedTs > :val")
                        .withExpressionAttributeValues(expressionAttributeValues);

                ScanResult scanResult = dbClient.scan(scanRequest);

                ArrayList<QuestionEntity> entities = buildEntities(scanResult.getItems());
                storeNewQuestionsLocally(entities);
                getQuestions();
            }
        });
    }

    private ArrayList<QuestionEntity> buildEntities(List<Map<String, AttributeValue>> results) {
        ArrayList<QuestionEntity> entities = new ArrayList<>();
        Iterator<Map<String, AttributeValue>> iterator = results.iterator();
        while (iterator.hasNext()) {
            Map<String, AttributeValue> result = iterator.next();
            QuestionEntity entity = new QuestionEntity();
            entity.setUin(getResult(result, "uin"));
            entity.setDateOfDivision(getResult(result, "date"));
            entity.setQuestion(getResult(result, "question"));
            entity.setTitle(getResult(result, "title"));
            entity.setType(getResult(result, "type"));
            entity.setOpinion(null);
            entity.setLastUpdatedTimestamp(Long.parseLong(getResult(result, "lastUpdatedTs")));
            entities.add(entity);
        }
        return entities;
    }

    private String getResult(Map<String, AttributeValue> result, String type) {
        return result.get(type).toString().substring(
                result.get(type).toString().indexOf(":") + 2,
                result.get(type).toString().lastIndexOf(","));
    }

    private void storeNewQuestionsLocally(ArrayList<QuestionEntity> entities) {
        if (entities != null && !entities.isEmpty() && entities.size() > 0) {
            mpDB.mpDao().insertAllQuestions(entities);
            if(getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "New Questions added",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getQuestions() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<QuestionEntity> questions = mpDB.mpDao().getAllQuestions().stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(QuestionEntity::getQuestion))),
                        ArrayList::new));
                updateRecycler(questions);
            }
        });
    }

    private void updateRecycler(List<QuestionEntity> questions) {
       if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                mAdapter = new QuestionAdapter(questions, getContext());
                mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
                RelativeLayout rv = getView().findViewById(R.id.loading);
                rv.setVisibility(View.GONE);
                ScrollView sv = getView().findViewById(R.id.sv);
                sv.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void insertNewQuestionToDynamoDb() {

//                Table dbTable = Table.loadTable(dbClient, "questions");
//                Document question = new Document();
//                question.put("uin", "uin");
//                question.put("date", "date");
//                question.put("title", "title");
//                question.put("question", "question");
//                question.put("type", "type");
//                question.put("lastUpdatedTs", String.valueOf(new Date()));
//                dbTable.putItem(question);
    }
}