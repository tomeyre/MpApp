package tom.eyre.mpapp.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import tom.eyre.mpapp.data.SocialMediaResponse;
import tom.eyre.mpapp.database.MpDatabase;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.service.google.KnowledgeGraphSearch;
import tom.eyre.mpapp.fragment.ui.mpexpensesvotes.mpdetails.MpDetails;
import tom.eyre.mpapp.util.HttpConnectUtil;

public class MpService {

    private static final Logger log = Logger.getLogger(String.valueOf(MpService.class));

    public MpService(){}

    private ExpenseService expenseService = new ExpenseService();

    private KnowledgeGraphSearch knowledgeGraphSearch = new KnowledgeGraphSearch();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private String json;
    private String jsonArrayString;

    public MpEntity getMpDetails(Integer id, MpDatabase mpDatabase){
        return mpDatabase.mpDao().findMpById(id);
    }

    public void getMpBio(final MpEntity mp, final MpDetails mpDetails, final String apiKey, MpDatabase mpDatabase, Integer id){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String bio = new JSONObject(knowledgeGraphSearch.searchForName(mp, apiKey)).getJSONArray("itemListElement")
                            .getJSONObject(0).getJSONObject("result").getJSONObject("detailedDescription").getString("articleBody");
                    if(bio.toLowerCase().contains("member of parliament") ||
                            bio.toLowerCase().contains("british politician")) {
                        mpDetails.setBio(bio);
                        mpDatabase.mpDao().updateBioById(id, bio);
                    }
                    else{
                        mpDetails.setBio("");
                        mpDatabase.mpDao().updateBioById(id, "unknown");
                    }
                    mpDatabase.mpDao().updateLastUpdatedTs(id, new Date().getTime());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getSocialMedia(final MpDetails mpDetails, MpDatabase mpDatabase, Integer id) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpConnectUtil httpConnect = new HttpConnectUtil();
                    log.info("http://lda.data.parliament.uk/members/" + id + ".json");
                    String json = httpConnect.getJSONFromUrl("http://lda.data.parliament.uk/members/" + id + ".json");

                    if (json != null && !json.equalsIgnoreCase("error")) {
                        JSONObject jsonObject = new JSONObject(json);
                        ObjectMapper mapper = new ObjectMapper()
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                        SocialMediaResponse socialMediaResponse = mapper.readValue(jsonObject.getJSONObject("result").getJSONObject("primaryTopic").toString(), SocialMediaResponse.class);

                        mpDetails.setSocialMedia(socialMediaResponse);
                        mpDatabase.mpDao().updateHomePage(id, socialMediaResponse.getHomePage());
                        mpDatabase.mpDao().updateTwitterUrl(id, socialMediaResponse.getTwitter().getUrl());
                        mpDatabase.mpDao().updateLastUpdatedTs(id, new Date().getTime());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
