package shanshan.tsinghua.edu.cn.shanshan;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;

import shanshan.tsinghua.edu.cn.model.Project;
import shanshan.tsinghua.edu.cn.util.AppUtil;
import shanshan.tsinghua.edu.cn.util.ToastUtil;

/**
 * donation
 */
public class DonateActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = DonateActivity.class.getSimpleName();

    private Button btn_donate_pay;
    private EditText et_donate_money;
    private RadioButton rb_donate_points;
    private RadioGroup rg_donate_channel;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CHANNEL_UPMP = "upmp";
    private static final String CHANNEL_WECHAT = "wx";
    private static final String CHANNEL_ALIPAY = "alipay";
    private static final String CHANNEL_POINTS = "points";
    private static final String URL = "http://shanshanlaichi.sinaapp.com/pay/";//结尾的`/`不能没有
    private static final String UPDATE_URL = "http://shanshanlaichi.sinaapp.com/updatep/";//结尾的`/`不能没有

    private int amount;
    private int points;
    private String channel;
    private Project project;
    private String currentAmount = "";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        project = (Project) (getIntent().getSerializableExtra("project"));

        btn_donate_pay = (Button) findViewById(R.id.btn_donate_pay);
        et_donate_money = (EditText) findViewById(R.id.et_donate_money);
        rb_donate_points = (RadioButton) findViewById(R.id.rb_donate_points);
        rg_donate_channel = (RadioGroup) findViewById(R.id.rg_donate_channel);

        btn_donate_pay.setOnClickListener(this);
        rg_donate_channel.check(R.id.rb_donate_points);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        points = preferences.getInt(AppUtil.POINTS_KEY, AppUtil.DEFAULT_POINTS);
        rb_donate_points.setText(getString(R.string.text_donate_pointspay, String.valueOf(points), String.valueOf(points / 10.0)));

        et_donate_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(currentAmount)) {
                    et_donate_money.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    if (new BigDecimal(cleanString).toString().equals("0")) {
                        et_donate_money.setText(null);
                    } else {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));
                        currentAmount = formatted;
                        et_donate_money.setText(formatted);
                        et_donate_money.setSelection(formatted.length());
                    }

                    et_donate_money.addTextChangedListener(this);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "pay clicked!");

        if (view.getId() != btn_donate_pay.getId()) {
            return;
        }
        String amountText = et_donate_money.getText().toString();
        if (amountText.equals("")) {
            ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_amoutempty));
            return;
        }

        String replaceable = String.format("[%s,.]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
        String cleanString = amountText.toString().replaceAll(replaceable, "");
        amount = Integer.valueOf(new BigDecimal(cleanString).toString());
        String subject = project.getId() + " " + project.getName() + " ";
        String body = project.getId() + " " + project.getName() + " ";
        if (rg_donate_channel.getCheckedRadioButtonId() == R.id.rb_donate_points) {
            if (amount / 10 > points) {//1 point = 10 jiao, input 0.10 amout = 10
                ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_amoutpoints));
                return;
            }
            subject += "points";
        }

        if (rg_donate_channel.getCheckedRadioButtonId() == R.id.rb_donate_points) {
            channel = CHANNEL_POINTS;
        } else if (rg_donate_channel.getCheckedRadioButtonId() == R.id.rb_donate_upmp) {
            channel = CHANNEL_UPMP;
        } else if (rg_donate_channel.getCheckedRadioButtonId() == R.id.rb_donate_ali) {
            channel = CHANNEL_ALIPAY;
        } else if (rg_donate_channel.getCheckedRadioButtonId() == R.id.rb_donate_wechat) {
            channel = CHANNEL_WECHAT;
        }
        new PaymentTask().execute(new PaymentRequest(channel, amount, subject, body));
    }

    class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {

        @Override
        protected void onPreExecute() {//按键点击之后的禁用，防止重复点击
            btn_donate_pay.setOnClickListener(null);
            btn_donate_pay.setEnabled(false);
        }

        @Override
        protected String doInBackground(PaymentRequest... pr) {
            PaymentRequest paymentRequest = pr[0];
            String data = null;
            try {//向 Ping++ Server SDK请求数据
                data = postJson(paymentRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            if (channel.equalsIgnoreCase(CHANNEL_POINTS)) {//points pay
                if (resultYes(data)) {//pay yes
                    SharedPreferences.Editor editor = preferences.edit();
                    float donations = preferences.getFloat(AppUtil.DONATIONS_KEY, AppUtil.DEFAULT_DONATIONS);
                    donations += amount / 100;
                    int level = AppUtil.getLevel(donations);
                    editor.putInt(AppUtil.LEVEL_KEY, level);
                    editor.putFloat(AppUtil.DONATIONS_KEY, donations);
                    if (channel.equalsIgnoreCase(CHANNEL_POINTS)) {//points pay
                        points -= amount / 10;
                        editor.putInt(AppUtil.POINTS_KEY, points);
                    }
                    editor.commit();
                    updateUIData();
                    ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_success));
                    //finish();//when pay success!TODO: can not do finish
                    startActivity(new Intent(DonateActivity.this, MainActivity.class));
                } else {//pay no
                    ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_fail));
                }
            } else {//other pay
                Intent intent = new Intent();
                String packageName = getPackageName();
                ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                intent.setComponent(componentName);
                intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }

        }

    }

    private void updateUIData() {
        rb_donate_points.setText(getString(R.string.text_donate_pointspay, String.valueOf(points), String.valueOf(points / 10.0)));
    }

    private boolean resultYes(String data) {
        JSONObject resultobj = null;
        try {
            resultobj = new JSONObject(data);
            if (resultobj.has("result")) {
                String result = resultobj.getString("result");
                if (result.equalsIgnoreCase("yes")) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();//TODO: remove
            return false;
        }
        return false;
    }

    // waiting for other pay result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        btn_donate_pay.setOnClickListener(this);
        btn_donate_pay.setEnabled(true);

        /* 支付页面返回处理 处理返回值
         * "success" - payment succeed
         * "fail"    - payment failed
         * "cancel"  - user canceld
         * "invalid" - payment plugin not installed
         */
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                Log.e(TAG, "result=" + result);
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                if (result.equalsIgnoreCase("success")) {//success
                    SharedPreferences.Editor editor = preferences.edit();
                    float donations = preferences.getFloat(AppUtil.DONATIONS_KEY, AppUtil.DEFAULT_DONATIONS);
                    donations += amount / 100;
                    Log.e(TAG, "donations=" + donations);//donations=10.0
                    int level = AppUtil.getLevel(donations);
                    Log.e(TAG, "level=" + level);//level=3
                    editor.putInt(AppUtil.LEVEL_KEY, level);
                    editor.putFloat(AppUtil.DONATIONS_KEY, donations);
                    editor.commit();
                    //TODO: whether update server data? yes
                    //pcount, mcount
                    new UpdateTask().execute();
                    ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_success));
                    //finish();//when pay success!TODO: can not do finish
                    startActivity(new Intent(DonateActivity.this, MainActivity.class));
                } else {//fail
                    ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_fail));
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Toast.makeText(this, "User canceled", Toast.LENGTH_SHORT).show();
                ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_cancel));
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                //Toast.makeText(this, "An invalid Credential was submitted.", Toast.LENGTH_SHORT).show();
                ToastUtil.showShortToast(getApplicationContext(), getString(R.string.text_donate_invalid));
            }
        }
    }

    private void updateServerData() throws IOException {
        Log.e(TAG, project.getId() + " " + amount);
        RequestBody body = new FormEncodingBuilder()
                .add("pid", project.getId())
                .add("amount", String.valueOf(amount)).build();
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

    private String postJson(PaymentRequest paymentRequest) throws IOException {
        RequestBody body = new FormEncodingBuilder()
                .add("pid", project.getId())
                .add("channel", paymentRequest.channel)
                .add("amount", String.valueOf(paymentRequest.amount))
                .add("subject", paymentRequest.subject)
                .add("body", paymentRequest.body).build();
        Request request = new Request.Builder().url(URL).post(body).build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private class PaymentRequest {
        int amount;
        String body;
        String channel;
        String subject;

        private PaymentRequest(String channel, int amount, String subject, String body) {
            this.body = body;
            this.amount = amount;
            this.channel = channel;
            this.subject = subject;
        }
    }
}
