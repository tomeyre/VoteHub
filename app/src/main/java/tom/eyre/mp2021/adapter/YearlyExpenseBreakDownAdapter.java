package tom.eyre.mp2021.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.data.ColorList;
import tom.eyre.mp2021.data.ExpenseBreakdown;
import tom.eyre.mp2021.data.ExpenseResponse;
import tom.eyre.mp2021.data.ExpenseTypesByYear;
import tom.eyre.mp2021.data.ExpensesByYear;
import tom.eyre.mp2021.utility.AnimateUtil;

import static android.view.View.GONE;
import static tom.eyre.mp2021.utility.ScreenUtils.convertDpToPixel;
import static tom.eyre.mp2021.utility.ScreenUtils.convertPixelsToDp;
import static tom.eyre.mp2021.utility.ScreenUtils.getMeasuredHeight;
import static tom.eyre.mp2021.utility.ScreenUtils.getParentMeasuredHeight;

public class YearlyExpenseBreakDownAdapter extends RecyclerView.Adapter<YearlyExpenseBreakDownAdapter.YearlyExpenseHolder> {

    public static final String PAYROLL = "payroll";

    public static class YearlyExpenseHolder extends RecyclerView.ViewHolder {

        private TextView rentCount;
//        private TextView rentPercent;
        private TextView utilitiesCount;
//        private TextView utilitiesPercent;
        private TextView councilTaxCount;
//        private TextView councilTaxPercent;

        private TextView londonHotelsCount;
//        private TextView londonHotelsPercent;
        private TextView otherHotelsCount;
//        private TextView otherHotelsPercent;

        private TextView trainCount;
//        private TextView trainPercent;
        private TextView taxiCount;
//        private TextView taxiPercent;
        private TextView airCount;
//        private TextView airPercent;
        private TextView ownCarCount;
//        private TextView ownCarPercent;
        private TextView partnerTravelCount;
//        private TextView partnerTravelPercent;
        private TextView staffTravelCount;
//        private TextView staffTravelPercent;
        private TextView salaryCount;
        private TextView staffSalaryCount;
//        private TextView staffSalaryPercent;
        private TextView officeCostTotalCount;
//        private TextView officeCostTotalPercent;
        private PieChart pieChart;
        private RecyclerView recyclerViewOfficeCostBreakdown;
        private LinearLayout linearLayoutOfficeCostBreakdown;
        private Button officeCostBreakdownBtn;
        private RelativeLayout officeCostsRelativeLayout;

        public YearlyExpenseHolder(View view) {
            super(view);
            rentCount = view.findViewById(R.id.rentCount);
//            rentPercent = view.findViewById(R.id.rentPercent);
            utilitiesCount = view.findViewById(R.id.utilitiesCount);
//            utilitiesPercent = view.findViewById(R.id.utilitiesPercent);
            councilTaxCount = view.findViewById(R.id.councilTaxCount);
//            councilTaxPercent = view.findViewById(R.id.councilTaxPercent);
            londonHotelsCount = view.findViewById(R.id.londonHotelsCount);
//            londonHotelsPercent = view.findViewById(R.id.londonHotelsPercent);
            otherHotelsCount = view.findViewById(R.id.otherHotelsCount);
//            otherHotelsPercent = view.findViewById(R.id.otherHotelsPercent);
            trainCount = view.findViewById(R.id.trainCount);
//            trainPercent = view.findViewById(R.id.trainPercent);
            taxiCount = view.findViewById(R.id.taxiCount);
//            taxiPercent = view.findViewById(R.id.taxiPercent);
            airCount = view.findViewById(R.id.airCount);
//            airPercent = view.findViewById(R.id.airPercent);
            ownCarCount = view.findViewById(R.id.ownCarCount);
//            ownCarPercent = view.findViewById(R.id.ownCarPercent);
            partnerTravelCount = view.findViewById(R.id.partnerTravelCount);
//            partnerTravelPercent = view.findViewById(R.id.partnerTravelPercent);
            staffTravelCount = view.findViewById(R.id.staffTravelCount);
//            staffTravelPercent = view.findViewById(R.id.staffTravelPercent);
            salaryCount = view.findViewById(R.id.salaryCount);
            staffSalaryCount = view.findViewById(R.id.staffSalaryCount);
//            staffSalaryPercent = view.findViewById(R.id.staffSalaryPercent);
            officeCostTotalCount = view.findViewById(R.id.officeCostTotalCount);
//            officeCostTotalPercent = view.findViewById(R.id.officeCostTotalPercent);
            officeCostBreakdownBtn = view.findViewById(R.id.officeCostBreakdownBtn);
            recyclerViewOfficeCostBreakdown = view.findViewById(R.id.piechartRv);
            linearLayoutOfficeCostBreakdown = view.findViewById(R.id.officeCostsBreakdown);
            officeCostsRelativeLayout = view.findViewById(R.id.officeCostsRelativeLayout);
            pieChart = view.findViewById(R.id.piechart);
        }

    }

