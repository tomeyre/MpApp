package tom.eyre.mpapp.service.google;

import tom.eyre.yourvotematters.data.google.Query;
import tom.eyre.yourvotematters.util.HttpConnectUtil;

public class KnowledgeGraphSearch {

    public String searchForName(String name, String apiKey) {
        HttpConnectUtil httpConnectUtil = new HttpConnectUtil();
        String firstName = name.split(" ")[name.split(" ").length-2];
        String lastName = name.split(" ")[name.split(" ").length-1];
        Query knowledgeSearchQuery = Query.ofPerson().firstName(firstName).lastName(lastName).apiKey(apiKey).build();
        return httpConnectUtil.getJSONFromUrl(knowledgeSearchQuery.getUrl());
    }
}
