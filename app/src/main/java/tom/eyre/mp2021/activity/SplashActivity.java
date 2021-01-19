package tom.eyre.mp2021.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.SneakyThrows;
import tom.eyre.mp2021.R;
import tom.eyre.mp2021.service.UpdateService;
import tom.eyre.mp2021.utility.DatabaseUtil;

import static tom.eyre.mp2021.utility.Constants.SEVEN_DAYS;
import static tom.eyre.mp2021.utility.DatabaseUtil.localDatabase;

public class SplashActivity extends Activity {

    private DatabaseUtil databaseUtil;
    private final UpdateService updateService = new UpdateService();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Future<Boolean> future;
    private Future<Boolean> futureTwo;
    private Future<Boolean> futureThree;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        databaseUtil = DatabaseUtil.getInstance(this);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        Long lastTimeUpdated = sharedPref.getLong(getString(R.string.first_time), 0l);
        if(lastTimeUpdated == 0l || System.currentTimeMillis() - lastTimeUpdated > SEVEN_DAYS) {
            setDatabase();
            sharedPref.edit().putLong(getString(R.string.first_time), System.currentTimeMillis()).apply();
        }else{
            goToMain();
        }
    }

    @SneakyThrows
    private void setDatabase(){
//        future = executorService.submit(new Callable<Boolean>() {
//            @Override
//            public Boolean call() throws Exception {
//                try {
//                    updateService.updateDivision(localDatabase);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    System.out.println("Something went wrong");
//                }
//                return true;
//            }
//        });
        futureTwo = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    updateService.updateMp(localDatabase);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("Something went wrong");
                }
                return true;
            }
        });
        futureThree = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                updateService.updateBills(localDatabase);
                return true;
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                getVotes();
            }
        });
    }

    private void getVotes(){
//        while(!future.isDone()){}
//        updateProgress();
        while(!futureTwo.isDone()){}
        updateProgress();
        while(!futureThree.isDone()){}
        updateProgress();
        future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    updateService.updateVotes(localDatabase,getApplicationContext());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "votes not updated",
                            Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "votes not updated",
                            Toast.LENGTH_LONG).show();
                    System.out.println("Something went wrong");
                }
                return true;
            }
        });
        while(!future.isDone()){}
        updateProgress();
        goToMain();
    }

    private void updateProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progressBar.getProgress() + 25);
            }
        });
    }

    private void goToMain(){
        Intent mainIntent = new Intent(SplashActivity.this, MpSelectActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }
}