    private List<ExpensesByYear> expensesByYear;
    private Context context;
    public static final String ACCOMMODATION = "accommodation";
    public static final String RENT = "rent";
    public static final String COUNCIL_TAX = "council tax";
    public static final String WATER = "water";
    public static final String ELECTRIC = "electric";
    public static final String TV_LICENSE = "license";
    public static final String GAS = "gas";
    public static final String HOTEL = "hotel";
    public static final String LONDON = "london";
    public static final String STAFF = "staff";
    public static final String OUTSIDE = "outside";
    public static final String NOT = "not";
    public static final String MP_TRAVEL = "mp travel";
    public static final String STAFF_TRAVEL = "staff travel";
    public static final String CAR = "car";
    public static final String RAIL = "rail";
    public static final String TAXI = "taxi";
    public static final String AIR = "air";
    public static final String STAFFING = "staffing";
    public static final String NOTHING_EXPENSED = "£0.00";
    public static final String DEPENDANT_TRAVEL = "dependant travel";
    public static final String OFFICE_COSTS = "office costs";
    private ColorList colors = ColorList.getInstance();
    private RecyclerView.Adapter mAdapterOfficeCostBreakdown;
    private RecyclerView.LayoutManager layoutManagerOfficeCostBreakdown;
    private Size size = new Size();

    public YearlyExpenseBreakDownAdapter(List<ExpensesByYear> expensesByYear, Context context) {
        this.expensesByYear = expensesByYear;
        this.context = context;
    }

