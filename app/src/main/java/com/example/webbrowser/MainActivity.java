package com.example.webbrowser;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity{
    WebView webViewContent;
    LinearLayout linearLayoutBottom;
    ImageButton imageButtonHome, imageButtonRefresh, imageButtonBack, imageButtonForward;
    ImageView imageViewInsertLink, imageViewSetBottom;
    String URL_FILE = "Url file";
    String URL = "Url";
    String notOnline_html = "<!doctype html>\n" +
                            "<html>\n" +
                            "\t<head>\n" +
                            "\t\t<meta charset=\"utf-8\">\n" +
                            "\t\t<title>Lỗi kết nối Internet</title>\n" +
                            "\t\t<style>\n" +
                            "\t\t\tbody{\n" +
                            "\t\t\t\ttext-align: center;\n" +
                            "\t\t\t}\n" +
                            "\t\t\tbody > p > b {\n" +
                            "\t\t\t\tcolor: red;\n" +
                            "\t\t\t}\n" +
                            "\t\t</style>\n" +
                            "\t</head>\n" +
                            "\t<body>\n" +
                            "\t\t<br><p><b>Lỗi kết nối Internet</b></p>\n" +
                            "\t\t<p>Vui lòng kiểm tra kết nối Internet và khởi chạy lại ứng dụng</p>\n" +
                            "\t</body>\n" +
                            "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_main);
        handling();
    }

    @Override
    public void onBackPressed() {

        if (webViewContent.canGoBack()) {
            webViewContent.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void handling() {
        mappingView();
        setView();
        loadWebsite();
        setOnClick();
    }

    void setStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void mappingView() {
        webViewContent = findViewById(R.id.web_view_content);
        linearLayoutBottom = findViewById(R.id.linear_layout_bottom);
        imageButtonHome = findViewById(R.id.image_button_home);
        imageButtonRefresh = findViewById(R.id.image_button_refresh);
        imageButtonBack = findViewById(R.id.image_button_back);
        imageButtonForward = findViewById(R.id.image_button_forward);
        imageViewInsertLink = findViewById(R.id.image_view_insert_link);
        imageViewSetBottom = findViewById(R.id.image_view_set_bottom);
    }

    private void setView() {
        imageViewSetBottom.setImageResource(R.drawable.ic_more_horiz_light_blue_500_24dp);
        linearLayoutBottom.setVisibility(View.GONE);
        //imageViewInsertLink.setVisibility(View.GONE);
    }

    private Boolean checkOnline() {
        boolean rtBoolean;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        rtBoolean = networkInfo != null && networkInfo.isConnected();
        return rtBoolean;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebsite() {
        webViewContent.getSettings().setJavaScriptEnabled(true);
        webViewContent.getSettings().setAppCacheEnabled(true);
        webViewContent.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean rtBoolean;

                if (url.startsWith("http:") || url.startsWith("https:")) {
                    rtBoolean = false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    rtBoolean = true;
                }
                return rtBoolean;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                boolean rtBoolean = checkOnline();

                if (!rtBoolean) {
                    webViewContent.loadData(notOnline_html, "text/html", "UTF-8");
                }
                //super.onReceivedError(view, request, error);
            }
        });

        webViewContent.loadUrl(getUrl());
    }

    private void saveUrl() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.insert_link);
        final EditText etUrl = dialog.findViewById(R.id.edit_text_url);
        final Button btOk = dialog.findViewById(R.id.button_ok);
        //dialog.setCancelable(false);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String url = etUrl.getText().toString().trim();

            if (!url.equals("")) {
                SharedPreferences sharedPreferences = getSharedPreferences(URL_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(URL, "http://" + url + "/");
                editor.apply();
                dialog.cancel();
                webViewContent.loadUrl(getUrl());
            } else {
                Toast.makeText(MainActivity.this, "Bạn chưa nhập liên kết", Toast.LENGTH_SHORT).show();
            }
            }
        });

        Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
        dialog.show();
    }

    private String getUrl() {
        SharedPreferences sharedPreferences = getSharedPreferences(URL_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(URL, "http://aoquan.webmienphi.info");
    }

    private void setOnClick() {

        imageViewInsertLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUrl();
            }
        });

        imageButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            webViewContent.loadUrl(getUrl());
            }
        });

        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViewContent.reload();
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (webViewContent.canGoBack()) {
                webViewContent.goBack();
            } else {
                Toast.makeText(MainActivity.this, "Không có lịch sử trang trước", Toast.LENGTH_SHORT).show();
            }
            }
        });

        imageButtonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (webViewContent.canGoForward()) {
                webViewContent.goForward();
            } else {
                Toast.makeText(MainActivity.this, "Không có lịch sử trang sau", Toast.LENGTH_SHORT).show();
            }
            }
        });

        imageViewSetBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (linearLayoutBottom.getVisibility() == View.VISIBLE) {
                imageViewSetBottom.setImageResource(R.drawable.ic_more_horiz_light_blue_500_24dp);
                linearLayoutBottom.setVisibility(View.GONE);
            } else {
                imageViewSetBottom.setImageResource(R.drawable.ic_remove_light_blue_500_24dp);
                linearLayoutBottom.setVisibility(View.VISIBLE);
            }
            }
        });
    }
}