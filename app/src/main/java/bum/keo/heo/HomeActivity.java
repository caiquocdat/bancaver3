package bum.keo.heo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

import bum.keo.heo.R;
import bum.keo.heo.adapter.LevelAdapter;
import bum.keo.heo.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding activityHomeBinding;
    private GridView gridView;
    private LevelAdapter levelAdapter;
    private ArrayList<LevelModel> levelList;
    private static final String PREFS_NAME = "DataGT";
    private static final String DATE_KEY = "SelectedGT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = activityHomeBinding.getRoot();
        setContentView(view);
//        activityHomeBinding.playImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent= new Intent(HomeActivity.this,PlayActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private int getValueFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(DATE_KEY, 0); // "Chưa lưu" là giá trị mặc định nếu chưa có giá trị nào được lưu.
    }

    private void setUpdata() {
        ArrayList<LevelModel> levelList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            levelList.add(new LevelModel(i, LevelState.LOCKED, R.drawable.img_lock));
        }
        int value = getValueFromPreferences();
        Log.d("Test_4", "setUpdata: "+value);
        if (value != 0&&value<24) {
            levelList.get(value ).setState(LevelState.PLAYABLE);
            levelList.get(value ).setImageResource(R.drawable.img_play_main);
            for (int i = 0; i < value; i++) {
                levelList.get(i).setState(LevelState.COMPLETED);
                levelList.get(i).setImageResource(R.drawable.img_pass);
            }
            for (int i = value + 1; i < 24; i++) {
                levelList.get(i).setState(LevelState.LOCKED);
                levelList.get(i).setImageResource(R.drawable.img_lock);
            }
        }else if (value==0){
            levelList.get(value).setState(LevelState.PLAYABLE);
            levelList.get(value).setImageResource(R.drawable.img_play_main);
        }else if (value>=24){
            levelList.get(0).setState(LevelState.PLAYABLE);
            levelList.get(0).setImageResource(R.drawable.img_play_main);
        }

        levelAdapter = new LevelAdapter(this, levelList);
        activityHomeBinding.gridView.setAdapter(levelAdapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        setUpdata();
    }
}