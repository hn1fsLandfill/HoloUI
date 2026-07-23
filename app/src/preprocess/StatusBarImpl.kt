package eu.hn1f.holoui

import android.app.AlertDialog
import android.app.ITransientNotificationCallback
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.IBiometricContextListener
import android.hardware.biometrics.IBiometricSysuiReceiver
import android.hardware.biometrics.PromptInfo
import android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback
import android.media.INearbyMediaDevicesProvider
import android.media.MediaRoute2Info
import android.media.session.MediaSession
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.UserHandle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.WindowInsetsController
import android.view.WindowManager
import com.android.internal.statusbar.DisableStates
import com.android.internal.statusbar.IAddTileResultCallback
import com.android.internal.statusbar.IStatusBar
import com.android.internal.statusbar.IUndoMediaTransferCallback
import com.android.internal.statusbar.LetterboxDetails
import com.android.internal.statusbar.StatusBarIcon
import com.android.internal.view.AppearanceRegion

// TODO: (these are called on normal usage of a device without authentication)
// appTransitionFinished
// appTransitionPending
// appTransitionStarting
// onSystemBarAttributesChanged
// setWindowState
// onDisplayAddSystemDecorations
// setUdfpsRefreshRateCallback
// addStateMonitorCallback
// setBiometicContextListener
// onDisplayAddSystemDecorations
// setImeWindowStatus

 class StatusBarImpl(val core: StatusBar): IStatusBar.Stub() {
    var mApplication: SystemUIApplication? = core.context.applicationContext as SystemUIApplication;
    val DEBUG_ISB = true

     fun trace(msg: String) {
         if(DEBUG_ISB)
             Log.v("StatusBarImpl", msg)
     }

     fun toggleRecents() {
         mApplication!!.runInUIThread {
             val mLocale = mApplication!!.resources.configuration.locale;
             val mLayoutDirection = TextUtils.getLayoutDirectionFromLocale(mLocale);

             mApplication!!.recents!!.toggleRecents(mApplication!!.display, mLayoutDirection, mApplication!!.statusBar!!.root)
         }
     }

    override fun addQsTile(p0: ComponentName?) {
        trace("addQsTile")
    }

    override fun addQsTileToFrontOrEnd(p0: ComponentName?, p1: Boolean) {
        trace("addQsTileToFrontOrEnd")
    }

    override fun animateCollapsePanels() {
        trace("animateCollapsePanels")
    }

    override fun animateExpandNotificationsPanel() {
        trace("animateExpandNotificationsPanel")
    }

    override fun animateExpandSettingsPanel(p0: String?) {
        trace("animateExpandSettingsPanel")
    }

    override fun appTransitionCancelled(p0: Int) {
        trace("appTransitionCancelled")
    }

    override fun appTransitionFinished(p0: Int) {
        trace("appTransitionFinished")
    }

    override fun appTransitionPending(p0: Int) {
        trace("appTransitionPending")
    }

    override fun appTransitionStarting(p0: Int, p1: Long, p2: Long) {
        trace("appTransitionStarting")
    }

    override fun cancelPreloadRecentApps() {
        trace("cancelPreloadRecentApps")
    }

    override fun cancelRequestAddTile(p0: String?) {
        trace("cancelRequestAddTile")
    }

    override fun clickQsTile(p0: ComponentName?) {
        trace("clickQsTile")
    }

    override fun confirmImmersivePrompt() {
        trace("TODO: ImmersivePrompt\n");
        // TODO("Not yet implemented")
    }

    override fun disable(p0: Int, p1: Int, p2: Int) {
        // TODO("Not yet implemented")
    }

    override fun disableForAllDisplays(p0: DisableStates?) {
        // TODO("Not yet implemented")
    }

    override fun dismissInattentiveSleepWarning(p0: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun dismissKeyboardShortcutsMenu() {
        // TODO("Not yet implemented")
    }

    override fun dumpProto(
        p0: Array<out String?>?,
        p1: ParcelFileDescriptor?
    ) {
        // TODO("Not yet implemented")
    }

    override fun handleSystemKey(p0: KeyEvent) {
        trace("TODO: handleSystemKey ${p0.action} ${p0.keyCode} $p0")
        // TODO("Not yet implemented")
    }

    override fun hideAuthenticationDialog(p0: Long) {
        // TODO("Not yet implemented")
    }

    override fun hideRecentApps(p0: Boolean, p1: Boolean) {
        mApplication!!.runInUIThread {
            mApplication!!.recents!!.closeRecents()
        }
    }

    override fun hideToast(packageName: String, token: IBinder) {
        mApplication!!.runInUIThread {
            mApplication!!.toaster!!.hideToast()
        }
    }

    override fun immersiveModeChanged(rootDisplayAreaId: Int, isImmersiveMode: Boolean, windowType: Int) {
        mApplication!!.runInUIThread {
            if(isImmersiveMode) {
                mApplication!!.navigationBar?.hide()
                core.hideStatusBar()
            }
            else {
                mApplication!!.navigationBar?.show()
                core.showStatusBar()
            }
        }
    }

    override fun moveFocusedTaskToDesktop(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun moveFocusedTaskToFullscreen(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun moveFocusedTaskToStageSplit(p0: Int, p1: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun onBiometricAuthenticated(p0: Int) {
        // TODO("Not yet implemented")
        trace("TODO: onBiometricAuthenticated")
    }

    override fun onBiometricError(p0: Int, p1: Int, p2: Int) {
        // TODO("Not yet implemented")
        trace("TODO: onBiometricError")
    }

    override fun onBiometricHelp(p0: Int, p1: String?) {
        // TODO("Not yet implemented")
        trace("TODO: onBiometricHelp")
    }

    override fun onCameraLaunchGestureDetected(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun onDisplayAddSystemDecorations(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun onDisplayRemoveSystemDecorations(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun onEmergencyActionLaunchGestureDetected() {
        // TODO("Not yet implemented")
    }

    // proposed rotation stuff
    // nice to have
    override fun onProposedRotationChanged(p0: Int, p1: Boolean) {
        // TODO("Not yet implemented")
    }

    // clear status bar stuff
    override fun onSystemBarAttributesChanged(
        displayId: Int,
        appearance: Int,
        appearanceRegions: Array<out AppearanceRegion>,
        navbarColorManagedByIme: Boolean,
        behavior: Int,
        requestedVisibleTypes: Int,
        packageName: String?,
        letterboxDetails: Array<out LetterboxDetails>
    ) {
        trace("onSystemBarAttributesChanged $displayId $appearance $appearanceRegions $navbarColorManagedByIme $behavior $requestedVisibleTypes $packageName $letterboxDetails")

        // TODO("Not yet implemented")
    }

    override fun onWalletLaunchGestureDetected() {
        // TODO("Not yet implemented")
    }

    override fun passThroughShellCommand(
        p0: Array<out String?>?,
        p1: ParcelFileDescriptor?
    ) {
        // TODO("Not yet implemented")
    }

    override fun preloadRecentApps() {
        // TODO("Not yet implemented")
    }

    override fun registerNearbyMediaDevicesProvider(p0: INearbyMediaDevicesProvider?) {
        // TODO("Not yet implemented")
    }

    override fun remQsTile(p0: ComponentName?) {
        // TODO("Not yet implemented")
    }

    override fun removeIcon(p0: String?) {
        // TODO("Not yet implemented")
    }

    override fun requestAddTile(
        p0: Int,
        p1: ComponentName?,
        p2: CharSequence?,
        p3: CharSequence?,
        p4: Icon?,
        p5: IAddTileResultCallback?
    ) {
        // TODO("Not yet implemented")
    }

    override fun requestMagnificationConnection(p0: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun requestTileServiceListeningState(p0: ComponentName?) {
        // TODO("Not yet implemented")
    }

    /* override fun restartSystemUI() {
        // TODO("Not yet implemented")
    } LineageOS specific function */

    override fun runGcForTest() {
        // TODO("Not yet implemented")
    }

    override fun setBiometicContextListener(p0: IBiometricContextListener?) {
        // TODO("Not yet implemented")
        trace("TODO: setBiometicContextListener")
    }

    override fun setIcon(slot: String, sbn: StatusBarIcon) {
        // TODO("Not yet implemented")
    }

    override fun setImeWindowStatus(
        p0: Int,
        p1: Int,
        p2: Int,
        p3: Boolean
    ) {
        // TODO("Not yet implemented")
        trace("TODO: setImeWindowStatus")
    }

    override fun setNavigationBarLumaSamplingEnabled(p0: Int, p1: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun setQsTiles(p0: Array<out String?>?) {
        // TODO("Not yet implemented")
    }

    override fun setSplitscreenFocus(p0: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun setTopAppHidesStatusBar(hidesStatusBar: Boolean) {
        mApplication!!.runInUIThread {
            if(hidesStatusBar) core.hideStatusBar()
            else core.showStatusBar()
        }
    }

    override fun setUdfpsRefreshRateCallback(callback: IUdfpsRefreshRateRequestCallback?) {
        // TODO("Not yet implemented")
        trace("TODO: setUdfpsRefreshRateCallback")
        callback?.onRequestEnabled(0);
    }

    override fun setWindowState(display: Int, window: Int, state: Int) {
        trace("TODO: setWindowState $display $window $state")
        // TODO("Not yet implemented")
    }

    override fun showAssistDisclosure() {
        trace("TODO: showAssistDisclosure")
        // TODO("Not yet implemented")
    }

    override fun showAuthenticationDialog(
        promptInfo: PromptInfo,
        sysuiReceiver: IBiometricSysuiReceiver,
        sensorIds: IntArray,
        credentialAllowed: Boolean,
        requireConfirmation: Boolean,
        userId: Int,
        operationId: Long,
        opPackageName: String,
        requestId: Long
    ) {
        // TODO("Not yet implemented")
        trace("TODO: showAuthenticationDialog")
        mApplication!!.runInUIThread {
            mApplication!!.authenticationForm!!.showAuthenticationDialog(promptInfo, sysuiReceiver)
        }
    }

    override fun showGlobalActionsMenu() {
        trace("TODO: showGlobalActionsMenu")
        // Power menu stuff it seems like
        // TODO("Not yet implemented")
    }

    override fun showInattentiveSleepWarning() {
        trace("TODO: showInattentiveSleepWarning")
        // TODO("Not yet implemented")
    }

    override fun showMediaOutputSwitcher(
        p0: String?,
        p1: UserHandle?,
        #ifdef BAKLAVA_QPR1_LATER
        p2: MediaSession.Token?
        #endif
    ) {
        // TODO("Not yet implemented")
    }

    override fun showPictureInPictureMenu() {
        // TODO("Not yet implemented")
    }

    override fun showPinningEnterExitToast(p0: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun showPinningEscapeToast() {
        // TODO("Not yet implemented")
    }

    override fun showRearDisplayDialog(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun showRecentApps(p0: Boolean) {
        toggleRecents()
    }

    override fun showScreenPinningRequest(p0: Int) {
        // TODO("Not yet implemented")
    }

    // TODO: Tackle LineageOS's custom rebootCustom property
    override fun showShutdownUi(isReboot: Boolean, reason: String?, /* rebootCustom: Boolean */) {
        // TODO("Not yet implemented")
        mApplication!!.runInUIThread {
            val shutdownUI = AlertDialog.Builder(mApplication)
                .setTitle("Powering off...")
                .setMessage(mApplication!!.getRebootMessage(isReboot, reason))
                .setCancelable(false)
                .create()
            shutdownUI.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
            shutdownUI.show()
        }
    }

    override fun showToast(
        uid: Int,
        packageName: String,
        token: IBinder,
        text: CharSequence,
        windowToken: IBinder,
        duration: Int,
        callback: ITransientNotificationCallback,
        displayId: Int
    ) {
        mApplication!!.runInUIThread {
            mApplication!!.toaster!!.showToast(windowToken, text.toString(), duration, callback)
        }
        // TODO("Not yet implemented")
    }

    override fun showTransient(displayId: Int, types: Int, isGestureOnSystemBar: Boolean) {
        trace("TODO: showTransient")
        // TODO("Not yet implemented")
    }

    override fun abortTransient(displayId: Int, types: Int) {
        trace("TODO: abortTransient")
        // TODO("Not yet implemented")
    }

    override fun showWirelessChargingAnimation(p0: Int) {
        trace("TODO: showWirelessChargingAnimation")
        // TODO("Not yet implemented")
    }

    override fun startAssist(p0: Bundle?) {
        trace("TODO: startAssist")
        // TODO("Not yet implemented")
    }

    override fun startTracing() {
        // TODO("Not yet implemented")
    }

    override fun stopTracing() {
        // TODO("Not yet implemented")
    }

    override fun suppressAmbientDisplay(p0: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun toggleKeyboardShortcutsMenu(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun toggleNotificationsPanel() {
        // TODO("Not yet implemented")
    }

    override fun toggleRecentApps() {
        toggleRecents()
    }

    override fun toggleSplitScreen() {
        // TODO("Not yet implemented")
    }

    override fun toggleTaskbar() {
        // TODO("Not yet implemented")
    }

    override fun unregisterNearbyMediaDevicesProvider(p0: INearbyMediaDevicesProvider?) {
        // TODO("Not yet implemented")
    }

    override fun updateMediaTapToTransferReceiverDisplay(
        p0: Int,
        p1: MediaRoute2Info?,
        p2: Icon?,
        p3: CharSequence?
    ) {
        // TODO("Not yet implemented")
    }

    override fun updateMediaTapToTransferSenderDisplay(
        p0: Int,
        p1: MediaRoute2Info?,
        p2: IUndoMediaTransferCallback?
    ) {
        // TODO("Not yet implemented")
    }

}