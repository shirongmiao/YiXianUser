package com.example.lfy.myapplication.FragmentMine.address;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.AddressBean;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


/**
 * Created by lfy on 2016/3/31.
 */
public class ManageAddress extends SwipeBackActivity {
    Manage_Adapter adapter;
    RecyclerView recyclerView;
    Button add_address;
    ImageView new_break;
    String from = null;
    String addressId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.mine_address);
        adapter = new Manage_Adapter();
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if (from.equals("order")) {
            addressId = intent.getStringExtra("addressId");
            adapter.setAddressId(addressId);
        }

        recyclerView = (RecyclerView) findViewById(R.id.set_address);
        add_address = (Button) findViewById(R.id.add_address);
        new_break = (ImageView) findViewById(R.id.new_break);
        new_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
    }

    @Override
    protected void onResume() {
        address_xUtil();
        super.onResume();
    }

    private void initView() {
        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageAddress.this, InsertAddress.class);
                startActivity(intent);
            }
        });

        adapter.setOnItemClickListenr(new Manage_Adapter.OnItemClickListenr() {

            @Override
            public void onItemClick(int position, AddressBean address) {
                if (from.equals("order")) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("address", address);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent(ManageAddress.this, UpdateAddress.class);
                    intent.putExtra("address", address);
                    startActivity(intent);
                }
            }

        });
    }

    AddressBean Default_address;
    @Override
    public void onBackPressed() {
        if (from.equals("order")) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("address", Default_address);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }else {
            super.onBackPressed();
        }
    }

    private void address_xUtil() {

        RequestParams params = new RequestParams(Variables.http_all_address);
        params.addBodyParameter("customerID", Variables.my.getCustomerID());
        x.http().post(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
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

    private void JSON(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            String Ret = jsonObject.getString("Ret");
            if (Ret.equals("1")) {
                recyclerView.setVisibility(View.VISIBLE);
                List<AddressBean> all = new ArrayList<AddressBean>();
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    AddressBean address = new AddressBean();
                    address.setId(object.getString("id"));
                    address.setCustomerID(object.getString("customerID"));
                    address.setName(object.getString("name"));
                    address.setPointname(object.getString("pointname"));
                    address.setCity(object.getString("city"));
                    address.setPhone(object.getString("phone"));
                    address.setDistrict(object.getString("district"));
                    address.setAddress(object.getString("address"));
                    address.setSex(object.getString("sex"));
                    address.setIsdefault(object.getString("Isdefault"));
                    if (object.getString("id").equals(addressId)){
                        Default_address = address;
                    }
                    all.add(address);
                }
                if (adapter.date == null) {
                    setAdapter(all);
                } else {
                    adapter.addDatas(all);
                    adapter.notifyDataSetChanged();
                }
//            } else {
//                recyclerView.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(List<AddressBean> all) {

        adapter.addDatas(all);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

}
