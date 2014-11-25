package shanshan.tsinghua.edu.cn.shanshan;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shanshan.tsinghua.edu.cn.model.Project;


public class ProjectListActivity extends ActionBarActivity {

    private static final String TAG = ProjectListActivity.class.getSimpleName();
    private static final String URL = "http://shanshanlaichi.sinaapp.com/listp/";

    private TextView tv_list_load;
    private ListView lv_main_projects;
    private SimpleProjectAdapter projectAdapter;
    private List<Project> projects = new ArrayList<Project>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectlist);

        tv_list_load = (TextView) findViewById(R.id.tv_list_load);
        lv_main_projects = (ListView) findViewById(R.id.lv_main_projects);

        projectAdapter = new SimpleProjectAdapter(this, R.layout.listitem_project, projects);
        lv_main_projects.setAdapter(projectAdapter);
        lv_main_projects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // project detail
                Intent intent = new Intent(ProjectListActivity.this, ProjectDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("project", projects.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        new FetchProjectsTask().execute();
    }

    @Override
    protected void onResume() {
        new FetchProjectsTask().execute();
        super.onResume();
    }

    class FetchProjectsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            tv_list_load.setVisibility(View.VISIBLE);
            lv_main_projects.setVisibility(View.INVISIBLE);
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
                    getProjectDataFromJson(data);
                } catch (JSONException e) {
                    e.printStackTrace();//TODO: no data
                }
            } else {
                Log.e(TAG, "no data");//6
            }
            tv_list_load.setVisibility(View.INVISIBLE);
            lv_main_projects.setVisibility(View.VISIBLE);
            super.onPostExecute(data);
        }
    }

    private void getProjectDataFromJson(String data) throws JSONException {
        projects.clear();//TODO
        JSONArray array = new JSONArray(data);
        Project project;
        for (int i = 0; i < array.length(); i++) {
            project = new Project();
            JSONObject obj = array.getJSONObject(i);
            project.setId(obj.getString("id"));
            project.setName(obj.getString("name"));//
            project.setContent(obj.getString("content"));
            project.setEmail(obj.getString("email"));//
            project.setImgfile(obj.getString("imgfile"));
            project.setMcount(Float.valueOf(obj.getString("mcount")));//
            project.setOrganiser(obj.getString("organiser"));
            project.setPcount(Integer.valueOf(obj.getString("pcount")));//
            project.setTarget(Float.valueOf(obj.getString("target")));
            projects.add(project);
        }
        projectAdapter.notifyDataSetChanged();
        Log.e(TAG, "project count = " + projects.size());//6
    }

}
