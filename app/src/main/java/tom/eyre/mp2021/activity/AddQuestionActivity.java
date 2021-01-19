package tom.eyre.mp2021.activity;

import android.content.ClipData;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.utility.HttpConnectUtil;

public class AddQuestionActivity extends AppCompatActivity {

    private EditText checkEt;
    private TextView checkTv;
    private Button checkBtn;
    private EditText questionEt;
    private EditText etType;
    private EditText etId;
    private Button submitBtn;
    private JSONObject jsonObject;

    private AmazonDynamoDBClient dbClient;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question_layout);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                this, getResources().getString(R.string.identityId), Regions.US_WEST_2);

        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        checkEt = findViewById(R.id.etCheck);
        checkTv = findViewById(R.id.tvCheck);
        checkBtn = findViewById(R.id.btnCheck);
        questionEt = findViewById(R.id.etQuestion);
        submitBtn = findViewById(R.id.btnSubmit);
        etType = findViewById(R.id.etType);
        etId = findViewById(R.id.etId);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executorService.execute(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        HttpConnectUtil httpConnectUtil = new HttpConnectUtil();
                        String json = httpConnectUtil.getJSONFromUrl("https://eldaddp.azurewebsites.net/commonsdivisions.json?uin=" + checkEt.getText().toString());

                        if (json != null && !json.equalsIgnoreCase("error")) {
                            JSONObject jsonObject1 = new JSONObject(json);
                            JSONArray jsonArray = jsonObject1.getJSONObject("result").getJSONArray("items");
                            if (jsonArray.length() != 0) {
                                jsonObject = jsonArray.getJSONObject(0);
                                runOnUiThread(new Runnable() {
                                    @SneakyThrows
                                    @Override
                                    public void run() {
                                        checkTv.setText(jsonObject.getString("title") + "\n" + jsonObject.getString("uin"));

                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkTv.setText("NO");
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executorService.execute(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Map<String,AttributeValue> item = new HashMap<String, AttributeValue>();
                        item.put("uin", new AttributeValue().withS(jsonObject.getString("uin")));
                        item.put("date", new AttributeValue().withS(jsonObject.getJSONObject("date").getString("_value")));
                        item.put("id", new AttributeValue().withN(etId.getText().toString()));
                        item.put("question", new AttributeValue().withS(questionEt.getText().toString()));
                        item.put("title", new AttributeValue().withS(jsonObject.getString("title")));
                        item.put("type", new AttributeValue().withS(etType.getText().toString()));
                        PutItemRequest putItemRequest = new PutItemRequest("questions", item);
                        PutItemResult putItemResult = dbClient.putItem(putItemRequest);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), putItemResult.toString(),
                                        Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });
            }
        });

    }
}
