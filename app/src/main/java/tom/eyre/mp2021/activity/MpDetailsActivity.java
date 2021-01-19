package tom.eyre.mp2021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.adapter.MpCompareSelectionAdapter;
import tom.eyre.mp2021.adapter.MpProgressBarAdapter;
import tom.eyre.mp2021.data.QuestionsByType;
import tom.eyre.mp2021.data.SocialMediaResponse;
import tom.eyre.mp2021.entity.BillsEntity;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.entity.PartyEntity;
import tom.eyre.mp2021.entity.PostEntity;
import tom.eyre.mp2021.entity.QuestionEntity;
import tom.eyre.mp2021.entity.VoteEntity;
import tom.eyre.mp2021.fragment.ExpensesFragment;
import tom.eyre.mp2021.service.MpService;
import tom.eyre.mp2021.utility.DrawableFromUrl;

import static android.view.View.GONE;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static tom.eyre.mp2021.utility.DatabaseUtil.localDatabase;

public class MpDetailsActivity extends AppCompatActivity {
    private MpEntity mp;
    private List<PartyEntity> partyEntities = new ArrayList<>();
    private List<BillsEntity> bills;
    private List<VoteEntity> votes;
    private List<String> divisions;
    private MpService mpService = new MpService();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    //    private ExpenseCalculatedResponse expenses;
//    private ExpenseService expenseService = new ExpenseService();
//
    private ImageView mpImage;
    private TextView mpName;
    private TextView mpParty;
    private TextView mpFor;
    private TextView sbCount;
    private TextView voteCount;
    private TextView voteTitle;
    private TextView yaMp;
    private TextView posts;
    private TextView parliamentaryPostsCount;
    private TextView governmentPostsCount;
    private TextView oppositionPostsCount;
    private Button takeQuizButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<QuestionsByType> results;
    private Button updateQuiz;
    private Button opinionTutorial;
    private TextView twitter;
    private TextView webPage;
    private List<QuestionEntity> yourAnswers;
    private TextView opinionsTitle;
    private ImageView twitterImage;
    private TextView inOfficeBetween;

    //    private TextView mpAge;
//    private TextView mpBio;
//    private TextView wikiLinkUrl;
//    private TextView mpHomePage;
//    private TextView twitterUrl;
//    private CardView topView;
//    private ImageView twitterImage;
//    private CardView expenseCv;
//    private TextView expenseTv;
//
//    @SneakyThrows
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mp = (MpEntity) getIntent().getSerializableExtra("mp");
        setContentView(R.layout.mp_details_layout);

        System.out.println(mp.getId());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<PostEntity> postEntities = localDatabase.localDatabaseDao().getAllPostsById(mp.getId());
                if (postEntities.isEmpty())
                    mpService.getMpPostsAndInterests(mp.getId(), localDatabase);
                mpService.getSocialMedia(MpDetailsActivity.this, localDatabase, mp.getId());
                updatePosts();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("mp", mp);
        ExpensesFragment expensesFragment = new ExpensesFragment();
        expensesFragment.setArguments(bundle);
        ft.replace(R.id.frag, expensesFragment);
        ft.commit();
//
//        ActivityInfo info = this.getPackageManager().getActivityInfo(
//                this.getComponentName(), 0);
//        if ((info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0) {
//            System.out.println(true);
//        } else {
//            System.out.println(false);
//        }
//        this.getWindow().setStatusBarColor(new ColorUtil().getColor(this, mp));
//
        mpImage = findViewById(R.id.mpImage);
        mpName = findViewById(R.id.mpName);
        mpParty = findViewById(R.id.mpParty);
        mpFor = findViewById(R.id.mpFor);
        sbCount = findViewById(R.id.sbCount);
        voteCount = findViewById(R.id.voteCount);
        voteTitle = findViewById(R.id.voteTitle);
        yaMp = findViewById(R.id.yampCount);
        posts = findViewById(R.id.posts);
        governmentPostsCount = findViewById(R.id.governmentPostsCount);
        parliamentaryPostsCount = findViewById(R.id.parliamentaryPostsCount);
        oppositionPostsCount = findViewById(R.id.oppositionPostsCount);
        takeQuizButton = findViewById(R.id.takeQuizButton);
        updateQuiz = findViewById(R.id.updateQuiz);
        opinionTutorial = findViewById(R.id.opinionTutorial);
        twitter = findViewById(R.id.twitter);
        webPage = findViewById(R.id.web);
        opinionsTitle = findViewById(R.id.opinionsTitle);
        twitterImage = findViewById(R.id.twitterImage);
        inOfficeBetween = findViewById(R.id.inOfficeBetween);

