package com.itube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    private EditText youtubeUrlEditText;
    private Button playButton, addToPlaylistButton, myPlaylistButton;
    private DatabaseHelper dbHelper;
    private int currentUserId; // Store the logged-in user's ID
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI elements
        youtubeUrlEditText = findViewById(R.id.youtubeUrlEditText);
        playButton = findViewById(R.id.playButton);
        addToPlaylistButton = findViewById(R.id.addToPlaylistButton);
        myPlaylistButton = findViewById(R.id.myPlaylistButton);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Get the current user's ID from shared preferences
        currentUserId = sharedPreferences.getInt("USER_ID", -1); // -1 is the default value if not found

        if (currentUserId == -1) {
            Toast.makeText(this, "Please log in to use this feature", Toast.LENGTH_SHORT).show();
            finish(); //finish the activity
            return;
        }

        // Set click listener for play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = youtubeUrlEditText.getText().toString().trim();
                if (isValidYouTubeUrl(youtubeUrl)) {
                    new ExtractVideoIdTask().execute(youtubeUrl);
                } else {
                    Toast.makeText(HomeActivity.this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for add to playlist button
        addToPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = youtubeUrlEditText.getText().toString().trim();
                if (isValidYouTubeUrl(youtubeUrl)) {
                    String videoId = extractVideoId(youtubeUrl);
                    if (videoId != null) {
                        // Add the URL and user ID to the playlist database
                        long result = dbHelper.insertVideo(currentUserId, youtubeUrl);
                        if (result != -1) {
                            Toast.makeText(HomeActivity.this, "Video added to playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Failed to add video to playlist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(HomeActivity.this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for my playlist button
        myPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PlaylistActivity and pass the user ID
                Intent intent = new Intent(HomeActivity.this, PlaylistActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
            }
        });
    }

    // Method to validate YouTube URL using Android Patterns
    private boolean isValidYouTubeUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches() &&
                (url.contains("youtube.com/watch?v=") || url.contains("youtu.be/"));
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
                Intent intent = new Intent(HomeActivity.this, VideoPlayerActivity.class);
                intent.putExtra("VIDEO_ID", videoId);
                startActivity(intent);
            } else {
                Toast.makeText(HomeActivity.this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to extract video ID from both YouTube URL formats
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