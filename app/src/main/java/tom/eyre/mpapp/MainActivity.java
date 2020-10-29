package tom.eyre.mpapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.yourvotematters.adapter.ListViewAdapter;
import tom.eyre.yourvotematters.entity.MpEntity;
import tom.eyre.yourvotematters.util.DatabaseUtil;

public class MainActivity extends AppCompatActivity {

    private DatabaseUtil databaseUtil;
//    private MpService  mpService = new MpService();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
//    private final UpdateService updateService = new UpdateService();
    private ListView list;
    private ListViewAdapter adapter;
    private SearchView editsearch;
    private List<MpEntity> mpList;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                GitUtil gitUtil = new GitUtil();
//                gitUtil.getFile();
//            }
//
//
//        });

        databaseUtil = DatabaseUtil.getInstance(this);
        list = findViewById(R.id.listview);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mpList = databaseUtil.mpDB.mpDao().getAllMps();
                updateAdapter();
            }
        });

        editsearch = findViewById(R.id.search);
        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("text submit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equalsIgnoreCase("")){
                    list.setVisibility(View.VISIBLE);
                }else{
                    list.setVisibility(View.INVISIBLE);
                }
                adapter.filter(newText);
                return false;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MainActivity.this, MpActivity.class);
                myIntent.putExtra("mp", mpList.get(position)); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });

        list.setVisibility(View.INVISIBLE);

//
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    updateService.updateDivision(mpDB);
//                    updateService.updateMp(mpDB);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    System.out.println("Something went wrong");
//                }
//
//            }
//        });

//        search.setOnClickListener(new View.OnClickListener() {
//            @SneakyThrows
//            @Override
//            public void onClick(View view) {
//                executorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        String response = null;
//                        try {
//                            response = mpService.getDetails(8, mpDB).toString();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (JsonProcessingException e) {
//                            e.printStackTrace();
//                        }
//                        tv.setText(response);
//                    }
//                });
//            }
//        });
    }

    private void updateAdapter(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter = new ListViewAdapter(getApplicationContext(), mpList);
                list.setAdapter(adapter);
            }
        });
    }
}