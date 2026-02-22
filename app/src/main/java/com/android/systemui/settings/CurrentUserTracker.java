/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.settings;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;

import java.lang.reflect.InvocationTargetException;

public abstract class CurrentUserTracker extends BroadcastReceiver {

    private Context mContext;
    private int mCurrentUserId;

    public CurrentUserTracker(Context context) {
        // ACTION_USER_SWITCHED
        IntentFilter filter = new IntentFilter("android.intent.action.USER_SWITCHED");
        context.registerReceiver(this, filter);
        try {
            mCurrentUserId = (int) ActivityManager.class.getMethod("getCurrentUser").invoke(null); // .getCurrentUser();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        mContext = context;
    }

    public int getCurrentUserId() {
        return mCurrentUserId;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
            int oldUserId = mCurrentUserId;
            mCurrentUserId = intent.getIntExtra("android.intent.extra.user_handle", 0);
            if (oldUserId != mCurrentUserId) {
                onUserSwitched(mCurrentUserId);
            }
        }
    }

    public void stopTracking() {
        mContext.unregisterReceiver(this);
    }

    public abstract void onUserSwitched(int newUserId);

    public boolean isCurrentUserOwner() {
        return mCurrentUserId == 0;
    }
}
