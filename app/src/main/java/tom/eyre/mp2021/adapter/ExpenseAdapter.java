package tom.eyre.mp2021.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.data.ExpenseResponse;
import tom.eyre.mp2021.data.ExpenseTypesByYear;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        private ImageView coloredSquare;
        private TextView expenseType;
        private TextView expenseTotal;

        public ExpenseViewHolder(View view) {
            super(view);
            this.coloredSquare = view.findViewById(R.id.colorSquare);
            this.expenseType = view.findViewById(R.id.expenseType);
            this.expenseTotal = view.findViewById(R.id.expenseTotal);
        }

    }

    private Map<String, List<ExpenseResponse>> expenses;
    private String[] colors;
    private String[] mKeys;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public ExpenseAdapter(Map<String, List<ExpenseResponse>> expenses, String[] colors){
        this.expenses = expenses;
        this.colors = colors;
        mKeys = expenses.keySet().toArray(new String[expenses.size()]);
    }

    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_expense_layout, parent, false);
        return new ExpenseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        holder.coloredSquare.setBackgroundColor(Color.parseColor(colors[position]));
        holder.expenseType.setText(mKeys[position]);
        holder.expenseTotal.setText("£" + df.format(expenses.get(mKeys[position]).stream().mapToDouble(ExpenseResponse::getAmountPaid).sum()));
    }

    @Override
    public int getItemCount() {
        if(expenses != null){
            return expenses.size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
