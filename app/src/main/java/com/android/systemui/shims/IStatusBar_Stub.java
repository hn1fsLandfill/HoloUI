package com.android.systemui.shims;

import android.app.ITransientNotificationCallback;
import android.content.ComponentName;
import android.graphics.drawable.Icon;
import android.hardware.biometrics.IBiometricContextListener;
import android.hardware.biometrics.IBiometricSysuiReceiver;
import android.hardware.biometrics.PromptInfo;
import android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback;
import android.media.INearbyMediaDevicesProvider;
import android.media.MediaRoute2Info;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.view.KeyEvent;

import com.android.internal.statusbar.DisableStates;
import com.android.internal.statusbar.IAddTileResultCallback;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.IUndoMediaTransferCallback;
import com.android.internal.statusbar.LetterboxDetails;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.view.AppearanceRegion;

public abstract class IStatusBar_Stub extends IStatusBar.Stub {
    @Override
    public void abortTransient(int i, int i1) throws RemoteException {

    }

    @Override
    public void addQsTile(ComponentName componentName) throws RemoteException {

    }

    @Override
    public void addQsTileToFrontOrEnd(ComponentName componentName, boolean b) throws RemoteException {

    }

    @Override
    public void animateCollapsePanels() throws RemoteException {

    }

    @Override
    public void animateExpandNotificationsPanel() throws RemoteException {

    }

    @Override
    public void animateExpandSettingsPanel(String s) throws RemoteException {

    }

    @Override
    public void appTransitionCancelled(int i) throws RemoteException {

    }

    @Override
    public void appTransitionFinished(int i) throws RemoteException {

    }

    @Override
    public void appTransitionPending(int i) throws RemoteException {

    }

    @Override
    public void appTransitionStarting(int i, long l, long l1) throws RemoteException {

    }

    @Override
    public void cancelPreloadRecentApps() throws RemoteException {

    }

    @Override
    public void cancelRequestAddTile(String s) throws RemoteException {

    }

    @Override
    public void clickQsTile(ComponentName componentName) throws RemoteException {

    }

    @Override
    public void confirmImmersivePrompt() throws RemoteException {

    }

    @Override
    public void disable(int i, int i1, int i2) throws RemoteException {

    }

    @Override
    public void disableForAllDisplays(DisableStates disableStates) throws RemoteException {

    }

    @Override
    public void dismissInattentiveSleepWarning(boolean b) throws RemoteException {

    }

    @Override
    public void dismissKeyboardShortcutsMenu() throws RemoteException {

    }

