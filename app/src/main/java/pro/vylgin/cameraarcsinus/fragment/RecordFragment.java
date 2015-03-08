package pro.vylgin.cameraarcsinus.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

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

    public static final int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_BACK;
//    private static final int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private Camera camera;
    private CameraPreview cameraPreview;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    static int countRecord = 0;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(getActivity(), camera);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.cameraPreview);
        preview.addView(cameraPreview);

        final Button captureButton = (Button) rootView.findViewById(R.id.captureButton);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRecording) {
                            // stop recording and release camera
                            mediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            camera.lock();         // take camera access back from MediaRecorder

                            // inform the user that recording has stopped
                            captureButton.setText("Capture");

                            isRecording = false;
                        } else {
                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                mediaRecorder.start();

                                // inform the user that recording has started
                                captureButton.setText("Stop");
                                isRecording = true;
                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
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


    private boolean prepareVideoRecorder() {
//        camera = getCameraInstance();
        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
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

        // Step 4: Set output file
        mediaRecorder.setOutputFile(VIDEO_PATH +
                String.valueOf(countRecord) +
                ".mp4");

        mediaRecorder.setOutputFile(getOutputMediaFile().toString());


        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        mediaRecorder.setOrientationHint(getCameraDisplayOrientation(getActivity(), CAMERA_ID, camera));

        // Step 6: Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    public static Camera getCameraInstance() {
        Camera camera = null;

        if (Camera.getNumberOfCameras() >= 2) {
            camera = Camera.open(CAMERA_ID);
        } else {
            camera = Camera.open();
        }

        return camera; // returns null if camera is unavailable
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(VIDEO_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");

        return mediaFile;
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
