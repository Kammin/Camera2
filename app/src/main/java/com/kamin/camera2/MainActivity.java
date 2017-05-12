package com.kamin.camera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int requestCodeCameraAndStorage = 100;

    private CameraManager cameramanager;
    private CameraDevice.StateCallback stateCallback;
    private String[] CameraId;
    private CameraDevice cameraDevice;
    CameraCharacteristics characteristics;
    private TextureView textureView;
    TextureView.SurfaceTextureListener textureListener;
    int widthSurface, heightSurface;
    Size imageDimension;


    @Override
    protected void onResume() {
        super.onResume();
        textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = (TextureView)findViewById(R.id.textureView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCodeCameraAndStorage);

        cameramanager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraId = cameramanager.getCameraIdList();
            Log.d(TAG,"cameras "+CameraId.length);
            for(int i=0;i<CameraId.length;i++){
                Log.d(TAG,"cameras "+i+" "+CameraId[i]);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraDevice = camera;
                Log.d(TAG, "Open camera "+cameraDevice.getId());
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                Log.d(TAG, "Disconnec camera "+cameraDevice.getId());
                try {
                    characteristics = cameramanager.getCameraCharacteristics(cameraDevice.getId());
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull CameraDevice camera,  int error) {
                Log.d(TAG, "Error camera "+cameraDevice.getId()+".  Error code "+ error);
            }
        };

        try {
            cameramanager.openCamera(CameraId[0],stateCallback,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d(TAG,"onSurfaceTextureAvailable");

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.d(TAG,"onSurfaceTextureChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.d(TAG,"onSurfaceTextureDestroyed");
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == requestCodeCameraAndStorage)
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.d(TAG, "GRANTED");
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Log.d(TAG, "DENIED");
            }


    }
}
