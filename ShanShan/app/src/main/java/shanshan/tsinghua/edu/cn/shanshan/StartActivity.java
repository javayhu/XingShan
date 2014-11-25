package shanshan.tsinghua.edu.cn.shanshan;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import shanshan.tsinghua.edu.cn.util.AppUtil;


/**
 * start activity
 * <p/>
 * just a splash
 */
public class StartActivity extends Activity {

    private static final int GO_MAIN = 1000;
    private static final int GO_GUIDE = 1001;
    private static final long SPLASH_DELAY_MILLIS = 2000;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_MAIN:
                    gotoMain();
                    break;
                case GO_GUIDE:
                    gotoGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_start);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(AppUtil.INITAPP_KEY, true)) {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(AppUtil.INITAPP_KEY, false);//not now!!!
            editor.putInt(AppUtil.LEVEL_KEY, AppUtil.DEFAULT_LEVEL);//
            editor.putInt(AppUtil.POINTS_KEY, AppUtil.DEFAULT_POINTS);//TODO: default is 100!!!
            editor.putFloat(AppUtil.DONATIONS_KEY, AppUtil.DEFAULT_DONATIONS);//
            editor.commit();// commit modifications
        } else {
            mHandler.sendEmptyMessageDelayed(GO_MAIN, SPLASH_DELAY_MILLIS);
        }
    }

    private void gotoGuide() {
        startActivity(new Intent(this, GuideActivity.class));
        finish();
    }

    private void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
