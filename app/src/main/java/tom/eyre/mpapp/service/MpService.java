package tom.eyre.mpapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tom.eyre.yourvotematters.MpActivity;
import tom.eyre.yourvotematters.database.MpDatabase;
import tom.eyre.yourvotematters.entity.MpEntity;
import tom.eyre.yourvotematters.service.google.KnowledgeGraphSearch;

public class MpService {

    public MpService(){}

    private ExpenseService expenseService = new ExpenseService();

    private KnowledgeGraphSearch knowledgeGraphSearch = new KnowledgeGraphSearch();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private String json;
    private String jsonArrayString;

    public MpEntity getMpDetails(Integer id, MpDatabase mpDatabase){
        return mpDatabase.mpDao().findMpById(id);
    }

    public void getMpBio(final String name, final MpActivity activity, final String apiKey){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    activity.setBio(new JSONObject(knowledgeGraphSearch.searchForName(name, apiKey)).getJSONArray("itemListElement")
                            .getJSONObject(0).getJSONObject("result").getJSONObject("detailedDescription").getString("articleBody"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getExpenses(final String username, final String password, final String name, final MpActivity mpActivity){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mpActivity.setExpenses(expenseService.calculateExpensesPerYear(username,password,name));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
