package tom.eyre.mpapp.service;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import tom.eyre.mpapp.entity.VoteEntity;
import tom.eyre.mpapp.util.DatabaseUtil;
import tom.eyre.mpapp.util.HttpConnectUtil;

public class VoteService {

    private Logger log = LoggerFactory.getLogger(VoteService.class);

    private DatabaseUtil databaseUtil;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void getVotes(Integer id, Context context){
        databaseUtil = DatabaseUtil.getInstance(context);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<VoteEntity> votes = databaseUtil.mpDB.mpDao().getAllVotesByMpId(id);
                if (votes == null || votes.isEmpty()) {
                    HttpConnectUtil httpConnect = new HttpConnectUtil();
                    log.info("https://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=5000&_page=0");
                    String json = httpConnect.getJSONFromUrl("https://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=5000&_page=0");

                    if (json != null && !json.equalsIgnoreCase("error")) {

                    }
                }
            }
        });
    }

    private void calculate(List<VoteEntity> votes){
        System.out.println(votes.stream().filter(voteEntity -> voteEntity.getResult().equalsIgnoreCase("aye")).collect(Collectors.toList()).size());
    }
}
