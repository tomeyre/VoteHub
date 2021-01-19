package tom.eyre.mp2021.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.adapter.ExpenseAdapter;
import tom.eyre.mp2021.adapter.YearlyExpenseBreakDownAdapter;
import tom.eyre.mp2021.data.ExpenseCalculatedResponse;
import tom.eyre.mp2021.data.ExpenseResponse;
import tom.eyre.mp2021.data.ExpenseTypesByYear;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.service.ExpenseService;
import tom.eyre.mp2021.utility.DatabaseUtil;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class ExpensesFragment extends Fragment {


    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private ExpenseService expenseService = new ExpenseService();
    private List<ExpenseResponse> expenses;
    private ExpenseCalculatedResponse expenseCalculatedResponse;
    private DatabaseUtil databaseUtil;
    private MpEntity mp;
    private ExpensesViewModel expensesViewModel;
    private Integer currentYear = 0;

    private BarChart yearlyBarChart;
    private static final String SET_LABEL = "Yearly Expense Total in £";
    private String[] YEARS;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager layoutManager;

    private RelativeLayout loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mp = (MpEntity) getArguments().getSerializable("mp");
        expensesViewModel = ViewModelProviders.of(this).get(ExpensesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_mp_expenses, container, false);

        yearlyBarChart = root.findViewById(R.id.yearlyBarChart);
        recyclerView = root.findViewById(R.id.yearlyExpenseRv);
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        loading = root.findViewById(R.id.loadingExpenses);

        final TextView yearTv = root.findViewById(R.id.year);

        expensesViewModel.getYears().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                yearTv.setText(s);
            }
        });

        expensesViewModel.getYears().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                yearTv.setText(s);
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    expenses = expenseService.getExpensesForMp(mp, databaseUtil.localDatabase);
                    expenseCalculatedResponse = expenseService.calculateExpenses(expenses);
                    YEARS = new String[expenseCalculatedResponse.getExpenseTypesByYears().size()];
                    YEARS = expenseCalculatedResponse.getExpenseTypesByYears().stream().map(expenseByYear -> "20" + expenseByYear.getYear().substring(0, 2)).collect(Collectors.toList()).toArray(YEARS);
                    setExpenses();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    private void updateRecyclerView() {
        mAdapter = new YearlyExpenseBreakDownAdapter(expenseService.buildYearlyList(expenses), getContext());
        mAdapter.setHasStableIds(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentYear = layoutManager.findFirstVisibleItemPosition();
                TextView yearTv = getView().findViewById(R.id.year);
                yearTv.setText(YEARS[currentYear]);
// highlight the entry and x-position 50 in the first (0) DataSet
                Highlight highlight = new Highlight(currentYear, 0, 0);

                yearlyBarChart.highlightValue(highlight, false); // highlight this value, don't call listener
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                currentYear = layoutManager.findFirstVisibleItemPosition();
//                TextView yearTv = getView().findViewById(R.id.year);
//                yearTv.setText(YEARS[currentYear]);
//// highlight the entry and x-position 50 in the first (0) DataSet
//                Highlight highlight = new Highlight(currentYear, 0, 0);
//
//                yearlyBarChart.highlightValue(highlight, false); // highlight this value, don't call listener
            }
        });
        TextView yearTv = getView().findViewById(R.id.year);
        if(!expenses.isEmpty()) {
            yearTv.setText(YEARS[currentYear]);
            SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(recyclerView);
        }else{
            TextView title = getView().findViewById(R.id.barChartTitle);
            yearlyBarChart.setVisibility(View.GONE);
            yearTv.setVisibility(View.GONE);
            title.setText("No Expenses Recorded For MP");
        }
        loading.setVisibility(View.GONE);
    }

    private void setExpenses() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                setYearlySpendMinusPayroll();
            }
        });
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateRecyclerView();
                }
            });
        }
    }

    private void setYearlySpendMinusPayroll() {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BarData data = createChartData();
                    configureChartAppearance();
                    prepareChartData(data);
                    yearlyBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            recyclerView.scrollToPosition((int) e.getX());
                            currentYear = (int) e.getX();
                            TextView yearTv = getView().findViewById(R.id.year);
                            yearTv.setText(YEARS[currentYear]);
                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });

                }
            });
        }
    }

    private void configureChartAppearance() {
        yearlyBarChart.getDescription().setEnabled(false);
        yearlyBarChart.setDrawValueAboveBar(false);

        XAxis xAxis = yearlyBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(YEARS));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis axisLeft = yearlyBarChart.getAxisLeft();
        axisLeft.setGranularity(1f);
        axisLeft.setAxisMinimum(0);
        yearlyBarChart.getAxisRight().setEnabled(false);
        yearlyBarChart.getXAxis().setDrawGridLines(false);

//        YAxis axisRight = yearlyBarChart.getAxisRight();
//        axisRight.setGranularity(1f);
//        axisRight.setAxisMinimum(0);
    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        float x = 0f;
        for (ExpenseTypesByYear expenseTypesByYear : expenseCalculatedResponse.getExpenseTypesByYears()) {
            Double total = 0d;
            total = expenseTypesByYear.getExpenseTypes().stream().mapToDouble(expenseType -> expenseType.getTotalSpent()).sum();
            float y = total.floatValue();
            values.add(new BarEntry(x, y));
            x++;
        }

        BarDataSet set1 = new BarDataSet(values, SET_LABEL);
        set1.setColor(getResources().getColor(R.color.parlimentGreen,null));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        return data;
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(0f);
        yearlyBarChart.setData(data);
        yearlyBarChart.invalidate();
        yearlyBarChart.setScaleEnabled(false);
        Highlight highlight = new Highlight(currentYear, 0, 0);
        yearlyBarChart.highlightValue(highlight, false); // highlight this value, don't call listener
    }


}
