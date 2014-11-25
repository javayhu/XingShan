package shanshan.tsinghua.edu.cn.shanshan;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import shanshan.tsinghua.edu.cn.util.AppUtil;


/**
 * guide activity
 * <p/>
 * guide user to main
 */
public class GuideActivity extends Activity {

    private List<View> pages;
    private ViewPager vp_start_guide;

    private RadioButton rb_start_sed;
    private RadioButton rb_start_ding;

    private String character = AppUtil.CHARACTER_SED;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_guide);

        vp_start_guide = (ViewPager) findViewById(R.id.vp_start_guide);
        pages = new ArrayList<View>();

        View page1 = LayoutInflater.from(this).inflate(R.layout.page_guide1, null);
        rb_start_sed = (RadioButton) page1.findViewById(R.id.rb_start_sed);
        rb_start_ding = (RadioButton) page1.findViewById(R.id.rb_start_ding);

        View page2 = LayoutInflater.from(this).inflate(R.layout.page_guide2, null);
        View page3 = LayoutInflater.from(this).inflate(R.layout.page_guide3, null);

        pages.add(page1);
        pages.add(page2);
        pages.add(page3);

        vp_start_guide.setAdapter(new StartViewPagerAdapter());
        vp_start_guide.setCurrentItem(0);
//        vp_start_guide.setOnPageChangeListener(this);
    }

    public void rb_start_character(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rb_start_sed:
                if (checked) {
                    rb_start_ding.setChecked(false);
                    character = AppUtil.CHARACTER_SED;
                }
                break;
            case R.id.rb_start_ding:
                if (checked) {
                    rb_start_sed.setChecked(false);
                    character = AppUtil.CHARACTER_DING;
                }
                break;
        }
    }

    public void btn_start_enter(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("character", character);
        startActivity(intent);
        finish();
    }

    class StartViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;//IMPORTANT!!!
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pages.get(position));
            return pages.get(position);
            //return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pages.get(position));
            //super.destroyItem(container, position, object);
        }
    }

}
