package com.example.lfy.myapplication.FragmentHome.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.UserInfo;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by lfy on 2016/1/7.
 */
public class Search_edit extends SwipeBackActivity {

    UserInfo userInfo;

    GridView search_hot;
    GridView search_history;
    String[] hot_text;
    String[] history_text;
    TextView search_sure;
    EditText search_edit;
    TextView search_clean;
    TextView history_in;
    ImageView imageView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.home_search_edit);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userInfo = new UserInfo(this);

        search_sure = (TextView) findViewById(R.id.search_sure);
        search_edit = (EditText) findViewById(R.id.search_edit);
        search_hot = (GridView) findViewById(R.id.search_hot);
        search_history = (GridView) findViewById(R.id.search_history);
        search_clean = (TextView) findViewById(R.id.search_clean);
        history_in = (TextView) findViewById(R.id.history_in);

        search_hot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplication(), Search.class);
                intent.putExtra("content", hot_text[position]);
                intent.putExtra("point", Variables.point.getID());
                intent.putExtra("title", hot_text[position]);
                startActivity(intent);

                setHistory(hot_text[position]);
            }
        });
        search_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplication(), Search.class);
                intent.putExtra("content", history_text[position]);
                intent.putExtra("point", Variables.point.getID());
                intent.putExtra("title", history_text[position]);
                startActivity(intent);
            }
        });

        search_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.setUserInfo("search_text", "");
                history_in.setVisibility(View.INVISIBLE);
                search_history.setVisibility(View.INVISIBLE);
                search_clean.setVisibility(View.INVISIBLE);
            }
        });

        search_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit = search_edit.getText().toString();
                if (edit.equals("")) {
                    Toast.makeText(Search_edit.this, "请输入搜索内容", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplication(), Search.class);
                    intent.putExtra("content", edit);
                    intent.putExtra("point", Variables.point.getID());
                    intent.putExtra("title", "搜索");
                    startActivity(intent);

                    setHistory(edit);
                }
            }
        });

        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))

                {
                    String edit = search_edit.getText().toString();
                    if (edit.equals("")) {
                        Toast.makeText(Search_edit.this, "请输入搜索内容", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getApplication(), Search.class);
                        intent.putExtra("content", edit);
                        intent.putExtra("point", Variables.point.getID());
                        intent.putExtra("title", "搜索");
                        startActivity(intent);

                        setHistory(edit);
                    }

                    return true;

                }

                return false;

            }

        });


        search_xUtil();
    }

    private void setHistory(String edit) {

        String string = userInfo.getStringInfo("search_text");
        Log.d("搜索历史", string);
        if (string.equals("")) {
            userInfo.setUserInfo("search_text", edit);
        } else {
            if (!string.contains(edit)) {
                int nu = method(string, "-");
                Log.d("我是 - 出现的次数", nu + "");
                if (nu > 7) {
                    String st = edit + "-" + string;
                    String text = st.substring(0, st.lastIndexOf("-"));
                    Log.d("大于7", text);
                    userInfo.setUserInfo("search_text", text);

                } else {
                    String text = edit + "-" + string;
                    userInfo.setUserInfo("search_text", text);
                }
            }
        }
    }

    //判断某个字符出现的次数
    public static int method(String str, String key) {

        int count = 0, index = 0;
        while (str.indexOf(key) != -1) {
            str = str.substring(index + key.length());
            count++;
            index = str.indexOf(key);
        }
        return count == 0 ? -1 : count;
    }

    @Override
    protected void onResume() {
        select_history();
        super.onResume();
    }

    private void select_history() {
        String str = userInfo.getStringInfo("search_text");
        if (!str.equals("")) {
            search_clean.setVisibility(View.VISIBLE);
            history_in.setVisibility(View.VISIBLE);
            search_history.setVisibility(View.VISIBLE);

            history_text = str.split("-");
            Search_edit_adapter adapter = new Search_edit_adapter(history_text, getApplication());
            search_history.setAdapter(adapter);
        }
    }

    private void search_xUtil() {

        RequestParams params = new RequestParams(Variables.http_hot_search);
        params.setCacheMaxAge(1000 * 60);
        Callback.Cancelable cancelable
                // 使用CacheCallback, xUtils将为该请求缓存数据.
                = x.http().get(params, new Callback.CacheCallback<String>() {

            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                // 得到缓存数据, 缓存过期后不会进入这个方法.
                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
                //
                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
                //
                // * 如果信任该缓存返回 true, 将不再请求网络;
                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
                //
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    JSON(result);
                }
            }

        });
    }

    private void JSON(String result) {
        try {
            JSONObject object = new JSONObject(result);
            JSONArray data = object.getJSONArray("Data");

            hot_text = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                JSONObject everyone = data.getJSONObject(i);
                String text = everyone.getString("text");
                hot_text[i] = text;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Search_edit_adapter adapter = new Search_edit_adapter(hot_text, getApplication());
        search_hot.setAdapter(adapter);
    }
}
