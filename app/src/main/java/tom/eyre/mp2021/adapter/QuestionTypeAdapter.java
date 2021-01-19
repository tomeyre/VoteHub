package tom.eyre.mp2021.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.activity.MpDetailsActivity;
import tom.eyre.mp2021.activity.QuestionActivity;
import tom.eyre.mp2021.activity.QuizActivity;
import tom.eyre.mp2021.data.QuestionGroupByType;

public class QuestionTypeAdapter extends RecyclerView.Adapter<QuestionTypeAdapter.QuestionViewHolder> {

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        private TextView type;
        private TextView questionsAnswered;
        private CardView questionBtn;

        public QuestionViewHolder(View view) {
            super(view);
            this.type = view.findViewById(R.id.questionType);
            this.questionsAnswered = view.findViewById(R.id.questionsAnswered);
            this.questionBtn = view.findViewById(R.id.questionBtn);
        }

    }

    private ArrayList<QuestionGroupByType> questionGroupByTypes;
    private Context context;

    public QuestionTypeAdapter(ArrayList<QuestionGroupByType> questionGroupByTypes, Context context){
        this.questionGroupByTypes = questionGroupByTypes;
        this.context = context;
    }

    @NonNull
    @Override
    public QuestionTypeAdapter.QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.question_type_layout, parent, false);
        return new QuestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionTypeAdapter.QuestionViewHolder holder, int position) {
        holder.type.setText(questionGroupByTypes.get(position).getType());
        holder.questionsAnswered.setText("Answered " + questionGroupByTypes.get(position).getQuestions().stream().filter(questionEntity -> questionEntity.getOpinion() != null).count() +
                " / " + questionGroupByTypes.get(position).getQuestions().size() + " Total");
        holder.questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, QuestionActivity.class);
                myIntent.putExtra("questions", questionGroupByTypes.get(position).getQuestions());
                context.startActivity(myIntent);
            }
        });
        if (questionGroupByTypes.get(position).getQuestions().stream().filter(questionEntity -> questionEntity.getOpinion() != null).count() !=
        questionGroupByTypes.get(position).getQuestions().size()){
            holder.questionBtn.setCardBackgroundColor(context.getResources().getColor(R.color.white, null));
            holder.questionsAnswered.setTextColor(context.getColor(R.color.black));
            holder.type.setTextColor(context.getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        if(questionGroupByTypes != null){
            return questionGroupByTypes.size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
