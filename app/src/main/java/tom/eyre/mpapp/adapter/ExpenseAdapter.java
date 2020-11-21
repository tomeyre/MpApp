package tom.eyre.mpapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

import tom.eyre.mpapp.R;
import tom.eyre.mpapp.data.ExpenseByYear;

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

    private ExpenseByYear expenseByYear;
    private String[] colors;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public ExpenseAdapter(ExpenseByYear expenseByYear, String[] colors){
        this.expenseByYear = expenseByYear;
        this.colors = colors;
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
        holder.expenseType.setText(expenseByYear.getExpenseTypes().get(position).getType());
        holder.expenseTotal.setText("£" + df.format(expenseByYear.getExpenseTypes().get(position).getTotalSpent()));
    }

    @Override
    public int getItemCount() {
        if(expenseByYear != null){
            return expenseByYear.getExpenseTypes().size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
