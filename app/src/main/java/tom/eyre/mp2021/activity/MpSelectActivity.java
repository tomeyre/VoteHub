package tom.eyre.mp2021.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import tom.eyre.mp2021.R;
import tom.eyre.mp2021.adapter.ListViewAdapter;
import tom.eyre.mp2021.adapter.MpCompareSelectionAdapter;
import tom.eyre.mp2021.entity.MpEntity;
import tom.eyre.mp2021.utility.AnimateUtil;
import tom.eyre.mp2021.utility.DatabaseUtil;

import static android.view.View.INVISIBLE;
import static tom.eyre.mp2021.utility.DatabaseUtil.localDatabase;
import static tom.eyre.mp2021.utility.ScreenUtils.convertDpToPixel;
import static tom.eyre.mp2021.utility.ScreenUtils.getScreenHeight;

public class MpSelectActivity extends AppCompatActivity {

    private static final Logger log = Logger.getLogger(String.valueOf(MpSelectActivity.class));
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ListView list;
    private ListViewAdapter adapter;
    private SearchView editSearch;

    private List<MpEntity> mpList;
    private RelativeLayout back;
    private Boolean vs;
    private MaterialCardView searchCv;
    private String previousQuery = "";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MpEntity> selectedMpsToCompare = new ArrayList<>();
    private ImageButton appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp_select_layout);

        recyclerView = findViewById(R.id.compareSelectedRv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        list = findViewById(R.id.listview);
        list.setVisibility(INVISIBLE);
        editSearch = findViewById(R.id.search);
        appInfo = findViewById(R.id.appInfo);

        back = findViewById(R.id.back);
        searchCv = findViewById(R.id.searchCv);

        float radius = convertDpToPixel(30f, this);
        searchCv.setShapeAppearanceModel(
                searchCv.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                        .build());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboardFrom(getApplicationContext(), view);
                editSearch.clearFocus();
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                setMpListAndUpdateAdapter();
            }
        });

        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null && list != null) {
                    if (!newText.equalsIgnoreCase("")) {
                        list.setVisibility(View.VISIBLE);
                    } else {
                        new AnimateUtil().shrinkListView(list, getApplicationContext(), getListViewMaxHeight());
                        if(selectedMpsToCompare == null || selectedMpsToCompare.isEmpty()) {
                            new AnimateUtil().roundOffSearchCorners(searchCv, getApplicationContext(),500);
                        }
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                list.setVisibility(INVISIBLE);
                            }
                        }, 500);
                    }
                    adapter.filter(newText);
                }
                if (previousQuery.equalsIgnoreCase("")) {
                    if(selectedMpsToCompare == null || selectedMpsToCompare.isEmpty()) {
                        new AnimateUtil().squareOffSearchCorners(searchCv, getApplicationContext());
                    }
                    new AnimateUtil().expandListView(list, getApplicationContext(), getListViewMaxHeight());
                }
//                else if(previousQuery.length() == 1 && editsearch.getQuery().toString().equalsIgnoreCase("")){
//                    new AnimateUtil().shrinkListView(list, getContext(), getListViewMaxHeight());
//                    new AnimateUtil().roundOffSearchCorners(searchCv, getContext());
//                }
                previousQuery = editSearch.getQuery().toString();
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (alreadySelectedOne()) {
                    if (selectedMpsToCompare.get(0).getId() == mpList.get(position).getId()) {
                        Toast.makeText(getApplicationContext(), "Please select a different MP",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Intent myIntent = new Intent(MpSelectActivity.this, CompareMpActivity.class);
                        myIntent.putExtra("mpA", selectedMpsToCompare.get(0)); //Optional parameters
                        myIntent.putExtra("mpB", mpList.get(position)); //Optional parameters
                        MpSelectActivity.this.startActivity(myIntent);
                    }
                }else {
                    Intent myIntent = new Intent(MpSelectActivity.this, MpDetailsActivity.class);
                    myIntent.putExtra("mp", mpList.get(position)); //Optional parameters
                    MpSelectActivity.this.startActivity(myIntent);
                }
            }
        });
        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MpSelectActivity.this, InfoActivity.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onPause() {
        editSearch.setQuery(null,false);
        editSearch.clearFocus();
        super.onPause();
    }

    private int getListViewMaxHeight() {
        return getScreenHeight(this) -
                searchCv.getLayoutParams().height - (int) convertDpToPixel(75f, this);
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setMpListAndUpdateAdapter() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mpList = localDatabase.localDatabaseDao().getAllMps();
                updateAdapter(getApplicationContext());
            }
        });
    }

    private void updateAdapter(final Context context) {
        if (getApplicationContext() == null) return;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter = new ListViewAdapter(context, mpList, MpSelectActivity.this);
                list.setAdapter(adapter);
            }
        });
    }

    private void updateRecycler() {
        if (this == null) return;
        if (mAdapter == null) {
            mAdapter = new MpCompareSelectionAdapter(selectedMpsToCompare, this);
            mAdapter.setHasStableIds(true);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateList(MpEntity mpEntity){
        if(!selectedMpsToCompare.stream().filter(mpEntity1 -> mpEntity1.getFullName().equalsIgnoreCase(mpEntity.getFullName())).findFirst().isPresent()) {
            if(selectedMpsToCompare == null || selectedMpsToCompare.isEmpty()){
                Toast.makeText(this, "Please select an MP to compare with",
                        Toast.LENGTH_SHORT).show();
            }
            selectedMpsToCompare.add(mpEntity);
            updateRecycler();
            editSearch.setQuery(null, false);
        }else{
            Toast.makeText(this, "Please select a different MP",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromList(int position){
        selectedMpsToCompare.remove(position);
        updateRecycler();
        if(selectedMpsToCompare.isEmpty()) {
            new AnimateUtil().roundOffSearchCorners(searchCv, getApplicationContext(),100);
        }
    }

    public Boolean alreadySelectedOne(){
        return selectedMpsToCompare != null && !selectedMpsToCompare.isEmpty();
    }

}