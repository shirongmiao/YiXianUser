package com.example.lfy.myapplication.splash;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lfy.myapplication.FragmentHome.ChoosePoint.MyLocation;
import com.example.lfy.myapplication.R;

/**
 * Created by lfy on 2016/7/16.
 */
public class SplashFragment extends Fragment {

    Button button;
    String num = null;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            num = arguments.getString("num");
        }
        if (num.equals("0")) {
            view = inflater.inflate(R.layout.splash_fragment0, container, false);
        } else if (num.equals("1")) {
            view = inflater.inflate(R.layout.splash_fragment1, container, false);
        } else if (num.equals("2")) {
            view = inflater.inflate(R.layout.splash_fragment2, container, false);
        } else if (num.equals("3")) {
            view = inflater.inflate(R.layout.splash_fragment3, container, false);
        } else {
            view = inflater.inflate(R.layout.splash_fragment4, container, false);
            button = (Button) view.findViewById(R.id.start);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable(getActivity())) {
                        Intent intent = new Intent(getActivity(), MyLocation.class); // 调用父类的intent方法
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Intent intent = new Intent(getActivity(), Wifi.class);
                        intent.putExtra("from", "first");
                        startActivity(intent);
                        getActivity().finish(); //
                    }
                }
            });
        }
        return view;
    }

    public boolean isNetworkAvailable(Context context) {
        boolean flag = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            //去进行判断网络是否连接
            if (cm.getActiveNetworkInfo() != null) {
                flag = cm.getActiveNetworkInfo().isAvailable();
            }
            return flag;
        }
        return false;
    }

    public static SplashFragment newInstance(int num) {
        Bundle bundle = new Bundle();
        bundle.putString("num", num + "");
        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
