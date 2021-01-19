package tom.eyre.mp2021.service.google;

import tom.eyre.mp2021.data.google.Query;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.utility.HttpConnectUtil;

public class KnowledgeGraphSearch {

    public String searchForName(MpEntity mp, String apiKey) {
        HttpConnectUtil httpConnectUtil = new HttpConnectUtil();
        Query knowledgeSearchQuery = Query.ofPerson().firstName(mp.getForename()).lastName(mp.getSurname()).apiKey(apiKey).build();
        return httpConnectUtil.getJSONFromUrl(knowledgeSearchQuery.getUrl());
    }
}
