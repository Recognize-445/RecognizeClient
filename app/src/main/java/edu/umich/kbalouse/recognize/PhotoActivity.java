package edu.umich.kbalouse.recognize;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class PhotoActivity extends Activity implements PhotoCallback {
    public byte[] mImage = null;
    Camera mCamera = null;
    SurfaceHolder mSurfaceHolder;
    SurfaceView mPreview;

    private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) { }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            captureImage(); // Take the photo
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mPreview = (SurfaceView) findViewById(R.id.preview);
        mPreview.getHolder().addCallback(mSurfaceHolderCallback);
    }

    public void captureImage() {
        try {
            mCamera = Camera.open(0);

            // Set the camera parameters
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            params.setJpegQuality(90);
            params.setPreviewFpsRange(30000, 30000);
            params.setPictureSize(sizes.get(1).width, sizes.get(1).height);
            params.setPreviewSize(sizes.get(1).width, sizes.get(1).height);
            mCamera.setParameters(params);

            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
            mCamera.takePicture(null, null, new PhotoHandler(this));

        } catch (Exception e) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mPreview.getHolder().removeCallback(mSurfaceHolderCallback);
                mCamera.release();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mSurfaceHolderCallback);
            mCamera.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void pictureTaken(byte[] image) {
        mImage = image;
        Bitmap photo = BitmapFactory.decodeByteArray(image, 0, image.length);
        photo = Bitmap.createScaledBitmap(photo, 750, 551, false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        RecognizeRequestor requestor = new RecognizeRequestor();
        String result = requestor.sendRequest(bytes);
        getIntent().putExtra("prediction", result);
        setResult(RESULT_OK, getIntent());
        finish();
    }
}
