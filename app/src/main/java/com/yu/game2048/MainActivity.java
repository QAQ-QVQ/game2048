package com.yu.game2048;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yu.game2048.Utils.GameView;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.game_view)
    GameView gameView;
    @BindView(R.id.game_title_view)
    TextView gameTitleView;//2048标题
    @BindView(R.id.game_score_view)
    TextView gameScoreView;//游戏分数
    @BindView(R.id.game_top_score_view)
    TextView gameTopScoreView;//最高分
    @BindView(R.id.ad_view)
    ImageView adView;
    @BindView(R.id.game_top_score_title)
    TextView gameTopScoreTitle;//最高分标题
    @BindView(R.id.game_score_title)
    TextView gameScoreTitle;//分数标题

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private JSONArray jsonArray;
    private int score = 0;//当前分数
    private int TopScore = 0;//最高分数
    private DataChangeInterface dataChangeInterface;
    //外界可以访问的实例
    private static MainActivity mainActivity = null;

    public MainActivity() {
        mainActivity = this;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        MainActivity.mainActivity = mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // editor.putBoolean("first",true);
        //  editor.commit();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(10);//圆角半径
        gradientDrawable.setColor(getResources().getColor(R.color.gameBackground));
//        gameTitleView.setBackground(gradientDrawable);
        gameScoreView.setBackground(gradientDrawable);
        gameTopScoreView.setBackground(gradientDrawable);
        gameTopScoreTitle.setBackground(gradientDrawable);
        gameScoreTitle.setBackground(gradientDrawable);
    }


    private boolean saveData() {
        jsonArray = new JSONArray();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                try {
                    jsonArray.put(gameView.cards[j][i].getNum());
                } catch (Exception e) {
                    return false;
                }
            }
        }
        editor.putString("map", jsonArray.toString());
        editor.putInt("score", score);
        editor.commit();
        //  ScoreDBHelper.getInstance().insertSister(score);
        Log.e("sssss", jsonArray.toString() + score);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("sssss", sharedPreferences.getString("map", ""));
        String s = sharedPreferences.getString("map", "");
    }


    public void ShowScore(int score) {
        this.score = score;
        gameScoreView.setText(String.valueOf(this.score));
        gameTopScoreView.setText(String.valueOf(getSharedPreferences().getInt("Topscore", 0)));
    }

    public void AddScore(int score) {
        this.score += score;
        if (this.score > TopScore) {
            TopScore = this.score;
            saveTopScore();
        }
        ShowScore(this.score);
    }

    public void ClearScore() {
        score = 0;
    }

    private void saveTopScore() {
        editor.putInt("Topscore", TopScore);
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        saveData();
    }

    @OnClick({R.id.game_title_view, R.id.ad_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.game_title_view:
                gameTitleView.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.view_anim));
                break;
            case R.id.ad_view:
                Toast.makeText(mainActivity, "广告位招租", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