        recyclerView = findViewById(R.id.opinionsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        voteCount.setText(getMpAge());
//        mpAge = findViewById(R.id.mpAge);
//        mpBio = findViewById(R.id.mpBio);
//        wikiLinkUrl = findViewById(R.id.wikiLink);
//        mpHomePage = findViewById(R.id.mpHomePage);
//        twitterUrl = findViewById(R.id.mpTwitter);
//        topView = findViewById(R.id.topView);
//        twitterImage = findViewById(R.id.twitterImage);
//        expenseCv = findViewById(R.id.expenseCv);
//        expenseTv = findViewById(R.id.expenseTv);
//
//        Animation rotation = AnimationUtils.loadAnimation(MpDetailsActivity.this, R.anim.rotate);
//        rotation.setFillAfter(true);
//        expenseCv.startAnimation(rotation);
//
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    expenses = expenseService.calculateExpensesPerYear(mp, databaseUtil.mpDB);
//                    if (expenses.getExpenseByYears() == null || expenses.getExpenseByYears().isEmpty()) {
//                        stopAnimation(false);
//                    } else {
//                        stopAnimation(true);
//                    }
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                    stopAnimation(false);
//                }
//            }
//        });
//
//        if (mp.getBio() != null && !mp.getBio().equalsIgnoreCase("unknown") && System.currentTimeMillis() - mp.getLastUpdatedTimestamp() < SEVEN_DAYS) {
//            mpBio.setText(mp.getBio());
//            wikiLinkUrl.setText(Html.fromHtml("<a href=\"" + mp.getWikiLink() + "\">Read more</a>"));
//            wikiLinkUrl.setMovementMethod(LinkMovementMethod.getInstance());
//        } else {
//            mpService.getMpBio(mp, this, getResources().getString(R.string.apiKey), mpDB, mp.getId());
//        }
//        if (mp.getHomePage() != null && System.currentTimeMillis() - mp.getLastUpdatedTimestamp() < SEVEN_DAYS) {
//            mpHomePage.setText(Html.fromHtml("<a href=\"" + mp.getHomePage() + "\">" + mp.getHomePage().substring(mp.getHomePage().lastIndexOf("www.") + 4)));
//            mpHomePage.setMovementMethod(LinkMovementMethod.getInstance());
//            mpHomePage.setVisibility(View.VISIBLE);
//        } else {
//            mpHomePage.setVisibility(View.INVISIBLE);
//        }
//        if (mp.getTwitterUrl() != null && System.currentTimeMillis() - mp.getLastUpdatedTimestamp() < SEVEN_DAYS) {
//            twitterUrl.setText(Html.fromHtml("<a href=\"" + mp.getTwitterUrl() + "\">" + '@' + mp.getTwitterUrl().substring(mp.getTwitterUrl().lastIndexOf('/') + 1) + "</a>"));
//            twitterUrl.setMovementMethod(LinkMovementMethod.getInstance());
//            mpHomePage.setVisibility(View.VISIBLE);
//        } else {
//            twitterUrl.setVisibility(View.INVISIBLE);
//        }
//        if (mp.getFullName().length() > 20) {
//            mpName.setTextSize(25);
//        } else {
//            mpName.setTextSize(30);
//        }
//        mpName.setText(mp.getFullName());
//        mpParty.setText(mp.getParty());
//        mpFor.setText(mp.getMpFor().equalsIgnoreCase("life peer") ? mp.getMpFor() : (mp.getActive() ? "Current MP for " : "Ex-MP for ") + mp.getMpFor());
//        Date dob = null;
//        try {
//            dob = sdf.parse(mp.getDateOfBirth());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Integer age = 0;
//        if (dob != null) {
//            age = getDiffYears(dob, new Date());
//        }
//        if (age > 0) {
//            mpAge.setText("Age: " + String.valueOf(age));
//        } else {
//            mpAge.setVisibility(View.GONE);
//        }
//
//        try {
//            topView.setBackgroundColor(new ColorUtil().getColor(this, mp));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        twitter.setText(mp.getTwitterUrl());
        webPage.setText(mp.getHomePage());

        takeQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MpDetailsActivity.this, QuizActivity.class);
                startActivity(myIntent);
            }
        });
        mpImage.setImageDrawable(new DrawableFromUrl().get(mp.getId()));
        mpName.setText(mp.getFullName());
        mpFor.setText(mp.getMpFor().equalsIgnoreCase("life peer") ? mp.getMpFor() : (mp.getActive() ? "Current MP for " : "Ex-MP for ") + mp.getMpFor());
        mpParty.setText(mp.getParty());

        if (mp.getActive()) {
            inOfficeBetween.setVisibility(GONE);
        } else {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            start.setTime(sdf.parse(mp.getHouseStartDate()));
            end.setTime(sdf.parse(mp.getHouseEndDate()));
            inOfficeBetween.setText("MP between " + start.get(Calendar.YEAR) + " - " + end.get(Calendar.YEAR));
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                bills = localDatabase.localDatabaseDao().getAllBillsByName(mp.getFullName(), mp.getForename() + " " + mp.getSurname());
                runOnUiThread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        sbCount.setText(String.valueOf(bills.size()));
                    }
                });
            }
        });
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                votes = localDatabase.localDatabaseDao().getAllVotes();
//                divisions = localDatabase.localDatabaseDao().getAllVoteDivisionUins();
//                runOnUiThread(new Runnable() {
//                    @SneakyThrows
//                    @Override
//                    public void run() {
//                        int agreesWith = (int) Math.round(agreesWithOwnPartyPercent());
//                        voteCount.setText(getMpAge());
//                        voteTitle.setText("Of The Time They Agree With The Majority Of " + mp.getParty());
//                    }
//                });
//            }
//        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                partyEntities = localDatabase.localDatabaseDao().getAllPartyTermsById(mp.getId());
                runOnUiThread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        yaMp.setText(yearsAsMp());
                    }
                });
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                updateRecyclerView();
            }
        });
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                updateRecyclerView();
            }
        });
    }

    private void updateRecyclerView() {
        results = getOpinions();
        results = results.stream()
                .filter(questionsByType -> questionsByType.getNoRecord() != questionsByType.getTotalVotes())
                .collect(Collectors.toList());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!results.isEmpty()) {
                    takeQuizButton.setVisibility(GONE);
                    updateQuiz.setVisibility(View.VISIBLE);
                    updateQuiz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(MpDetailsActivity.this, QuizActivity.class);
                            startActivity(myIntent);
                        }
                    });
                    opinionTutorial.setVisibility(View.VISIBLE);
                    opinionTutorial.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(MpDetailsActivity.this, OpinionTutorial.class);
                            startActivity(myIntent);
                        }
                    });
                } else if (results.isEmpty() && yourAnswers.stream().filter(questionEntity -> questionEntity.getOpinion() != null).count() > 0) {
                    if (yourAnswers.stream().filter(questionEntity -> questionEntity.getOpinion() != null).count() == yourAnswers.size()) {
                        takeQuizButton.setVisibility(GONE);
                        opinionsTitle.setText("This MP Has Not Voted On Any Of The Issues Selected. This May Be Due To The Fact That The MP Was Not In Office At The Time Of Any Of The Votes Selected.");
                    }

                }
                mAdapter = new MpProgressBarAdapter(results, getApplicationContext());
                mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }

    @SneakyThrows
    private String getMpAge() {
        return String.valueOf((int) ((new Date().getTime() - sdf.parse(mp.getDateOfBirth()).getTime()) / DateUtils.YEAR_IN_MILLIS));
    }

    private ArrayList<QuestionsByType> getOpinions() {
        List<VoteEntity> votes = localDatabase.localDatabaseDao().getAllVotesByMpId(mp.getId());
        yourAnswers = localDatabase.localDatabaseDao().getAllQuestions();
        ArrayList<QuestionsByType> questions = new ArrayList<>();
        ;
        if (yourAnswers.stream().filter(ans -> ans.getOpinion() != null).findAny().isPresent()) {
            try {
                for (QuestionEntity questionEntity : yourAnswers) {
                    Optional<VoteEntity> vote = votes == null ? null : votes.stream().filter(voteEntity -> voteEntity.getUin().equalsIgnoreCase(questionEntity.getUin())).findFirst();
                    Optional<QuestionsByType> current = questions.stream().filter(questionsByType -> questionsByType.getType().equalsIgnoreCase(questionEntity.getType())).findFirst();
                    if (current.isPresent()) {
                        current = Optional.ofNullable(setResult(current.get(), vote, yourAnswers));
                    } else {
                        QuestionsByType questionsByType = new QuestionsByType();
                        questionsByType.setType(questionEntity.getType());
                        questionsByType = setResult(questionsByType, vote, yourAnswers);
                        questions.add(questionsByType);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return questions;
    }


    private QuestionsByType setResult(QuestionsByType questionsByType, Optional<VoteEntity> vote, List<QuestionEntity> yourAnswers) {
        questionsByType.setNoRecord(questionsByType.getNoRecord() + (!vote.isPresent() ? 1 : didUserGiveAnOpinion(vote, yourAnswers) ? 0 : 1));
        questionsByType.setVoteFor(questionsByType.getVoteFor() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("aye") ? 1 : 0 : 0));
        questionsByType.setVoteAgainst(questionsByType.getVoteAgainst() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("novote") ? 1 : 0 : 0));
        questionsByType.setVoteAbstained(questionsByType.getVoteAbstained() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("abstained") ? 1 : 0 : 0));
        questionsByType.setVoteDidNotVote(questionsByType.getVoteDidNotVote() + (vote.isPresent() ? vote.get().getResult().toLowerCase().contains("didnotvote") ? 1 : 0 : 0));
        questionsByType.setAgreedWithUser(questionsByType.getAgreedWithUser() + agreedWithUser(vote, yourAnswers));
        questionsByType.setTotalVotes(questionsByType.getTotalVotes() + 1);

        return questionsByType;
    }

    private Boolean didUserGiveAnOpinion(Optional<VoteEntity> vote, List<QuestionEntity> yourAnswers) {
        if (vote.isPresent()) {
            Optional<QuestionEntity> temp = yourAnswers.stream().filter(answers -> answers.getUin().equalsIgnoreCase(vote.get().getUin()))
                    .findFirst();
            if (temp.isPresent()) {
                return temp.get().getOpinion() != null ? true : false;
            }
        }
        return false;
    }

    private int agreedWithUser(Optional<VoteEntity> vote, List<QuestionEntity> yourAnswers) {
        if (!vote.isPresent()) return 0;
        for (QuestionEntity questionEntity : yourAnswers) {
            if (vote.get().getUin().equalsIgnoreCase(questionEntity.getUin())) {
                if (questionEntity.getOpinion() == null) {
                    return 0;
                } else if (questionEntity.getOpinion()) {
                    if (vote.isPresent() && vote.get().getResult().toLowerCase().contains("aye")) {
                        return 1;
                    }
                } else if (questionEntity.getOpinion() == false) {
                    if (vote.isPresent() && vote.get().getResult().toLowerCase().contains("novote")) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    private void updatePosts() {
        List<PostEntity> postEntities = localDatabase.localDatabaseDao().getAllPostsById(mp.getId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!postEntities.isEmpty()) {
                    parliamentaryPostsCount.setText(Long.toString(postEntities.stream().filter(postEntity -> postEntity.getType().equalsIgnoreCase("parliamentary")).count()));
                    governmentPostsCount.setText(Long.toString(postEntities.stream().filter(postEntity -> postEntity.getType().equalsIgnoreCase("government")).count()));
                    oppositionPostsCount.setText(Long.toString(postEntities.stream().filter(postEntity -> postEntity.getType().equalsIgnoreCase("opposition")).count()));
                    posts.setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder();
                    for (PostEntity postEntity : postEntities) {
                        if (postEntity.getEndDate() == null) {
                            if (postEntity.getHansardName() == null) {
                                sb.append(postEntity.getPosition() + "\n");
                            } else {
                                sb.append(postEntity.getHansardName() + "\n");
                            }
                        }
                    }
                    posts.setText(sb.toString());
                } else {
                    parliamentaryPostsCount.setText("0");
                    governmentPostsCount.setText("0");
                    oppositionPostsCount.setText("0");
                    posts.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private String getStringMajority(float percent) {
        if (percent <= 20) {
            return "Very Rarely Agrees With The Majority Of ";
        } else if (percent <= 40) {
            return "Rarely Agrees With The Majority Of ";
        } else if (percent <= 60) {
            return "Occasionally Agrees With The Majority Of ";
        } else if (percent <= 80) {
            return "Frequently Agrees With v";
        } else if (percent <= 100) {
            return "Very Frequently Agrees With The Majority Of ";
        }
        return "Oops";
    }

    private Double agreesWithOwnPartyPercent() {
        int agreesWithParty = 0;
        int totalMpVotes = 0;
        Map<String, Long> votetotals;
        for (String uin : divisions) {
            List<VoteEntity> temp = votes.stream()
                    .filter(voteEntity -> voteEntity.getUin().equalsIgnoreCase(uin))
                    .collect(Collectors.toList())
                    .stream()
                    .filter(voteEntity -> voteEntity.getParty().equalsIgnoreCase(mp.getParty()))
                    .collect(Collectors.toList());
            votetotals = temp.stream().collect(groupingBy(VoteEntity::getResult, counting()));
            if (temp.stream().filter(voteEntity -> voteEntity.getMpId().intValue() == mp.getId().intValue()).findFirst().isPresent()) {
                if (temp.stream().filter(voteEntity -> voteEntity.getMpId().intValue() == mp.getId().intValue()).findFirst().get().getResult().equalsIgnoreCase(maxUsingIteration(votetotals))) {
                    agreesWithParty++;
                    totalMpVotes++;
                }
                if (!temp.stream().filter(voteEntity -> voteEntity.getMpId().intValue() == mp.getId().intValue()).findFirst().get().getResult().equalsIgnoreCase("didNotvote")) {
                    totalMpVotes++;
                }
            }
        }
        return (100d / totalMpVotes) * agreesWithParty;
    }

    public <K, V extends Comparable<V>> K maxUsingIteration(Map<K, V> map) {
        Map.Entry<K, V> maxEntry = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (maxEntry == null || entry.getValue()
                    .compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }

    @SneakyThrows
    private String yearsAsMp() {
        long total = 0l;
        for (PartyEntity partyEntity : partyEntities) {
            Date startDate = sdf.parse(partyEntity.getStartDate());
            Date endDate;
            if (partyEntity.getEndDate() != null) {
                endDate = sdf.parse(partyEntity.getEndDate());
            } else {
                endDate = new Date();
            }
            total += endDate.getTime() - startDate.getTime();
        }
        return String.valueOf(total / DateUtils.YEAR_IN_MILLIS);// + " Years " + ((total % DateUtils.YEAR_IN_MILLIS) / (DateUtils.DAY_IN_MILLIS * 30)) + " Months ";
    }

    //
//    private void stopAnimation(Boolean hasExpenses) {
//        if (hasExpenses) {
//            this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    expenseCv.clearAnimation();
//                    expenseCv.setCardBackgroundColor(getResources().getColor(R.color.parlimentGreen, null));
//                    new AnimateUtil().slowHide(expenseTv, 0, 0);
//                    expenseTv.setText("Expenses");
//                    new AnimateUtil().expand(expenseCv, getApplicationContext());
//                    new AnimateUtil().slowShow(expenseTv, 500, 500);
//                }
//            });
//        } else {
//            this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    expenseCv.clearAnimation();
//                    expenseCv.setCardBackgroundColor(getResources().getColor(R.color.transparent_black, null));
//                }
//            });
//        }
//    }
//
//    private static int getDiffYears(Date first, Date last) {
//        Calendar a = getCalendar(first);
//        Calendar b = getCalendar(last);
//        int diff = b.get(YEAR) - a.get(YEAR);
//        if (a.get(MONTH) > b.get(MONTH) ||
//                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
//            diff--;
//        }
//        return diff;
//    }
//
//    private static Calendar getCalendar(Date date) {
//        Calendar cal = Calendar.getInstance(Locale.UK);
//        cal.setTime(date);
//        return cal;
//    }
//
//    public void setBioAndWikiLink(final String bio, final String wikiLink) {
//        if (this == null) return;
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (bio.equalsIgnoreCase("")) {
//                    mpBio.setVisibility(View.GONE);
//                    wikiLinkUrl.setVisibility(View.GONE);
//                } else {
//                    mpBio.setText(bio);
//                    wikiLinkUrl.setText(Html.fromHtml("<a href=\"" + wikiLink + "\">Read more</a>"));
//                    wikiLinkUrl.setMovementMethod(LinkMovementMethod.getInstance());
//                }
//            }
//        });
//    }
//
    public void setSocialMedia(final SocialMediaResponse socialMediaResponse) {
        if (this == null) return;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (socialMediaResponse != null) {
                    if (socialMediaResponse.getHomePage() != null) {
                        webPage.setText(Html.fromHtml("<a href=" + socialMediaResponse.getHomePage() + ">" + socialMediaResponse.getHomePage().replace("http://", ""), 0));
                        webPage.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    if (socialMediaResponse.getTwitter() != null && socialMediaResponse.getTwitter().getUrl() != null) {
                        twitter.setText(Html.fromHtml("<a href=" + socialMediaResponse.getTwitter().getUrl() + ">" + socialMediaResponse.getTwitter().getUrl().substring(socialMediaResponse.getTwitter().getUrl().lastIndexOf("/") + 1), 0));
                        twitter.setMovementMethod(LinkMovementMethod.getInstance());
                    } else {
                        twitterImage.setVisibility(GONE);
                    }
                }
            }
        });
    }

////    private void setLayoutBelow(int layoutBelow, int viewId){
////        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) getView().findViewById(viewId).getLayoutParams();
////        params.addRule(RelativeLayout.BELOW, layoutBelow);
////        getView().findViewById(viewId).setLayoutParams(params);

}
