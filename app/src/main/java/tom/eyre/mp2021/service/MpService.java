package tom.eyre.mp2021.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import lombok.SneakyThrows;
import tom.eyre.mp2021.activity.MpDetailsActivity;
import tom.eyre.mp2021.data.MpResponse;
import tom.eyre.mp2021.data.Post;
import tom.eyre.mp2021.data.SocialMediaResponse;
import tom.eyre.mp2021.database.local.LocalDatabase;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.entity.PostEntity;
import tom.eyre.mp2021.service.google.KnowledgeGraphSearch;
import tom.eyre.mp2021.utility.HttpConnectUtil;

public class MpService {

    private static final Logger log = Logger.getLogger(String.valueOf(MpService.class));

    public MpService(){}

    private ExpenseService expenseService = new ExpenseService();

    private KnowledgeGraphSearch knowledgeGraphSearch = new KnowledgeGraphSearch();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private String json;
    private String jsonArrayString;

    @SneakyThrows
    public void getMpPostsAndInterests(int id, LocalDatabase localDatabase){
        final String UNICODE = "\uFEFF";
        HttpConnectUtil httpConnectUtil = new HttpConnectUtil();
        log.info("http://data.parliament.uk/membersdataplatform/services/mnis/members/query/id=" + id + "/GovernmentPosts|ParliamentaryPosts|OppositionPosts|Interests/");
        String json = httpConnectUtil.getJSONFromUrl("http://data.parliament.uk/membersdataplatform/services/mnis/members/query/id=" + id + "/GovernmentPosts|ParliamentaryPosts|OppositionPosts|Interests/");
        if (json != null && !json.equalsIgnoreCase("") && !json.equalsIgnoreCase("error")) {
            JSONObject jsonObject = new JSONObject(json.replaceAll(UNICODE, "").replaceAll("ï»¿", ""));
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                    .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            MpResponse mpResponse = mapper.readValue(jsonObject.getJSONObject("Members").getJSONObject("Member").toString(), new TypeReference<MpResponse>() {
            });
            savePostsAndInterests(mpResponse, localDatabase, id);
        }
    }

    private void savePostsAndInterests(MpResponse mpResponse, LocalDatabase localDatabase, int id){
        List<PostEntity> posts = new ArrayList<>();

        if(mpResponse.getGovernmentPosts() != null && mpResponse.getGovernmentPosts().getGovernmentPosts() != null && !mpResponse.getGovernmentPosts().getGovernmentPosts().isEmpty()) {
            for (Post post : mpResponse.getGovernmentPosts().getGovernmentPosts()) {
                posts.add(createPost(id, post, "Government"));
            }
        }
        if(mpResponse.getOppositionPosts() != null && mpResponse.getOppositionPosts().getOppositionPosts() != null && !mpResponse.getOppositionPosts().getOppositionPosts().isEmpty()) {
            for (Post post : mpResponse.getOppositionPosts().getOppositionPosts()) {
                posts.add(createPost(id, post, "Opposition"));
            }
        }
            if(mpResponse.getParliamentaryPosts() != null && mpResponse.getParliamentaryPosts().getParliamentaryPosts() != null && !mpResponse.getParliamentaryPosts().getParliamentaryPosts().isEmpty()) {
                for (Post post : mpResponse.getParliamentaryPosts().getParliamentaryPosts()) {
                    posts.add(createPost(id, post, "Parliamentary"));
                }
            }

        localDatabase.localDatabaseDao().insertAllPosts(posts);
    }

    private PostEntity createPost(int id, Post post, String type){
        PostEntity postEntity = new PostEntity();
        postEntity.setMpId(id);
        postEntity.setEmail(post.getEmail());
        postEntity.setEndDate(post.getEndDate() instanceof String ? (String) post.getEndDate() : null);
        postEntity.setStartDate(post.getStartDate());
        postEntity.setEndNote(post.getEndNote());
        postEntity.setHansardName(post.getHansardName());
        postEntity.setIsJoint(post.getIsJoint());
        postEntity.setIsUnpaid(post.getIsUnpaid());
        postEntity.setNote(post.getNote());
        postEntity.setPosition(post.getPosition());
        postEntity.setType(type);
        postEntity.setLayingMinisterName(post.getLayingMinisterName());
        return postEntity;
    }

    public MpEntity getMpDetails(Integer id, LocalDatabase localDatabase){
        return localDatabase.localDatabaseDao().findMpById(id);
    }

    public void getMpBio(final MpEntity mp, final MpDetailsActivity mpDetails, final String apiKey, LocalDatabase localDatabase, Integer id){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject detailedDescription = new JSONObject(knowledgeGraphSearch.searchForName(mp, apiKey)).getJSONArray("itemListElement")
                            .getJSONObject(0).getJSONObject("result").getJSONObject("detailedDescription");
                    String bio = detailedDescription.getString("articleBody");
                    String wikiLink = detailedDescription.getString("url");
                    if(bio.toLowerCase().contains("member of parliament") ||
                            bio.toLowerCase().contains("british politician")) {
//                        mpDetails.setBioAndWikiLink(bio, wikiLink);
                        localDatabase.localDatabaseDao().updateBioById(id, bio);
                        localDatabase.localDatabaseDao().updateWikiLinkById(id, wikiLink);
                    }
                    else{
//                        mpDetails.setBioAndWikiLink("", "");
                        localDatabase.localDatabaseDao().updateBioById(id, "unknown");
                        localDatabase.localDatabaseDao().updateWikiLinkById(id, "unknown");
                    }
                    localDatabase.localDatabaseDao().updateLastUpdatedTs(id, new Date().getTime());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getSocialMedia(final MpDetailsActivity mpDetails, LocalDatabase localDatabase, Integer id) {
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
                        localDatabase.localDatabaseDao().updateHomePage(id, socialMediaResponse.getHomePage());
                        localDatabase.localDatabaseDao().updateTwitterUrl(id, socialMediaResponse.getTwitter().getUrl());
                        localDatabase.localDatabaseDao().updateLastUpdatedTs(id, new Date().getTime());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
