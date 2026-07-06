// Separated from Notification.java
package eu.hn1f.holoui.notification;

import static android.app.Notification.PRIORITY_LOW;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

import eu.hn1f.holoui.R;

public class JohnNotificationBuilder {
    private static final int MAX_ACTION_BUTTONS = 3;

    Notification notif;
    Context mContext;

    CharSequence mContentTitle;
    CharSequence mContentText;
    CharSequence mContentInfo;
    int mNumber;
    CharSequence mSubText;
    int mProgressMax;
    int mProgress;
    boolean mProgressIndeterminate;
    long mWhen;
    boolean mShowWhen;
    boolean mUseChronometer;
    ArrayList<Notification.Action> mActions;
    LayoutInflater inflater;
    ViewGroup parent;

    public JohnNotificationBuilder(Context context, Notification newNotif, ViewGroup parent2) {
        notif = newNotif;
        mContext = context;
        parent = parent2;

        mContentTitle = notif.extras.getCharSequence(Notification.EXTRA_TITLE);
        mContentText = notif.extras.getCharSequence(Notification.EXTRA_TEXT);
        mContentInfo = notif.extras.getCharSequence(Notification.EXTRA_INFO_TEXT);
        mNumber = notif.number;
        mSubText = notif.extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        mProgressMax = notif.extras.getInt(Notification.EXTRA_PROGRESS_MAX);
        mProgress = notif.extras.getInt(Notification.EXTRA_PROGRESS);
        mProgressIndeterminate = notif.extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE);
        mWhen = notif.when;
        mShowWhen = notif.extras.getBoolean(Notification.EXTRA_SHOW_WHEN);
        mUseChronometer = notif.extras.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER);
        if(notif.actions != null)
            mActions = new ArrayList<>(Arrays.asList(notif.actions));
        else
            mActions = new ArrayList<>();

        inflater = (LayoutInflater)mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    private RemoteViews applyStandardTemplate(int resId, boolean fitIn1U) {
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), resId);

        boolean showLine3 = false;
        boolean showLine2 = false;
        int smallIconImageViewId = R.id.icon;
        if (notif.getLargeIcon() != null) {
            contentView.setImageViewIcon(R.id.icon, notif.getLargeIcon());
            smallIconImageViewId = R.id.right_icon;
        }
        if (notif.priority < PRIORITY_LOW) {
            contentView.setInt(R.id.icon,
                    "setBackgroundResource", R.drawable.notification_template_icon_low_bg);
            contentView.setInt(R.id.status_bar_latest_event_content,
                    "setBackgroundResource", R.drawable.notification_bg_low);
        }
        if (notif.getSmallIcon() != null) {
            contentView.setImageViewIcon(smallIconImageViewId, notif.getSmallIcon());
            contentView.setViewVisibility(smallIconImageViewId, View.VISIBLE);
        } else {
            contentView.setViewVisibility(smallIconImageViewId, View.GONE);
        }
        if (mContentTitle != null) {
            contentView.setTextViewText(R.id.title, mContentTitle);
        }
        if (mContentText != null) {
            contentView.setTextViewText(R.id.text, mContentText);
            showLine3 = true;
        }
        if (mContentInfo != null) {
            contentView.setTextViewText(R.id.info, mContentInfo);
            contentView.setViewVisibility(R.id.info, View.VISIBLE);
            showLine3 = true;
        } else if (mNumber > 0) {
            final int tooBig = mContext.getResources().getInteger(
                    R.integer.status_bar_notification_info_maxnum);
            if (mNumber > tooBig) {
                contentView.setTextViewText(R.id.info, mContext.getResources().getString(
                        R.string.status_bar_notification_info_overflow));
            } else {
                NumberFormat f = NumberFormat.getIntegerInstance();
                contentView.setTextViewText(R.id.info, f.format(mNumber));
            }
            contentView.setViewVisibility(R.id.info, View.VISIBLE);
            showLine3 = true;
        } else {
            contentView.setViewVisibility(R.id.info, View.GONE);
        }

        // Need to show three lines?
        if (mSubText != null) {
            contentView.setTextViewText(R.id.text, mSubText);
            if (mContentText != null) {
                contentView.setTextViewText(R.id.text2, mContentText);
                contentView.setViewVisibility(R.id.text2, View.VISIBLE);
                showLine2 = true;
            } else {
                contentView.setViewVisibility(R.id.text2, View.GONE);
            }
        } else {
            contentView.setViewVisibility(R.id.text2, View.GONE);
            if (mProgressMax != 0 || mProgressIndeterminate) {
                contentView.setProgressBar(
                        R.id.progress, mProgressMax, mProgress, mProgressIndeterminate);
                contentView.setViewVisibility(R.id.progress, View.VISIBLE);
                showLine2 = true;
            } else {
                contentView.setViewVisibility(R.id.progress, View.GONE);
            }
        }
        if (showLine2) {
            if (fitIn1U) {
                // need to shrink all the type to make sure everything fits
                final Resources res = mContext.getResources();
                final float subTextSize = res.getDimensionPixelSize(
                        R.dimen.notification_subtext_size);
                contentView.setTextViewTextSize(R.id.text, TypedValue.COMPLEX_UNIT_PX, subTextSize);
            }
            // vertical centering
            contentView.setViewPadding(R.id.line1, 0, 0, 0, 0);
        }

        if (mWhen != 0 && mShowWhen) {
            if (mUseChronometer) {
                contentView.setViewVisibility(R.id.chronometer, View.VISIBLE);
                contentView.setLong(R.id.chronometer, "setBase",
                        mWhen + (SystemClock.elapsedRealtime() - System.currentTimeMillis()));
                contentView.setBoolean(R.id.chronometer, "setStarted", true);
            } else {
                contentView.setViewVisibility(R.id.time, View.VISIBLE);
                contentView.setLong(R.id.time, "setTime", mWhen);
            }
        } else {
            contentView.setViewVisibility(R.id.time, View.GONE);
        }

        contentView.setViewVisibility(R.id.line3, showLine3 ? View.VISIBLE : View.GONE);
        contentView.setViewVisibility(R.id.overflow_divider, showLine3 ? View.VISIBLE : View.GONE);

        return contentView;
    }

    private RemoteViews applyStandardTemplateWithActions(int layoutId) {
        RemoteViews big = applyStandardTemplate(layoutId, false);

        int N = mActions.size();
        if (N > 0) {
            // Log.d("Notification", "has actions: " + mContentText);
            big.setViewVisibility(R.id.actions, View.VISIBLE);
            big.setViewVisibility(R.id.action_divider, View.VISIBLE);
            if (N>MAX_ACTION_BUTTONS) N=MAX_ACTION_BUTTONS;
            big.removeAllViews(R.id.actions);
            for (int i=0; i<N; i++) {
                final RemoteViews button = generateActionButton(mActions.get(i));
                //Log.d("Notification", "adding action " + i + ": " + mActions.get(i).title);
                big.addView(R.id.actions, button);
            }
        }
        return big;
    }

    public RemoteViews makeContentView() {
        return applyStandardTemplate(R.layout.notification_template_base, true); // no more special large_icon flavor
    }

    /* TODO: Add ticking later? private RemoteViews makeTickerView() {
        if (mTickerView != null) {
            return mTickerView;
        } else {
            if (mContentView == null) {
                return applyStandardTemplate(notif.getLargeIcon() == null
                        ? R.layout.status_bar_latest_event_ticker
                        : R.layout.status_bar_latest_event_ticker_large_icon, true);
            } else {
                return null;
            }
        }
    } */

    public RemoteViews makeBigContentView() {
        if (mActions.isEmpty()) return null;

        return applyStandardTemplateWithActions(R.layout.notification_template_big_base);
    }

    private RemoteViews generateActionButton(Notification.Action action) {
        final boolean tombstone = (action.actionIntent == null);
        RemoteViews button = new RemoteViews(mContext.getPackageName(),
                tombstone ? R.layout.notification_action_tombstone
                        : R.layout.notification_action);
        button.setTextViewCompoundDrawablesRelative(R.id.action0, action.icon, 0, 0, 0);
        button.setTextViewText(R.id.action0, action.title);
        if (!tombstone) {
            button.setOnClickPendingIntent(R.id.action0, action.actionIntent);
        }
        button.setContentDescription(R.id.action0, action.title);
        return button;
    }
}
