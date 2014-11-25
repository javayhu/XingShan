package shanshan.tsinghua.edu.cn.shanshan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import shanshan.tsinghua.edu.cn.listener.AnimateFirstDisplayListener;
import shanshan.tsinghua.edu.cn.model.Project;
import shanshan.tsinghua.edu.cn.util.AppUtil;


public class ProjectDetailActivity extends ActionBarActivity {

    private TextView tv_detail_name;
    private TextView tv_detail_count;
    private TextView tv_detail_content;
    private ImageView iv_detail_imgfile;

    private Project project;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectdetail);

        iv_detail_imgfile = (ImageView) findViewById(R.id.iv_detail_imgfile);
        tv_detail_name = (TextView) findViewById(R.id.tv_detail_name);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);
        tv_detail_count = (TextView) findViewById(R.id.tv_detail_count);

        project = (Project) (getIntent().getSerializableExtra("project"));
        tv_detail_name.setText(project.getName());
        tv_detail_content.setText(project.getContent());
        tv_detail_count.setText(getString(R.string.text_detail_info,
                String.valueOf(project.getPcount()), String.valueOf(project.getMcount())));

        //title name
        setTitle(project.getName());

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.charity)
                .showImageForEmptyUri(R.drawable.charity)
                .showImageOnFail(R.drawable.charity)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .build();

        animateFirstListener = new AnimateFirstDisplayListener();

        //image loader try to load image
        ImageLoader.getInstance().displayImage(project.getImgfile(), iv_detail_imgfile, options, animateFirstListener);
    }

    public void btn_detail_donate(View view) {
        Intent intent = new Intent(this, DonateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("project", project);
        intent.putExtras(bundle);
        startActivity(intent);
        //finish();//TODO: for now!!!
    }
}
