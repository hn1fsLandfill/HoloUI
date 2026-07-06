// Separated from BaseStatusBar

package eu.hn1f.holoui.notification;

import android.app.ActivityManagerNative;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.internal.statusbar.StatusBarIcon;

import java.util.Locale;

import eu.hn1f.holoui.R;
import eu.hn1f.holoui.SystemUIApplication;
import eu.hn1f.holoui.policy.NotificationRowLayout;
import eu.hn1f.holoui.widgets.ExpandableNotificationRow;
import eu.hn1f.holoui.widgets.SizeAdaptiveLayout;

public abstract class NotificationBase {
    public static final String TAG = "Notification";
    public static final boolean DEBUG = true;
    public final Context mContext;

    // all notifications
    protected NotificationData mNotificationData = new NotificationData();
    protected NotificationRowLayout mPile;

    protected NotificationData.Entry mInterruptingNotificationEntry;
    protected long mInterruptingNotificationTime;

    // used to notify status bar for suppressing notification LED
    protected boolean mPanelSlightlyVisible;

    protected PopupMenu mNotificationBlamePopup;

    protected int mCurrentUserId = 0; // TODO: Multiuser

    protected int mLayoutDirection = -1; // invalid
    private Locale mLocale;
    protected boolean mUseHeadsUp = false;

    protected IDreamManager mDreamManager;
    PowerManager mPowerManager;
    protected int mRowHeight;

    public static final int EXPANDED_LEAVE_ALONE = -10000;
    public static final int EXPANDED_FULL_OPEN = -10001;

    protected static final boolean ENABLE_HEADS_UP = false; // No heads up for now

    public NotificationBase(Context newContext) {
        mContext = newContext;
    }

