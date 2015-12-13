package edu.umich.kbalouse.recognize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Created by kbalo on 12/4/2015.
 */
public class RecognizeRequestor {
    private String name;
    private final String host = "216.106.156.136";
    private final int port = 9090;

    public String sendRequest(ByteArrayOutputStream photoBytes) {
        try {
            MatlabRequestTask serverTask = new MatlabRequestTask();
            String name = serverTask.execute(photoBytes).get();
            return name;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "";
    }

    private class MatlabRequestTask extends AsyncTask<ByteArrayOutputStream, Integer, String> {
        private PrintWriter out;
        private BufferedReader in;
        private Socket mySocket;

        protected String doInBackground(ByteArrayOutputStream... imageBytes) {
            String answer = "Error: No internet connection.";

            try {
                mySocket = new Socket();
                mySocket.connect(new InetSocketAddress(host, port), 5000);
                out = new PrintWriter(mySocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                int size = imageBytes[0].size();
                out.println(imageBytes[0].size());

                byte[] bytes = imageBytes[0].toByteArray();
                int len = bytes.length;
                InputStream inFile = new ByteArrayInputStream(bytes);
                OutputStream outFile = mySocket.getOutputStream();

                copy(inFile, outFile, len);
                answer = in.readLine();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return answer;
        }

        protected void onPostExecute(String name) { }

        private void copy(InputStream in, OutputStream out, long bytes) throws IOException {
            byte[] buf = new byte[(int)bytes];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
    }
}
