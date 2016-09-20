package com.example.lfy.myapplication.FragmentHome.scan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.CarDbBean;
import com.example.lfy.myapplication.FragmentHome.scan.zxing.camera.CameraManager;
import com.example.lfy.myapplication.FragmentHome.scan.zxing.decoding.CaptureActivityHandler;
import com.example.lfy.myapplication.FragmentHome.scan.zxing.decoding.InactivityTimer;
import com.example.lfy.myapplication.FragmentHome.scan.zxing.view.ViewfinderView;
import com.example.lfy.myapplication.FragmentMine.help.Help;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.permission.PermissionAction;
import com.example.lfy.myapplication.Util.permission.PermissionHandler;
import com.example.lfy.myapplication.Util.permission.PermissionManager;
import com.example.lfy.myapplication.Variables;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends AppCompatActivity implements SurfaceHolder.Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.home_scan);
        getPermission();
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        ImageView mButtonBack = (ImageView) findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MipcaActivityCapture.this.finish();

            }
        });
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    public void getPermission() {
        PermissionHandler permissionHandler = PermissionManager.getInstance().getPermissionHandler(this);
        permissionHandler.requestPermission(new PermissionAction() {
            @Override
            public void onGrated(String permission) {
                //权限拥有
                if (Manifest.permission.CAMERA.equals(permission)) {

                }
            }

            @Override
            public void onDenied(String permission) {
                //权限拒绝
                if (Manifest.permission.CAMERA.equals(permission)) {


                }
            }
        }, new String[]{Manifest.permission.CAMERA});
        //权限拒绝不显示对话框的回调
        permissionHandler.setOnRationaleListener(new PermissionHandler.OnRationaleListener() {
            @Override
            public void onRationale(String[] permission) {
                Toast.makeText(MipcaActivityCapture.this, "请在权限管理中允许一鲜使用相机", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MipcaActivityCapture.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            look_code(resultString);
        }
        MipcaActivityCapture.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    private void look_code(final String url) {

        RequestParams params = new RequestParams(url);
        params.addBodyParameter("point", Variables.point.getID());
        params.setCacheMaxAge(1000 * 60);
        x.http().post(params, new org.xutils.common.Callback.CacheCallback<String>() {
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
                    JSON(result, url);
                }
            }
        });

    }

    private void JSON(String result, String url) {
        try {
            JSONObject object = new JSONObject(result);
            String Ret = object.getString("Ret");
            if (Ret.equals("0")) {
                Dialog dialog = new AlertDialog.Builder(MipcaActivityCapture.this)
                        .setIcon(android.R.drawable.btn_star)
                        .setTitle("本店没有您要的商品，我们会继续努力的")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create();
                dialog.show();

            } else {
                JSONArray data = object.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    CarDbBean ever = new CarDbBean();
                    String ImageUrl = everyone.getString("Image");
                    ever.setImage("http://www.baifenxian.com/" + java.net.URLEncoder.encode(ImageUrl, "UTF-8"));
                    ever.setProductID(everyone.getString("ProductID"));//
                    ever.setPrice(everyone.getDouble("Price"));//
                }
//                Intent intent1 = new Intent(getApplication(), GoodsDetails.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("goods", sear.get(0));
//                intent1.putExtras(bundle);
//                startActivity(intent1);
            }
        } catch (JSONException e) {
            e.printStackTrace();

            Intent intent = new Intent(getApplication(), Help.class);
            intent.putExtra("url", url);
            startActivity(intent);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}