    @NonNull
    @Override
    public YearlyExpenseBreakDownAdapter.YearlyExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_yearly_expense_layout, parent, false);
        return new YearlyExpenseBreakDownAdapter.YearlyExpenseHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull YearlyExpenseBreakDownAdapter.YearlyExpenseHolder holder, int position) {
        if (size.fullHeight == 0) {
            View parent = (View) holder.linearLayoutOfficeCostBreakdown.getParent();
            parent.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            size.fullHeight = (int) getParentMeasuredHeight((ViewGroup)parent);
        }
        if(size.shrunkHeight != 0){ holder.officeCostsRelativeLayout.getLayoutParams().height = size.shrunkHeight;}
        holder.linearLayoutOfficeCostBreakdown.setVisibility(View.GONE);
        layoutManagerOfficeCostBreakdown = new LinearLayoutManager(context);
        holder.recyclerViewOfficeCostBreakdown.setLayoutManager(layoutManagerOfficeCostBreakdown);
        //----------------------------CALCULATIONS---------------------------------------------
        Double totalSpend = expensesByYear.get(position).getExpenses().stream()
//                .filter(expenseResponse -> !expenseResponse.getExpenseType().equalsIgnoreCase(PAYROLL))
                .mapToDouble(expenseResponse -> expenseResponse.getAmountPaid()).sum();
        Double onePercent = 100d / totalSpend;
        Double rent = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(ACCOMMODATION) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(RENT))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double utilities = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(ACCOMMODATION) &&
                        (expenseEntity.getExpenseType().toLowerCase().contains(WATER) ||
                                expenseEntity.getExpenseType().toLowerCase().contains(ELECTRIC) ||
                                expenseEntity.getExpenseType().toLowerCase().contains(TV_LICENSE) ||
                                expenseEntity.getExpenseType().toLowerCase().contains(GAS)))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double councilTax = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(ACCOMMODATION) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(COUNCIL_TAX))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double londonHotels = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(ACCOMMODATION) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(HOTEL) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(LONDON) &&
                        !expenseEntity.getExpenseType().toLowerCase().contains(STAFF) &&
                        !expenseEntity.getExpenseType().toLowerCase().contains(NOT) &&
                        !expenseEntity.getExpenseType().toLowerCase().contains(OUTSIDE))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double otherHotels = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(ACCOMMODATION) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(HOTEL) &&
                        (expenseEntity.getExpenseType().toLowerCase().contains(NOT) ||
                                expenseEntity.getExpenseType().toLowerCase().contains(STAFF) ||
                                expenseEntity.getExpenseType().toLowerCase().contains(OUTSIDE)))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double train = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(MP_TRAVEL) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(RAIL) &&
                        !expenseEntity.getExpenseType().toLowerCase().contains(STAFF))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double taxi = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(MP_TRAVEL) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(TAXI) &&
                        !expenseEntity.getExpenseType().toLowerCase().contains(STAFF))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double air = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(MP_TRAVEL) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(AIR) &&
                        !expenseEntity.getExpenseType().toLowerCase().contains(STAFF))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double ownCar = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(MP_TRAVEL) &&
                        expenseEntity.getExpenseType().toLowerCase().contains(CAR) &&
                        !expenseEntity.getExpenseType().toLowerCase().contains(STAFF))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double partnerTravel = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(DEPENDANT_TRAVEL))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double staffTravel = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(STAFF_TRAVEL))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double salary = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getExpenseType().equalsIgnoreCase(PAYROLL))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double staffSalary = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> !expenseEntity.getExpenseType().equalsIgnoreCase(PAYROLL) && expenseEntity.getCategory().equalsIgnoreCase(STAFFING))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        Double officeCostTotal = expensesByYear.get(position).getExpenses().stream()
                .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(OFFICE_COSTS))
                .collect(Collectors.toList()).stream().mapToDouble(expenseEntity -> expenseEntity.getAmountPaid()).sum();
        //-----------------SECOND HOME------------------------------
        holder.rentCount.setText(currencyWithChosenLocalisation(rent, new Locale("en", "GB")));
//        holder.rentPercent.setText(percent(onePercent * rent));
        holder.utilitiesCount.setText(currencyWithChosenLocalisation(utilities, new Locale("en", "GB")));
//        holder.utilitiesPercent.setText(percent(onePercent * utilities));
        holder.councilTaxCount.setText(currencyWithChosenLocalisation(councilTax, new Locale("en", "GB")));
//        holder.councilTaxPercent.setText(percent(onePercent * councilTax));
        // -----------------HOTELS------------------------------
        holder.londonHotelsCount.setText(currencyWithChosenLocalisation(londonHotels, new Locale("en", "GB")));
//        holder.londonHotelsPercent.setText(percent(onePercent * londonHotels));
        holder.otherHotelsCount.setText(currencyWithChosenLocalisation(otherHotels, new Locale("en", "GB")));
//        holder.otherHotelsPercent.setText(percent(onePercent * otherHotels));
        //-----------------TRAVEL------------------------------
        holder.trainCount.setText(currencyWithChosenLocalisation(train, new Locale("en", "GB")));
//        holder.trainPercent.setText(percent(onePercent * train));
        holder.taxiCount.setText(currencyWithChosenLocalisation(taxi, new Locale("en", "GB")));
//        holder.taxiPercent.setText(percent(onePercent * taxi));
        holder.airCount.setText(currencyWithChosenLocalisation(air, new Locale("en", "GB")));
