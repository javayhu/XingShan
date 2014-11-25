package shanshan.tsinghua.edu.cn.shanshan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import shanshan.tsinghua.edu.cn.model.Task;

/**
 * Created by hujiawei on 14/11/21.
 * <p/>
 * simple task adapter
 */
public class SimpleTaskAdapter extends ArrayAdapter<Task> {

    private Context context;
    private List<Task> tasks;

    public SimpleTaskAdapter(Context context, int resource, List<Task> objects) {
        super(context, resource, objects);
        this.context = context;
        this.tasks = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderTask viewHolderTask;
        if (convertView != null && convertView.getTag() instanceof ViewHolderTask) {
            viewHolderTask = (ViewHolderTask) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_task, parent, false);
            viewHolderTask = new ViewHolderTask(convertView);
            convertView.setTag(viewHolderTask);
        }
        Task project = tasks.get(position);

        //set data
        viewHolderTask.tv_listq_name.setText(project.getName());
        viewHolderTask.tv_listq_count.setText(context.getString(R.string.text_listiemq_info,
                String.valueOf(project.getPcount())));//html does not work

        return convertView;
    }

    private static class ViewHolderTask {
        public final TextView tv_listq_name;
        public final TextView tv_listq_count;

        public ViewHolderTask(View view) {
            tv_listq_name = (TextView) view.findViewById(R.id.tv_listq_name);
            tv_listq_count = (TextView) view.findViewById(R.id.tv_listq_count);
        }
    }

}
