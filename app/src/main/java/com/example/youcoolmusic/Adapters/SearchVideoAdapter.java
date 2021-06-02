package com.example.youcoolmusic.Adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.youcoolmusic.App;
import com.example.youcoolmusic.BackgroundMusicService;
import com.example.youcoolmusic.DataBase;
import com.example.youcoolmusic.R;
import com.example.youcoolmusic.Video;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class SearchVideoAdapter extends RecyclerView.Adapter<SearchVideoAdapter.ExampleViewHolder> {

    private Context context;
    private DataBase dataBase;
    private SQLiteDatabase sql;
    private Cursor cursor;
    private Intent intent;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageVideo;
        TextView nameView;
        Button addVideo;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            imageVideo = itemView.findViewById(R.id.imageVideo);
            nameView = itemView.findViewById(R.id.nameView);
            addVideo = itemView.findViewById(R.id.addVideo);
        }
    }

    public SearchVideoAdapter(Context context){
        this.context=context;
        this.dataBase = new DataBase(context);
        this.sql = dataBase.getWritableDatabase();
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videolist, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, final int position) {
        final Video video = App.videos.get(position);

        boolean isContains;
        try {
            cursor = sql.rawQuery("SELECT " + dataBase.ID + " FROM " + dataBase.TABLE_PLAY_LIST + " WHERE "
                    + dataBase.ID + " = '" + video.getId() + "'", null);
            String localId = "";
            cursor.moveToFirst();
            localId = cursor.getString(cursor.getColumnIndex(dataBase.ID));
            cursor.close();
            if(!localId.isEmpty()){
                isContains = true;
            }else{
                isContains = false;
            }
        }catch (Exception e){
            isContains = false;
        }

        holder.nameView.setText(video.getTitle());

        if(isContains){
            Picasso.with(context).load(video.getImg()).placeholder(R.drawable.youtube).into(holder.imageVideo);
            holder.addVideo.setBackgroundResource(android.R.drawable.ic_menu_delete);
            holder.addVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(context.getString(R.string.delete_video));
                    dialog.setMessage(context.getString(R.string.you_exactly_want_delete_video));
                    dialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sql.delete(dataBase.TABLE_PLAY_LIST, dataBase.ID + " = '"+video.getId()+"'",null);
                            int newPos = holder.getAdapterPosition();
                            if(!App.videos.isEmpty()) {
                                App.videos.remove(newPos);
                            }
                            notifyItemRemoved(newPos);
                        }
                    });
                    dialog.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog newDialog = dialog.create();
                    newDialog.show();
                }
            });
        }else{
            Picasso.with(context).load(video.getImg())
                    .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.youtube).into(holder.imageVideo);
            holder.addVideo.setBackgroundResource(android.R.drawable.ic_menu_add);
            holder.addVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(dataBase.ID, video.getId());
                    contentValues.put(dataBase.TITLE, video.getTitle());
                    contentValues.put(dataBase.IMG, video.getImg());

                    sql.insert(dataBase.TABLE_PLAY_LIST, null, contentValues);
                    Toast.makeText(context,context.getString(R.string.successfully_append_video),Toast.LENGTH_LONG).show();
                }
            });
        }

        holder.imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartVideo(position);
            }
        });
        holder.nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartVideo(position);
            }
        });
    }

    private void StartVideo(int p){
        try {
            App.positionArr = p;
            intent = new Intent(context, BackgroundMusicService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (App.runingService) {
                App.is_Stop = false;
                App.runingService = false;
                App.backgroundVideo.destroy();
                App.windowManager.removeView(App.view);
                context.stopService(intent);
            }
            ContextCompat.startForegroundService(context,intent);
        }catch (Exception e){
            Toast.makeText(context,context.getString(R.string.start_video_error),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return App.videos.size();
    }
}