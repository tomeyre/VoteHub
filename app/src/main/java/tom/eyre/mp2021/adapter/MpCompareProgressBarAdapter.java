package tom.eyre.mp2021.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.data.QuestionsByType;

public class MpCompareProgressBarAdapter extends Adapter<MpCompareProgressBarAdapter.MpCompareProgressBarViewHolder> {

    public static class MpCompareProgressBarViewHolder extends RecyclerView.ViewHolder {

        private TextView titleA;
        private TextView totalsA;
        private TextView pbTextA;
        private ProgressBar progressBarA;

        private TextView titleB;
        private TextView totalsB;
        private TextView pbTextB;
        private ProgressBar progressBarB;

        public MpCompareProgressBarViewHolder(@NonNull View itemView) {
            super(itemView);
            this.titleA = itemView.findViewById(R.id.titleA);
            this.totalsA = itemView.findViewById(R.id.totalsA);
            this.pbTextA = itemView.findViewById(R.id.pbTextA);
            this.progressBarA = itemView.findViewById(R.id.progressBarA);
            this.titleB = itemView.findViewById(R.id.titleB);
            this.totalsB = itemView.findViewById(R.id.totalsB);
            this.pbTextB = itemView.findViewById(R.id.pbTextB);
            this.progressBarB = itemView.findViewById(R.id.progressBarB);
        }
    }

    private ArrayList<QuestionsByType> mpAVotes = new ArrayList<>();
    private ArrayList<QuestionsByType> mpBVotes = new ArrayList<>();

    public MpCompareProgressBarAdapter(ArrayList<QuestionsByType> mpAVotes, ArrayList<QuestionsByType> mpBVotes){
        this.mpAVotes = mpAVotes;
        this.mpBVotes = mpBVotes;
    }

    @NonNull
    @Override
    public MpCompareProgressBarAdapter.MpCompareProgressBarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.compare_progress_bar_layout, parent, false);
        return new MpCompareProgressBarAdapter.MpCompareProgressBarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MpCompareProgressBarAdapter.MpCompareProgressBarViewHolder holder, int position) {
        holder.pbTextA.setVisibility(View.INVISIBLE);
        holder.totalsA.setVisibility(View.VISIBLE);
        holder.progressBarA.setVisibility(View.VISIBLE);
        if(mpAVotes != null && !mpAVotes.isEmpty()) {
            holder.progressBarA.setVisibility(View.VISIBLE);
            holder.titleA.setText(mpAVotes.get(position).getType());
            holder.totalsA.setText(mpAVotes.get(position).getAgreedWithUser() + "/" + (mpAVotes.get(position).getTotalVotes() - (mpAVotes.get(position).getNoRecord() + mpAVotes.get(position).getVoteDidNotVote())));
            holder.progressBarA.setMax(mpAVotes.get(position).getTotalVotes() - (mpAVotes.get(position).getNoRecord() + mpAVotes.get(position).getVoteDidNotVote()));
            holder.progressBarA.setProgress(mpAVotes.get(position).getAgreedWithUser());
            if (mpAVotes.get(position).getTotalVotes() == mpAVotes.get(position).getNoRecord()) {
                holder.pbTextA.setVisibility(View.VISIBLE);
                holder.totalsA.setVisibility(View.INVISIBLE);
                holder.progressBarA.setVisibility(View.INVISIBLE);
                holder.pbTextA.setText("Not MP at time of vote");
            }
            if(mpAVotes.get(position).getTotalVotes() != mpAVotes.get(position).getNoRecord() &&
                    mpAVotes.get(position).getTotalVotes() == (mpAVotes.get(position).getNoRecord() + mpAVotes.get(position).getVoteDidNotVote() + mpAVotes.get(position).getVoteAbstained())){
                holder.pbTextA.setVisibility(View.VISIBLE);
                holder.totalsA.setVisibility(View.INVISIBLE);
                holder.progressBarA.setVisibility(View.INVISIBLE);
                holder.pbTextA.setText("Did not vote on issue");
            }
        }
        if(mpBVotes != null && !mpBVotes.isEmpty()) {
            holder.pbTextB.setVisibility(View.INVISIBLE);
            holder.totalsB.setVisibility(View.VISIBLE);
            holder.progressBarB.setVisibility(View.VISIBLE);
            holder.titleB.setText(mpBVotes.get(position).getType());
            holder.totalsB.setText(mpBVotes.get(position).getAgreedWithUser() + "/" + (mpBVotes.get(position).getTotalVotes() - (mpBVotes.get(position).getNoRecord() + mpBVotes.get(position).getVoteDidNotVote() + mpBVotes.get(position).getVoteAbstained())));
            holder.progressBarB.setMax(mpBVotes.get(position).getTotalVotes() - (mpBVotes.get(position).getNoRecord() + mpBVotes.get(position).getVoteDidNotVote() + mpBVotes.get(position).getVoteAbstained()));
            holder.progressBarB.setProgress(mpBVotes.get(position).getAgreedWithUser());
            if (mpBVotes.get(position).getTotalVotes() - mpBVotes.get(position).getNoRecord() == 0) {
                holder.pbTextB.setVisibility(View.VISIBLE);
                holder.totalsB.setVisibility(View.INVISIBLE);
                holder.progressBarB.setVisibility(View.INVISIBLE);
                holder.pbTextB.setText("No records");
            }
            if(mpBVotes.get(position).getTotalVotes() != mpBVotes.get(position).getNoRecord() &&
                    mpBVotes.get(position).getTotalVotes() == (mpBVotes.get(position).getNoRecord() + mpBVotes.get(position).getVoteDidNotVote() + mpBVotes.get(position).getVoteAbstained())){
                holder.pbTextB.setVisibility(View.VISIBLE);
                holder.totalsB.setVisibility(View.INVISIBLE);
                holder.progressBarB.setVisibility(View.INVISIBLE);
                holder.pbTextB.setText("Did not vote");
            }
        }
    }
    //paul maynard - pete wishart

    @Override
    public int getItemCount() {
        if(mpAVotes != null){
            return mpAVotes.size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
