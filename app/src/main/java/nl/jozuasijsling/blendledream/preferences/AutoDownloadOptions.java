/*
 * Copyright (c) 2015 Jozua Sijsling <jozua.sijsling@gmail.com>
 *
 * All rights reserved. No warranty, explicit or implicit, provided.
 *
 * NOTICE:  All information contained herein is, and remains the property of Jozua Sijsling. The
 * intellectual and technical concepts contained herein are proprietary to Jozua Sijsling and are
 * protected by trade secret or copyright law. Dissemination of this information or reproduction
 * of this material is strictly forbidden unless prior written permission is obtained from Jozua
 * Sijsling. Access to the source code contained herein is hereby forbidden to anyone unless
 * prior written permission is obtained from Jozua Sijsling. This includes those who have
 * executed Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure
 * of this source code, which includes information that is confidential and/or proprietary, and
 * is a trade secret, of Jozua Sijsling. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC
 * PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT
 * THE EXPRESS WRITTEN CONSENT OF  Jozua Sijsling IS STRICTLY PROHIBITED, AND IN
 * VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION
 * OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR  IMPLY ANY
 * RIGHTS  TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE,
 * OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

package nl.jozuasijsling.blendledream.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Wrapper for a shared preferences file that holds auto download settings.
 */
public class AutoDownloadOptions {

    public static final String SHARED_PREFERENCES_NAME = "auto_download_options";
    private static final String KEY_ENABLED = "enabled";

    private final boolean mEnabled;

    public AutoDownloadOptions(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    @NonNull
    public static AutoDownloadOptions fromPreferences(@NonNull Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        boolean enabled = preferences.getBoolean(KEY_ENABLED, false);
        return new AutoDownloadOptions(enabled);
    }

    public void writeToPreferences(@NonNull Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putBoolean(KEY_ENABLED, mEnabled).apply();
    }

    @NonNull
    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