    @Override
    public void dumpProto(String[] strings, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {

    }

    @Override
    public void handleSystemKey(KeyEvent keyEvent) throws RemoteException {

    }

    @Override
    public void hideAuthenticationDialog(long l) throws RemoteException {

    }

    @Override
    public void hideRecentApps(boolean b, boolean b1) throws RemoteException {

    }

    @Override
    public void hideToast(String s, IBinder iBinder) throws RemoteException {

    }

    @Override
    public void immersiveModeChanged(int i, boolean b, int i1) throws RemoteException {

    }

    @Override
    public void moveFocusedTaskToDesktop(int i) throws RemoteException {

    }

    @Override
    public void moveFocusedTaskToFullscreen(int i) throws RemoteException {

    }

    @Override
    public void moveFocusedTaskToStageSplit(int i, boolean b) throws RemoteException {

    }

    @Override
    public void onBiometricAuthenticated(int i) throws RemoteException {

    }

    @Override
    public void onBiometricError(int i, int i1, int i2) throws RemoteException {

    }

    @Override
    public void onBiometricHelp(int i, String s) throws RemoteException {

    }

    @Override
    public void onCameraLaunchGestureDetected(int i) throws RemoteException {

    }

    @Override
    public void onDisplayAddSystemDecorations(int i) throws RemoteException {

    }

    @Override
    public void onDisplayRemoveSystemDecorations(int i) throws RemoteException {

    }

    @Override
    public void onEmergencyActionLaunchGestureDetected() throws RemoteException {

    }

    @Override
    public void onProposedRotationChanged(int i, boolean b) throws RemoteException {

    }

    @Override
    public void onSystemBarAttributesChanged(int i, int i1, AppearanceRegion[] appearanceRegions, boolean b, int i2, int i3, String s, LetterboxDetails[] letterboxDetails) throws RemoteException {

    }

    @Override
    public void onWalletLaunchGestureDetected() throws RemoteException {

    }

    @Override
    public void passThroughShellCommand(String[] strings, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException {

    }

    @Override
    public void preloadRecentApps() throws RemoteException {

    }

    @Override
    public void registerNearbyMediaDevicesProvider(INearbyMediaDevicesProvider iNearbyMediaDevicesProvider) throws RemoteException {

    }

    @Override
    public void remQsTile(ComponentName componentName) throws RemoteException {

    }

    @Override
    public void removeIcon(String s) throws RemoteException {

    }

    @Override
    public void requestAddTile(int i, ComponentName componentName, CharSequence charSequence, CharSequence charSequence1, Icon icon, IAddTileResultCallback iAddTileResultCallback) throws RemoteException {

    }

    @Override
    public void requestMagnificationConnection(boolean b) throws RemoteException {

    }

    @Override
    public void requestTileServiceListeningState(ComponentName componentName) throws RemoteException {

    }

    @Override
    public void restartSystemUI() throws RemoteException {

    }

    @Override
    public void runGcForTest() throws RemoteException {

    }

    @Override
    public void setBiometicContextListener(IBiometricContextListener iBiometricContextListener) throws RemoteException {

    }

    @Override
    public void setIcon(String s, StatusBarIcon statusBarIcon) throws RemoteException {

    }

    @Override
    public void setImeWindowStatus(int i, int i1, int i2, boolean b) throws RemoteException {

    }

    @Override
    public void setNavigationBarLumaSamplingEnabled(int i, boolean b) throws RemoteException {

    }

    @Override
    public void setQsTiles(String[] strings) throws RemoteException {

    }

    @Override
    public void setSplitscreenFocus(boolean b) throws RemoteException {

    }

    @Override
    public void setTopAppHidesStatusBar(boolean b) throws RemoteException {

    }

    @Override
    public void setUdfpsRefreshRateCallback(IUdfpsRefreshRateRequestCallback iUdfpsRefreshRateRequestCallback) throws RemoteException {

    }

    @Override
    public void setWindowState(int i, int i1, int i2) throws RemoteException {

    }

    @Override
    public void showAssistDisclosure() throws RemoteException {

    }

    @Override
    public void showAuthenticationDialog(PromptInfo promptInfo, IBiometricSysuiReceiver iBiometricSysuiReceiver, int[] ints, boolean b, boolean b1, int i, long l, String s, long l1) throws RemoteException {

    }

    @Override
    public void showGlobalActionsMenu() throws RemoteException {

    }

    @Override
    public void showInattentiveSleepWarning() throws RemoteException {

    }

    @Override
    public void showMediaOutputSwitcher(String s, UserHandle userHandle, MediaSession.Token token) throws RemoteException {

    }

    @Override
    public void showPictureInPictureMenu() throws RemoteException {

    }

    @Override
    public void showPinningEnterExitToast(boolean b) throws RemoteException {

    }

    @Override
    public void showPinningEscapeToast() throws RemoteException {

    }

    @Override
    public void showRearDisplayDialog(int i) throws RemoteException {

    }

    @Override
    public void showRecentApps(boolean b) throws RemoteException {

    }

    @Override
    public void showScreenPinningRequest(int i) throws RemoteException {

    }

    @Override
    public void showShutdownUi(boolean b, String s, boolean b1) throws RemoteException {

    }

    @Override
    public void showToast(int i, String s, IBinder iBinder, CharSequence charSequence, IBinder iBinder1, int i1, ITransientNotificationCallback iTransientNotificationCallback, int i2) throws RemoteException {

    }

    @Override
    public void showTransient(int i, int i1, boolean b) throws RemoteException {

    }

    @Override
    public void showWirelessChargingAnimation(int i) throws RemoteException {

    }

    @Override
    public void startAssist(Bundle bundle) throws RemoteException {

    }

    @Override
    public void startTracing() throws RemoteException {

    }

    @Override
    public void stopTracing() throws RemoteException {

    }

    @Override
    public void suppressAmbientDisplay(boolean b) throws RemoteException {

    }

    @Override
    public void toggleKeyboardShortcutsMenu(int i) throws RemoteException {

    }

    @Override
    public void toggleNotificationsPanel() throws RemoteException {

    }

    @Override
    public void toggleRecentApps() throws RemoteException {

    }

    @Override
    public void toggleSplitScreen() throws RemoteException {

    }

    @Override
    public void toggleTaskbar() throws RemoteException {

    }

    @Override
    public void unregisterNearbyMediaDevicesProvider(INearbyMediaDevicesProvider iNearbyMediaDevicesProvider) throws RemoteException {

    }

    @Override
    public void updateMediaTapToTransferReceiverDisplay(int i, MediaRoute2Info mediaRoute2Info, Icon icon, CharSequence charSequence) throws RemoteException {

    }

    @Override
    public void updateMediaTapToTransferSenderDisplay(int i, MediaRoute2Info mediaRoute2Info, IUndoMediaTransferCallback iUndoMediaTransferCallback) throws RemoteException {

    }
}
