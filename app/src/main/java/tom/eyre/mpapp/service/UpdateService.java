package tom.eyre.mpapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tom.eyre.yourvotematters.data.DivisionResponse;
import tom.eyre.yourvotematters.data.MpResponse;
import tom.eyre.yourvotematters.data.VoteResponse;
import tom.eyre.yourvotematters.database.MpDatabase;
import tom.eyre.yourvotematters.entity.DivisionEntity;
import tom.eyre.yourvotematters.entity.MpEntity;
import tom.eyre.yourvotematters.entity.VoteEntity;
import tom.eyre.yourvotematters.util.HttpConnectUtil;

public class UpdateService {

    private Boolean limitResults;
    private Integer limit;

    public String updateMp(MpDatabase mpDatabase) throws JSONException, JsonProcessingException {
        final String UNICODE = "\uFEFF";
        HttpConnectUtil httpConnectUtil = new HttpConnectUtil();
        System.out.println("http://data.parliament.uk/membersdataplatform/services/mnis/members/query/house=Commons|membership=all");
        String json = httpConnectUtil.getJSONFromUrl("http://data.parliament.uk/membersdataplatform/services/mnis/members/query/house=Commons|membership=all"/*BasicDetails*/);
        if(json != null && !json.equalsIgnoreCase("") && !json.equalsIgnoreCase("error")){
            JSONObject jsonObject = new JSONObject(json.replaceAll(UNICODE, "").replaceAll("ï»¿",""));
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            ArrayList<MpResponse> mpEntities = mapper.readValue(jsonObject.getJSONObject("Members").getJSONArray("Member").toString(), new TypeReference<ArrayList<MpResponse>>() { });
            BuildAndSaveMpEntities(mpEntities, mpDatabase);
        }
        return null;
    }

    public String updateDivision(MpDatabase mpDatabase) throws JsonProcessingException, JSONException {
        HttpConnectUtil httpConnect = new HttpConnectUtil();
        System.out.println("https://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=5000&_page=0");
        String json  = httpConnect.getJSONFromUrl("https://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=5000&_page=0");

        if(json != null && !json.equalsIgnoreCase("error")){
            JSONObject jsonObject = new JSONObject(json);
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            ArrayList<DivisionResponse> divisionEntities = mapper.readValue(jsonObject.getJSONObject("result").getJSONArray("items").toString(), new TypeReference<ArrayList<DivisionResponse>>() { });
            BuildAndSaveDivisionEntities(divisionEntities, mpDatabase);
        }
        return null;
    }

    public String updateVotes(MpDatabase mpDatabase) throws JsonProcessingException, JSONException {
        ArrayList<DivisionEntity> entities = (ArrayList<DivisionEntity>) mpDatabase.mpDao().getAllDivisions();
        int count = 0;
        for(DivisionEntity division : entities) {
            count++;
            HttpConnectUtil httpConnect = new HttpConnectUtil();
            System.out.println("http://eldaddp.azurewebsites.net/commonsdivisions.json?uin=" + division.getUin());
            String json = httpConnect.getJSONFromUrl("http://eldaddp.azurewebsites.net/commonsdivisions.json?uin=" + division.getUin());

            if (json != null && !json.equalsIgnoreCase("error")) {
                JSONObject jsonObject = new JSONObject(json);
                ObjectMapper mapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                ArrayList<VoteResponse> VoteEntities = mapper.readValue(jsonObject.getJSONObject("result")
                        .getJSONArray("items").getJSONObject(0)
                        .getJSONArray("vote").toString(), new TypeReference<ArrayList<VoteResponse>>() {
                });
                BuildAndSaveDivisionVoteEntities(VoteEntities, division.getUin(), mpDatabase);
            }
            if(limitResults && count > limit) break;
        }
        return null;
    }

    private void BuildAndSaveMpEntities(ArrayList<MpResponse> mps, MpDatabase mpDatabase){
        List<MpEntity> entities = new ArrayList<>();
        List<MpEntity> currentMps = mpDatabase.mpDao().getAllMps();
        for(MpResponse mp : mps){
            if(mps == null || mps.isEmpty() ||
                    (mps != null && !mps.isEmpty() && !containsId(currentMps, mp.getMemberId()))) {
                MpEntity entity = new MpEntity();
                entity.setId(mp.getMemberId());
                entity.setName(mp.getDisplayAs());
                entity.setParty(mp.getParty().getText());
                entity.setMpFor(mp.getMemberFrom());
                entity.setActive(mp.getCurrentStatus().getActive());
                entity.setGender(mp.getGender());
                entity.setDateOfBirth(mp.getDateOfBirth() instanceof String ? mp.getDateOfBirth().toString() : "");
                entity.setDateOfDeath(mp.getDateOfDeath() instanceof String ? mp.getDateOfBirth().toString() : "");
                entity.setHouseStartDate(mp.getHouseStartDate() instanceof String ? mp.getHouseStartDate().toString() : "");
                entities.add(entity);
            }
        }
        if(entities.size() > 0) {
            mpDatabase.mpDao().insertAllMps(entities);
        }
    }

    private static boolean containsId(List<MpEntity> currentMps, Integer id) {
        for (MpEntity object : currentMps) {
            if (object.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void BuildAndSaveDivisionEntities(ArrayList<DivisionResponse> divisions, MpDatabase mpDatabase){
        ArrayList<DivisionEntity> entities = new ArrayList<>();
        List<DivisionEntity> currentDivisions = mpDatabase.mpDao().getAllDivisions();
        for(DivisionResponse division : divisions){
            if(currentDivisions == null || currentDivisions.isEmpty() ||
                    (currentDivisions != null && !currentDivisions.isEmpty() && !containsUin(currentDivisions, division.getUin()))) {
                DivisionEntity entity = new DivisionEntity();
                entity.setUin(division.getUin());
                entity.setDate(division.getDate().getValue());
                entity.setTitle(division.getTitle());
                entities.add(entity);
            }
        }
        if(entities.size() > 0) {
            mpDatabase.mpDao().insertAllDivisions(entities);
        }
    }

    private static boolean containsUin(List<DivisionEntity> currentDivisions, String uin) {
        for (DivisionEntity object : currentDivisions) {
            if (object.getUin().equalsIgnoreCase(uin)) {
                return true;
            }
        }
        return false;
    }

    private void BuildAndSaveDivisionVoteEntities(ArrayList<VoteResponse> votes, String uin, MpDatabase mpDatabase){
        ArrayList<VoteEntity> entities = new ArrayList<>();
        for(VoteResponse vote : votes){
            VoteEntity entity = new VoteEntity();
            entity.setUin(uin);
            entity.setMpId(Integer.parseInt(vote.getMember().get(0).getAbout().substring(vote.getMember().get(0).getAbout().lastIndexOf("/") + 1).trim()));
            entity.setResult(vote.getResult().substring(vote.getResult().lastIndexOf("#") + 1).trim());
            entity.setParty(vote.getParty());
            entities.add(entity);
        }
        mpDatabase.mpDao().insertAllVotes(entities);

    }

}
