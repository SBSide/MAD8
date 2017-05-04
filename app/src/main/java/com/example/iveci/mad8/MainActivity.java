package com.example.iveci.mad8;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    ListView listView;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    ArrayAdapter<String> adapter;
    WebSettings webSettings;
    EditText e1;
    ProgressDialog progressDialog;
    Animation animTop;
    LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Web");
        init();
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        progressDialog = new ProgressDialog(this);
        webView.loadUrl("https://google.com");
        webView.addJavascriptInterface(new JavaScriptMethods(), "myApp");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.setMessage("로드 중");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                e1.setText(url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) progressDialog.dismiss();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });
        animTop = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        animTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linear.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                linear.setVisibility(View.VISIBLE);
                webView.loadUrl(urls.get(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 2, 0, "즐겨찾기추가");
        menu.add(0, 1, 0, "즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1){
            listView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
            linear.setAnimation(animTop);
            animTop.start();
        }
        else if (item.getItemId() == 2){
            listView.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            linear.setAnimation(animTop);
            animTop.start();
            webView.loadUrl("file:///android_asset/www/urladd.html");
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler();
    class JavaScriptMethods {
        @JavascriptInterface
        public void displayToast() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("그림변경")
                            .setMessage("그림을 변경하시겠습니까?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    webView.loadUrl("javascript:changeImage()");
                                }
                            })
                            .setNegativeButton("NO", null)
                            .show();
                }
            });
        }

        @JavascriptInterface
        public void displayURL() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    linear.setVisibility(View.VISIBLE);
                }
            });

        }

        @JavascriptInterface
        public void addlist(final String sitename, final String address){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (String s : urls){
                        if (s.equals(address)) webView.loadUrl("javascript:displayMsg()");
                    }
                    names.add("<" + sitename + "> " + address);
                    urls.add(address);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void init(){
        e1 = (EditText) findViewById(R.id.eturl);
        webView = (WebView) findViewById(R.id.web);
        linear = (LinearLayout) findViewById(R.id.linear);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setVisibility(View.INVISIBLE);
    }
}
