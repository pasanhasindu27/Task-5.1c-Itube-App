package com.itube;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements VideoListAdapter.OnItemClickListener{

    private RecyclerView videoListRecyclerView;
    private VideoListAdapter videoListAdapter;
    private List<String> videoUrls;
    private DatabaseHelper dbHelper;
    private TextView noVideosTextView;
    private int userId; // The logged-in user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Initialize UI elements
        videoListRecyclerView = findViewById(R.id.videoListRecyclerView);
        videoListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noVideosTextView = findViewById(R.id.noVideosTextView);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Get the user ID from the intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get video URLs from the database for the logged-in user
        videoUrls = getVideoUrlsFromDatabase(userId);

        // Initialize the adapter
        videoListAdapter = new VideoListAdapter(this, videoUrls, this);
        videoListRecyclerView.setAdapter(videoListAdapter);

        if (videoUrls.isEmpty()) {
            noVideosTextView.setVisibility(View.VISIBLE);
            videoListRecyclerView.setVisibility(View.GONE);
        } else {
            noVideosTextView.setVisibility(View.GONE);
            videoListRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Method to get video URLs from the database for a specific user
    private List<String> getVideoUrlsFromDatabase(int userId) {
        List<String> urls = new ArrayList<>();
        Cursor cursor = dbHelper.getVideosByUser(userId);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(2); // Get URL from the third column in new table
                urls.add(url);
            }
        }
        cursor.close();
        return urls;
    }

    @Override
    public void onItemClick(String videoUrl) {
        new PlaylistActivity.ExtractVideoIdTask().execute(videoUrl);
    }

    private class ExtractVideoIdTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            return extractVideoId(url);
        }

        @Override
        protected void onPostExecute(String videoId) {
            if (videoId != null) {
                Intent intent = new Intent(PlaylistActivity.this, VideoPlayerActivity.class);
                intent.putExtra("VIDEO_ID", videoId);
                startActivity(intent);
            } else {
                Toast.makeText(PlaylistActivity.this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to extract video ID from YouTube URL
    private String extractVideoId(String url) {
        String videoId = null;

        if (url.contains("youtube.com/watch?v=")) {
            // Extract video ID from standard YouTube URL
            String[] parts = url.split("v=");
            if (parts.length > 1) {
                videoId = parts[1].split("&")[0]; // Remove additional parameters
            }
        } else if (url.contains("youtu.be/")) {
            // Extract video ID from shortened URL
            String[] parts = url.split("youtu.be/");
            if (parts.length > 1) {
                videoId = parts[1].split("\\?")[0]; // Remove additional parameters if present
            }
        }

        return videoId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}