package tom.eyre.mpapp.fragment.ui.mpexpensesvotes.mpexpenses;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.mpapp.R;
import tom.eyre.mpapp.adapter.ExpenseAdapter;
import tom.eyre.mpapp.data.ColorList;
import tom.eyre.mpapp.data.ExpenseCalculatedResponse;
import tom.eyre.mpapp.data.ExpenseType;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.service.ExpenseService;
import tom.eyre.mpapp.util.DatabaseUtil;
import tom.eyre.mpapp.util.DrawableFromUrl;

public class ExpensesFragment extends Fragment {

    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private ExpenseService expenseService = new ExpenseService();
    private ExpensesViewModel expensesViewModel;
    private DatabaseUtil databaseUtil;
    private MpEntity mp;

    private ExpenseCalculatedResponse expenses;
    private ColorList colors = ColorList.getInstance();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private Integer current;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mp = (MpEntity) getArguments().getSerializable("mp");
        expensesViewModel =
                ViewModelProviders.of(this).get(ExpensesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_expenses, container, false);

        recyclerView = root.findViewById(R.id.expenseRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        final TextView mpName = root.findViewById(R.id.mpName);
        final TextView expenseYear = root.findViewById(R.id.expenseYear);
        final ImageView mpImage = root.findViewById(R.id.mpImage);
        final Button next = root.findViewById(R.id.next);
        final Button previous = root.findViewById(R.id.previous);
        final CardView topView = root.findViewById(R.id.topView);
        final RelativeLayout loading = root.findViewById(R.id.loading);
        loading.setBackgroundColor(getColor());
        loading.setAlpha(0.7f);

        expensesViewModel.getMpName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mpName.setText(s);
            }
        });
        expensesViewModel.getExpenseYear().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                expenseYear.setText(s);
            }
        });
        expensesViewModel.getMpImage().observe(getViewLifecycleOwner(), new Observer<Drawable>() {
            @Override
            public void onChanged(@Nullable Drawable drawable) {
                mpImage.setImageDrawable(drawable);
            }
        });
        expensesViewModel.getTopView().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String c) {
                topView.setBackgroundColor(Color.parseColor(c));
            }
        });

        next.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current == 0){
                    previous.setVisibility(View.VISIBLE);
                }
                current++;
                setData(current);
                if(current == expenses.getExpenseByYears().size()-1) {
                    next.setVisibility(View.INVISIBLE);
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current == expenses.getExpenseByYears().size()-1){
                    next.setVisibility(View.VISIBLE);
                }
                current--;
                setData(current);
                if(current == 0) {
                    previous.setVisibility(View.INVISIBLE);
                }
            }
        });

        try {
            topView.setBackgroundColor(getColor());
            next.setBackgroundColor(getColor());
            previous.setBackgroundColor(getColor());
        }catch (Exception e){
            e.printStackTrace();
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    expenses = expenseService.calculateExpensesPerYear(mp,databaseUtil.mpDB);
                    updateExpenseFragment();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    private void updateExpenseFragment(){
        current = expenses.getExpenseByYears().size()-1;
        updateMpDetails();
        setData(current);
    }

    private void setData(int year) {
        if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                if(getView() != null) {
                    TextView expenseYear = getView().findViewById(R.id.expenseYear);
                    PieChart pieChart = getView().findViewById(R.id.piechart);
                    pieChart.clearChart();
                    updateRecycler(year);
                    expenseYear.setText("Total : £" + getTotalByYear(year) + "\nYear : 20" + expenses.getExpenseByYears().get(year).getYear().split("_")[0]);
                    for (int i = 0; i < expenses.getExpenseByYears().get(year).getExpenseTypes().size(); i++) {
                        pieChart.addPieSlice(
                                new PieModel(
                                        expenses.getExpenseByYears().get(year).getExpenseTypes().get(i).getType(),
                                        expenses.getExpenseByYears().get(year).getExpenseTypes().get(i).getTotalSpent().intValue(),
                                        Color.parseColor(colors.getList()[i])));

                    }

                    pieChart.startAnimation();
                }
            }
        });
    }

    private void updateRecycler(int year){
        if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                mAdapter = new ExpenseAdapter(expenses.getExpenseByYears().get(year), colors.getList());
                mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
                RelativeLayout loading = getView().findViewById(R.id.loading);
                loading.setVisibility(View.GONE);
                RelativeLayout expense = getView().findViewById(R.id.expense);
                expense.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getTotalByYear(int year){
        Double total = 0d;
        for(ExpenseType type : expenses.getExpenseByYears().get(year).getExpenseTypes()){
            total += type.getTotalSpent();
        }
        return df.format(total);
    }

    private void updateMpDetails() {
        if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                final Drawable drawable = new DrawableFromUrl().get(mp.getId());
                if(getView() != null) {
                    ImageView mpImage = getView().findViewById(R.id.mpImage);
                    TextView mpName = getView().findViewById(R.id.mpName);
                    mpImage.setImageDrawable(drawable);
                    if (mp.getFullName().length() > 20) {
                        mpName.setTextSize(25);
                    } else {
                        mpName.setTextSize(30);
                    }
                    mpName.setText(mp.getFullName());
                }
            }
        });
    }

    private int getColor(){
        return  getResources().getColor(getResources().getIdentifier(mp.getParty().toLowerCase().trim()
                .replace("&", "and")
                .replace(" +", " ")
                .replace(" ", "_")
                .replace("-", "_")
                .replace(",", "")
                .replace("'", "")
                .replace(":", "")
                .replace(")", "")
                .replace("-", "")
                .replace("(", ""), "color", getActivity().getPackageName()));
    }

}