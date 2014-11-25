package shanshan.tsinghua.edu.cn.shanshan;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * Created by hujiawei on 14/11/21.
 * <p/>
 * simple project adapter
 */
public class SimpleProjectAdapter extends ArrayAdapter<Project> {

    private Context context;
    private List<Project> projects;

    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    public SimpleProjectAdapter(Context context, int resource, List<Project> objects) {
        super(context, resource, objects);
        this.context = context;
        this.projects = objects;

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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderProject viewHolderProject;
        if (convertView != null && convertView.getTag() instanceof ViewHolderProject) {
            viewHolderProject = (ViewHolderProject) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_project, parent, false);
            viewHolderProject = new ViewHolderProject(convertView);
            convertView.setTag(viewHolderProject);
        }
        Project project = projects.get(position);

        //set data
        viewHolderProject.tv_list_name.setText(project.getName());
        viewHolderProject.tv_list_content.setText(getSimpleProjectContent(project.getContent()));
        viewHolderProject.tv_list_count.setText(context.getString(R.string.text_listiem_info,
                String.valueOf(project.getPcount()), String.valueOf(project.getMcount())));//html does not work

        //image loader try to load image
        ImageLoader.getInstance().displayImage(project.getImgfile(), viewHolderProject.iv_list_icon, options, animateFirstListener);

        return convertView;
    }

    private String getSimpleProjectContent(String content) {
        return content.substring(0, 60) + "...";
    }

    private static class ViewHolderProject {
        public final ImageView iv_list_icon;
        public final TextView tv_list_name;
        public final TextView tv_list_content;
        public final TextView tv_list_count;

        public ViewHolderProject(View view) {
            iv_list_icon = (ImageView) view.findViewById(R.id.iv_list_icon);
            tv_list_name = (TextView) view.findViewById(R.id.tv_list_name);
            tv_list_content = (TextView) view.findViewById(R.id.tv_list_content);
            tv_list_count = (TextView) view.findViewById(R.id.tv_list_count);
        }
    }

}
