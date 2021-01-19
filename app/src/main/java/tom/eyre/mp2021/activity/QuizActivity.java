package tom.eyre.mp2021.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.adapter.QuestionAdapter;
import tom.eyre.mp2021.adapter.QuestionTypeAdapter;
import tom.eyre.mp2021.data.QuestionGroupByType;
import tom.eyre.mp2021.entity.QuestionEntity;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static tom.eyre.mp2021.utility.DatabaseUtil.localDatabase;

public class QuizActivity extends AppCompatActivity {

    private AmazonDynamoDBClient dbClient;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout rv;
    private ScrollView sv;
    private ArrayList<QuestionGroupByType> questionGroupByTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_layout);

        rv = findViewById(R.id.loading);
        sv = findViewById(R.id.sv);

        recyclerView = findViewById(R.id.questionRv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                this, getResources().getString(R.string.identityId), Regions.US_WEST_2);

        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        checkForNewQuestions();
    }

    private void checkForNewQuestions() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Integer id = localDatabase.localDatabaseDao().getId();
                    if (id == null) {
                        id = 0;
                    }

                    Map<String, AttributeValue> expressionAttributeValues =
                            new HashMap<String, AttributeValue>();
                    expressionAttributeValues.put(":val", new AttributeValue().withN(id.toString()));

                    ScanRequest scanRequest = new ScanRequest()
                            .withTableName("questions")
                            .withFilterExpression("id > :val")
                            .withExpressionAttributeValues(expressionAttributeValues);

                    ScanResult scanResult = dbClient.scan(scanRequest);

                    ArrayList<QuestionEntity> entities = buildEntities(scanResult.getItems());
                    storeNewQuestionsLocally(entities);

                }catch (Exception e){
                    e.printStackTrace();
                }
                getQuestions();
            }
        });
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

    private void storeNewQuestionsLocally(ArrayList<QuestionEntity> entities) {
        if (entities != null && !entities.isEmpty() && entities.size() > 0) {
            localDatabase.localDatabaseDao().insertAllQuestions(entities);
            if(this == null) return;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "New Questions added",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getQuestions();
    }

    private void getQuestions() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<QuestionEntity> questions = localDatabase.localDatabaseDao().getAllQuestions().stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(QuestionEntity::getQuestion))),
                        ArrayList::new));
                questionGroupByTypes = new ArrayList<>();
                questions.stream().forEach(questionEntity -> insertQuestion(questionEntity));
                updateRecycler(questionGroupByTypes);
            }
        });
    }

    private void insertQuestion(QuestionEntity questionEntity){
        if(questionGroupByTypes.isEmpty()){
            questionGroupByTypes.add(buildQuestionGroupByType(questionEntity));
        }else {
            for(int i = 0; i < questionGroupByTypes.size(); i++){
                if(questionGroupByTypes.get(i).getType().equalsIgnoreCase(questionEntity.getType())){
                    ArrayList<QuestionEntity> questions = questionGroupByTypes.get(i).getQuestions();
                    questions.add(questionEntity);
                    questionGroupByTypes.get(i).setQuestions(questions);
                    break;
                }else if(i == questionGroupByTypes.size() - 1){
                    questionGroupByTypes.add(buildQuestionGroupByType(questionEntity));
                    break;
                }
            }
        }
    }

    private QuestionGroupByType buildQuestionGroupByType(QuestionEntity questionEntity){
        QuestionGroupByType questionGroupByType = new QuestionGroupByType();
        ArrayList<QuestionEntity> questions = new ArrayList<>();
        questions.add(questionEntity);
        questionGroupByType.setType(questionEntity.getType());
        questionGroupByType.setQuestions(questions);
        return questionGroupByType;
    }

    private void updateRecycler(ArrayList<QuestionGroupByType> questionGroupByTypes) {
        if(this == null) return;
        this.runOnUiThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                mAdapter = new QuestionTypeAdapter(questionGroupByTypes, getApplicationContext());
                mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
                rv.setVisibility(View.GONE);
                sv.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void insertNewQuestionToDynamoDb() {

//                Table dbTable = Table.loadTable(dbClient, "questions");
//                Document question = new Document();
//                question.put("uin", "uin");
//                question.put("date", "date");
//                question.put("title", "title");
//                question.put("question", "question");
//                question.put("type", "type");
//                question.put("lastUpdatedTs", String.valueOf(new Date()));
//                dbTable.putItem(question);
    }

}
