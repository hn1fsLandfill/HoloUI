// Separated from Notification.java
package eu.hn1f.holoui.notification;

import static android.app.Notification.PRIORITY_LOW;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DateTimeView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

    public JohnNotificationBuilder(Context context, Notification newNotif) {
        notif = newNotif;
        mContext = context;

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

    private View applyStandardTemplate(int resId, boolean fitIn1U) {
        ViewGroup contentView = (ViewGroup) inflater.inflate(resId, null);

        boolean showLine3 = false;
        boolean showLine2 = false;
        ImageView smallIconImageView = contentView.findViewById(R.id.icon);
        Chronometer chronometerView = contentView.findViewById(R.id.chronometer);
        DateTimeView timeView = contentView.findViewById(R.id.time);
        ProgressBar progressView = contentView.findViewById(R.id.progress);
        TextView info = contentView.findViewById(R.id.info);
        TextView title = contentView.findViewById(R.id.title);
        TextView text = contentView.findViewById(R.id.text);
        TextView text2 = contentView.findViewById(R.id.text2);
        LinearLayout line1 = contentView.findViewById(R.id.line1);
        LinearLayout line3 = contentView.findViewById(R.id.line3);

        if (notif.getLargeIcon() != null) {
            smallIconImageView.setImageIcon(notif.getLargeIcon());
            smallIconImageView = contentView.findViewById(R.id.right_icon);
        }
        if (notif.priority < PRIORITY_LOW) {
            contentView.findViewById(R.id.icon)
                    .setBackgroundResource(R.drawable.notification_template_icon_low_bg);
            contentView.findViewById(R.id.status_bar_latest_event_content)
                    .setBackgroundResource(R.drawable.notification_bg_low);
        }
        if (notif.getSmallIcon() != null) {
            smallIconImageView.setImageIcon(notif.getSmallIcon());
            smallIconImageView.setVisibility(View.VISIBLE);
        } else {
            smallIconImageView.setVisibility(View.GONE);
        }
        if (mContentTitle != null) {
            title.setText(mContentTitle);
        }
        if (mContentText != null) {
            text.setText(mContentText);
            showLine3 = true;
        }
        if (mContentInfo != null) {
            info.setText(mContentInfo);
            info.setVisibility(View.VISIBLE);
            showLine3 = true;
        } else if (mNumber > 0) {
            final int tooBig = mContext.getResources().getInteger(
                    R.integer.status_bar_notification_info_maxnum);
            if (mNumber > tooBig) {
                info.setText(mContext.getResources().getString(
                        R.string.status_bar_notification_info_overflow));
            } else {
                NumberFormat f = NumberFormat.getIntegerInstance();
                info.setText(f.format(mNumber));
            }
            info.setVisibility(View.VISIBLE);
            showLine3 = true;
        } else {
            info.setVisibility(View.GONE);
        }

        // Need to show three lines?
        if (mSubText != null) {
            text.setText(mSubText);
            if (mContentText != null) {
                text2.setText(mContentText);
                text2.setVisibility(View.VISIBLE);
                showLine2 = true;
            } else {
                text2.setVisibility(View.GONE);
            }
        } else {
            text2.setVisibility(View.GONE);
            if (mProgressMax != 0 || mProgressIndeterminate) {
                progressView.setMax(mProgressMax);
                progressView.setIndeterminate(mProgressIndeterminate);
                progressView.setProgress(mProgress);
                progressView.setVisibility(View.VISIBLE);
                showLine2 = true;
            } else {
                progressView.setVisibility(View.GONE);
            }
        }
        if (showLine2) {
            if (fitIn1U) {
                // need to shrink all the type to make sure everything fits
                final Resources res = mContext.getResources();
                final float subTextSize = res.getDimensionPixelSize(
                        R.dimen.notification_subtext_size);
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, subTextSize);
            }
            // vertical centering
            line1.setPadding(0, 0, 0, 0);
        }

        if (mWhen != 0 && mShowWhen) {
            if (mUseChronometer) {
                chronometerView.setVisibility(View.VISIBLE);
                chronometerView.setBase(
                        mWhen + (SystemClock.elapsedRealtime() - System.currentTimeMillis())
                );
                chronometerView.start();
            } else {
                timeView.setVisibility(View.VISIBLE);
                timeView.setTime(mWhen);
            }
        } else {
            timeView.setVisibility(View.GONE);
        }

        line3.setVisibility(showLine3 ? View.VISIBLE : View.GONE);
        View overflow = contentView.findViewById(R.id.overflow_divider);
        if(overflow != null)
                overflow.setVisibility(showLine3 ? View.VISIBLE : View.GONE);

        return contentView;
    }

    private View applyStandardTemplateWithActions(int layoutId) {
        ViewGroup big = (ViewGroup) applyStandardTemplate(layoutId, false);

        int N = mActions.size();
        if (N > 0) {
            // Log.d("Notification", "has actions: " + mContentText);
            ViewGroup actions = big.findViewById(R.id.actions);
            actions.setVisibility(View.VISIBLE);
            big.findViewById(R.id.action_divider).setVisibility(View.VISIBLE);
            if (N>MAX_ACTION_BUTTONS) N=MAX_ACTION_BUTTONS;
            actions.removeAllViews();
            for (int i=0; i<N; i++) {
                final Button button = generateActionButton(mActions.get(i));
                //Log.d("Notification", "adding action " + i + ": " + mActions.get(i).title);
                actions.addView(button);
            }
        }
        return big;
    }

    public View makeContentView() {
        return applyStandardTemplate(R.layout.notification_template_base, true); // no more special large_icon flavor
    }

    /* TODO: Add ticking later? public View makeTickerView() {
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

    public View makeBigContentView() {
        if (mActions.isEmpty()) return null;

        return applyStandardTemplateWithActions(R.layout.notification_template_big_base);
    }

    private Button generateActionButton(Notification.Action action) {
        final boolean tombstone = (action.actionIntent == null);
        Button button = (Button) inflater.inflate(
                tombstone ? R.layout.notification_action_tombstone
                        : R.layout.notification_action, null);

        TextView action0 = button.findViewById(R.id.action0);

        Drawable icon = null;
        try {
            if(action.getIcon() != null)
                icon = action.getIcon().loadDrawable(mContext);
        } catch(Resources.NotFoundException ignored) {
            icon = mContext.getResources().getDrawable(android.R.drawable.btn_plus);
        }
        action0.setCompoundDrawablesRelative(icon, null, null, null);
        action0.setText(action.title);
        if (!tombstone) {
            button.setOnClickListener(ignored -> {
                try {
                    action.actionIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    // Whoopsie
                    Log.v("HoloUI", "Notification action has been canceled?\n"+e.getMessage());
                }
            });
        }
        action0.setContentDescription(action.title);
        return button;
    }
}
