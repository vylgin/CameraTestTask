package pro.vylgin.cameraarcsinus.view;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;

import pro.vylgin.cameraarcsinus.fragment.RecordFragment;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private Context context;
    private SurfaceHolder holder;
    private Camera camera;
    private int cameraId;

    public CameraPreview(Context context, int cameraId, Camera camera) {
        super(context);
        this.context = context;
        this.camera = camera;
        this.cameraId = cameraId;

        holder = getHolder();
        holder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.holder.getSurface() == null){
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception ignored){
        }

        int rotation = RecordFragment.getCameraDisplayOrientation((Activity) context, cameraId, camera);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setRecordingHint(true);
        parameters.setRotation(rotation);
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(rotation);

        try {
            camera.setPreviewDisplay(this.holder);
            camera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void removeView(ViewGroup rootLayout) {
        surfaceDestroyed(holder);
        getHolder().removeCallback(this);
        rootLayout.removeView(this);
    }

    public void showView(ViewGroup rootLayout) {
        holder.addCallback(this);
        rootLayout.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
