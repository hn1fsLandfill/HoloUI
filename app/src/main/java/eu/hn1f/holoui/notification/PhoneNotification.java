package eu.hn1f.holoui.notification;

import android.content.Context;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import eu.hn1f.holoui.R;
import eu.hn1f.holoui.SystemUIApplication;

public class PhoneNotification extends NotificationBase {
    boolean mExpandedVisible;

    int mNaturalBarHeight = -1;
    int mIconSize = -1;
    int mIconHPadding = -1;

    SystemUIApplication mApplication;
    IconMerger mNotificationIcons;

    public PhoneNotification(Context newContext) {
        super(newContext);
        mApplication = (SystemUIApplication)mContext.getApplicationContext();

        mPile = mApplication.getStatusBar().getShade()
                .getRoot().findViewById(R.id.latestItems);
        mNotificationIcons = mApplication.getStatusBar()
                .getRoot().findViewById(R.id.notificationIcons);
    }

    @Override
    protected void haltTicker() {}

    @Override
    protected void setAreThereNotifications() {}

    @Override
    protected void updateNotificationIcons() {
        final LinearLayout.LayoutParams params
                = new LinearLayout.LayoutParams(mIconSize + 2*mIconHPadding, mNaturalBarHeight);

        int N = mNotificationData.size();

        if (DEBUG) {
            Log.d(TAG, "refreshing icons: " + N + " notifications, mNotificationIcons=" + mNotificationIcons);
        }

        ArrayList<View> toShow = new ArrayList<View>();

        // If the device hasn't been through Setup, we only show system notifications
        for (int i=0; i<N; i++) {
            NotificationData.Entry ent = mNotificationData.get(N-i-1);
            if (!notificationIsForCurrentUser(ent.notification)) continue;
            toShow.add(ent.icon);
        }

        // Buggy for now
        /* ArrayList<View> toRemove = new ArrayList<View>();
        for (int i=0; i<mNotificationIcons.getChildCount(); i++) {
            View child = mNotificationIcons.getChildAt(i);
            if (!toShow.contains(child)) {
                toRemove.add(child);
            }
        }

        for (View remove : toRemove) {
            mNotificationIcons.removeView(remove);
        } */

        mNotificationIcons.removeAllViews();

        for (int i=0; i<toShow.size(); i++) {
            View v = toShow.get(i);
            if (v.getParent() == null) {
                mNotificationIcons.addView(v, i, params);
            }
        }

        updateNotificationShade();
    }

    protected void updateNotificationShade() {
        int N = mNotificationData.size();

        ArrayList<View> toShow = new ArrayList<View>();

        // If the device hasn't been through Setup, we only show system notifications
        for (int i=0; i<N; i++) {
            NotificationData.Entry ent = mNotificationData.get(N-i-1);
            if (!notificationIsForCurrentUser(ent.notification)) continue;
            toShow.add(ent.row);
        }

        ArrayList<View> toRemove = new ArrayList<View>();
        for (int i=0; i<mPile.getChildCount(); i++) {
            View child = mPile.getChildAt(i);
            if (!toShow.contains(child)) {
                toRemove.add(child);
            }
        }

        for (View remove : toRemove) {
            mPile.removeView(remove);
        }

        for (int i=0; i<N; i++) {
            NotificationData.Entry ent = mNotificationData.get(N-i-1);
            if (!notificationIsForCurrentUser(ent.notification)) continue;
            toShow.add(ent.row);
        }

        for (int i=0; i<toShow.size(); i++) {
            View v = toShow.get(i);
            if (v.getParent() == null) {
                mPile.addView(v, i);
            }
        }
    }

    @Override
    protected void tick(String key, StatusBarNotification n, boolean firstTime) {}

    @Override
    public void updateExpandedViewPos(int thingy) {}

    @Override
    protected int getExpandedViewMaxHeight() {
        return 0;
    }

    @Override
    protected boolean shouldDisableNavbarGestures() {
        return mExpandedVisible;
    }
}
