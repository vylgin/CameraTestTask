package pro.vylgin.cameraarcsinus.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pro.vylgin.cameraarcsinus.R;
import pro.vylgin.cameraarcsinus.view.CameraPreview;

public class RecordFragment extends Fragment {

    public static final String TAG = RecordFragment.class.getSimpleName();

    public static final String VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator +
            "CameraArcsinus" +
            File.separator;

    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public static RecordFragment newInstance() {
        File videoPath = new File(VIDEO_PATH);
        if (!videoPath.exists()) {
            videoPath.mkdirs();
        }

        RecordFragment recordFragment = new RecordFragment();
        Bundle args = new Bundle();
        recordFragment.setArguments(args);

        return recordFragment;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            ((ActionBarActivity)getActivity()).getSupportActionBar().hide();
        } else {
//            ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        camera = getCameraInstance();
        cameraPreview = new CameraPreview(getActivity(), cameraId, camera);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.cameraPreview);
        preview.addView(cameraPreview);

        final Button captureButton = (Button) rootView.findViewById(R.id.captureButton);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRecording) {
                            mediaRecorder.stop();
                            releaseMediaRecorder();
                            camera.lock();
                            captureButton.setText("Record");
                            isRecording = false;
                        } else {
                            if (prepareVideoRecorder()) {
                                mediaRecorder.start();
                                captureButton.setText("Stop");
                                isRecording = true;
                            } else {
                                releaseMediaRecorder();
                            }
                        }
                    }
                }
        );


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isRecording) {
            mediaRecorder.stop();
        }

        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_record, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.changeCamera) {
            Toast.makeText(getActivity(), "Change Camera", Toast.LENGTH_SHORT).show();
            releaseMediaRecorder();
            releaseCamera();

            if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            } else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }

            prepareCameraPreview();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean prepareVideoRecorder() {
        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile profile = null;

//        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
//        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
//        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
//        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
//            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        }

        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        }

        if (profile != null) {
            mediaRecorder.setProfile(profile);
        }

        mediaRecorder.setOutputFile(getOutputMediaFile().toString());
        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());
        mediaRecorder.setOrientationHint(getCameraDisplayOrientation(getActivity(), cameraId, camera));

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    public Camera getCameraInstance() {
        Camera camera;

        if (Camera.getNumberOfCameras() >= 2) {
            camera = Camera.open(cameraId);
        } else {
            camera = Camera.open();
        }

        return camera;
    }

    private void prepareCameraPreview() {
        camera = getCameraInstance();

        ViewGroup rootLayout = (ViewGroup) cameraPreview.getParent();
        cameraPreview.removeView(rootLayout);
        cameraPreview = new CameraPreview(getActivity(), cameraId, camera);
        cameraPreview.showView(rootLayout);
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(VIDEO_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");

        return mediaFile;
    }

    public static int getCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

}
