package tom.eyre.mpapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tom.eyre.mpapp.R;
import tom.eyre.mpapp.entity.BillsEntity;
import tom.eyre.mpapp.entity.QuestionEntity;
import tom.eyre.mpapp.util.DatabaseUtil;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        private TextView question;
        private Button agree;
        private Button disagree;

        public QuestionViewHolder(View view) {
            super(view);
            this.question = view.findViewById(R.id.question);
            this.agree = view.findViewById(R.id.agree);
            this.disagree = view.findViewById(R.id.disagree);
        }

    }

    private List<QuestionEntity> questions;
    private Context context;
    private DatabaseUtil databaseUtil;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public QuestionAdapter(List<QuestionEntity> questions, Context context){
        this.questions = questions;
        this.context = context;
        this.databaseUtil = DatabaseUtil.getInstance(context);
    }

    @NonNull
    @Override
    public QuestionAdapter.QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_question_layout, parent, false);
        return new QuestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.QuestionViewHolder holder, int position) {
        holder.question.setText(questions.get(position).getQuestion());
        holder.agree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
        holder.agree.setTextColor(context.getResources().getColor(R.color.black, null));
        holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
        holder.disagree.setTextColor(context.getResources().getColor(R.color.black, null));
        if (questions.get(position).getOpinion() != null) {
            if (questions.get(position).getOpinion()) {
                holder.agree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                holder.agree.setTextColor(context.getResources().getColor(R.color.white, null));
            } else {
                holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                holder.disagree.setTextColor(context.getResources().getColor(R.color.white, null));
            }
        }
        holder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.agree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                holder.agree.setTextColor(context.getResources().getColor(R.color.white, null));
                holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                holder.disagree.setTextColor(context.getResources().getColor(R.color.black, null));
                changeOpinion(position, true);
            }
        });
        holder.disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                holder.disagree.setTextColor(context.getResources().getColor(R.color.white, null));
                holder.agree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                holder.agree.setTextColor(context.getResources().getColor(R.color.black, null));
                changeOpinion(position, false);
            }
        });
    }

    private void changeOpinion(int position, Boolean agree){
        Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                questions.get(position).setOpinion(agree);
                databaseUtil.mpDB.mpDao().updateQuestionOpinion(questions.get(position).getQuestion(), agree);
                return true;
            }
        });
        while (!future.isDone());
    }

    @Override
    public int getItemCount() {
        if(questions != null){
            return questions.size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
