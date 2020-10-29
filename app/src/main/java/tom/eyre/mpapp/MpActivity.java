package tom.eyre.mpapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.yourvotematters.entity.MpEntity;
import tom.eyre.yourvotematters.service.MpService;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static tom.eyre.yourvotematters.util.DatabaseUtil.mpDB;

public class MpActivity extends AppCompatActivity {

    private MpService mpService = new MpService();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ImageView mpImage;
    private TextView mpName;
    private TextView mpParty;
    private TextView mpFor;
    private TextView mpAge;
    private TextView mpBio;
    private Button mpExpenses;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private final String IMAGE_URI = "https://data.parliament.uk/membersdataplatform/services/images/MemberPhoto/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final MpEntity mp = (MpEntity) intent.getSerializableExtra("mp");
        setContentView(R.layout.mp_details_layout);

        mpService.getExpenses(getResources().getString(R.string.username),getResources().getString(R.string.password),mp.getName(), this);
        mpService.getMpBio(mp.getName(), this, getResources().getString(R.string.apiKey));

        mpImage = findViewById(R.id.mpImage);
        mpName = findViewById(R.id.mpName);
        mpName.setText(mp.getName());
        mpParty = findViewById(R.id.mpParty);
        mpParty.setText(mp.getParty());
        mpFor = findViewById(R.id.mpFor);
        mpFor.setText((mp.getActive() ? "Current MP for " : "Ex-MP for ") + mp.getMpFor());
        mpAge = findViewById(R.id.mpAge);
        Date dob = null;
        try {
            dob = sdf.parse(mp.getDateOfBirth());
        }catch (Exception e){e.printStackTrace();}
        Integer age = 0;
        if(dob != null){
            age = getDiffYears(dob, new Date());
        }
        if(age > 0) {
            mpAge.setText(String.valueOf(age));
        }else{
            mpAge.setVisibility(View.GONE);
        }
        mpBio = findViewById(R.id.mpBio);
        mpExpenses = findViewById(R.id.mpExpenses);

        executorService.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                updateMpDetails(mpService.getMpDetails(mp.getId(), mpDB));
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

    private void updateMpDetails(final MpEntity mp) throws IOException {
        final Drawable drawable = drawableFromUrl(IMAGE_URI + mp.getId() + "/Web/");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mpImage.setImageDrawable(drawable);
            }
        });
    }

    public void setBio(final String bio){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mpBio.setText(bio);
            }
        });
    }

    public void setExpenses(String expenses){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mpExpenses.setVisibility(View.VISIBLE);
            }
        });
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }
}
