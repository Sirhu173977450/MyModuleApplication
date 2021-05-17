package com.shi.androidstudio.webviewandjs;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView mWebview;
    private Button bt_,bt_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebview = (WebView) findViewById(R.id.webview);
        //支持js
        mWebview.getSettings().setJavaScriptEnabled(true);
        bt_1 = (Button) findViewById(R.id.bt_1);
        bt_ = (Button) findViewById(R.id.bt_);
        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,JSActivity.class));
            }
        });
        bt_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过Handler发送消息
                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        // 注意调用的JS方法名要对应上
                        mWebview.loadUrl("javascript:callJS('"+123456+"')");
                        mWebview.loadUrl("javascript:callH5('"+123456+"')");
                    }
                });
            }
        });
        mWebview.loadUrl("file:///android_asset/index.html");


        mWebview.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @JavascriptInterface
            public void showToast(String msg) {
                Toast.makeText(getApplicationContext(),
                        "通过interface回调的结果："+msg, Toast.LENGTH_SHORT).show();
            }
        }, "JSTest");
        //需要弹出alert,需要默认设置一个ChromeClient
        mWebview.setWebChromeClient(new WebChromeClient());
        mWebview.setWebViewClient(new MyWebChromeClient());
    }

    public class MyWebChromeClient extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //协议的参数 url = "js://webview?orderNumber=8173411475"（同时也是约定好的需要拦截的）
            Uri uri = Uri.parse(url);
//            if (null != uri && uri.getScheme().equals("js")) {
//                if (uri.getAuthority().equals("webview")) {
                    String arg1 = uri.getQueryParameter("arg1");
                    Toast.makeText(MainActivity.this,arg1,Toast.LENGTH_SHORT).show();
//                }
                return true;
//            }
//            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
