package tom.eyre.mp2021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.adapter.MpCompareProgressBarAdapter;
import tom.eyre.mp2021.data.QuestionsByType;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.entity.QuestionEntity;
import tom.eyre.mp2021.entity.VoteEntity;
import tom.eyre.mp2021.utility.ColorUtil;
import tom.eyre.mp2021.utility.DatabaseUtil;
import tom.eyre.mp2021.utility.DrawableFromUrl;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class CompareMpActivity extends AppCompatActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private MpEntity mpA;
    private MpEntity mpB;

    private ImageView mpImageA;
    private ImageView mpImageB;

    private CardView topViewA;
    private CardView topViewB;
    private TextView mpNameA;
    private TextView mpNameB;
    private TextView mpAgeA;
    private TextView mpAgeB;
    private TextView partyA;
    private TextView partyB;
    private TextView mpForA;
    private TextView mpForB;
    private RelativeLayout takeQuizView;
    private TextView info;
    private Button goToQuiz;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseUtil databaseUtil;

    private List<QuestionEntity> userQuestionResults;
    private List<VoteEntity> mpAVotes;
    private List<VoteEntity> mpBVotes;

    private Future<Boolean> getUserResults;
    private Future<Boolean> getMpAVotes;
    private Future<Boolean> getMpBVotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mpA = (MpEntity) getIntent().getSerializableExtra("mpA");
        mpB = (MpEntity) getIntent().getSerializableExtra("mpB");
        setContentView(R.layout.compare_mp_layout);

        databaseUtil = DatabaseUtil.getInstance(this);

        recyclerView = findViewById(R.id.mpCompareRv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        takeQuizView = findViewById(R.id.takeQuizView);
        info = findViewById(R.id.info);
        goToQuiz = findViewById(R.id.goToQuiz);
        goToQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(CompareMpActivity.this, QuizActivity.class);
                CompareMpActivity.this.startActivity(mainIntent);
                CompareMpActivity.this.finish();
            }
        });

        getUserResults = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                userQuestionResults = databaseUtil.localDatabase.localDatabaseDao().getAllQuestions();
                if(userQuestionResults != null && !userQuestionResults.isEmpty() &&
                userQuestionResults.stream().filter(result -> result.getOpinion() != null).findFirst().isPresent()){
                    takeQuizView.setVisibility(View.GONE);
                    setViewAdapters();
                }
                return true;
            }
        });

        getMpAVotes = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mpAVotes = databaseUtil.localDatabase.localDatabaseDao().getAllVotesByMpId(mpA.getId());
                return true;
            }
        });

        getMpBVotes = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mpBVotes = databaseUtil.localDatabase.localDatabaseDao().getAllVotesByMpId(mpB.getId());
                return true;
            }
        });

        mpImageA = findViewById(R.id.mpImageA);
        mpImageB = findViewById(R.id.mpImageB);

        topViewA = findViewById(R.id.topViewA);
        topViewB = findViewById(R.id.topViewB);

        mpNameA = findViewById(R.id.mpNameA);
        mpNameB = findViewById(R.id.mpNameB);

        mpAgeA = findViewById(R.id.mpAgeA);
        mpAgeB = findViewById(R.id.mpAgeB);

        partyA = findViewById(R.id.mpPartyA);
        partyB = findViewById(R.id.mpPartyB);

        mpForA = findViewById(R.id.mpForA);
        mpForB = findViewById(R.id.mpForB);

        try {
            topViewA.setBackgroundColor(new ColorUtil().getColor(this,mpA));
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            topViewB.setBackgroundColor(new ColorUtil().getColor(this,mpB));
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            mpImageA.setImageDrawable(new DrawableFromUrl().get(mpA.getId()));
            mpImageB.setImageDrawable(new DrawableFromUrl().get(mpB.getId()));
        }catch (Exception e){e.printStackTrace();}

        mpNameA.setText(mpA.getFullName());
        mpNameB.setText(mpB.getFullName());

        Date dobA = null;
        Date dobB = null;
        try {
            dobA = sdf.parse(mpA.getDateOfBirth());
        }catch (Exception e){e.printStackTrace();}
        try {
            dobB = sdf.parse(mpB.getDateOfBirth());
        }catch (Exception e){e.printStackTrace();}
        Integer ageA = 0;
        Integer ageB = 0;
        if(dobA != null){
            ageA = getDiffYears(dobA, new Date());
        }
        if(dobB != null){
            ageB = getDiffYears(dobB, new Date());
        }
        if(ageA > 0) {
            mpAgeA.setText("Age: " + String.valueOf(ageA));
        }else{
            mpAgeA.setText("Age: Unknown");
        }
        if(ageB > 0) {
            mpAgeB.setText("Age: " + String.valueOf(ageB));
        }else{
            mpAgeB.setText("Age: Unknown");
        }

        partyA.setText(mpA.getParty());
        partyB.setText(mpB.getParty());

        mpForA.setText(mpA.getMpFor().equalsIgnoreCase("life peer") ? mpA.getMpFor() : (mpA.getActive() ? "Current MP for " : "Ex-MP for ") + mpA.getMpFor());
        mpForB.setText(mpB.getMpFor().equalsIgnoreCase("life peer") ? mpB.getMpFor() : (mpB.getActive() ? "Current MP for " : "Ex-MP for ") + mpB.getMpFor());

    }

    private static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.setTime(date);
        return cal;
    }

    private static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    private void setViewAdapters() {
        while (!getUserResults.isDone() &&
        !getMpAVotes.isDone() && !getMpBVotes.isDone()){}
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new MpCompareProgressBarAdapter(calculateQuestionResultsByType(mpAVotes),calculateQuestionResultsByType(mpBVotes));
                mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }

    private ArrayList<QuestionsByType> calculateQuestionResultsByType(List<VoteEntity> votes) {
        ArrayList<QuestionsByType> questions = new ArrayList<>();
        try {
            for (QuestionEntity questionEntity : userQuestionResults) {
                Optional<VoteEntity> vote = votes == null ? null : votes.stream().filter(voteEntity -> voteEntity.getUin().equalsIgnoreCase(questionEntity.getUin())).findFirst();
                Optional<QuestionsByType> current = questions.stream().filter(questionsByType -> questionsByType.getType().equalsIgnoreCase(questionEntity.getType())).findFirst();
                if (current.isPresent()) {
                    current = Optional.ofNullable(setResult(current.get(), vote));
                } else {
                    QuestionsByType questionsByType = new QuestionsByType();
                    questionsByType.setType(questionEntity.getType());
                    questionsByType = setResult(questionsByType, vote);
                    questions.add(questionsByType);
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return questions;
    }

    private QuestionsByType setResult(QuestionsByType questionsByType, Optional<VoteEntity> vote){
        questionsByType.setNoRecord(questionsByType.getNoRecord() + (vote.isPresent() ? 0 : 1));
        questionsByType.setVoteFor(questionsByType.getVoteFor() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("aye") ? 1 : 0 : 0));
        questionsByType.setVoteAgainst(questionsByType.getVoteAgainst() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("novote") ? 1 : 0 : 0));
        questionsByType.setVoteAbstained(questionsByType.getVoteAbstained() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("abstained") ? 1 : 0 : 0));
        questionsByType.setVoteDidNotVote(questionsByType.getVoteDidNotVote() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("didnotvote") ? 1 : 0 : 0));
        questionsByType.setAgreedWithUser(questionsByType.getAgreedWithUser() + agreedWithUser(vote));
        questionsByType.setTotalVotes(questionsByType.getTotalVotes() + 1);

        return questionsByType;
    }

    private int agreedWithUser(Optional<VoteEntity> vote){
        if(!vote.isPresent()) return 0;
        for(QuestionEntity questionEntity : userQuestionResults){
            if(vote.get().getUin().equalsIgnoreCase(questionEntity.getUin())){
                if(questionEntity.getOpinion() == null){
                    return 0;
                }else if(questionEntity.getOpinion()){
                    if(vote.isPresent() && vote.get().getResult().toLowerCase().contains("aye")){
                        return 1;
                    }
                }else if(questionEntity.getOpinion() == false){
                    if(vote.isPresent() && vote.get().getResult().toLowerCase().contains("novote")){
                        return 1;
                    }
                }
            }
        }
        return  0;
    }
}
