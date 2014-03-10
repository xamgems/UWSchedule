package com.amgems.uwschedule.api.uw;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.amgems.uwschedule.api.Response;
import com.amgems.uwschedule.util.NetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JeremyTeoMBP on 9/2/14.
 */
public class GetStudentSlns {
    private String mCookie;
    private String mHtml;
    private Response mResponse;

    /** Context for WebView instantiation */
    private WebView mWebView;

    Handler mHandler;
    int mCount;

    private volatile boolean mLoadingFinished;

    private GetStudentSlns(Context context, Handler activityHandler, String cookie) {
        mCookie = cookie;
        mLoadingFinished = false;
        mWebView = new WebView(context);
        mHandler = activityHandler;
        //CookieManager.getInstance().removeAllCookie();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void processHTML(final String html) {
                mHtml = html;
                mLoadingFinished = true;
            }
        }, "GETHTMLBODY");
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void getCookies(final String cookies) {
                mCookie = cookies;
            }
        }, "GETCOOKIES");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //if (mCount >= 1) {
                    view.loadUrl("javascript:window.GETHTMLBODY.processHTML(document.getElementsByTagName('html')[0].innerHTML);");


                mCount++;
            }
        });
    }

    public static GetStudentSlns newInstance(Context context, Handler activityHandler, String cookie) {
        return new GetStudentSlns(context, activityHandler, cookie);
    }

    public void execute() {

        final Map<String, String> cookieMap = new HashMap<String, String>();
        cookieMap.put("Cookie", mCookie);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mWebView.getContext(), "Cookie: " + mCookie, Toast.LENGTH_LONG).show();
                mWebView.loadUrl(NetUtils.REGISTRATION_URL, cookieMap);
            }
        });


        while (!mLoadingFinished) {  }

    }

    public Response getResponse() {return mResponse;}

    public String getHtml() { return mHtml; }
}
