package shanshan.tsinghua.edu.cn.shanshan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

import shanshan.tsinghua.edu.cn.model.Task;
import shanshan.tsinghua.edu.cn.util.AppUtil;
import shanshan.tsinghua.edu.cn.util.ToastUtil;

/**
 * shoe task detail
 */
public class TaskDetailActivity extends ActionBarActivity {

    private final String TAG = TaskDetailActivity.class.getSimpleName();

    private Task task;
    private WebView wv_detail_task;
    private boolean taskFinished = false;

    private SharedPreferences preferences;
    private static final String UPDATE_URL = "http://shanshanlaichi.sinaapp.com/updateq/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);//before setting content view
        setContentView(R.layout.activity_taskdetail);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        wv_detail_task = (WebView) findViewById(R.id.wv_detail_task);
        WebSettings webSettings = wv_detail_task.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);//
        //webSettings.setBuiltInZoomControls(true);//something wrong when returned
        webSettings.setLoadWithOverviewMode(true);
        wv_detail_task.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                taskFinished = true;//for now, just make this as task finished
                ToastUtil.showLongToast(getApplicationContext(), getApplicationContext().getString(R.string.task_finished));
                view.loadUrl(url);
                //add points by 10
                int points = preferences.getInt(AppUtil.POINTS_KEY, 0);
                points += 10;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(AppUtil.POINTS_KEY, points);
                editor.commit();
                //TODO: whether update server data? yes
                new UpdateTask().execute();
                startActivity(new Intent(TaskDetailActivity.this, MainActivity.class));
                return true;
            }
        });

        wv_detail_task.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                TaskDetailActivity.this.setProgress(progress * 1000);
            }
        });

        task = (Task) (getIntent().getSerializableExtra("task"));
        wv_detail_task.loadUrl(task.getUrl());
        setTitle(task.getName());
    }

    private void updateServerData() throws IOException {
        Log.e(TAG, task.getId() + " ");
        RequestBody body = new FormEncodingBuilder().add("qid", task.getId()).build();
        Request request = new Request.Builder().url(UPDATE_URL).post(body).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).execute();
    }

    class UpdateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                updateServerData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_detail_task.canGoBack()) {
            wv_detail_task.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up
        // to the default system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

}
