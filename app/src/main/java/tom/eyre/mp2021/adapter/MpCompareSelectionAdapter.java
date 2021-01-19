package tom.eyre.mp2021.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lombok.SneakyThrows;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.activity.MpSelectActivity;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.utility.DrawableFromUrl;

public class MpCompareSelectionAdapter extends RecyclerView.Adapter<MpCompareSelectionAdapter.MpCompareSelectionViewHolder> {

    public static class MpCompareSelectionViewHolder extends RecyclerView.ViewHolder {

        private ImageView mpImage;
        private TextView mpName;
        private TextView mpParty;
        private TextView mpConstituency;
        private CardView cvImageOuter;
        private Button removeBtn;

        public MpCompareSelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            mpImage = itemView.findViewById(R.id.mpImage);
            mpName = itemView.findViewById(R.id.mpName);
            mpParty = itemView.findViewById(R.id.partyEntity);
            mpConstituency = itemView.findViewById(R.id.mpConstituency);
            cvImageOuter= itemView.findViewById(R.id.cvImageOuter);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }

    private ArrayList<MpEntity> mps = new ArrayList<>();
    private Context context;

    public MpCompareSelectionAdapter(ArrayList<MpEntity> mps, Context context){
        this.mps = mps;
        this.context = context;
    }

    @NonNull
    @Override
    public MpCompareSelectionAdapter.MpCompareSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.mp_compare_selected_layout, parent, false);
        return new MpCompareSelectionAdapter.MpCompareSelectionViewHolder(v);
    }

    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull MpCompareSelectionAdapter.MpCompareSelectionViewHolder holder, int position) {
        holder.mpImage.setImageDrawable(new DrawableFromUrl().get(mps.get(position).getId()));
        holder.mpName.setText(mps.get(position).getFullName());
        holder.mpParty.setText(mps.get(position).getParty());
        holder.mpConstituency.setText(mps.get(position).getMpFor().equalsIgnoreCase("life peer") ? mps.get(position).getMpFor() : (mps.get(position).getActive() ? "Current MP for " : "Ex-MP for ") + mps.get(position).getMpFor());
        if (mps.get(position).getActive()) {
            int color;
            try {
                color = context.getResources().getColor(R.color.parlimentGreen, null);//getColor(mps.get(position));
            } catch (Exception e) {
                color = context.getResources().getColor(R.color.parlimentGreen, null);
            }
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.RIGHT_LEFT,
                    new int[]{ColorUtils.setAlphaComponent(color, 0xaa),
                            0x00000000});
            gd.setCornerRadius(0f);
            holder.cvImageOuter.setBackground(gd);
        }else {
            holder.cvImageOuter.setBackgroundColor(context.getResources().getColor(R.color.white, null));
        }
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MpSelectActivity)context).removeFromList(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mps != null){
            return mps.size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
