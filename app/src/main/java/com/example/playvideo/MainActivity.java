package com.example.playvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    ImageView videoView;
    Handler handler = new Handler();
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (ImageView) findViewById(R.id.videoView);

        callAsynchronousTask();

    }

    public void callAsynchronousTask() {
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (Constants.internetConnectionAvailable(4)) {
                                new GetImageLink().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 1000 ms
    }

    public class GetImageLink extends AsyncTask<Void, Void, Void> {

        private String responseImageLink;

        @Override
        protected Void doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .build();

//            Log.d(TAG, "doInBackground: GetRoamIOLocation : " + Constants.getRoamIOLocation);
            Request request = new Request.Builder()
                    .url(Constants.getImage)
                    .build();

            okhttp3.Response response = null;

            try {

                response = client.newCall(request).execute();
                assert response.body() != null;
                responseImageLink = response.body().string();


            } catch (InterruptedIOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Error " + e);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            String imageLink = null;
//            Log.d(TAG, "onPostExecute: Image response: " + responseImageLink);
            try {
                if (responseImageLink != null && responseImageLink.length() > 3) {
                    JSONArray jsonArray = new JSONArray(responseImageLink);
                    JSONArray jsonArray1 = jsonArray.getJSONArray(0);
                    imageLink = valueOf(jsonArray1.get(1));

                }
                Log.d(TAG, "onPostExecute: Image link:  " + imageLink);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new DownloadImageTask().execute(imageLink);

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Void> {

        byte[] bytes;
        Bitmap picture = null;
        String imageString;

        @Override
        protected Void doInBackground(String... urls) {

            if (urls[0] != null) {
                final OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(urls[0])
                        .build();

                Response response = null;

                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert response != null;
                if (response.isSuccessful()) {
                    try {
                        imageString = Objects.requireNonNull(response.body()).string();
                        Log.d(TAG, "doInBackground: image string : " + imageString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            handleResultsFreedomVideo(imageString);
        }


        private void handleResultsFreedomVideo(String videoFrameBase64) {
            if (videoFrameBase64 != null) {
                //decode base64 string to image
                byte[] imageBytes = Base64.decode(videoFrameBase64, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                videoView.setImageBitmap(decodedImage);
                videoView.requestLayout();
                videoView.getLayoutParams().height = 480;
                videoView.getLayoutParams().width = 640;
            }
        }

    }

}