package com.example.youcoolmusic.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.youcoolmusic.Adapters.SearchVideoAdapter;
import com.example.youcoolmusic.App;
import com.example.youcoolmusic.CastomToast;
import com.example.youcoolmusic.DataBase;
import com.example.youcoolmusic.NetWork;
import com.example.youcoolmusic.R;
import com.example.youcoolmusic.Video;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    ImageView logo;
    ImageView searchButton;
    TextView textSort;
    TextView count_items;
    ImageView imageSort;
    RecyclerView listVideo;
    private LinearLayoutManager mLinearLayoutManager;
    DrawerLayout drawer;
    private static CastomToast toast = new CastomToast();
    private NotificationManagerCompat notificationManager;
    private DataBase dataBase;
    private SQLiteDatabase sql;
    private Cursor cursor;

    private String request = "SELECT * FROM " + dataBase.TABLE_PLAY_LIST;
    private boolean statusSort = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissionsOverlay();
        checkPermissionsFiles(MainActivity.this);
        notificationManager = NotificationManagerCompat.from(this);

        drawer = findViewById(R.id.drawer_layout);
        final NavigationView menu = (NavigationView) findViewById(R.id.nav_view);
        listVideo = (RecyclerView) findViewById(R.id.listVideo);
        mLinearLayoutManager = new LinearLayoutManager(this);
        listVideo.setLayoutManager( mLinearLayoutManager);
        logo = (ImageView)findViewById(R.id.logo);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        textSort = (TextView) findViewById(R.id.textSort);
        count_items = (TextView) findViewById(R.id.count_items);
        imageSort = (ImageView) findViewById(R.id.imageSort);

        dataBase = new DataBase(MainActivity.this);
        sql = dataBase.getWritableDatabase();
        showPlay_List();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        App.width = size.x;
        App.height = size.y;

        logo.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        textSort.setOnClickListener(this);
        imageSort.setOnClickListener(this);
        menu.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logo:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.searchButton:
                if(NetWork.hasConnection(MainActivity.this)) {
                    Intent intent1 = new Intent();
                    intent1.setClass(MainActivity.this, SearchActivity.class);
                    startActivity(intent1);
                }else{
                    toast.showToas(MainActivity.this,getString(R.string.error_internet),false);
                }
                break;
            case R.id.textSort:
            case R.id.imageSort:
                if(statusSort) {
                    statusSort = false;
                    request = "SELECT * FROM " + dataBase.TABLE_PLAY_LIST+" ORDER BY "+dataBase.TITLE+" DESC";
                    imageSort.setImageResource(android.R.drawable.arrow_down_float);
                }else{
                    statusSort = true;
                    request = "SELECT * FROM " + dataBase.TABLE_PLAY_LIST+" ORDER BY "+dataBase.TITLE+" ASC";
                    imageSort.setImageResource(android.R.drawable.arrow_up_float);
                }
                showPlay_List();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.b_export:
                try {
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < App.videos.size();i++){
                        Video video = App.videos.get(i);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id",video.getId());
                        jsonObject.put("title",video.getTitle());
                        jsonObject.put("img",video.getImg());
                        array.put(jsonObject.toString(1));
                    }
                    checkPermissionsFiles(MainActivity.this);
                    String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    File file = new File(folder + "/" + "YouCoolMusic_PlayList.txt");
                    file.createNewFile();
                    FileOutputStream output = new FileOutputStream(file);
                    output.write(array.toString(1).getBytes());
                    output.flush();
                    output.close();
                    toast.showToas(MainActivity.this,getString(R.string.create_play_list),true);
                } catch (Exception e) {
                    e.printStackTrace();
                    toast.showToas(MainActivity.this,getString(R.string.error),false);
                }
                break;
            case R.id.b_import:
                try {
                    JSONArray items = getPlayList();
                    if (items != null) {
                        sql.delete(dataBase.TABLE_PLAY_LIST, null, null);
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject x = new JSONObject(items.getString(i));
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(dataBase.ID, x.getString("id"));
                            contentValues.put(dataBase.TITLE, x.getString("title"));
                            contentValues.put(dataBase.IMG, x.getString("img"));

                            sql.insert(dataBase.TABLE_PLAY_LIST, null, contentValues);
                        }
                        showPlay_List();
                        toast.showToas(MainActivity.this, getString(R.string.play_list_overading), true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    toast.showToas(MainActivity.this, getString(R.string.error), false);
                }
                break;
            case R.id.b_add_play_list:
                try {
                    JSONArray items = getPlayList();
                    if (items != null) {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject x = new JSONObject(items.getString(i));
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(dataBase.ID, x.getString("id"));
                            contentValues.put(dataBase.TITLE, x.getString("title"));
                            contentValues.put(dataBase.IMG, x.getString("img"));

                            sql.insert(dataBase.TABLE_PLAY_LIST, null, contentValues);
                        }
                        showPlay_List();
                        toast.showToas(MainActivity.this, getString(R.string.play_list_add), true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    toast.showToas(MainActivity.this, getString(R.string.error), false);
                }
                break;
            case R.id.b_delete_play_list:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(getString(R.string.delete_play_list));
                dialog.setMessage(getString(R.string.you_exactly_want_delete_play_list));
                dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sql.delete(dataBase.TABLE_PLAY_LIST,null,null);
                        showPlay_List();
                    }
                });
                dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog newDialog = dialog.create();
                newDialog.show();
                showPlay_List();
                break;
            case R.id.b_about:
                Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent1);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onRestart() {
        showPlay_List();
        super.onRestart();
    }

    private JSONArray getPlayList(){
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/YouCoolMusic_PlayList.txt";
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder res = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null){
                res.append(line).append("\n");
            }
            JSONArray items = new JSONArray(res.toString());
            return items;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void showPlay_List(){
        try {
            App.videos = new ArrayList<>();
            cursor = sql.rawQuery(request, null);
            if (cursor.moveToFirst()) {
                do {
                    App.videos.add(new Video(cursor.getString(cursor.getColumnIndex(dataBase.ID)), cursor.getString(cursor.getColumnIndex(dataBase.TITLE)), cursor.getString(cursor.getColumnIndex(dataBase.IMG))));
                } while (cursor.moveToNext());
            }
            cursor.close();
            listVideo.setAdapter(new SearchVideoAdapter(MainActivity.this));
            count_items.setText(getString(R.string.count_items)+": "+App.videos.size());
            mLinearLayoutManager.scrollToPosition(App.positionArr);
            if(App.videos.isEmpty()){
                toast.showToas(MainActivity.this,getString(R.string.not_found_play_list),false);
            }
        }catch (Exception e){
            e.printStackTrace();
            toast.showToas(MainActivity.this,getString(R.string.not_found_play_list),false);
        }
    }
    public static void checkPermissionsFiles(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermissionsOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 2323);
        }
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}
