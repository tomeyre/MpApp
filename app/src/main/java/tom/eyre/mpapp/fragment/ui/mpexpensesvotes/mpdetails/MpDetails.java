package tom.eyre.mpapp.fragment.ui.mpexpensesvotes.mpdetails;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.mpapp.R;
import tom.eyre.mpapp.adapter.BillAdapter;
import tom.eyre.mpapp.data.SocialMediaResponse;
import tom.eyre.mpapp.entity.BillsEntity;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.service.MpService;
import tom.eyre.mpapp.util.DrawableFromUrl;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static tom.eyre.mpapp.util.Constants.SEVEN_DAYS;
import static tom.eyre.mpapp.util.DatabaseUtil.mpDB;

public class MpDetails extends Fragment {

    private MpDetailsViewModel mpDetailsViewModel;
    private MpEntity mp;
    private MpService mpService = new MpService();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @SneakyThrows
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mp = (MpEntity) getArguments().getSerializable("mp");
        mpDetailsViewModel =
                ViewModelProviders.of(this).get(MpDetailsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mp_details, container, false);

        final ImageView mpImage = root.findViewById(R.id.mpImage);
        final TextView mpName = root.findViewById(R.id.mpName);
        final TextView mpParty = root.findViewById(R.id.mpParty);
        final TextView mpFor = root.findViewById(R.id.mpFor);
        final TextView mpAge = root.findViewById(R.id.mpAge);
        final TextView mpBio = root.findViewById(R.id.mpBio);
        final TextView mpHomePage = root.findViewById(R.id.mpHomePage);
        final TextView twitterUrl = root.findViewById(R.id.mpTwitter);
        final CardView topView = root.findViewById(R.id.topView);
        getActivity().getWindow().setStatusBarColor(getColor());

        mpDetailsViewModel.getDrawable().observe(getViewLifecycleOwner(), new Observer<Drawable>() {
            @Override
            public void onChanged(@Nullable Drawable d) {
                mpImage.setImageDrawable(d);
            }
        });
        mpDetailsViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mpName.setText(s);
            }
        });
        mpDetailsViewModel.getMpParty().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mpParty.setText(s);
            }
        });
        mpDetailsViewModel.getMpFor().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mpFor.setText(s);
            }
        });
        mpDetailsViewModel.getMpAge().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mpAge.setText(s);
            }
        });
        mpDetailsViewModel.getBio().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mpBio.setText(s);
            }
        });
        mpDetailsViewModel.getMpHomePage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mpHomePage.setText(s);
            }
        });
        mpDetailsViewModel.getTwitterUrl().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                twitterUrl.setText(s);
            }
        });
        mpDetailsViewModel.getTopView().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String c) {
                topView.setBackgroundColor(Color.parseColor(c));
            }
        });

        if(mp.getBio() != null && !mp.getBio().equalsIgnoreCase("unknown") && System.currentTimeMillis() - mp.getLastUpdatedTimestamp() < SEVEN_DAYS){
            mpBio.setText(mp.getBio());
        }else {
            mpService.getMpBio(mp, this, getResources().getString(R.string.apiKey), mpDB, mp.getId());
        }
        if((mp.getHomePage() != null || mp.getTwitterUrl() != null) && System.currentTimeMillis() - mp.getLastUpdatedTimestamp() < SEVEN_DAYS){
            mpHomePage.setText(mp.getHomePage());
            twitterUrl.setText(mp.getTwitterUrl());
        }else {
            mpService.getSocialMedia(this, mpDB, mp.getId());
        }
        if(mp.getFullName().length() > 20){mpName.setTextSize(25);}
        else{mpName.setTextSize(30);}
        mpName.setText(mp.getFullName());
        mpParty.setText(mp.getParty());
        mpFor.setText(mp.getMpFor().equalsIgnoreCase("life peer") ? mp.getMpFor() : (mp.getActive() ? "Current MP for " : "Ex-MP for ") + mp.getMpFor());
        Date dob = null;
        try {
            dob = sdf.parse(mp.getDateOfBirth());
        }catch (Exception e){e.printStackTrace();}
        Integer age = 0;
        if(dob != null){
            age = getDiffYears(dob, new Date());
        }
        if(age > 0) {
            mpAge.setText("Age: " + String.valueOf(age));
        }else{
            mpAge.setVisibility(View.GONE);
        }

        recyclerView = root.findViewById(R.id.mpBillRv);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        try {
            topView.setBackgroundColor(getColor());
        }catch (Exception e){
            e.printStackTrace();
        }

        mpImage.setImageDrawable(new DrawableFromUrl().get(mp.getId()));
        updateRecycler(mp);
        return root;
    }

    private void updateRecycler(MpEntity mp){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<BillsEntity> bills = mpDB.mpDao().getAllBillsByName(mp.getFullName(), mp.getForename() + " " + mp.getSurname());
                if(bills == null || bills.isEmpty() || bills.size() == 0){
                    getView().findViewById(R.id.billsLayout).setVisibility(View.GONE);
                }
                if(getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        mAdapter = new BillAdapter(bills);
                        mAdapter.setHasStableIds(true);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
            }
        });
    }

    private static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    private static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.setTime(date);
        return cal;
    }

    public void setBio(final String bio){
        if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView mpBio = getView().findViewById(R.id.mpBio);
                if(bio.equalsIgnoreCase("")){
                    getView().findViewById(R.id.mpBio).setVisibility(View.GONE);
                    if(getView().findViewById(R.id.socialLayout).getVisibility() == View.VISIBLE){
                        setLayoutBelow(R.id.ll, R.id.socialLayout);
                    } else if(getView().findViewById(R.id.billsLayout).getVisibility() == View.VISIBLE){
                        setLayoutBelow(R.id.ll, R.id.billsLayout);
                    }
                }else{
                    mpBio.setText(bio);
                }
            }
        });
    }

    public void setSocialMedia(final SocialMediaResponse socialMediaResponse){
        if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView mpHomePage = getView().findViewById(R.id.mpHomePage);
                TextView twitterUrl = getView().findViewById(R.id.mpTwitter);
                ImageView twitterImage = getView().findViewById(R.id.twitterImage);
                if (socialMediaResponse.getHomePage() == null &&
                        socialMediaResponse.getTwitter() == null) {
                    getView().findViewById(R.id.socialLayout).setVisibility(View.GONE);
                    if(getView().findViewById(R.id.billsLayout).getVisibility() == View.VISIBLE){
                        if(getView().findViewById(R.id.bioLayout).getVisibility() == View.VISIBLE){
                            setLayoutBelow(R.id.billsLayout, R.id.bioLayout);
                        }
                        else{
                            setLayoutBelow(R.id.billsLayout, R.id.ll);
                        }
                    }
                } else {
                    if (socialMediaResponse.getHomePage() == null) {
                        mpHomePage.setVisibility(View.GONE);
                    } else {
                        mpHomePage.setText(socialMediaResponse.getHomePage());
                    }

                    if (socialMediaResponse.getTwitter() == null) {
                        twitterUrl.setVisibility(View.GONE);
                        twitterImage.setVisibility(View.GONE);
                    } else {
                        twitterUrl.setText(socialMediaResponse.getTwitter().getUrl());
                    }
                }
            }
        });
    }

    private void setLayoutBelow(int layoutBelow, int viewId){
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) getView().findViewById(viewId).getLayoutParams();
        params.addRule(RelativeLayout.BELOW, layoutBelow);
        getView().findViewById(viewId).setLayoutParams(params);
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