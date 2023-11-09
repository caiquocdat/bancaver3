package bum.keo.bancagame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import bum.keo.bancagame.adapter.HistoryAdapter;
import bum.keo.bancagame.databinding.ActivityHistoryBinding;


public class HistoryActivity extends AppCompatActivity {
    private ActivityHistoryBinding activityHistoryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHistoryBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        View view = activityHistoryBinding.getRoot();
        setContentView(view);
        hideSystemUI();
        setUpRcv();
        activityHistoryBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUpRcv() {
        DatabaseHelper db = new DatabaseHelper(HistoryActivity.this);
        ArrayList<Integer> listData = new ArrayList<>();
        listData = db.getTop10();
        if (listData.size() > 0) {
            HistoryAdapter adapter = new HistoryAdapter(this, listData);
            activityHistoryBinding.historyRcv.setLayoutManager(new LinearLayoutManager(this));
            activityHistoryBinding.historyRcv.setAdapter(adapter);
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}