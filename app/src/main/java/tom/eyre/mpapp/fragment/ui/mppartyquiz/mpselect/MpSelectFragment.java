package tom.eyre.mpapp.fragment.ui.mppartyquiz.mpselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import tom.eyre.mpapp.activity.CompareMpActivity;
import tom.eyre.mpapp.R;
import tom.eyre.mpapp.activity.MpSelectActivity;
import tom.eyre.mpapp.adapter.ListViewAdapter;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.fragment.container.MpExpensesVotesFragmentContainerActivity;
import tom.eyre.mpapp.fragment.container.MpPartyQuizFragmentContainerActivity;
import tom.eyre.mpapp.util.AnimateUtil;
import tom.eyre.mpapp.util.DatabaseUtil;

import static android.view.View.INVISIBLE;
import static tom.eyre.mpapp.util.ScreenUtils.convertDpToPixel;
import static tom.eyre.mpapp.util.ScreenUtils.getScreenHeight;

public class MpSelectFragment extends Fragment {

    private MpSelectViewModel mpSelectViewModel;
    private static final Logger log = Logger.getLogger(String.valueOf(MpSelectFragment.class));
    private DatabaseUtil databaseUtil;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ListView list;
    private ListViewAdapter adapter;
    private SearchView editsearch;
    private ListView listB;
    private ListViewAdapter adapterB;
    private SearchView editsearchB;

    private List<MpEntity> mpList;
    private List<MpEntity> mpListB;
    private Button compareMps;
    private RelativeLayout mpView2;
    private RelativeLayout back;
    private Boolean vs;
    private MaterialCardView searchCv;

    private String previousQuery = "";

    private Boolean compareModeActivated = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vs = getArguments().getBoolean("vs");
        mpSelectViewModel =
                ViewModelProviders.of(this).get(MpSelectViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mp_select, container, false);

        databaseUtil = DatabaseUtil.getInstance(getActivity());

        list = root.findViewById(R.id.listview);
        list.setVisibility(INVISIBLE);
        editsearch = root.findViewById(R.id.search);
        listB = root.findViewById(R.id.listviewB);
        listB.setVisibility(INVISIBLE);
        editsearchB = root.findViewById(R.id.searchB);
        back = root.findViewById(R.id.back);
        searchCv = root.findViewById(R.id.searchCv);

        float radius = convertDpToPixel(30f, getContext());
        searchCv.setShapeAppearanceModel(
                searchCv.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                        .build());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboardFrom(getContext(), root);
                editsearch.clearFocus();
            }
        });

        compareMps = root.findViewById(R.id.compareMps);
        mpView2 = root.findViewById(R.id.mpView2);

        if (vs) {
            mpView2.setVisibility(View.VISIBLE);
            compareModeActivated = true;
            editsearch.setQuery(null, false);
            editsearchB.setQuery(null, false);
            compareMps.setVisibility(View.VISIBLE);
        } else {
            mpView2.setVisibility(INVISIBLE);
            compareModeActivated = false;
            editsearch.setQuery(null, false);
            editsearchB.setQuery(null, false);
            compareMps.setVisibility(View.INVISIBLE);
        }

        compareMps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!editsearch.getQuery().toString().equalsIgnoreCase("") &&
                        !editsearchB.getQuery().toString().equalsIgnoreCase("")) &&
                        (!editsearch.getQuery().toString().equalsIgnoreCase(editsearchB.getQuery().toString()))) {
                    Intent myIntent = new Intent(getActivity(), CompareMpActivity.class);
                    myIntent.putExtra("mpA", mpList.get(0));
                    myIntent.putExtra("mpB", mpListB.get(0));
                    editsearch.setQuery(null, false);
                    editsearchB.setQuery(null, false);
                    getActivity().startActivity(myIntent);
                } else {
                    Toast.makeText(getActivity(), "Please select 2 different MP's to compare",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        setMpListAndUpdateAdapter();

        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null && list != null) {
                    if (!newText.equalsIgnoreCase("")) {
                        list.setVisibility(View.VISIBLE);
                    } else {
                        new AnimateUtil().shrinkListView(list, getContext(), getListViewMaxHeight());
                        new AnimateUtil().roundOffSearchCorners(searchCv, getContext());
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                list.setVisibility(INVISIBLE);
                            }
                        }, 500);
                    }
                    adapter.filter(newText);
                }
                if(previousQuery.equalsIgnoreCase("")){
                    new AnimateUtil().squareOffSearchCorners(searchCv, getContext());
                    new AnimateUtil().expandListView(list, getContext(), getListViewMaxHeight());
                }
//                else if(previousQuery.length() == 1 && editsearch.getQuery().toString().equalsIgnoreCase("")){
//                    new AnimateUtil().shrinkListView(list, getContext(), getListViewMaxHeight());
//                    new AnimateUtil().roundOffSearchCorners(searchCv, getContext());
//                }
                previousQuery = editsearch.getQuery().toString();
                return false;
            }
        });

        editsearchB.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapterB != null && list != null) {
                    if (!newText.equalsIgnoreCase("")) {
                        listB.setVisibility(View.VISIBLE);
                    } else {
                        listB.setVisibility(INVISIBLE);
                    }
                    adapterB.filter(newText);
                }
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (compareModeActivated) {
                    editsearch.setQuery(mpList.get(position).getFullName(), false);
                    list.setVisibility(INVISIBLE);
                    hideKeyboardFrom(getContext(), root);
                } else {
                    Intent myIntent = new Intent(getActivity(), MpExpensesVotesFragmentContainerActivity.class);
                    myIntent.putExtra("mp", mpList.get(position)); //Optional parameters
                    getActivity().startActivity(myIntent);
                }
            }
        });

        listB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editsearchB.setQuery(mpListB.get(position).getFullName(), false);
                listB.setVisibility(INVISIBLE);
                hideKeyboardFrom(getContext(), root);
            }
        });

        return root;
    }

    private int getListViewMaxHeight(){
        return getScreenHeight(getContext()) -
                searchCv.getLayoutParams().height - ((MpPartyQuizFragmentContainerActivity)getActivity()).getNavViewHeight() - (int) convertDpToPixel(75f, getContext());
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateAdapter(final Context context) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                adapter = new ListViewAdapter(context, mpList, MpSelectActivity.this);
                list.setAdapter(adapter);
            }
        });
    }

    private void updateAdapterB(final Context context) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                adapterB = new ListViewAdapter(context, mpListB, MpSelectActivity.this);
                listB.setAdapter(adapterB);
            }
        });
    }

//    @Override
//    protected void onPause() {
//        editsearch.clearFocus();
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
////        editsearch.requestFocus();
//        super.onResume();
//    }


    private void setMpListAndUpdateAdapter() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mpList = databaseUtil.mpDB.mpDao().getAllMps();
                mpListB = databaseUtil.mpDB.mpDao().getAllMps();
                updateAdapter(getActivity());
                updateAdapterB(getActivity());
            }
        });
    }

}