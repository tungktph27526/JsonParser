package com.envy.jsonparser;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.envy.jsonparser.adapters.AlbumAdapter;
import com.envy.jsonparser.databinding.ActivityMainBinding;
import com.envy.jsonparser.models.AlbumModel;
import com.envy.jsonparser.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GetAlbumData getAlbumData = new GetAlbumData();
        getAlbumData.execute();
    }

    private void setView(List<AlbumModel> list) {
        AlbumAdapter albumAdapter = new AlbumAdapter(this, list);
        binding.rcvData.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvData.setAdapter(albumAdapter);
        binding.tvAlbum.setVisibility(View.VISIBLE);
        binding.rcvData.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }

    @SuppressLint("StaticFieldLeak")
    private class GetAlbumData extends AsyncTask<String, Void, String> {
        String result;
        String readLine;

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = Constants.SERVER;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuffer = new StringBuilder();
                while ((readLine = bufferedReader.readLine()) != null) {
                    stringBuffer.append(readLine);
                }
                Log.d(TAG, "doInBackground: " + stringBuffer.getClass().getSimpleName());
                result = stringBuffer.toString();
            } catch (IOException e) {
                Log.d("Log", "doInBackground: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            List<AlbumModel> albumList = new ArrayList<>();
            super.onPostExecute(result);
            if (result == null) {

                return;
            }
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    AlbumModel album = new AlbumModel();

                    album.setId(object.getString("id"));
                    album.setAlbumId(object.getString("albumId"));
                    album.setTitle(object.getString("title"));
                    album.setUrl(object.getString("url"));
                    album.setThumbnailUrl(object.getString("thumbnailUrl"));

                    albumList.add(album);

//                Log.d(TAG, "onPostExecute: " + albumList);
                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            setView(albumList);
        }
    }
}