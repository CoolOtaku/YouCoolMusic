package com.example.youcoolmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.youcoolmusic.Adapters.SearchVideoAdapter;
import com.example.youcoolmusic.App;
import com.example.youcoolmusic.CastomToast;
import com.example.youcoolmusic.NetWork;
import com.example.youcoolmusic.R;
import com.example.youcoolmusic.Video;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameVideo;
    ImageView searchButton;
    RecyclerView listVideo;
    ProgressBar Search_progressBar;
    DownloadJSONTask task = new DownloadJSONTask();
    CastomToast toast = new CastomToast();
    TextView count_items;
    TextView textSort;
    ImageView imageSort;

    private boolean statusSort = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        nameVideo = (EditText) findViewById(R.id.nameVideo);
        searchButton = (ImageView) findViewById(R.id.searchButton);
        Search_progressBar = (ProgressBar) findViewById(R.id.Search_progressBar);
        count_items = (TextView) findViewById(R.id.count_items);
        textSort = (TextView) findViewById(R.id.textSort);
        imageSort = (ImageView) findViewById(R.id.imageSort);
        listVideo = (RecyclerView) findViewById(R.id.listVideo);
        listVideo.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(this);
        textSort.setOnClickListener(this);
        imageSort.setOnClickListener(this);

        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=Музыка&key=[YouTube_API_KEY]");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                String name = nameVideo.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(SearchActivity.this, getString(R.string.please_paint_text),Toast.LENGTH_LONG).show();
                    break;
                }
                Search_progressBar.setVisibility(View.VISIBLE);
                try {
                    if(NetWork.hasConnection(SearchActivity.this)) {
                        task = new DownloadJSONTask();
                        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=" + URLEncoder.encode(name, String.valueOf(StandardCharsets.UTF_8))
                                + "&key=[YouTube_API_KEY]");
                    }else{
                        toast.showToas(SearchActivity.this,getString(R.string.error_internet),false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.textSort:
            case R.id.imageSort:
                if(statusSort) {
                    statusSort = false;
                    Collections.sort(App.videos,new Comparator<Video>() {
                        @Override
                        public int compare(Video lhs, Video rhs) {
                            return lhs.title.compareTo(rhs.title);
                        }
                    });
                    Collections.reverse(App.videos);
                    imageSort.setImageResource(android.R.drawable.arrow_down_float);
                }else{
                    statusSort = true;
                    Collections.sort(App.videos,new Comparator<Video>() {
                        @Override
                        public int compare(Video lhs, Video rhs) {
                            return lhs.title.compareTo(rhs.title);
                        }
                    });
                    imageSort.setImageResource(android.R.drawable.arrow_up_float);
                }
                listVideo.setAdapter(new SearchVideoAdapter(SearchActivity.this));
                break;
        }
    }

    public class DownloadJSONTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConection = (HttpURLConnection) url.openConnection();
                InputStream inputSteram = urlConection.getInputStream();
                InputStreamReader inputSteramReader = new InputStreamReader(inputSteram);
                BufferedReader reader = new BufferedReader(inputSteramReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConection != null) {
                    urlConection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) {
                try {
                    Search_progressBar.setVisibility(View.GONE);
                    App.videos = new ArrayList<>();
                    JSONObject object = new JSONObject(result);
                    JSONArray items = object.getJSONArray("items");

                    for(int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        JSONObject snippet = item.getJSONObject("snippet");

                        JSONObject id = item.getJSONObject("id");
                        JSONObject img = snippet.getJSONObject("thumbnails");
                        img = img.getJSONObject("default");

                        App.videos.add(new Video(id.getString("videoId"),Jsoup.parse(snippet.getString("title")).text(),
                                img.getString("url")));
                    }
                    listVideo.setAdapter(new SearchVideoAdapter(SearchActivity.this));
                    count_items.setText(getString(R.string.count_items)+": "+App.videos.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
