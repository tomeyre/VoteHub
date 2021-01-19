package tom.eyre.mp2021.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.entity.QuestionEntity;
import tom.eyre.mp2021.utility.DatabaseUtil;

import static tom.eyre.mp2021.utility.DatabaseUtil.localDatabase;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        private TextView question;
        private CardView agree;
        private TextView agreeText;
        private CardView disagree;
        private TextView disagreeText;

        public QuestionViewHolder(View view) {
            super(view);
            this.question = view.findViewById(R.id.question);
            this.agree = view.findViewById(R.id.agree);
            this.disagree = view.findViewById(R.id.disagree);
            this.agreeText = view.findViewById(R.id.agreeText);
            this.disagreeText = view.findViewById(R.id.disagreeText);
        }

    }

    private List<QuestionEntity> questions;
    private Context context;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public QuestionAdapter(List<QuestionEntity> questions, Context context){
        this.questions = questions;
        this.context = context;
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
        holder.agreeText.setTextColor(context.getResources().getColor(R.color.black, null));
        holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
        holder.disagreeText.setTextColor(context.getResources().getColor(R.color.black, null));
        if (questions.get(position).getOpinion() != null) {
            if (questions.get(position).getOpinion()) {
                holder.agree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                holder.agreeText.setTextColor(context.getResources().getColor(R.color.white, null));
            } else {
                holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                holder.disagreeText.setTextColor(context.getResources().getColor(R.color.white, null));
            }
        }
        holder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.agreeText.getCurrentTextColor() == context.getResources().getColor(R.color.white, null)){
                    holder.agree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                    holder.agreeText.setTextColor(context.getResources().getColor(R.color.black, null));
                    changeOpinion(position, null);
                }else{
                    holder.agree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                    holder.agreeText.setTextColor(context.getResources().getColor(R.color.white, null));
                    holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                    holder.disagreeText.setTextColor(context.getResources().getColor(R.color.black, null));
                    changeOpinion(position, true);
                }
            }
        });
        holder.disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.disagreeText.getCurrentTextColor() == context.getResources().getColor(R.color.white, null)){
                    holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                    holder.disagreeText.setTextColor(context.getResources().getColor(R.color.black, null));
                    changeOpinion(position, null);
                }else {
                    holder.disagree.setBackgroundColor(context.getResources().getColor(R.color.parlimentGreen, null));
                    holder.disagreeText.setTextColor(context.getResources().getColor(R.color.white, null));
                    holder.agree.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                    holder.agreeText.setTextColor(context.getResources().getColor(R.color.black, null));
                    changeOpinion(position, false);
                }
            }
        });
    }

    private void changeOpinion(int position, Boolean agree){
        Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                questions.get(position).setOpinion(agree);
                localDatabase.localDatabaseDao().updateQuestionOpinion(questions.get(position).getQuestion(), agree);
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
