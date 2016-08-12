package com.example.lfy.myapplication.FragmentMine.communication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.MineBean;
import com.example.lfy.myapplication.FragmentMine.address.widget.ChangeAddressDialog;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.CircleImageView;
import com.example.lfy.myapplication.Variables;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by lfy on 2015/12/28.
 */
public class Communication extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout head, name, mobile, email, sex, birthday;

    TextView mine_name, mine_mobile, mine_email, mine_sex,
            mine_birthday;

    CircleImageView mine_head;
    Button saver_mine;
    ImageView return_all;

    private Uri photoUri;
    private final int PIC_FROM_CAMERA = 1;
    private final int PIC_FROM＿LOCALPHOTO = 0;


    MineBean my;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.fragment_communication);
        my = Variables.my;
        //字段生日不能为空，否则报错,需要设置默认值
        if (my.getBirthday().equals("")) {
            my.setBirthday("2015-12-12");
        }

        initView();

        return_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {

        return_all = (ImageView) findViewById(R.id.return_all);
        head = (RelativeLayout) findViewById(R.id.head);
        name = (RelativeLayout) findViewById(R.id.com_name);
        mobile = (RelativeLayout) findViewById(R.id.mobile);
        email = (RelativeLayout) findViewById(R.id.email);
        sex = (RelativeLayout) findViewById(R.id.sex);
        birthday = (RelativeLayout) findViewById(R.id.birthday);
        mine_head = (CircleImageView) findViewById(R.id.mine_head);

        mine_name = (TextView) findViewById(R.id.mine_name);
        mine_mobile = (TextView) findViewById(R.id.mine_mobile);
        mine_email = (TextView) findViewById(R.id.mine_email);
        mine_sex = (TextView) findViewById(R.id.mine_sex);
        mine_birthday = (TextView) findViewById(R.id.mine_birthday);
        saver_mine = (Button) findViewById(R.id.saver_mine);

        mine_name.setText(my.getCustomerName());
        mine_mobile.setText(my.getPhoneNameber());
        if (my.getEmailAddress().equals("null")) {
            mine_email.setText("请设置");
        } else {
            mine_email.setText(my.getEmailAddress());
        }
        if (my.getSex().equals("1")) {
            mine_sex.setText("男");
        } else {
            mine_sex.setText("女");
        }

        Log.d("我是生日", my.getBirthday());
        if (my.getBirthday().length() > 10) {
            mine_birthday.setText(my.getBirthday().substring(0, 10));
        } else {
            mine_birthday.setText(my.getBirthday());
        }

        head.setOnClickListener(this);
        name.setOnClickListener(this);
        birthday.setOnClickListener(this);
        mobile.setOnClickListener(this);
        email.setOnClickListener(this);
        sex.setOnClickListener(this);
        saver_mine.setOnClickListener(this);

        if (Variables.my.getImage().equals("none")) {
            mine_head.setImageResource(R.mipmap.mine_default_photo);
        } else {
            try {
                String str = Variables.my.getImage();
                str = str.substring(str.indexOf(",") + 1);
                Bitmap bitmap = Variables.base64ToBitmap(str);
                mine_head.setImageBitmap(bitmap);
            } catch (Exception e) {
                mine_head.setImageResource(R.mipmap.mine_default_photo);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.birthday:
                ChangeBirthDialog mChangeBirthDialog = new ChangeBirthDialog(Communication.this);
                int a = Integer.parseInt(my.getBirthday().substring(0, 4));//2015-03-03
                int b = Integer.parseInt(my.getBirthday().substring(5, 7));
                int c = Integer.parseInt(my.getBirthday().substring(8, 10));
                mChangeBirthDialog.setDate(a, b, c);
                mChangeBirthDialog.show();
                mChangeBirthDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {

                    @Override
                    public void onClick(String year, String month, String day) {
                        String a = month;
                        String b = day;
                        if (month.length() < 2) {
                            a = "0" + month;
                        }
                        if (day.length() < 2) {
                            b = "0" + day;
                        }
                        my.setBirthday(year + "-" + a + "-" + b);
                        mine_birthday.setText(my.getBirthday());
                    }
                });
                break;
            case R.id.head:
                new android.support.v7.app.AlertDialog.Builder(Communication.this)
                        .setMessage("照片来源")
                        .setPositiveButton("拍摄", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doHandlerPhoto(PIC_FROM_CAMERA);// 用户点击了从照相机获取
                            }
                        })
                        .setNegativeButton("本地", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doHandlerPhoto(PIC_FROM＿LOCALPHOTO);//从本地获取
                            }
                        })
                        .setNeutralButton("取消", null).create().show();
                break;
            case R.id.com_name:
                final EditText editText_name = new EditText(this);
                editText_name.setBackgroundResource(R.drawable.round_edit);
                editText_name.setPadding(30, 5, 0, 5);
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setMessage("请输入您的姓名")
                        .setView(editText_name)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                my.setCustomerName(editText_name.getText().toString());
                                mine_name.setText(my.getCustomerName());
                            }
                        })
                        .setNegativeButton("取消", null).show();

                break;
            case R.id.mobile:
                final EditText editText_mobile = new EditText(this);
                editText_mobile.setBackgroundResource(R.drawable.round_edit);
                editText_mobile.setPadding(30, 20, 0, 30);
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setMessage("请输入您的电话")
                        .setView(editText_mobile)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                my.setPhoneNameber(editText_mobile.getText().toString());
                                mine_mobile.setText(my.getPhoneNameber());
                            }
                        })
                        .setNegativeButton("取消", null).show();

                break;
            case R.id.email:
                final EditText editText_email = new EditText(this);
                editText_email.setBackgroundResource(R.drawable.round_edit);
                editText_email.setPadding(30, 20, 0, 30);
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setMessage("请输入您的邮箱")
                        .setView(editText_email)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                my.setEmailAddress(editText_email.getText().toString());
                                mine_email.setText(my.getEmailAddress());
                            }
                        })
                        .setNegativeButton("取消", null).show();
                break;
            case R.id.sex:
                int aa = Integer.parseInt(my.getSex());
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("单选框").setSingleChoiceItems(
                        new String[]{"女", "男"}, aa,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    my.setSex("0");
                                    mine_sex.setText("女");
                                } else {
                                    my.setSex("1");
                                    mine_sex.setText("男");
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();

                break;
            case R.id.saver_mine:
                saver();
                break;

        }

    }

    private void doHandlerPhoto(int type) {
        try {
            // 保存裁剪后的图片文件
            File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/upload");
            if (!pictureFileDir.exists()) {
                pictureFileDir.mkdirs();
            }
            File picFile = new File(pictureFileDir, "upload.jpeg");
            if (!picFile.exists()) {
                try {
                    picFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            photoUri = Uri.fromFile(picFile);
            if (photoUri != null) {
                Bitmap bitmap = decodeUriAsBitmap(photoUri);
                mine_head.setImageBitmap(bitmap);
            }
            if (type == PIC_FROM＿LOCALPHOTO) {
                Intent intent = getCropImageIntent();
                startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
            }

        } catch (Exception e) {
            Log.i("HandlerPicError", "处理图片出现错误");
        }
    }

    /**
     * 调用图片剪辑程序
     */
    public Intent getCropImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        setIntentParams(intent);
        return intent;
    }

    /**
     * 启动裁剪
     */
    private void cropImageUriByTakePhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        setIntentParams(intent);
        startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
    }

    /**
     * 设置公用参数
     */
    private void setIntentParams(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PIC_FROM_CAMERA: // 拍照
                try {
                    cropImageUriByTakePhoto();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PIC_FROM＿LOCALPHOTO:
                try {
                    if (photoUri != null) {
                        Bitmap bitmap = decodeUriAsBitmap(photoUri);
                        mine_head.setImageBitmap(bitmap);
                        xutil(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private void xutil(Bitmap bitmap) {
        final String image = "data:image/jpeg;base64," + Variables.bitmapToBase64(bitmap);

        RequestParams params = new RequestParams(Variables.Mine_Head);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.addBodyParameter("image", image);
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
                    Variables.my.setImage(image);
                    if (Variables.my.getImage().equals("")) {
                        mine_head.setImageResource(R.mipmap.mine_default_photo);
                    } else {
                        String str = Variables.my.getImage().substring(22);
                        Bitmap bitmap = Variables.base64ToBitmap(str);
                        mine_head.setImageBitmap(bitmap);
                    }
                }
            }
        });
    }


    private void saver() {

        RequestParams params = new RequestParams(Variables.update_mine);
        params.addBodyParameter("userid", my.getCustomerID());
        params.addBodyParameter("CustomerName", my.getCustomerName());
        params.addBodyParameter("Sex", my.getSex());
        params.addBodyParameter("Birthday", my.getBirthday());
        params.addBodyParameter("EmailAddress", my.getEmailAddress());
        params.addBodyParameter("AddressP", "");
        params.addBodyParameter("AddressC", "");
        params.addBodyParameter("AddressD", "");
        params.addBodyParameter("AddressSQ", "");
        params.addBodyParameter("AddressZ", "");
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
                    Dialog dialog = new android.support.v7.app.AlertDialog.Builder(Communication.this)
                            .setMessage("数据更新成功！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton("取消", null).create();
                    dialog.show();
                    Variables.my = my;
                }
            }
        });
    }


}
