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


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by AlexZhaoBin on 13/05/15.
 */
public class FillrToolbarView extends RelativeLayout {
    private boolean alreadyInitialized = false;
    private FillrToolbarView toolbarView;
    private Context mContext;
    private ImageView autofillIcon;
    private TextView autofillTextView;
    private ImageView dismissImageView;
    private TextView yesTextView;

    private boolean shouldDismissToolbar = false;
    private View view;

    public FillrToolbarView(Context context) {
        super(context);
        //setOrientation(HORIZONTAL);
        initViews(context);
    }

    public FillrToolbarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //setOrientation(HORIZONTAL);
    }

    public FillrToolbarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //setOrientation(HORIZONTAL);
        initViews(context);
    }

    private void initViews(final Context context) {
        if (alreadyInitialized) {
            // Only initialize once
            return;
        }
        alreadyInitialized = true;

        toolbarView = this;
        mContext = context;
        this.setBackgroundColor(Color.rgb((int) (0.89f * 255), (int) (0.91f * 255), (int) (0.91f * 255)));

        LinearLayout leftContainer = new LinearLayout(context);
        leftContainer.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeParams.setMargins(dpToPixel(context, 15), 0, dpToPixel(context, 15), 0);
        this.addView(leftContainer, relativeParams);

        autofillIcon = new ImageView(context);
        autofillIcon.setImageResource(R.drawable.com_fillr_icon_keyboard_sdk);
        //autofillIcon.setId(R.id.f_fill_button);
        LinearLayout.LayoutParams leftContainerParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        leftContainer.addView(autofillIcon, leftContainerParams);

        autofillIcon.setVisibility(View.VISIBLE);

        autofillTextView = new TextView(context);
        if (isFillrAppInstalled()) {
            autofillTextView.setText(context.getText(R.string.install_fillr_tool_bar_text));
        } else {
            autofillTextView.setText(context.getText(R.string.install_fillr_tool_bar_text));
        }
        autofillTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        autofillTextView.setTextColor(Color.argb(255, 66, 66, 66));
        autofillTextView.setGravity(Gravity.CENTER);
        leftContainerParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        leftContainerParams.setMargins(dpToPixel(context, 7), 0, dpToPixel(context, 10), 0);
        leftContainer.addView(autofillTextView, leftContainerParams);

        dismissImageView = new ImageView(context);
        dismissImageView.setImageResource(R.drawable.com_fillr_keyboard_arrow_down);

        //cdcecf

        dismissImageView.setId(R.id.id_btn_yes); // Set an arbitrary id, so the layout can work properly
        relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeParams.setMargins(dpToPixel(context, 10), 0, dpToPixel(context, 10), 0);
        this.addView(dismissImageView, relativeParams);

        view = new View(context);
        view.setId(R.id.line_seperator);
        view.setBackgroundResource(R.color.com_fillr_browsersdk_toolbar_line);
        relativeParams = new RelativeLayout.LayoutParams(dpToPixel(context, 1), LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.LEFT_OF, dismissImageView.getId());
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeParams.setMargins(dpToPixel(context, 5), dpToPixel(context, 10), dpToPixel(context, 1), dpToPixel(context, 10));

        this.addView(view, relativeParams);

        yesTextView = new TextView(context);
        yesTextView.setText("  ?  ");
        yesTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        //yesTextView.setId(R.id.f_next_button); // Set an arbitrary id, so the layout can work properly
        yesTextView.setTextColor(Color.argb(255, 126, 126, 126));
        yesTextView.setGravity(Gravity.CENTER);
        relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.LEFT_OF, view.getId());
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeParams.setMargins(dpToPixel(context, 10), 0, dpToPixel(context, 10), 0);
        this.addView(yesTextView, relativeParams);
        if (isFillrAppInstalled()) {
            yesTextView.setVisibility(View.INVISIBLE);
        } else {
            yesTextView.setVisibility(View.VISIBLE);
        }

        leftContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final View leftContainer = view;
                leftContainer.setEnabled(false);
                leftContainer.setClickable(false);

                if (isFillrAppInstalled()) {
                    Fillr.getInstance().showPinScreen();
                } else {
                    Fillr.getInstance().showDownloadDialog(mContext);
                }
                toolbarView.setVisibility(View.INVISIBLE);

                // To avoid double tap, set a timer to re-enable the button
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        leftContainer.setEnabled(true);
                        leftContainer.setClickable(true);
                    }
                };
                handler.postDelayed(r, 1000);
            }
        });

        dismissImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                shouldDismissToolbar = true;
                toolbarView.setVisibility(View.INVISIBLE);
            }
        });

        yesTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Fillr.getInstance().showDownloadDialog(mContext);
                toolbarView.setVisibility(View.INVISIBLE);
            }
        });

        this.setVisibility(View.INVISIBLE);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (FillrAuthenticationStore.isEnabled() && Fillr.getInstance().webViewHasFocus()) {
                    Rect displayRect = new Rect();
                    toolbarView.getWindowVisibleDisplayFrame(displayRect);
                    int rootHeight = toolbarView.getRootView().getHeight();
                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = rootHeight - displayRect.bottom;

                    if (keypadHeight > rootHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        if (!shouldDismissToolbar) {
                            // keyboard is opened
                            toolbarView.setVisibility(View.VISIBLE);

                            if (isFillrAppInstalled()) {
                                autofillIcon.setVisibility(View.VISIBLE);
                                autofillTextView.setText(context.getText(R.string.install_fillr_tool_bar_text));
                                yesTextView.setVisibility(View.INVISIBLE);
                            } else {
                                autofillIcon.setVisibility(View.VISIBLE);
                                autofillTextView.setText(context.getText(R.string.install_fillr_tool_bar_text));
                                yesTextView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            shouldDismissToolbar = false;
                        }
                    } else {
                        // keyboard is closed
                        toolbarView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    toolbarView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public boolean isFillrAppInstalled() {
        return isPackageInstalled("com.fillr", mContext);
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private int dpToPixel(Context context, int dp) {
        Resources resources = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return px;
    }
}
