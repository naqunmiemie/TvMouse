package org.yjn.tvmouse;

import android.app.Activity;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private CustomWebView webView;
    private MouseView mouseView;

    public static final String expmpleUrl = "https://www.bilibili.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        webView.loadUrl(expmpleUrl);
        mouseView = new MouseView(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("moveMouse","dispatchKeyEvent");
        if (mouseView.moveMouse(webView,event)){
            return true;
        }


        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i("tag","点击ev.getX():"+ev.getX()+" ,ev.getY():"+ev.getY());
        return super.dispatchTouchEvent(ev);
    }
}
