package tom.eyre.mp2021.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.adapter.QuestionAdapter;
import tom.eyre.mp2021.adapter.QuestionTypeAdapter;
import tom.eyre.mp2021.data.QuestionGroupByType;
import tom.eyre.mp2021.entity.QuestionEntity;

public class QuestionActivity extends AppCompatActivity {

    private ArrayList<QuestionEntity> questions;
    private AmazonDynamoDBClient dbClient;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout rv;
    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questions = (ArrayList<QuestionEntity>) getIntent().getSerializableExtra("questions");
        setContentView(R.layout.questions_layout);

        rv = findViewById(R.id.loading);
        sv = findViewById(R.id.sv);

        recyclerView = findViewById(R.id.questionRv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        updateRecycler();

    }

    private void updateRecycler() {
        if(this == null) return;
        this.runOnUiThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                mAdapter = new QuestionAdapter(questions, getApplicationContext());
                mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
                rv.setVisibility(View.GONE);
                sv.setVisibility(View.VISIBLE);

            }
        });
    }
}
