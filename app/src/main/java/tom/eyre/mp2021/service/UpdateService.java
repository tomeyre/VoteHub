package tom.eyre.mp2021.service;

import android.content.Context;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import tom.eyre.mp2021.database.local.LocalDatabase;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.data.BillResponse;
import tom.eyre.mp2021.data.DivisionResponse;
import tom.eyre.mp2021.data.MpPartiesResponse;
import tom.eyre.mp2021.data.MpResponse;
import tom.eyre.mp2021.data.Post;
import tom.eyre.mp2021.data.VoteResponse;
import tom.eyre.mp2021.database.local.LocalDatabase;
import tom.eyre.mp2021.entity.BillsEntity;
import tom.eyre.mp2021.entity.DivisionEntity;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.entity.PartyEntity;
import tom.eyre.mp2021.entity.PostEntity;
import tom.eyre.mp2021.entity.QuestionEntity;
import tom.eyre.mp2021.entity.VoteEntity;
import tom.eyre.mp2021.utility.HttpConnectUtil;

public class UpdateService {

    private static final Logger log = Logger.getLogger(String.valueOf(tom.eyre.mp2021.service.UpdateService.class));

    private AmazonDynamoDBClient dbClient;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);


    public void updateMp(LocalDatabase localDatabase) throws JSONException, JsonProcessingException {
        final String UNICODE = "\uFEFF";
        HttpConnectUtil httpConnectUtil = new HttpConnectUtil();
        log.info("http://data.parliament.uk/membersdataplatform/services/mnis/members/query/house=Commons|membership=all/BasicDetails|Parties");//|OppositionPosts|ParliamentaryPosts");
        String json = httpConnectUtil.getJSONFromUrl("http://data.parliament.uk/membersdataplatform/services/mnis/members/query/house=Commons|membership=all/BasicDetails|Parties");//|OppositionPosts|ParliamentaryPosts");
        if (json != null && !json.equalsIgnoreCase("") && !json.equalsIgnoreCase("error")) {
            JSONObject jsonObject = new JSONObject(json.replaceAll(UNICODE, "").replaceAll("ï»¿", ""));
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            ArrayList<MpResponse> mpEntities = mapper.readValue(jsonObject.getJSONObject("Members").getJSONArray("Member").toString(), new TypeReference<ArrayList<MpResponse>>() {
            });
            BuildAndSaveMpEntities(mpEntities, localDatabase);
        }
    }

    public void updateDivision(LocalDatabase localDatabase) throws JsonProcessingException, JSONException {
        HttpConnectUtil httpConnect = new HttpConnectUtil();
        log.info("https://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=100&_page=40");
        String json = httpConnect.getJSONFromUrl("https://lda.data.parliament.uk/commonsdivisions.json?_view=Commons+Divisions&_pageSize=100&_page=0");

        if (json != null && !json.equalsIgnoreCase("error")) {
            JSONObject jsonObject = new JSONObject(json);
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            ArrayList<DivisionResponse> divisionEntities = mapper.readValue(jsonObject.getJSONObject("result").getJSONArray("items").toString(), new TypeReference<ArrayList<DivisionResponse>>() {
            });
            BuildAndSaveDivisionEntities(divisionEntities, localDatabase);
        }
    }

    public void updateBills(LocalDatabase localDatabase) {
        HttpConnectUtil httpConnect = new HttpConnectUtil();
        log.info("https://lda.data.parliament.uk/bills.json?_view=Bills&_pageSize=3000&_page=0");
        String json = httpConnect.getJSONFromUrl("https://lda.data.parliament.uk/bills.json?_view=Bills&_pageSize=3000&_page=0");

        try {
            if (json != null && !json.equalsIgnoreCase("error")) {
                JSONObject jsonObject = new JSONObject(json);
                ObjectMapper mapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("items");
                while(!(jsonArray.get(jsonArray.length()-1) instanceof JSONObject)){ jsonArray.remove(jsonArray.length()-1); }
                ArrayList<BillResponse> billEntities = mapper.readValue(jsonObject.getJSONObject("result").getJSONArray("items").toString(), new TypeReference<ArrayList<BillResponse>>() {
                });
                BuildAndSaveBillEntities(billEntities, localDatabase);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String updateVotes(LocalDatabase localDatabase, Context context) throws JsonProcessingException, JSONException {
        credentialsProvider = new CognitoCachingCredentialsProvider(
               context, context.getResources().getString(R.string.identityId), Regions.US_WEST_2);

        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        ScanRequest scanRequest = new ScanRequest()
                .withTableName("questions");
        ScanResult scanResult = dbClient.scan(scanRequest);
        ArrayList<QuestionEntity> entities = buildEntities(scanResult.getItems());

        for (QuestionEntity question : entities) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpConnectUtil httpConnect = new HttpConnectUtil();
                        log.info("https://eldaddp.azurewebsites.net/commonsdivisions.json?uin=" + question.getUin());
                        String json = httpConnect.getJSONFromUrl("https://eldaddp.azurewebsites.net/commonsdivisions.json?uin=" + question.getUin());

                        if (json != null && !json.equalsIgnoreCase("error")) {
                            JSONObject jsonObject = new JSONObject(json);
                            jsonObject.getJSONObject("result")
                                    .getJSONArray("items").getJSONObject(0)
                                    .getJSONArray("vote").remove(jsonObject.getJSONObject("result")
                                    .getJSONArray("items").getJSONObject(0)
                                    .getJSONArray("vote").length() - 1);
                            ObjectMapper mapper = new ObjectMapper()
                                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                            ArrayList<VoteResponse> VoteEntities = mapper.readValue(jsonObject.getJSONObject("result")
                                    .getJSONArray("items").getJSONObject(0)
                                    .getJSONArray("vote").toString(), new TypeReference<ArrayList<VoteResponse>>() {
                            });
                            BuildAndSaveDivisionVoteEntities(VoteEntities, question.getUin(), localDatabase);
                        }
                    }catch (Exception e){
                        Toast.makeText(context, "votes not updated",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        executorService.shutdown();
        return null;
    }

    private ArrayList<QuestionEntity> buildEntities(List<Map<String, AttributeValue>> results) {
        ArrayList<QuestionEntity> entities = new ArrayList<>();
        Iterator<Map<String, AttributeValue>> iterator = results.iterator();
        while (iterator.hasNext()) {
            Map<String, AttributeValue> result = iterator.next();
            QuestionEntity entity = new QuestionEntity();
            entity.setUin(getResult(result, "uin"));
            entity.setDateOfDivision(getResult(result, "date"));
            entity.setQuestion(getResult(result, "question"));
            entity.setTitle(getResult(result, "title"));
            entity.setType(getResult(result, "type"));
            entity.setOpinion(null);
            entity.setId(Integer.parseInt(getResult(result, "id")));
            entities.add(entity);
        }
        return entities;
    }

    private String getResult(Map<String, AttributeValue> result, String type) {
        return result.get(type).toString().substring(
                result.get(type).toString().indexOf(":") + 2,
                result.get(type).toString().lastIndexOf(","));
    }

    private void BuildAndSaveMpEntities(ArrayList<MpResponse> mps, LocalDatabase localDatabase) {
        try {
            List<MpEntity> entities = new ArrayList<>();
            List<PartyEntity> parties = new ArrayList<>();
            List<PostEntity> posts = new ArrayList<>();
            List<MpEntity> currentMps = localDatabase.localDatabaseDao().getAllMps();
            for (MpResponse mp : mps) {
                if (mps == null || mps.isEmpty() ||
                        (mps != null && !mps.isEmpty() && !containsId(currentMps, mp.getMemberId()))) {
                    MpEntity entity = new MpEntity();
                    entity.setId(mp.getMemberId());
                    entity.setForename(mp.getBasicDetails().getForename());
                    entity.setSurname(mp.getBasicDetails().getSurname());
                    entity.setParty(mp.getParty().getText());
                    entity.setMpFor(mp.getMemberFrom());
                    entity.setFullName(mp.getDisplayAs());
                    entity.setActive(mp.getCurrentStatus().getActive());
                    entity.setGender(mp.getGender());
                    entity.setDateOfBirth(mp.getDateOfBirth() instanceof String ? mp.getDateOfBirth().toString() : "");
                    entity.setDateOfDeath(mp.getDateOfDeath() instanceof String ? mp.getDateOfBirth().toString() : "");
                    entity.setHouseStartDate(mp.getHouseStartDate() instanceof String ? mp.getHouseStartDate().toString() : "");
                    entity.setHouseEndDate(mp.getHouseEndDate() instanceof String ? mp.getHouseEndDate().toString() : "");
                    entity.setLastUpdatedTimestamp(new Date().getTime());
                    entities.add(entity);
                    for (MpPartiesResponse partiesResponse : mp.getParties().getMpPartyResponses()) {
                        PartyEntity partyEntity = new PartyEntity();
                        partyEntity.setMpId(mp.getMemberId());
                        partyEntity.setParty(partiesResponse.getName());
                        partyEntity.setStartDate(partiesResponse.getStartDate());
                        partyEntity.setEndDate(partiesResponse.getEndDate() instanceof String ? (String) partiesResponse.getEndDate() : null);
                        parties.add(partyEntity);
                    }
                    if (mp.getOppositionPosts() != null && mp.getOppositionPosts().getOppositionPosts() != null) {
                        for (Post oppositionPosts : mp.getOppositionPosts().getOppositionPosts()) {
                            PostEntity postEntity = new PostEntity();
                            postEntity.setMpId(mp.getMemberId());
                            postEntity.setEmail(oppositionPosts.getEmail());
                            postEntity.setEndDate(oppositionPosts.getEndDate() instanceof String ? (String) oppositionPosts.getEndDate() : null);
                            postEntity.setStartDate(oppositionPosts.getStartDate());
                            postEntity.setEndNote(oppositionPosts.getEndNote());
                            postEntity.setHansardName(oppositionPosts.getHansardName());
                            postEntity.setIsJoint(oppositionPosts.getIsJoint());
                            postEntity.setIsUnpaid(oppositionPosts.getIsUnpaid());
                            postEntity.setNote(oppositionPosts.getNote());
                            postEntity.setPosition(oppositionPosts.getPosition());
                            postEntity.setLayingMinisterName(oppositionPosts.getLayingMinisterName());
                            posts.add(postEntity);
                        }
                    }
                    if (mp.getParliamentaryPosts() != null && mp.getParliamentaryPosts().getParliamentaryPosts() != null) {
                        for (Post parliamentaryPost : mp.getParliamentaryPosts().getParliamentaryPosts()) {
                            PostEntity postEntity = new PostEntity();
                            postEntity.setMpId(mp.getMemberId());
                            postEntity.setEmail(parliamentaryPost.getEmail());
                            postEntity.setEndDate(parliamentaryPost.getEndDate() instanceof String ? (String) parliamentaryPost.getEndDate() : null);
                            postEntity.setStartDate(parliamentaryPost.getStartDate());
                            postEntity.setEndNote(parliamentaryPost.getEndNote());
                            postEntity.setHansardName(parliamentaryPost.getHansardName());
                            postEntity.setIsJoint(parliamentaryPost.getIsJoint());
                            postEntity.setIsUnpaid(parliamentaryPost.getIsUnpaid());
                            postEntity.setNote(parliamentaryPost.getNote());
                            postEntity.setPosition(parliamentaryPost.getPosition());
                            postEntity.setLayingMinisterName(parliamentaryPost.getLayingMinisterName());
                            posts.add(postEntity);
                        }
                    }
                }
            }
            if (entities.size() > 0) {
                localDatabase.localDatabaseDao().insertAllMps(entities);
                localDatabase.localDatabaseDao().insertAllParties(parties);
                localDatabase.localDatabaseDao().insertAllPosts(posts);
            }
        }catch (Exception e){
            e.printStackTrace();
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

    private void BuildAndSaveBillEntities(ArrayList<BillResponse> bills, LocalDatabase localDatabase) {
        ArrayList<BillsEntity> entities = new ArrayList<>();
        List<BillsEntity> currentBills = localDatabase.localDatabaseDao().getAllBills();
        for (BillResponse bill : bills) {
            if (currentBills == null || currentBills.isEmpty() ||
                    (currentBills != null && !currentBills.isEmpty() && !containsTitle(currentBills, bill.getTitle()))) {
                BillsEntity billsEntity = new BillsEntity();
                billsEntity.setUrl(bill.getHomePage());
                billsEntity.setTitle(bill.getTitle() != null ? bill.getTitle() : "");
                if(bill.getSponsors() != null && !bill.getSponsors().isEmpty()) {
                    billsEntity.setSponsorAId(bill.getSponsors().get(0).getSponsorPrinted().get(0));
                    if(bill.getSponsors().size() > 1) {
                        billsEntity.setSponsorBId(bill.getSponsors().get(1).getSponsorPrinted().get(0));
                    }
                }
                billsEntity.setBillDate(bill.getDate().getValue());
                billsEntity.setLastUpdatedTimestamp(new Date().getTime());
                entities.add(billsEntity);
            }
        }
        if (entities.size() > 0) {
            localDatabase.localDatabaseDao().insertAllBills(entities);
        }
    }

    private Boolean containsTitle(List<BillsEntity> currentBills, String title){
        for(BillsEntity bill: currentBills){
            if(bill.getTitle().equalsIgnoreCase(title)){
                return true;
            }
        }
        return false;
    }

    private void BuildAndSaveDivisionEntities(ArrayList<DivisionResponse> divisions, LocalDatabase localDatabase){
        ArrayList<DivisionEntity> entities = new ArrayList<>();
        List<DivisionEntity> currentDivisions = localDatabase.localDatabaseDao().getAllDivisions();
        for(DivisionResponse division : divisions){
            if(currentDivisions == null || currentDivisions.isEmpty() ||
                    (currentDivisions != null && !currentDivisions.isEmpty() && !containsUin(currentDivisions, division.getUin()))) {
                DivisionEntity entity = new DivisionEntity();
                entity.setUin(division.getUin());
                entity.setDate(division.getDate().getValue());
                entity.setTitle(division.getTitle());
                entity.setLastUpdatedTimestamp(new Date().getTime());
                entities.add(entity);
            }
        }
        if(entities.size() > 0) {
            localDatabase.localDatabaseDao().insertAllDivisions(entities);
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

    synchronized private void BuildAndSaveDivisionVoteEntities(ArrayList<VoteResponse> votes, String uin, LocalDatabase localDatabase){
        ArrayList<VoteEntity> entities = new ArrayList<>();
        for(VoteResponse vote : votes){
            VoteEntity entity = new VoteEntity();
            entity.setUin(uin);
            entity.setMpId(Integer.parseInt(vote.getMember().get(0).getAbout().substring(vote.getMember().get(0).getAbout().lastIndexOf("/") + 1).trim()));
            entity.setResult(vote.getResult().substring(vote.getResult().lastIndexOf("#") + 1).trim());
            entity.setParty(vote.getParty());
            entity.setLastUpdatedTimestamp(new Date().getTime());
            entities.add(entity);
        }
        localDatabase.localDatabaseDao().insertAllVotes(entities);

    }

}
