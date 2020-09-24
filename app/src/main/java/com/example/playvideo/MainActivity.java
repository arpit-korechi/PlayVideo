package com.example.playvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    ImageView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (ImageView) findViewById(R.id.videoView);

        new GetImageLink().execute();

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

        @Override
        protected Void doInBackground(String... urls) {

            if (urls[0]!=null) {
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
                        assert response.body() != null;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IOUtils.copy(response.body().byteStream(), baos);
                        bytes = baos.toByteArray();
                        picture = BitmapFactory.decodeStream(response.body().byteStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

           /* ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                URL url = new URL(urls[0]);
                byte[] chunk = new byte[4096];
                int bytesRead;
                InputStream stream = url.openStream();

                while ((bytesRead = stream.read(chunk)) > 0) {
                    outputStream.write(chunk, 0, bytesRead);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            bytes = outputStream.toByteArray();*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bytes != null) {
                Log.d(TAG, "onPostExecute: bytes: " + bytes.length);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                videoView.setImageBitmap(bitmap);
            }

        }

    }

}