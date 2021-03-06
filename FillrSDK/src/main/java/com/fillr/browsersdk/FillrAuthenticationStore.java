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
import android.content.SharedPreferences;

public class FillrAuthenticationStore {

    private static final String F_ANALYICS = "F_ANALYTICS";

    private static final String SHARED_PREF_KEY = "com.fillr.browsersdk";

    public static void setAnalyticsFeature(Context context, boolean isAnalyticsOn) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(F_ANALYICS, isAnalyticsOn);
        editor.commit();
    }

    public static boolean getAnalyticsFlag(Context context) {
        boolean retVal = true;
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
            if (sharedPref != null) {
                retVal = sharedPref.getBoolean(F_ANALYICS, true);
            }
        }
        return retVal;
    }

    public static final boolean isEnabled() {
        SharedPreferences settings = Fillr.getInstance().getParentActivity().getSharedPreferences(SHARED_PREF_KEY, 0);
        return settings.getBoolean("enabled", true);
    }

    public static final void setEnabled(boolean value) {
        SharedPreferences settings = Fillr.getInstance().getParentActivity().getSharedPreferences(SHARED_PREF_KEY, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("enabled", value);
        editor.commit();
    }


}
