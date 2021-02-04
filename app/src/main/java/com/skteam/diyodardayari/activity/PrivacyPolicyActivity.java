package com.skteam.diyodardayari.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.simpleclasses.Variables;


public class PrivacyPolicyActivity extends AppCompatActivity {

    private String url;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);



        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String work = bundle.getString("work", "privacy_policy");

            String privacy_policy = Variables.DOMAIN +  Variables.PATH+ "privacy_policy.html";
            String about_us = Variables.DOMAIN+Variables.PATH+"about_us.html";
            switch (work) {

                case "about_us":
                    url = about_us;
                    break;

                default:
                    url = privacy_policy;
                    break;
            }


        }



        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
    }
}
