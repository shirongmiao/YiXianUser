package com.example.lfy.myapplication.FragmentOrder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Variables;

/**
 * Created by lfy on 2016/8/21.
 */
public class PingLun extends AppCompatActivity implements View.OnClickListener {
    ImageView pinglun_back;
    RecyclerView rv;
    MyAdapter adapter;
    RatingBar serviceStar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.pinglun);
        initView();
        setListener();
    }

    public void initView() {
        pinglun_back = (ImageView) findViewById(R.id.pinglun_back);
        rv = (RecyclerView) findViewById(R.id.pinglun_rv);
        adapter = new MyAdapter();
        serviceStar = (RatingBar) findViewById(R.id.serviceStar);
    }

    public void setListener() {
        pinglun_back.setOnClickListener(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pinglun_back:
                finish();
                break;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(PingLun.this).inflate(R.layout.pinglun_item, parent, false));


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            RatingBar pinglun_item_rating;

            public MyViewHolder(View itemView) {
                super(itemView);
                pinglun_item_rating = (RatingBar) itemView.findViewById(R.id.pinglun_item_rating);
            }
        }

    }

}
