package com.example.lfy.myapplication.Group;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.DividerItemDecoration;


public class GroupMine extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout sw;
    private RecyclerView rv;
    View view;

    public GroupMine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_group_mine, container, false);
        sw = (SwipeRefreshLayout) view.findViewById(R.id.groupmine_swipe_refresh);
        sw.setOnRefreshListener(this);
        rv = (RecyclerView) view.findViewById(R.id.groupmine_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new MyAdapter());
        rv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));

        return view;
    }



    @Override
    public void onRefresh() {

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.groupmine_item, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView groupmine_item_order, groupmine_item_group;

            public MyViewHolder(View itemView) {
                super(itemView);
                groupmine_item_order = (TextView) itemView.findViewById(R.id.groupmine_item_order);
                groupmine_item_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GroupOrderDetail.class);
                        startActivity(intent);
                    }
                });
                groupmine_item_group = (TextView) itemView.findViewById(R.id.groupmine_item_group);
                groupmine_item_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), GroupDetail.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
