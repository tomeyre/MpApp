package tom.eyre.mpapp.service.google;

import tom.eyre.mpapp.data.google.Query;
import tom.eyre.mpapp.entity.MpEntity;
import tom.eyre.mpapp.util.HttpConnectUtil;

public class KnowledgeGraphSearch {

    public String searchForName(MpEntity mp, String apiKey) {
        HttpConnectUtil httpConnectUtil = new HttpConnectUtil();
        Query knowledgeSearchQuery = Query.ofPerson().firstName(mp.getForename()).lastName(mp.getSurname()).apiKey(apiKey).build();
        return httpConnectUtil.getJSONFromUrl(knowledgeSearchQuery.getUrl());
    }
}
