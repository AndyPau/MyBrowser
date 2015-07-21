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
package com.fillr.browsersdk.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by naveedzanoon on 30/04/15.
 * <p/>
 * Singleton to handle fonts
 */
public class FillrFontUtility {

    public enum FONT_TYPE {
        ROBOTO_REGULAR, ROBOTO_MEDIUM, ROBOTO_BOLD
    }

    ;


    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();


    private static FillrFontUtility instance = null;

    private FillrFontUtility() {

    }

    public static FillrFontUtility getInstance() {
        if (instance == null) {
            instance = new FillrFontUtility();
        }
        return instance;
    }

    public void setCustomFont(Context context, FONT_TYPE type, boolean isBold,
                              View... textViews) {

        if (textViews != null) {
            Typeface font = getCustomFont(context, type);

            if (font != null) {
                for (View v : textViews) {
                    if (v instanceof TextView) {
                        if (isBold) {
                            ((TextView) v).setTypeface(font, Typeface.BOLD);
                        } else {
                            ((TextView) v).setTypeface(font, Typeface.NORMAL);
                        }
                    }
                }
            }
        }
    }

    private Typeface getCustomFont(Context context, FONT_TYPE type) {
        Typeface tempFont = null;
        String name = getFontName(type);
        if (name != null) {
            tempFont = fontCache.get(name);
            if (tempFont == null) {
                tempFont = Typeface.createFromAsset(context.getAssets(), name);
                fontCache.put(name, tempFont);
            }
        }
        return tempFont;
    }

    private String getFontName(FONT_TYPE type) {
        switch (type) {
            case ROBOTO_BOLD:
                return "Fonts/roboto_bold.ttf";
            case ROBOTO_MEDIUM:
                return "Fonts/roboto_medium.ttf";
            case ROBOTO_REGULAR:
                return "Fonts/roboto_regular.ttf";
        }
        return null;
    }

}
