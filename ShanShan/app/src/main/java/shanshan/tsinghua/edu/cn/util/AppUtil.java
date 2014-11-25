package shanshan.tsinghua.edu.cn.util;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import shanshan.tsinghua.edu.cn.shanshan.R;

/**
 * Created by hujiawei on 14/11/24.
 * <p/>
 * app util
 */
public class AppUtil {


    public static final String CHARACTER_SED = "sed";
    public static final String CHARACTER_DING = "ding";

    public static final String INITAPP_KEY = "init";
    public static final String CHARACTER_KEY = "character";
    public static final String LEVEL_KEY = "level";
    public static final String DONATIONS_KEY = "donations";//
    public static final String POINTS_KEY = "points";//int
    public static final String NAME_KEY = "name";

    public static final int DEFAULT_LEVEL = 0;
    public static final int DEFAULT_POINTS = 500;
    public static final float DEFAULT_DONATIONS = 0f;

    //level calculation
    public static int getLevel(float donations) {
        if (donations >= 1000.0) {
            return 10;
        } else if (donations >= 800.0) {
            return 9;
        } else if (donations >= 500.0) {
            return 8;
        } else if (donations >= 200.0) {
            return 7;
        } else if (donations >= 100.0) {
            return 6;
        } else if (donations >= 50.0) {
            return 5;
        } else if (donations >= 20.0) {
            return 4;
        } else if (donations >= 10.0) {
            return 3;
        } else if (donations >= 1.0) {
            return 2;
        } else if (donations >= 0.1) {
            return 1;
        }
        return 0;
    }
}
