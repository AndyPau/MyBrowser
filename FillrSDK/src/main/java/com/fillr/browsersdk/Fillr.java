/*
 * Copyright 2015-present Pop Tech Pty Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fillr.browsersdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.fillr.browsersdk.utilities.FillrUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by AlexZhaoBin on 13/05/15.
 */
public final class Fillr {


    public interface FillrFormProcessListener {
        void getResult(HashMap<String, String> fieldsWithData, HashMap<String, String> allProfileData);
    }

    public static final int FILLR_REQUEST_CODE = 101;
    private static final String MOBILE_BROWSER_WIDGET = "https://d2o8n2jotd2j7i.cloudfront.net/widget/android/sdk/MobileWidget.js";

    private static final String EXTRA_KEY_FIELDS = "com.fillr.jsonfields";
    private static final String EXTRA_KEY_DEV_KEY = "com.fillr.devkey";
    private static final String EXTRA_KEY_SDK_PACKAGE = "com.fillr.sdkpackage";

    private static String javascriptData = null;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static Fillr fillrIntance = null;

    private WebView mWebView = null;
    private boolean webViewHasFocus = false;
    private Activity parentActivity = null;
    private String devKey = null;
    private String mPackageName = null;

    private long startTime = System.currentTimeMillis();
    public FillrFormProcessListener formProcessListener;

    private Fillr() {
        //private constructor
    }

    public static Fillr getInstance() {
        if (fillrIntance == null) {
            fillrIntance = new Fillr();
        }
        return fillrIntance;
    }

    /**
     * This method is the starting point of integrating the SDK.
     * Call this method with your developer key and your activity
     *
     * @param devKey
     * @param parentAct
     */
    public final void initialise(String devKey, FragmentActivity parentAct) {
        if (parentAct != null && devKey != null) {
            parentActivity = parentAct;
            fillrIntance.getWidgetInfoFromServer(false);
            this.devKey = devKey;
            mPackageName = parentActivity.getApplicationContext().getPackageName();
        } else {
            throw new IllegalArgumentException("Please provide a valid activity and developer key");
        }
    }

    public boolean webViewHasFocus() {
        return mWebView.hasFocus();
    }

    /**
     * Needs to called every time a @android.wiWebView is attached     *
     *
     * @param webView is attached.
     */
    public void trackWebView(WebView webView) {
        mWebView = webView;
        initializeWebViewSettings(mWebView.getSettings(), parentActivity);
        mWebView.addJavascriptInterface(new JSNativeInterface(), "androidInterface");
        mWebView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == mWebView) {
                    //webViewHasFocus = hasFocus;
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebViewSettings(WebSettings settings, Context context) {
        settings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    void showPinScreen() {
        injectJavascriptIntoWebView();
    }


    public void injectJavascriptIntoWebView() {
        String javascript = getWidgetInfoFromServer(true);
        if (javascript != null) {
            loadWidget();
        }
    }

    public static void showDownloadDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.transparent_dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);


        View viewCreated = inflater.inflate(R.layout.com_fillr_dialog_fragment_install_fillr, null);
        TextView titleText = (TextView) viewCreated.findViewById(R.id.dialog_title_text);
        TextView contentText = (TextView) viewCreated.findViewById(R.id.dialog_content_text);

        Button closeDialog = (Button) viewCreated.findViewById(R.id.id_btn_no);
        Button approveDialog = (Button) viewCreated.findViewById(R.id.id_btn_yes);
        closeDialog.setTransformationMethod(null);
        approveDialog.setTransformationMethod(null);

        builder.setView(viewCreated);

        final AlertDialog alertDialog = builder.create();

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        approveDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Fillr.getInstance().downloadFillrApp();
            }
        });
        alertDialog.show();
    }

    public void downloadFillrApp() {
        // Save return package name
        if (parentActivity != null) {

            String browserPackageName = parentActivity.getApplicationContext().getPackageName();

            setClipboardData("ReturnPackageName", browserPackageName);
            String appPackageName = "com.fillr";
            try {
                parentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                parentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    private void setClipboardData(String key, String value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) parentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(key, value);
            clipboard.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) parentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(value);
        }
    }

    public class JSNativeInterface {
        @JavascriptInterface
        public void setFields(final String json) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FillrUtils.hideKeyboard(parentActivity);
                    //DO NOT CHANGE
                    Intent newIntent = new Intent();
                    newIntent.setComponent(new ComponentName("com.fillr", "com.fillr.browsersdk.activities.FillrBSDKProfileDataApproveActivity"));
                    newIntent.putExtra(EXTRA_KEY_FIELDS, json);
                    newIntent.putExtra(EXTRA_KEY_DEV_KEY, getDeveloperKey());
                    newIntent.putExtra(EXTRA_KEY_SDK_PACKAGE, mPackageName);
                    parentActivity.startActivityForResult(newIntent, FILLR_REQUEST_CODE);

                }
            });
        }
    }

    //step 1
    private final void loadWidget() {
        startTime = System.currentTimeMillis();
        mWebView.loadUrl("javascript: " + javascriptData);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript: PopWidgetInterface.getFields();");
            }
        }, 100);
    }

    private final String getWidgetInfoFromServer(final boolean loadWidgetAfterFinish) {
        if (javascriptData != null) {
            return javascriptData;
        }

        client.get(MOBILE_BROWSER_WIDGET, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                String decoded;
                try {
                    decoded = new String(arg2, "UTF-8");
                    javascriptData = decoded;

                    if (loadWidgetAfterFinish) {
                        if (mWebView != null && mWebView.getVisibility() == View.VISIBLE) {
                            loadWidget();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        return null;
    }

    public static final void doNotTrack(boolean value) {
        FillrAuthenticationStore.setAnalyticsFeature(Fillr.getInstance().parentActivity, value);
    }

    private final String getDeveloperKey() {
        return devKey;
    }

    public void processForm(Intent data) {
        //DO NOT CHANGE THESE
        String payload = data.getStringExtra("com.fillr.payload");
        String mappings = data.getStringExtra("com.fillr.mappings");

        if (payload != null && mappings != null) {
            mWebView.loadUrl("javascript:PopWidgetInterface.populateWithMappings(JSON.parse('" +
                    mappings.replaceAll("(\\\\t|\\\\n|\\\\r')", " ") + "'), JSON.parse('" + payload.toString() + "'));");
        }
    }

    Activity getParentActivity() {
        return parentActivity;
    }

    public void onResume() {
        String shouldTrigger = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) parentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = clipboard.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                shouldTrigger = String.valueOf(data.getItemAt(0).getText());
            }
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) parentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.getText() != null) {
                shouldTrigger = clipboard.getText().toString();
            }
        }
        if (shouldTrigger != null && shouldTrigger.equals("com.fillr.load.yes")) {
            if (mWebView != null && mWebView.getVisibility() == View.VISIBLE) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadWidget();
                        setClipboardData("com.fillr.triggerautofill", "");
                    }
                }, 300);
            }
        }
    }


}