//        holder.airPercent.setText(percent(onePercent * air));
        holder.ownCarCount.setText(currencyWithChosenLocalisation(ownCar, new Locale("en", "GB")));
//        holder.ownCarPercent.setText(percent(onePercent * ownCar));
        holder.partnerTravelCount.setText(currencyWithChosenLocalisation(partnerTravel, new Locale("en", "GB")));
//        holder.partnerTravelPercent.setText(percent(onePercent * partnerTravel));
        holder.staffTravelCount.setText(currencyWithChosenLocalisation(staffTravel, new Locale("en", "GB")));
//        holder.staffTravelPercent.setText(percent(onePercent * staffTravel));
        holder.salaryCount.setText(currencyWithChosenLocalisation(salary, new Locale("en", "GB")));
        holder.staffSalaryCount.setText(currencyWithChosenLocalisation(staffSalary, new Locale("en", "GB")));
//        holder.staffSalaryPercent.setText(percent(onePercent * staffSalary));
        holder.officeCostTotalCount.setText(currencyWithChosenLocalisation(officeCostTotal, new Locale("en", "GB")));
//        holder.officeCostTotalPercent.setText(percent(onePercent * officeCostTotal));

        holder.pieChart.clearChart();
        Map<String, List<ExpenseResponse>> expenseBreakdowns =
                getExpenseBreakdownList(expensesByYear.get(position).getExpenses().stream()
                        .filter(expenseEntity -> expenseEntity.getCategory().equalsIgnoreCase(OFFICE_COSTS))
                        .collect(Collectors.toList()));

        int count = 0;
        for (Map.Entry<String, List<ExpenseResponse>> entry : expenseBreakdowns.entrySet()) {
            holder.pieChart.addPieSlice(
                    new PieModel(
                            entry.getKey(),
                            (float) entry.getValue().stream().mapToDouble(expenseResponse -> expenseResponse.getAmountPaid()).sum(),
                            Color.parseColor(colors.getList()[count])));
            count++;

        }
        holder.officeCostBreakdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (size.shrunkHeight == 0 || size.shrunkHeight == (int) getParentMeasuredHeight(holder.officeCostsRelativeLayout)) {
                    if (size.shrunkHeight == 0) {
                        size.shrunkHeight = (int) getParentMeasuredHeight(holder.officeCostsRelativeLayout);
                    }
                    if (holder.linearLayoutOfficeCostBreakdown.getVisibility() == GONE) {
                        holder.linearLayoutOfficeCostBreakdown.setVisibility(View.VISIBLE);
                    }
                    new AnimateUtil().officeCostsBreakdown(holder.officeCostsRelativeLayout, context, size.shrunkHeight, size.fullHeight);
                } else if (size.fullHeight == (int) getParentMeasuredHeight(holder.linearLayoutOfficeCostBreakdown)) {
                    new AnimateUtil().officeCostsBreakdown(holder.officeCostsRelativeLayout, context, size.fullHeight, size.shrunkHeight);
                }
            }
        });

        mAdapterOfficeCostBreakdown = new ExpenseAdapter(expenseBreakdowns, colors.getList());
        mAdapterOfficeCostBreakdown.setHasStableIds(true);
        holder.recyclerViewOfficeCostBreakdown.setAdapter(mAdapterOfficeCostBreakdown);
        mAdapterOfficeCostBreakdown.notifyDataSetChanged();

    }

    private Map<String, List<ExpenseResponse>> getExpenseBreakdownList(List<ExpenseResponse> expensesByYear) {
        return expensesByYear.stream().collect(Collectors.groupingBy(ExpenseResponse::getExpenseType));
    }

    private static String currencyWithChosenLocalisation(double value, Locale locale) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        return nf.format(Math.round(value)).substring(0, nf.format(Math.round(value)).length() - 3);
    }

    private static String percent(double value) {
        return round(value, 1) + "%";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public int getItemCount() {
        if (expensesByYear != null) {
            return expensesByYear.size();
        } else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

class Size{
    int fullHeight = 0;
    int shrunkHeight = 0;
}
