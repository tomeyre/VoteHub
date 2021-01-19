package tom.eyre.mp2021.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.activity.MpSelectActivity;
import tom.eyre.mp2021.data.QuestionsByType;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.entity.QuestionEntity;
import tom.eyre.mp2021.entity.VoteEntity;
import tom.eyre.mp2021.utility.ColorUtil;
import tom.eyre.mp2021.utility.DrawableFromUrl;

import static tom.eyre.mp2021.utility.ScreenUtils.convertDpToPixel;
import static tom.eyre.mp2021.utility.ScreenUtils.convertPixelsToDp;

public class MpProgressBarAdapter extends RecyclerView.Adapter<MpProgressBarAdapter.MpProgressViewHolder> {

    public static class MpProgressViewHolder extends RecyclerView.ViewHolder {

        private TextView questionName;
        private LinearLayout progressBar;

        public MpProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            questionName = itemView.findViewById(R.id.questionName);
            progressBar = itemView.findViewById(R.id.opinionProgressBar);
        }
    }

    private List<QuestionsByType> answers;
    private Context context;
    private int[] colors;
    private int width;

    public MpProgressBarAdapter(List<QuestionsByType> answers, Context context){
        this.answers = answers;
        this.context = context;
        colors = new int[] {context.getResources().getColor(R.color.parlimentGreen, null),
                context.getResources().getColor(R.color.light_gray),
                context.getResources().getColor(R.color.red)};
    }

    @NonNull
    @Override
    public MpProgressBarAdapter.MpProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.opinions_progress_layout, parent, false);
        return new MpProgressBarAdapter.MpProgressViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MpProgressBarAdapter.MpProgressViewHolder holder, int position) {
        holder.questionName.setText(answers.get(position).getType());
//        View parent = (View) holder.progressBar.getParent();
//        parent.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY));
//        int width =  (int) convertDpToPixel(parent.getMeasuredWidth(), context);
        View parent = (View) holder.progressBar.getParent();

        parent.post(new Runnable() {
            @Override
            public void run() {
                width = parent.getMeasuredWidth();
                int[] results = {answers.get(position).getAgreedWithUser(),
                        answers.get(position).getVoteDidNotVote() + answers.get(position).getNoRecord(),
                        answers.get(position).getTotalVotes() - answers.get(position).getVoteDidNotVote() - answers.get(position).getNoRecord() - answers.get(position).getAgreedWithUser()};
                int i = 0;
                while(i < 3){
                    CardView cardView = new CardView(context);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            (width / answers.get(position).getTotalVotes()) * results[i], (int) convertDpToPixel(20, context));
                    layoutParams.setMargins(1,0,0,0);
                    cardView.setBackgroundColor(colors[i]);
                    cardView.setLayoutParams(layoutParams);

                    holder.progressBar.addView(cardView);

                    i++;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(answers != null){
            return answers.size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
