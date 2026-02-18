package eu.hn1f.holoui

import android.app.ITransientNotificationCallback
import android.content.ComponentName
import android.graphics.drawable.Icon
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
import android.view.KeyEvent
import android.view.View
import com.android.internal.statusbar.DisableStates
import com.android.internal.statusbar.IAddTileResultCallback
import com.android.internal.statusbar.IStatusBar
import com.android.internal.statusbar.IUndoMediaTransferCallback
import com.android.internal.statusbar.LetterboxDetails
import com.android.internal.statusbar.StatusBarIcon
import com.android.internal.view.AppearanceRegion

class StatusBarImpl(val core: StatusBar): IStatusBar.Stub() {
    override fun abortTransient(displayId: Int, types: Int) {
        // TODO("Not yet implemented")
    }

    override fun addQsTile(p0: ComponentName?) {
        // TODO("Not yet implemented")
    }

    override fun addQsTileToFrontOrEnd(p0: ComponentName?, p1: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun animateCollapsePanels() {
        // TODO("Not yet implemented")
    }

    override fun animateExpandNotificationsPanel() {
        // TODO("Not yet implemented")
    }

    override fun animateExpandSettingsPanel(p0: String?) {
        // TODO("Not yet implemented")
    }

    override fun appTransitionCancelled(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun appTransitionFinished(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun appTransitionPending(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun appTransitionStarting(p0: Int, p1: Long, p2: Long) {
        // TODO("Not yet implemented")
    }

    override fun cancelPreloadRecentApps() {
        // TODO("Not yet implemented")
    }

    override fun cancelRequestAddTile(p0: String?) {
        // TODO("Not yet implemented")
    }

    override fun clickQsTile(p0: ComponentName?) {
        // TODO("Not yet implemented")
    }

    override fun confirmImmersivePrompt() {
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

    override fun handleSystemKey(p0: KeyEvent?) {
        // TODO("Not yet implemented")
    }

    override fun hideAuthenticationDialog(p0: Long) {
        // TODO("Not yet implemented")
    }

    override fun hideRecentApps(p0: Boolean, p1: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun hideToast(p0: String?, p1: IBinder?) {
        // TODO("Not yet implemented")
    }

    override fun immersiveModeChanged(rootDisplayAreaId: Int, isImmersiveMode: Boolean, windowType: Int) {
        if(isImmersiveMode) core.hideStatusBar()
        else core.showStatusBar()
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
    }

    override fun onBiometricError(p0: Int, p1: Int, p2: Int) {
        // TODO("Not yet implemented")
    }

    override fun onBiometricHelp(p0: Int, p1: String?) {
        // TODO("Not yet implemented")
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

    override fun onProposedRotationChanged(p0: Int, p1: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun onSystemBarAttributesChanged(
        p0: Int,
        p1: Int,
        p2: Array<out AppearanceRegion?>?,
        p3: Boolean,
        p4: Int,
        p5: Int,
        p6: String?,
        p7: Array<out LetterboxDetails?>?
    ) {
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

    override fun restartSystemUI() {
        // TODO("Not yet implemented")
    }

    override fun runGcForTest() {
        // TODO("Not yet implemented")
    }

    override fun setBiometicContextListener(p0: IBiometricContextListener?) {
        // TODO("Not yet implemented")
    }

    override fun setIcon(p0: String?, p1: StatusBarIcon?) {
        // TODO("Not yet implemented")
    }

    override fun setImeWindowStatus(
        p0: Int,
        p1: Int,
        p2: Int,
        p3: Boolean
    ) {
        // TODO("Not yet implemented")
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
        if(hidesStatusBar) core.hideStatusBar()
        else core.showStatusBar()
    }

    override fun setUdfpsRefreshRateCallback(callback: IUdfpsRefreshRateRequestCallback?) {
        // TODO("Not yet implemented")
        callback?.onRequestEnabled(0);
    }

    override fun setWindowState(p0: Int, p1: Int, p2: Int) {
        // TODO("Not yet implemented")
    }

    override fun showAssistDisclosure() {
        // TODO("Not yet implemented")
    }

    override fun showAuthenticationDialog(
        promptInfo: PromptInfo?,
        sysuiReceiver: IBiometricSysuiReceiver?,
        sensorIds: IntArray?,
        credentialAllowed: Boolean,
        requireConfirmation: Boolean,
        userId: Int,
        operationId: Long,
        opPackageName: String?,
        requestId: Long
    ) {
        // TODO("Not yet implemented")
        sysuiReceiver?.onDialogDismissed(0, null)
    }

    override fun showGlobalActionsMenu() {
        // TODO("Not yet implemented")
    }

    override fun showInattentiveSleepWarning() {
        // TODO("Not yet implemented")
    }

    override fun showMediaOutputSwitcher(
        p0: String?,
        p1: UserHandle?,
        p2: MediaSession.Token?
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
        // TODO("Not yet implemented")
    }

    override fun showScreenPinningRequest(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun showShutdownUi(p0: Boolean, p1: String?, p2: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun showToast(
        p0: Int,
        p1: String?,
        p2: IBinder?,
        p3: CharSequence?,
        p4: IBinder?,
        p5: Int,
        p6: ITransientNotificationCallback?,
        p7: Int
    ) {
        // TODO("Not yet implemented")
    }

    override fun showTransient(p0: Int, p1: Int, p2: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun showWirelessChargingAnimation(p0: Int) {
        // TODO("Not yet implemented")
    }

    override fun startAssist(p0: Bundle?) {
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
        // TODO("Not yet implemented")
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