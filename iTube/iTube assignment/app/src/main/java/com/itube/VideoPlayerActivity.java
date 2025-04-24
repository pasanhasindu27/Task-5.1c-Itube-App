package com.itube;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VideoPlayerActivity extends AppCompatActivity {

    private WebView webView;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Initialize UI elements
        webView = findViewById(R.id.webView);

        // Get the video ID from the intent
        videoId = getIntent().getStringExtra("VIDEO_ID");

        // Load the YouTube video in the WebView using iframe embed
        if (videoId != null) {
            String html = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe>";
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Page is loaded
                }
            });
            webView.loadData(html, "text/html", "utf-8");
        } else {
            // Handle the case where videoId is null (e.g., show an error message)
            webView.loadData("<h1>Error: No Video ID provided</h1>", "text/html", "utf-8");
        }
    }
}