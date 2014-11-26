package shanshan.tsinghua.edu.cn.shanshan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shanshan.tsinghua.edu.cn.model.Task;

/**
 * show task list
 */
public class TaskListActivity extends ActionBarActivity {

    private static final String TAG = TaskListActivity.class.getSimpleName();
    private static final String URL = "http://shanshanlaichi.sinaapp.com/listq/";

    private TextView tv_listq_load;
    private ListView lv_main_tasks;
    private SimpleTaskAdapter taskAdapter;
    private List<Task> tasks = new ArrayList<Task>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);

        tv_listq_load = (TextView) findViewById(R.id.tv_listq_load);
        lv_main_tasks = (ListView) findViewById(R.id.lv_main_tasks);

        taskAdapter = new SimpleTaskAdapter(this, R.layout.listitem_task, tasks);
        lv_main_tasks.setAdapter(taskAdapter);

        lv_main_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // task detail
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("task", tasks.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        new FetchTasksTask().execute();
    }

    @Override
    protected void onResume() {
        new FetchTasksTask().execute();
        super.onResume();
    }

    class FetchTasksTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            tv_listq_load.setVisibility(View.VISIBLE);
            lv_main_tasks.setVisibility(View.INVISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(URL).get().build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();//TODO: remove it
                return null;
            }
        }

        @Override
        protected void onPostExecute(String data) {
            if (data != null) {
                try {
                    getTaskDataFromJson(data);
                } catch (JSONException e) {
                    e.printStackTrace();//TODO: no data
                }
            } else {
                Log.e(TAG, "no data");//
            }
            tv_listq_load.setVisibility(View.INVISIBLE);
            lv_main_tasks.setVisibility(View.VISIBLE);
            super.onPostExecute(data);
        }
    }

    private void getTaskDataFromJson(String data) throws JSONException {
        tasks.clear();//TODO
        JSONArray array = new JSONArray(data);
        Task task;
        for (int i = 0; i < array.length(); i++) {
            task = new Task();
            JSONObject obj = array.getJSONObject(i);
            task.setId(obj.getString("id"));
            task.setUrl(obj.getString("url"));
            task.setName(obj.getString("name"));//
            task.setEmail(obj.getString("email"));//
            task.setOrganiser(obj.getString("organiser"));
            task.setMoney(Integer.valueOf(obj.getString("money")));
            task.setMcount(Integer.valueOf(obj.getString("mcount")));//
            task.setPcount(Integer.valueOf(obj.getString("pcount")));//
            tasks.add(task);
        }
        taskAdapter.notifyDataSetChanged();
        Log.e(TAG, "task count = " + tasks.size());//
    }

}
