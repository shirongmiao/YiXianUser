package com.example.lfy.myapplication.Util.pay_dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.FragmentMine.balance.ForgetBalancePwd;
import com.example.lfy.myapplication.R;

import java.util.ArrayList;

/**
 * Dialog 显示视图
 *
 * @author LanYan
 */
@SuppressLint("InflateParams")
public class PayPasswordView implements View.OnClickListener {


    private TextView del;
    private TextView zero;
    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private TextView six;
    private TextView seven;
    private TextView eight;
    private TextView nine;

    private ImageView cancel;
    private TextView forget;
    private TextView box1;
    private TextView box2;
    private TextView box3;
    private TextView box4;
    private TextView box5;
    private TextView box6;
    private TextView title;
    private TextView content;

    private ArrayList<String> mList = new ArrayList<String>();
    private View mView;
    private OnPayListener listener;
    private Context mContext;

    public PayPasswordView(String monney, Context mContext, OnPayListener listener) {
        getDecorView(monney, mContext, listener);
    }

    public static PayPasswordView getInstance(String monney, Context mContext, OnPayListener listener) {
        return new PayPasswordView(monney, mContext, listener);
    }

    public void getDecorView(String monney, Context mContext, OnPayListener listener) {
        this.listener = listener;
        this.mContext = mContext;
        mView = LayoutInflater.from(mContext).inflate(R.layout.paypassword, null);
        del = (TextView) mView.findViewById(R.id.pay_keyboard_del);
        zero = (TextView) mView.findViewById(R.id.pay_keyboard_zero);
        one = (TextView) mView.findViewById(R.id.pay_keyboard_one);
        two = (TextView) mView.findViewById(R.id.pay_keyboard_two);
        three = (TextView) mView.findViewById(R.id.pay_keyboard_three);
        four = (TextView) mView.findViewById(R.id.pay_keyboard_four);
        five = (TextView) mView.findViewById(R.id.pay_keyboard_five);
        six = (TextView) mView.findViewById(R.id.pay_keyboard_six);
        seven = (TextView) mView.findViewById(R.id.pay_keyboard_seven);
        eight = (TextView) mView.findViewById(R.id.pay_keyboard_eight);
        nine = (TextView) mView.findViewById(R.id.pay_keyboard_nine);

        cancel = (ImageView) mView.findViewById(R.id.pay_cancel);
        forget = (TextView) mView.findViewById(R.id.pay_forget);
        box1 = (TextView) mView.findViewById(R.id.pay_box1);
        box2 = (TextView) mView.findViewById(R.id.pay_box2);
        box3 = (TextView) mView.findViewById(R.id.pay_box3);
        box4 = (TextView) mView.findViewById(R.id.pay_box4);
        box5 = (TextView) mView.findViewById(R.id.pay_box5);
        box6 = (TextView) mView.findViewById(R.id.pay_box6);
        title = (TextView) mView.findViewById(R.id.pay_title);
        content = (TextView) mView.findViewById(R.id.pay_content);

        content.setText(monney + "");


        del.setOnClickListener(this);
        zero.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        cancel.setOnClickListener(this);
        forget.setOnClickListener(this);

        del.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                parseActionType(KeyboardEnum.longdel);
                return false;
            }
        });
        content.setText("消费金额：" + monney + "元");
    }

    private void parseActionType(KeyboardEnum type) {
        if (type.getType() == KeyboardEnum.ActionEnum.add) {
            if (mList.size() < 6) {
                mList.add(type.getValue());
                updateUi();
            }
        } else if (type.getType() == KeyboardEnum.ActionEnum.delete) {
            if (mList.size() > 0) {
                mList.remove(mList.get(mList.size() - 1));
                updateUi();
            }
        } else if (type.getType() == KeyboardEnum.ActionEnum.cancel) {
            listener.onCancelPay();
        } else if (type.getType() == KeyboardEnum.ActionEnum.sure) {
            Toast.makeText(mContext, "支付密码必须6位", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, ForgetBalancePwd.class);
            mContext.startActivity(intent);

        } else if (type.getType() == KeyboardEnum.ActionEnum.longClick) {
            mList.clear();
            updateUi();
        }

    }

    private void updateUi() {
        if (mList.size() == 0) {
            box1.setText("");
            box2.setText("");
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 1) {
            box1.setText(mList.get(0));
            box2.setText("");
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 2) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText("");
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 3) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText("");
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 4) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText(mList.get(3));
            box5.setText("");
            box6.setText("");
        } else if (mList.size() == 5) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText(mList.get(3));
            box5.setText(mList.get(4));
            box6.setText("");
        } else if (mList.size() == 6) {
            box1.setText(mList.get(0));
            box2.setText(mList.get(1));
            box3.setText(mList.get(2));
            box4.setText(mList.get(3));
            box5.setText(mList.get(4));
            box6.setText(mList.get(5));

            String payValue = "";
            for (int i = 0; i < mList.size(); i++) {
                payValue += mList.get(i);
            }
            listener.onSurePay(payValue);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_keyboard_del:
                parseActionType(KeyboardEnum.del);
                break;
            case R.id.pay_keyboard_zero:
                parseActionType(KeyboardEnum.zero);
                break;
            case R.id.pay_keyboard_one:
                parseActionType(KeyboardEnum.one);
                break;
            case R.id.pay_keyboard_two:
                parseActionType(KeyboardEnum.two);
                break;
            case R.id.pay_keyboard_three:
                parseActionType(KeyboardEnum.three);
                break;
            case R.id.pay_keyboard_four:
                parseActionType(KeyboardEnum.four);
                break;
            case R.id.pay_keyboard_five:
                parseActionType(KeyboardEnum.five);
                break;
            case R.id.pay_keyboard_six:
                parseActionType(KeyboardEnum.six);
                break;
            case R.id.pay_keyboard_seven:
                parseActionType(KeyboardEnum.seven);
                break;
            case R.id.pay_keyboard_eight:
                parseActionType(KeyboardEnum.eight);
                break;
            case R.id.pay_keyboard_nine:
                parseActionType(KeyboardEnum.nine);
                break;
            case R.id.pay_cancel:
                parseActionType(KeyboardEnum.cancel);
                break;
            case R.id.pay_forget:
                parseActionType(KeyboardEnum.sure);
                break;
            case R.id.pay_box1:
                break;
            case R.id.pay_box2:
                break;
            case R.id.pay_box3:
                break;
            case R.id.pay_box4:
                break;
            case R.id.pay_box5:
                break;
            case R.id.pay_box6:
                break;
            case R.id.pay_title:
                break;
            case R.id.pay_content:
                break;
        }
    }

    public interface OnPayListener {
        void onCancelPay();

        void onSurePay(String password);
    }

    public View getView() {
        return mView;
    }
}
