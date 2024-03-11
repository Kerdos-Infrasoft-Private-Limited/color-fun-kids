package com.binlango.ColorFunkids;

import static android.content.ContentValues.TAG;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener,
        Handler.Callback {
    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;
    private final Handler handler = new Handler(this);
    WebView webView;
    private Dialog confirmationDialog;
    WebViewClient client;
    private InterstitialAd mInterstitialAd;
    RelativeLayout banner;
    private MediaPlayer mediaPlayer;
    private final String currentUrl = "file:///android_asset/index.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        webView.setOnTouchListener(MainActivity.this);
        client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };
        client = new MyWebViewClientx() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };




        // Inside your activity's onCreate() method
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
// Inside your activity's onCreate() method
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setDisplayShowTitleEnabled(false); // Optional - hide the title
            actionBar.setDisplayHomeAsUpEnabled(false); // Optional - hide the back button
        }




        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl("file:///android_asset/index.html");
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Force landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setSupportZoom(true);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );




        MobileAds.initialize(MainActivity.this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(MainActivity.this,"ca-app-pub-5421280510780014/4041373396", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        LoadAds();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

        mediaPlayer = MediaPlayer.create(this,R.raw.kids);
        mediaPlayer.setLooping(true); // Set looping to true to autoplay audio
        mediaPlayer.start();





    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release(); // Release media player resources when done
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }










    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }







    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (equals(request.getUrl().getHost()) && !"https://play.google.com/store/apps/developer?id=Smart+AppKids&gl=MA".equals(request.getUrl().getHost()) ) {
                // This is my website, so do not override; let my WebView load the page

                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            startActivity(intent);
            return true;
        }
    }



    private class MyWebViewClientx extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if ("https://play.google.com/store/apps/details?id=com.xd9.binlango&gl=MA".equals(request.getUrl().getHost()) ) {
                // This is my website, so do not override; let my WebView load the page

                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            showRateDialogForRate(MainActivity.this);
            return true;
        }
    }






    int count = 0;

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == CLICK_ON_URL) {
            handler.removeMessages(CLICK_ON_WEBVIEW);


            return true;
        }
        if (msg.what == CLICK_ON_WEBVIEW) {




            count += 1;

            if (count % 25 == 0) {



                MobileAds.initialize(MainActivity.this, initializationStatus -> {
                });

                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(MainActivity.this,"ca-app-pub-5421280510780014/6792598195", adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                mInterstitialAd = interstitialAd;
                                Log.i(TAG, "onAdLoaded");
                                LoadAds();
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.i(TAG, loadAdError.getMessage());
                                mInterstitialAd = null;
                            }
                        });


            }




            if ( count==40 ||count==60 ||count==90  ) {
                showRateDialogForRate(MainActivity.this);
            }

            if ( count % 100 == 0) {
                shareDialogForRate(MainActivity.this);
            }


            return true;
        }

        return false;
    }



    private void LoadAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getId() == R.id.webview && event.getAction() == MotionEvent.ACTION_DOWN) {
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);


        }



        return false;
    }




    @Override
    public void onBackPressed() {
        // Check if WebView can go back
        if (webView.canGoBack()) {
            // Go back in WebView history
            webView.goBack();
        } else {
            // If WebView cannot go back, let the default back button behavior take place
            super.onBackPressed();
        }
    }












    public static void showRateDialogForRate(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Rate application")
                .setMessage("Please, rate the app at play store")
                .setPositiveButton("RATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context != null) {
                            ////////////////////////////////
                            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            // To count with Play market backstack, After pressing back button,
                            // to taken back to our application, we need to add following flags to intent.
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                context.startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                            }


                        }
                    }
                })
                .setNegativeButton("CANCEL", null);
        builder.show();
    }



    public void shareDialogForRate(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Share Our application")
                .setMessage("Please, if you like your apps share it ")
                .setPositiveButton("SHARE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context != null) {
                            shareApp(MainActivity.this);

                        }
                    }
                })
                .setNegativeButton("CANCEL", null);
        builder.show();
    }

    public static void shareApp(Context context) {


        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, " Learn drawing with colors for kids so Install this cool application:\uD83D\uDE0B   https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }


}
