package shanshan.tsinghua.edu.cn.shanshan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Random;

import shanshan.tsinghua.edu.cn.listener.AnimateFirstDisplayListener;
import shanshan.tsinghua.edu.cn.ui.LedTextView;
import shanshan.tsinghua.edu.cn.util.AppUtil;

/**
 * Main activity
 */
public class MainActivity extends ActionBarActivity {

    private TextView tv_main_id;
    private TextView tv_main_name;
    private TextView tv_main_level;
    private ImageView iv_main_avatar;
    private LedTextView ltv_main_points;
    private LedTextView ltv_main_donations;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_main_avatar = (ImageView) findViewById(R.id.iv_main_avatar);
        tv_main_id = (TextView) findViewById(R.id.tv_main_id);
        tv_main_name = (TextView) findViewById(R.id.tv_main_name);
        tv_main_level = (TextView) findViewById(R.id.tv_main_level);
        ltv_main_points = (LedTextView) findViewById(R.id.ltv_main_points);
        ltv_main_donations = (LedTextView) findViewById(R.id.ltv_main_donations);

        initImageLoader(this);//do this only once!!!

        String ch, name;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if ((ch = getIntent().getStringExtra("character")) != null) {//第一次进入
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppUtil.CHARACTER_KEY, ch);
            editor.commit();
        } else {//不是第一次进入
            ch = preferences.getString(AppUtil.CHARACTER_KEY, AppUtil.CHARACTER_SED);
        }
        if (ch.equalsIgnoreCase(AppUtil.CHARACTER_SED)) {
            iv_main_avatar.setImageResource(R.drawable.sed1);
            name = getString(R.string.character_sed);
        } else {
            iv_main_avatar.setImageResource(R.drawable.ding1);
            name = getString(R.string.character_ding);
        }
        if (preferences.getString(AppUtil.NAME_KEY, "").equalsIgnoreCase("")) {//no name
            tv_main_name.setText(name);
        } else {
            tv_main_name.setText(preferences.getString(AppUtil.NAME_KEY, name));//
        }

        tv_main_id.setText(String.valueOf(new Random().nextInt(1000)));//random int

        setUIdata();
    }

    private void setUIdata() {
        tv_main_level.setText(String.valueOf(preferences.getInt(AppUtil.LEVEL_KEY, AppUtil.DEFAULT_LEVEL)));
        ltv_main_points.setText(String.valueOf(preferences.getInt(AppUtil.POINTS_KEY, AppUtil.DEFAULT_POINTS)));
        ltv_main_donations.setText(String.valueOf(preferences.getFloat(AppUtil.DONATIONS_KEY, AppUtil.DEFAULT_DONATIONS)));
    }

    // This configuration tuning is custom.
    //https://github.com/nostra13/Android-Universal-Image-Loader
    public static void initImageLoader(Context context) {
        //be careful here, otherwise Out of memory will occur
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))//add this to remove that error!!!
                .memoryCacheSize(10 * 1024 * 1024)//2 MB ok
                .memoryCacheSizePercentage(13) // default
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        //.writeDebugLogs() // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public void gotoProjects(View view) {
        startActivity(new Intent(this, ProjectListActivity.class));
    }

    public void gotoTasks(View view) {
        startActivity(new Intent(this, TaskListActivity.class));
    }

    @Override
    protected void onResume() {
        setUIdata();
        super.onResume();
    }

}
