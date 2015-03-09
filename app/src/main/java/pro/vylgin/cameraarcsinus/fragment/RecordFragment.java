package pro.vylgin.cameraarcsinus.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.IOException;

import pro.vylgin.cameraarcsinus.R;
import pro.vylgin.cameraarcsinus.utils.Utils;
import pro.vylgin.cameraarcsinus.view.CameraPreview;

public class RecordFragment extends Fragment {

    public static final String TAG = RecordFragment.class.getSimpleName();

    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean deviceHaveTwoCameras = Camera.getNumberOfCameras() >= 2;
    private boolean deviceHaveFlash;
    private boolean isFlashing = false;
    private ImageButton flashButton;

    public static RecordFragment newInstance() {
        RecordFragment recordFragment = new RecordFragment();
        Bundle args = new Bundle();
        recordFragment.setArguments(args);

        return recordFragment;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        deviceHaveFlash = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        ((ActionBarActivity)getActivity()).getSupportActionBar().hide();

        camera = getCameraInstance();
        cameraPreview = new CameraPreview(getActivity(), cameraId, camera, isFlashing);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.cameraPreview);
        preview.addView(cameraPreview);

        final ImageButton changeCameraImageButton = (ImageButton) rootView.findViewById(R.id.changeCameraImageButton);
        changeCameraImageButton.setVisibility(deviceHaveTwoCameras ? View.VISIBLE : View.INVISIBLE);
        changeCameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCamera();
            }
        });

        flashButton = (ImageButton) rootView.findViewById(R.id.flashImageButton);
        flashButton.setVisibility(deviceHaveFlash ? View.VISIBLE : View.INVISIBLE);
        flashButton.setBackgroundResource(isFlashing ? R.drawable.flash_on_selector : R.drawable.flash_off_selector);
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashing) {
                    isFlashing = false;
                } else {
                    isFlashing = true;
                }

                cameraPreview.restartPreviewWithParameters(isFlashing);
                flashButton.setBackgroundResource(isFlashing ? R.drawable.flash_on_selector : R.drawable.flash_off_selector);
            }
        });

        final Button recordButton = (Button) rootView.findViewById(R.id.recordButton);
        recordButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRecording) {
                            mediaRecorder.stop();
                            releaseMediaRecorder();
                            camera.lock();
                            recordButton.setText(getActivity().getString(R.string.record));
                            changeCameraImageButton.setVisibility(deviceHaveTwoCameras ? View.VISIBLE : View.INVISIBLE);
                            isRecording = false;
                        } else {
                            if (prepareVideoRecorder()) {
                                mediaRecorder.start();
                                recordButton.setText(getActivity().getString(R.string.stop_record));
                                changeCameraImageButton.setVisibility(View.INVISIBLE);
                                isRecording = true;
                            } else {
                                releaseMediaRecorder();
                            }
                        }
                    }
                }
        );

        Button cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        ImageButton galleryButton = (ImageButton) rootView.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isRecording) {
            mediaRecorder.stop();
        }

        releaseMediaRecorder();

        if (getActivity().isFinishing()) {
            releaseCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        cameraPreview.restartPreviewWithParameters(isFlashing);
    }

    @Override
    public void onDestroyView() {
        ((ActionBarActivity)getActivity()).getSupportActionBar().show();

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        releaseCamera();

        super.onDestroy();
    }

    private void changeCamera() {
        if (deviceHaveTwoCameras) {
            releaseMediaRecorder();
            releaseCamera();

            if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                flashButton.setVisibility(View.VISIBLE);
            } else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                flashButton.setVisibility(View.INVISIBLE);
            }

            prepareCameraPreview();
        }
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

        mediaRecorder.setOutputFile(Utils.getOutputMediaFile(Utils.MediaType.VIDEO).toString());
        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());
        mediaRecorder.setOrientationHint(getCameraDisplayOrientation(getActivity(), cameraId, true));

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

        if (this.camera == null) {
            if (deviceHaveTwoCameras) {
                camera = Camera.open(cameraId);
            } else {
                camera = Camera.open();
            }
        } else {
            camera = this.camera;
        }

        return camera;
    }

    private void prepareCameraPreview() {
        camera = getCameraInstance();

        ViewGroup rootLayout = (ViewGroup) cameraPreview.getParent();
        cameraPreview.removeView(rootLayout);
        cameraPreview = new CameraPreview(getActivity(), cameraId, camera, isFlashing);
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

    public static int getCameraDisplayOrientation(Activity activity, int cameraId, boolean forMediaRecorder) {
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
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && degrees == 90 && forMediaRecorder) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && !forMediaRecorder) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }

        return result;
    }

}
