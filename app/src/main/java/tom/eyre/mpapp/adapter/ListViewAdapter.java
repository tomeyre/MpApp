package tom.eyre.mpapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tom.eyre.yourvotematters.R;
import tom.eyre.yourvotematters.entity.MpEntity;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    private Context mContext;
    private LayoutInflater inflater;
    private List<MpEntity> mps = null;
    private ArrayList<MpEntity> arraylist;

    public ListViewAdapter(Context context, List<MpEntity> mps) {
        this.mContext = context;
        this.mps = mps;
        this.inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(mps);
    }

    public class ViewHolder {
        TextView name;
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
            view = inflater.inflate(R.layout.list_view_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(mps.get(position).getName());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mps.clear();
        if (charText.length() == 0) {
            mps.addAll(arraylist);
        } else {
            for (MpEntity mp : arraylist) {
                if (mp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mps.add(mp);
                }
            }
        }
        notifyDataSetChanged();
    }

}