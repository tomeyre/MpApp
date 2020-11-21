package tom.eyre.mpapp.adapter;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tom.eyre.mpapp.R;
import tom.eyre.mpapp.entity.BillsEntity;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    public static class BillViewHolder extends RecyclerView.ViewHolder {

        private TextView billName;

        public BillViewHolder(View view) {
            super(view);
            this.billName = view.findViewById(R.id.billName);
        }

    }

    private List<BillsEntity> bills;

    public BillAdapter(List<BillsEntity> bills){
        this.bills = bills;
    }

    @NonNull
    @Override
    public BillAdapter.BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_bill_layout, parent, false);
        return new BillViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.BillViewHolder holder, int position) {
        String text = "<a href=\"" + getUrl(position) + "\"> " + bills.get(position).getTitle() + " </a>";
        holder.billName.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        holder.billName.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String getUrl(int position){
        String url = bills.get(position).getUrl();
        if(url.charAt(4) != 's'){
            url = url.substring(0,4) + 's' + url.substring(4);
        }
        if(!Character.isDigit(url.charAt(url.lastIndexOf("bills/") + "bills/".length()))){
            url = url.substring(0, url.lastIndexOf("bills/") + "bills".length()) + "/20" + (Integer.parseInt(bills.get(position).getBillDate().substring(2, 4)) - 1) + "-" +
                    (bills.get(position).getBillDate().substring(2, 4)) + "/" + url.substring(url.lastIndexOf("bills/") + "bills/".length());
        }
        return url;
    }

    @Override
    public int getItemCount() {
        if(bills != null){
            return bills.size();
        }
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
