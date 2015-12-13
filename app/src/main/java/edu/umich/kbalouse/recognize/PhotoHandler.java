package edu.umich.kbalouse.recognize;

import android.hardware.Camera;
import android.os.AsyncTask;



public class PhotoHandler implements Camera.PictureCallback {


    private PhotoCallback photoCallback;

    public PhotoHandler(PhotoCallback photoCallback) {
        super();
        this.photoCallback = photoCallback;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        new ProcessCapturedImage().execute(data);
    }

    private class ProcessCapturedImage extends AsyncTask<byte[], Void, byte[]> {

        @Override
        protected byte[] doInBackground(byte[]... params) {

            if (null == params || null == params[0])
                return null;

            return params[0];

        }

        @Override
        protected void onPostExecute(byte[] params) {
            photoCallback.pictureTaken(params);
        }
    }

}