    protected View.OnLongClickListener getNotificationLongClicker() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String packageNameF = (String) v.getTag();
                if (packageNameF == null) return false;
                if (v.getWindowToken() == null) return false;
                mNotificationBlamePopup = new PopupMenu(mContext, v);
                mNotificationBlamePopup.getMenuInflater().inflate(
                        R.menu.notification_popup_menu,
                        mNotificationBlamePopup.getMenu());
                mNotificationBlamePopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.notification_inspect_item) {
                            // startApplicationDetailsActivity(packageNameF);
                            // TODO animateCollapsePanels(CommandQueue.FLAG_EXCLUDE_NONE);
                        } else {
                            return false;
                        }
                        return true;
                    }
                });
                mNotificationBlamePopup.show();

                return true;
            }
        };
    }

    public boolean notificationIsForCurrentUser(StatusBarNotification n) {
        final int thisUserId = mCurrentUserId;
        final int notificationUserId = n.getUserId();
        if (DEBUG) {
            Log.v(TAG, String.format("%s: current userid: %d, notification userid: %d",
                    n, thisUserId, notificationUserId));
        }
        return notificationUserId == UserHandle.USER_ALL
                || thisUserId == notificationUserId;
    }

    public NotificationClicker makeClicker(PendingIntent intent, String pkg, String tag, int id) {
        return new NotificationClicker(intent, pkg, tag, id);
    }

    protected class NotificationClicker implements View.OnClickListener {
        private PendingIntent mIntent;
        private String mPkg;
        private String mTag;
        private int mId;

        public NotificationClicker(PendingIntent intent, String pkg, String tag, int id) {
            mIntent = intent;
            mPkg = pkg;
            mTag = tag;
            mId = id;
        }

        public void onClick(View v) {
            try {
                // The intent we are sending is for the application, which
                // won't have permission to immediately start an activity after
                // the user switches to home.  We know it is safe to do at this
                // point, so make sure new activity switches are now allowed.
                ActivityManagerNative.getDefault().resumeAppSwitches();
                // Also, notifications can be launched from the lock screen,
                // so dismiss the lock screen when the activity starts.
                // ActivityManagerNative.getDefault().dismissKeyguardOnNextActivity();
            } catch (RemoteException e) {
            }

            if (mIntent != null) {
                int[] pos = new int[2];
                v.getLocationOnScreen(pos);
                Intent overlay = new Intent();
                overlay.setSourceBounds(
                        new Rect(pos[0], pos[1], pos[0]+v.getWidth(), pos[1]+v.getHeight()));
                try {
                    mIntent.send(mContext, 0, overlay);
                } catch (PendingIntent.CanceledException e) {
                    // the stack trace isn't very helpful here.  Just log the exception message.
                    Log.w(TAG, "Sending contentIntent failed: " + e);
                }

                // TODO KeyguardTouchDelegate.getInstance(mContext).dismiss();
            }

            /* try {
                mBarService.onNotificationClick(mPkg, mTag, mId);
            } catch (RemoteException ex) {
                // system process is dead if we're here.
            } */

            // close the shade if it was open
            ((SystemUIApplication)mContext.getApplicationContext()).getStatusBar()
                    .getShade().hide();
        }
    }

    private RemoteViews.InteractionHandler mOnClickHandler = new RemoteViews.InteractionHandler() {
        private boolean onClickHandlerCompat(View view, PendingIntent pendingIntent,
                                             RemoteViews.RemoteResponse fillInIntent) {
            try {
                // TODO: Unregister this handler if PendingIntent.FLAG_ONE_SHOT?
                Context context = view.getContext();
                Pair<Intent, ActivityOptions> opts = fillInIntent.getLaunchOptions(view);
                context.startIntentSender(
                        pendingIntent.getIntentSender(), opts.first,
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                        Intent.FLAG_ACTIVITY_NEW_TASK, 0, opts.second.toBundle());
            } catch (IntentSender.SendIntentException e) {
                android.util.Log.e(TAG, "Cannot send pending intent: ", e);
                return false;
            } catch (Exception e) {
                android.util.Log.e(TAG, "Cannot send pending intent due to " +
                        "unknown exception: ", e);
                return false;
            }
            return true;
        }

        @Override
        public boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse fillInIntent) {
            if (DEBUG) {
                Log.v(TAG, "Notification click handler invoked for intent: " + pendingIntent);
            }
            final boolean isActivity = pendingIntent.isActivity();
            if (isActivity) {
                try {
                    // The intent we are sending is for the application, which
                    // won't have permission to immediately start an activity after
                    // the user switches to home.  We know it is safe to do at this
                    // point, so make sure new activity switches are now allowed.
                    ActivityManagerNative.getDefault().resumeAppSwitches();
                    // Also, notifications can be launched from the lock screen,
                    // so dismiss the lock screen when the activity starts.
                    // TODO ActivityManagerNative.getDefault().dismissKeyguardOnNextActivity();
                } catch (RemoteException e) {
                }
            }

            boolean handled = this.onClickHandlerCompat(view, pendingIntent, fillInIntent);

            if (isActivity && handled) {
                // close the shade if it was open
                ((SystemUIApplication)mContext.getApplicationContext()).getStatusBar()
                        .getShade().hide();
                // visibilityChanged(false);
            }
            return handled;
        }
    };

    protected void applyLegacyRowBackground(StatusBarNotification sbn, View content) {
            int version = 0;
            try {
                ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(sbn.getPackageName(), 0);
                version = info.targetSdkVersion;
            } catch (PackageManager.NameNotFoundException ex) {
                Log.e(TAG, "Failed looking up ApplicationInfo for " + sbn.getPackageName(), ex);
            }
            if (version > 0 && version < Build.VERSION_CODES.GINGERBREAD) {
                // TODO: normally this is notification_row_legacy_bg
                content.setBackgroundResource(R.drawable.notification_bg);
            } else {
                content.setBackgroundResource(R.drawable.notification_bg);
            }
    }

    public boolean inflateViews(NotificationData.Entry entry, ViewGroup parent) {
        int minHeight =
                mContext.getResources().getDimensionPixelSize(R.dimen.notification_min_height);
        int maxHeight =
                mContext.getResources().getDimensionPixelSize(R.dimen.notification_max_height);
        StatusBarNotification sbn = entry.notification;
        RemoteViews contentView = sbn.getNotification().contentView;
        RemoteViews bigContentView = sbn.getNotification().bigContentView;
        if (contentView == null) {
            return false;
        }

        // create the row view
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ExpandableNotificationRow row = (ExpandableNotificationRow) inflater.inflate(
                R.layout.notification_row, parent, false);

        // for blaming (see SwipeHelper.setLongPressListener)
        row.setTag(sbn.getPackageName());

        View vetoButton = updateNotificationVetoButton(row, sbn);
        vetoButton.setContentDescription(mContext.getString(
                R.string.accessibility_remove_notification));

        // NB: the large icon is now handled entirely by the template

        // bind the click event to the content area
        ViewGroup content = (ViewGroup)row.findViewById(R.id.content);
        ViewGroup adaptive = (ViewGroup)row.findViewById(R.id.adaptive);

        content.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        PendingIntent contentIntent = sbn.getNotification().contentIntent;
        if (contentIntent != null) {
            final View.OnClickListener listener = new NotificationClicker(contentIntent,
                    sbn.getPackageName(), sbn.getTag(), sbn.getId());
            content.setOnClickListener(listener);
        } else {
            content.setOnClickListener(null);
        }

        View contentViewLocal = null;
        View bigContentViewLocal = null;
        try {
            contentViewLocal = contentView.apply(mContext, adaptive, mOnClickHandler);
            if (bigContentView != null) {
                bigContentViewLocal = bigContentView.apply(mContext, adaptive, mOnClickHandler);
            }
        }
        catch (RuntimeException e) {
            final String ident = sbn.getPackageName() + "/0x" + Integer.toHexString(sbn.getId());
            Log.e(TAG, "couldn't inflate view for notification " + ident, e);
            return false;
        }

        if (contentViewLocal != null) {
            SizeAdaptiveLayout.LayoutParams params =
                    new SizeAdaptiveLayout.LayoutParams(contentViewLocal.getLayoutParams());
            params.minHeight = minHeight;
            params.maxHeight = minHeight;
            adaptive.addView(contentViewLocal, params);
        }
        if (bigContentViewLocal != null) {
            SizeAdaptiveLayout.LayoutParams params =
                    new SizeAdaptiveLayout.LayoutParams(bigContentViewLocal.getLayoutParams());
            params.minHeight = minHeight+1;
            params.maxHeight = maxHeight;
            adaptive.addView(bigContentViewLocal, params);
        }
        row.setDrawingCacheEnabled(true);

        applyLegacyRowBackground(sbn, content);

        if (DEBUG) {
            TextView debug = (TextView) row.findViewById(R.id.debug_info);
            if (debug != null) {
                debug.setVisibility(View.VISIBLE);
                debug.setText("U " + entry.notification.getUserId());
            }
        }
        entry.row = row;
        entry.row.setRowHeight(mRowHeight);
        entry.content = content;
        entry.expanded = contentViewLocal;
        entry.setBigContentView(bigContentViewLocal);

        return true;
    }

    /**
     * Cancel this notification and tell the StatusBarManagerService / NotificationManagerService
     * about the failure.
     *
     * WARNING: this will call back into us.  Don't hold any locks.
     */
    // public abstract void removeNotification(String key);

    void handleNotificationError(String key, StatusBarNotification n, String message) {
        removeNotification(n);

        Log.e(TAG, "Notification error: "+message);
        /* try {
            mBarService.onNotificationError(n.getPackageName(), n.getTag(), n.getId(), n.getUid(), n.getInitialPid(), message);
        } catch (RemoteException ex) {
            // The end is nigh.
        } */
    }

    protected StatusBarNotification removeNotificationViews(String key) {
        NotificationData.Entry entry = mNotificationData.remove(key);
        if (entry == null) {
            Log.w(TAG, "removeNotification for unknown key: " + key);
            return null;
        }
        // Remove the expanded view.
        ViewGroup rowParent = (ViewGroup)entry.row.getParent();
        if (rowParent != null) rowParent.removeView(entry.row);
        updateExpansionStates();
        updateNotificationIcons();

        return entry.notification;
    }

    protected NotificationData.Entry createNotificationViews(String key,
                                                             StatusBarNotification notification) {
        if (DEBUG) {
            Log.d(TAG, "createNotificationViews(key=" + key + ", notification=" + notification);
        }
        // Construct the icon.
        final StatusBarIconView iconView = new StatusBarIconView(mContext,
                notification.getPackageName() + "/0x" + Integer.toHexString(notification.getId()),
                notification.getNotification());
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        final StatusBarIcon ic = new StatusBarIcon(notification.getUser(),
                notification.getPackageName(),
                notification.getNotification().getSmallIcon(),
                notification.getNotification().iconLevel,
                notification.getNotification().number,
                notification.getNotification().tickerText, StatusBarIcon.Type.NotifSmallIcon);
        if (!iconView.set(ic)) {
            handleNotificationError(key, notification, "Couldn't create icon: " + ic);
            return null;
        }
        // Construct the expanded view.
        NotificationData.Entry entry = new NotificationData.Entry(key, notification, iconView);
        if (!inflateViews(entry, mPile)) {
            handleNotificationError(key, notification, "Couldn't expand RemoteViews for: "
                    + notification);
            return null;
        }
        return entry;
    }

    protected void addNotificationViews(NotificationData.Entry entry) {
        // Add the expanded view and icon.
        int pos = mNotificationData.add(entry);
        if (DEBUG) {
            Log.d(TAG, "addNotificationViews: added at " + pos);
        }
        updateExpansionStates();
        updateNotificationIcons();
    }

    private void addNotificationViews(String key, StatusBarNotification notification) {
        addNotificationViews(createNotificationViews(key, notification));
    }

    protected void updateExpansionStates() {
        int N = mNotificationData.size();
        for (int i = 0; i < N; i++) {
            NotificationData.Entry entry = mNotificationData.get(i);
            if (!entry.row.isUserLocked()) {
                if (i == (N-1)) {
                    if (DEBUG) Log.d(TAG, "expanding top notification at " + i);
                    entry.row.setExpanded(true);
                } else {
                    if (!entry.row.isUserExpanded()) {
                        if (DEBUG) Log.d(TAG, "collapsing notification at " + i);
                        entry.row.setExpanded(false);
                    } else {
                        if (DEBUG) Log.d(TAG, "ignoring user-modified notification at " + i);
                    }
                }
            } else {
                if (DEBUG) Log.d(TAG, "ignoring notification being held by user at " + i);
            }
        }
    }

    protected abstract void haltTicker();
    protected abstract void setAreThereNotifications();
    protected abstract void updateNotificationIcons();
    protected abstract void tick(String key, StatusBarNotification n, boolean firstTime);
    protected abstract void updateExpandedViewPos(int expandedPosition);
    protected abstract int getExpandedViewMaxHeight();
    protected abstract boolean shouldDisableNavbarGestures();

    protected boolean isTopNotification(ViewGroup parent, NotificationData.Entry entry) {
        return parent != null && parent.indexOfChild(entry.row) == 0;
    }

    protected View updateNotificationVetoButton(View row, StatusBarNotification n) {
        View vetoButton = row.findViewById(R.id.veto);
        if (n.isClearable() || (mInterruptingNotificationEntry != null
                && mInterruptingNotificationEntry.row == row)) {
            final String _pkg = n.getPackageName();
            final String _tag = n.getTag();
            final int _id = n.getId();
            vetoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Accessibility feedback
                    v.announceForAccessibility(
                            mContext.getString(R.string.accessibility_notification_dismissed));
                    /* try {
                        mBarService.onNotificationClear(_pkg, _tag, _id);

                    } catch (RemoteException ex) {
                        // system process is dead if we're here.
                    } */
                }
            });
            vetoButton.setVisibility(View.VISIBLE);
        } else {
            vetoButton.setVisibility(View.GONE);
        }
        vetoButton.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        return vetoButton;
    }

    public void updateNotification(String key, StatusBarNotification notification) {
        if (DEBUG) Log.d(TAG, "updateNotification(" + key + " -> " + notification + ")");

        final NotificationData.Entry oldEntry = mNotificationData.findByKey(key);
        if (oldEntry == null) {
            Log.w(TAG, "updateNotification for unknown key: " + key);
            return;
        }

        final StatusBarNotification oldNotification = oldEntry.notification;

        // XXX: modify when we do something more intelligent with the two content views
        final RemoteViews oldContentView = oldNotification.getNotification().contentView;
        final RemoteViews contentView = notification.getNotification().contentView;
        final RemoteViews oldBigContentView = oldNotification.getNotification().bigContentView;
        final RemoteViews bigContentView = notification.getNotification().bigContentView;

        if (DEBUG) {
            Log.d(TAG, "old notification: when=" + oldNotification.getNotification().when
                    + " ongoing=" + oldNotification.isOngoing()
                    + " expanded=" + oldEntry.expanded
                    + " contentView=" + oldContentView
                    + " bigContentView=" + oldBigContentView
                    + " rowParent=" + oldEntry.row.getParent());
            Log.d(TAG, "new notification: when=" + notification.getNotification().when
                    + " ongoing=" + oldNotification.isOngoing()
                    + " contentView=" + contentView
                    + " bigContentView=" + bigContentView);
        }

        // Can we just reapply the RemoteViews in place?  If when didn't change, the order
        // didn't change.

        // 1U is never null
        boolean contentsUnchanged = oldEntry.expanded != null
                && contentView.getPackage() != null
                && oldContentView.getPackage() != null
                && oldContentView.getPackage().equals(contentView.getPackage())
                && oldContentView.getLayoutId() == contentView.getLayoutId();
        // large view may be null
        boolean bigContentsUnchanged =
                (oldEntry.getBigContentView() == null && bigContentView == null)
                        || ((oldEntry.getBigContentView() != null && bigContentView != null)
                        && bigContentView.getPackage() != null
                        && oldBigContentView.getPackage() != null
                        && oldBigContentView.getPackage().equals(bigContentView.getPackage())
                        && oldBigContentView.getLayoutId() == bigContentView.getLayoutId());
        ViewGroup rowParent = (ViewGroup) oldEntry.row.getParent();
        boolean orderUnchanged = notification.getNotification().when== oldNotification.getNotification().when;
        // score now encompasses/supersedes isOngoing()

        boolean updateTicker = notification.getNotification().tickerText != null
                && !TextUtils.equals(notification.getNotification().tickerText,
                oldEntry.notification.getNotification().tickerText);
        boolean isTopAnyway = isTopNotification(rowParent, oldEntry);
        if (contentsUnchanged && bigContentsUnchanged && (orderUnchanged || isTopAnyway)) {
            if (DEBUG) Log.d(TAG, "reusing notification for key: " + key);
            oldEntry.notification = notification;
            try {
                updateNotificationViews(oldEntry, notification);

                if (ENABLE_HEADS_UP && mInterruptingNotificationEntry != null
                        && oldNotification == mInterruptingNotificationEntry.notification) {
                    if (!shouldInterrupt(notification)) {
                        if (DEBUG) Log.d(TAG, "no longer interrupts!");
                        // mHandler.sendEmptyMessage(MSG_HIDE_HEADS_UP);
                    } else {
                        if (DEBUG) Log.d(TAG, "updating the current heads up:" + notification);
                        mInterruptingNotificationEntry.notification = notification;
                        updateNotificationViews(mInterruptingNotificationEntry, notification);
                    }
                }

                // Update the icon.
                final StatusBarIcon ic = new StatusBarIcon(notification.getUser(),
                        notification.getPackageName(),
                        notification.getNotification().getSmallIcon(), notification.getNotification().iconLevel,
                        notification.getNotification().number,
                        notification.getNotification().tickerText, StatusBarIcon.Type.NotifSmallIcon);
                if (!oldEntry.icon.set(ic)) {
                    handleNotificationError(key, notification, "Couldn't update icon: " + ic);
                    return;
                }
                updateExpansionStates();
            }
            catch (RuntimeException e) {
                // It failed to add cleanly.  Log, and remove the view from the panel.
                Log.w(TAG, "Couldn't reapply views for package " + contentView.getPackage(), e);
                removeNotificationViews(key);
                addNotificationViews(key, notification);
            }
        } else {
            if (DEBUG) Log.d(TAG, "not reusing notification for key: " + key);
            if (DEBUG) Log.d(TAG, "contents was " + (contentsUnchanged ? "unchanged" : "changed"));
            if (DEBUG) Log.d(TAG, "order was " + (orderUnchanged ? "unchanged" : "changed"));
            if (DEBUG) Log.d(TAG, "notification is " + (isTopAnyway ? "top" : "not top"));
            final boolean wasExpanded = oldEntry.row.isUserExpanded();
            removeNotificationViews(key);
            addNotificationViews(key, notification);  // will also replace the heads up
            if (wasExpanded) {
                final NotificationData.Entry newEntry = mNotificationData.findByKey(key);
                newEntry.row.setExpanded(true);
                newEntry.row.setUserExpanded(true);
            }
        }

        // Update the veto button accordingly (and as a result, whether this row is
        // swipe-dismissable)
        updateNotificationVetoButton(oldEntry.row, notification);

        // Is this for you?
        boolean isForCurrentUser = notificationIsForCurrentUser(notification);
        if (DEBUG) Log.d(TAG, "notification is " + (isForCurrentUser ? "" : "not ") + "for you");

        // Restart the ticker if it's still running
        if (updateTicker && isForCurrentUser) {
            haltTicker();
            tick(key, notification, false);
        }

        // Recalculate the position of the sliding windows and the titles.
        setAreThereNotifications();
        updateExpandedViewPos(EXPANDED_LEAVE_ALONE);
    }

    private void updateNotificationViews(NotificationData.Entry entry,
                                         StatusBarNotification notification) {
        final RemoteViews contentView = notification.getNotification().contentView;
        final RemoteViews bigContentView = notification.getNotification().bigContentView;
        // Reapply the RemoteViews
        contentView.reapply(mContext, entry.expanded, mOnClickHandler);
        if (bigContentView != null && entry.getBigContentView() != null) {
            bigContentView.reapply(mContext, entry.getBigContentView(), mOnClickHandler);
        }
        // update the contentIntent
        final PendingIntent contentIntent = notification.getNotification().contentIntent;
        if (contentIntent != null) {
            final View.OnClickListener listener = makeClicker(contentIntent,
                    notification.getPackageName(), notification.getTag(), notification.getId());
            entry.content.setOnClickListener(listener);
        } else {
            entry.content.setOnClickListener(null);
        }
    }

    protected void notifyHeadsUpScreenOn(boolean screenOn) {
        /*if (!screenOn && mInterruptingNotificationEntry != null) {
            mHandler.sendEmptyMessage(MSG_ESCALATE_HEADS_UP);
        } */
    }

    // EXTRA_NOTIFICATION_ID

    protected boolean shouldInterrupt(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        // some predicates to make the boolean logic legible
        boolean isNoisy = (notification.defaults & Notification.DEFAULT_SOUND) != 0
                || (notification.defaults & Notification.DEFAULT_VIBRATE) != 0
                || notification.sound != null
                || notification.vibrate != null;
        boolean isHighPriority = sbn.getNotification().priority == Notification.PRIORITY_HIGH;
        boolean isFullscreen = notification.fullScreenIntent != null;

        // final KeyguardTouchDelegate keyguard = KeyguardTouchDelegate.getInstance(mContext);
        boolean interrupt = (isFullscreen || (isHighPriority && isNoisy))
                && mPowerManager.isScreenOn();
                /* TODO && !keyguard.isShowingAndNotHidden()
                && !keyguard.isInputRestricted(); */
        try {
            interrupt = interrupt && !mDreamManager.isDreaming();
        } catch (RemoteException e) {
            Log.d(TAG, "failed to query dream manager", e);
        }
        if (DEBUG) Log.d(TAG, "interrupt: " + interrupt);
        return interrupt;
    }

    public void addNotification(StatusBarNotification notification) {
        String key = notification.getPackageName();

        if(mNotificationData.findByKey(key) != null) {
            updateNotification(key, notification);
            return;
        }

        NotificationData.Entry shadeEntry = createNotificationViews(key, notification);
        if (shadeEntry == null) {
            return;
        }
        /* TODO: Heads up if (mUseHeadsUp && shouldInterrupt(notification)) {
            if (DEBUG) Log.d(TAG, "launching notification in heads up mode");
            NotificationData.Entry interruptionCandidate = new NotificationData.Entry(key, notification, null);
            if (inflateViews(interruptionCandidate, mHeadsUpNotificationView.getHolder())) {
                mInterruptingNotificationTime = System.currentTimeMillis();
                mInterruptingNotificationEntry = interruptionCandidate;
                shadeEntry.setInterruption();

                // 1. Populate mHeadsUpNotificationView
                mHeadsUpNotificationView.setNotification(mInterruptingNotificationEntry);

                // 2. Animate mHeadsUpNotificationView in
                mHandler.sendEmptyMessage(MSG_SHOW_HEADS_UP);

                // 3. Set alarm to age the notification off
                resetHeadsUpDecayTimer();
            }
        } else */ if (notification.getNotification().fullScreenIntent != null) {
            // Stop screensaver if the notification has a full-screen intent.
            // (like an incoming phone call)
            // TODO awakenDreams();

            // not immersive & a full-screen alert should be shown
            if (DEBUG) Log.d(TAG, "Notification has fullScreenIntent; sending fullScreenIntent");
            try {
                notification.getNotification().fullScreenIntent.send();
            } catch (PendingIntent.CanceledException e) {
            }
        } else {
            // usual case: status bar visible & not immersive

            // show the ticker if there isn't already a heads up
            if (mInterruptingNotificationEntry == null) {
                tick(null, notification, true);
            }
        }
        addNotificationViews(shadeEntry);
        // Recalculate the position of the sliding windows and the titles.
        setAreThereNotifications();
        updateExpandedViewPos(EXPANDED_LEAVE_ALONE);
    }

    public void removeNotification(StatusBarNotification notification) {
        String key = notification.getPackageName();

        StatusBarNotification old = removeNotificationViews(key);
        // if (SPEW) Log.d(TAG, "removeNotification key=" + key + " old=" + old);

        if (old != null) {
            // Cancel the ticker if it's still running
            // mTicker.removeEntry(old);

            // Recalculate the position of the sliding windows and the titles.
            updateExpandedViewPos(EXPANDED_LEAVE_ALONE);

            /* TODO if (ENABLE_HEADS_UP && mInterruptingNotificationEntry != null
                    && old == mInterruptingNotificationEntry.notification) {
                mHandler.sendEmptyMessage(MSG_HIDE_HEADS_UP);
            }

            if (CLOSE_PANEL_WHEN_EMPTIED && mNotificationData.size() == 0
                    && !mNotificationPanel.isTracking()) {
                animateCollapsePanels();
            } */
        }

        setAreThereNotifications();
    }
}
