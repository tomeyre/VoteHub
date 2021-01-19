package tom.eyre.mp2021.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.activity.MpSelectActivity;
import tom.eyre.mp2021.entity.MpEntity;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    private Context mContext;
    private LayoutInflater inflater;
    private List<MpEntity> mps = null;
    private ArrayList<MpEntity> arraylist;
    private MpSelectActivity mpSelectActivity;

    public ListViewAdapter(Context context, List<MpEntity> mps, MpSelectActivity mpSelectActivity){
        this.mContext = context;
        this.mps = mps;
        this.inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(mps);
        this.mpSelectActivity = mpSelectActivity;
    }

    public class ViewHolder {
        TextView name;
        TextView party;
        TextView constituency;
        LinearLayout colorParty;
        Button compareBtn;
        RelativeLayout active;
    }

    @Override
    public int getCount() {
        return mps.size();
    }

    @Override
    public MpEntity getItem(int position) {
        return mps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_search_item, null);
            // Locate the TextViews in listview_item.xml
            holder.constituency = view.findViewById(R.id.constituency);
            holder.name = view.findViewById(R.id.name);
            holder.colorParty = view.findViewById(R.id.colorParty);
            holder.compareBtn = view.findViewById(R.id.compareBtn);
            holder.active = view.findViewById(R.id.active);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(mps.get(position).getFullName());
        if (mps.get(position).getActive() &&
                !mps.get(position).getMpFor().equalsIgnoreCase("speaker") &&
                !mps.get(position).getMpFor().equalsIgnoreCase("life peer")) {
            holder.constituency.setText("Current MP for " + mps.get(position).getMpFor());
        } else if (!mps.get(position).getMpFor().equalsIgnoreCase("speaker") &&
                !mps.get(position).getMpFor().equalsIgnoreCase("life peer")) {
            holder.constituency.setText("Ex-MP for " + mps.get(position).getMpFor());
        } else {
            holder.constituency.setText(mps.get(position).getMpFor());
        }
        holder.party = view.findViewById(R.id.party);
        holder.party.setText(mps.get(position).getParty());
        if (!mps.get(position).getActive()) {
            holder.active.setBackgroundColor(mContext.getColor(R.color.red));
        }else{
            holder.active.setBackgroundColor(mContext.getColor(R.color.white));
        }
//            int color;
//            try {
//                color = mContext.getResources().getColor(R.color.parlimentGreen, null);//getColor(mps.get(position));
//            } catch (Exception e) {
//                color = mContext.getResources().getColor(R.color.parlimentGreen, null);
//            }
//            GradientDrawable gd = new GradientDrawable(
//                    GradientDrawable.Orientation.RIGHT_LEFT,
//                    new int[]{ColorUtils.setAlphaComponent(color, 0xaa),
//                            0x00000000});
//            gd.setCornerRadius(0f);
//            holder.colorParty.setBackground(gd);
//        }else {
//            holder.colorParty.setBackgroundColor(mContext.getResources().getColor(R.color.white, null));
//        }
        holder.compareBtn.setVisibility(View.GONE);
//        holder.compareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mpSelectActivity.updateList(mps.get(position));
//            }
//        });
//        if(mpSelectActivity.alreadySelectedOne()){
//            holder.compareBtn.setVisibility(View.INVISIBLE);
//        }else{
//            holder.compareBtn.setVisibility(View.VISIBLE);
//        }
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        if (charText != null) {
            charText = charText.toLowerCase(Locale.getDefault());
            mps.clear();
            if (charText.length() == 0) {
                mps.addAll(arraylist);
            } else {
                for (MpEntity mp : arraylist) {
                    if ((mp.getFullName()).toLowerCase(Locale.getDefault()).contains(charText)) {
                        mps.add(mp);
                    } else if (mp.getMpFor().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mps.add(mp);
                    } else if (mp.getParty().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mps.add(mp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

}