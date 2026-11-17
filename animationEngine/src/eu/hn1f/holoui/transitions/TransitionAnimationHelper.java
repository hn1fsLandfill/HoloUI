/*
 * Copyright (C) 2024 The Android Open Source Project
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
package eu.hn1f.holoui.transitions;

import static android.window.TransitionInfo.FLAG_IS_DISPLAY;
import static android.window.TransitionInfo.FLAGS_IS_NON_APP_WINDOW;

import android.graphics.Insets;
import android.graphics.Rect;
import android.view.InsetsSource;
import android.view.InsetsState;
import android.window.TransitionInfo;
import static eu.hn1f.holoui.transitions.WindowManagerConsts.*;

public class TransitionAnimationHelper {
    /**
     * Gets the final transition type from {@link TransitionInfo} for determining the animation.
     */
    public static int getTransitionTypeFromInfo(TransitionInfo info) {
        final int type = info.getType();
        // This back navigation is canceled, check whether the transition should be open or close
        if (type == TRANSIT_PREPARE_BACK_NAVIGATION
                || type == TRANSIT_CLOSE_PREPARE_BACK_NAVIGATION) {
            if (!info.getChanges().isEmpty()) {
                final TransitionInfo.Change change = info.getChanges().get(0);
                return TransitionUtil.isOpeningMode(change.getMode())
                        ? TRANSIT_OPEN : TRANSIT_CLOSE;
            }
        }
        // If the info transition type is opening transition, iterate its changes to see if it
        // has any opening change, if none, returns TRANSIT_CLOSE type for closing animation.
        if (type == TRANSIT_OPEN) {
            boolean hasOpenTransit = false;
            for (TransitionInfo.Change change : info.getChanges()) {
                if ((change.getTaskInfo() != null || change.hasFlags(FLAG_IS_DISPLAY))
                        && !TransitionUtil.isStationary(change)) {
                    // This isn't an activity-level transition.
                    return type;
                }
                if (change.getTaskInfo() != null
                        && change.hasFlags(FLAG_IS_DISPLAY | FLAGS_IS_NON_APP_WINDOW)) {
                    // Ignore non-activity containers.
                    continue;
                }
                if (change.getMode() == TRANSIT_OPEN) {
                    hasOpenTransit = true;
                    break;
                }
            }
            if (!hasOpenTransit) {
                return TRANSIT_CLOSE;
            }
        }
        return type;
    }

    /**
     * In some situations (eg. TaskBar) the content area of a display appears to be rounded. For
     * these situations, we may want the animation to also express the same rounded corners (even
     * though in steady-state, the app internally manages the insets). This class Keeps track of,
     * and provides, the bounds of rounded-corner display content.
     *
     * This is used to enable already-running animations to adapt to changes in taskbar/navbar
     * position live.
     */
    public static class RoundedContentPerDisplay {

        /** The current bounds of the display content (post-inset). */
        final Rect mBounds = new Rect();

        public void insetsChanged(InsetsState insetsState) {
            Insets insets = Insets.NONE;
            for (int i = insetsState.sourceSize() - 1; i >= 0; i--) {
                final InsetsSource source = insetsState.sourceAt(i);
                if (!source.hasFlags(InsetsSource.FLAG_INSETS_ROUNDED_CORNER)) {
                    continue;
                }
                Rect displayFrame = insetsState.getDisplayFrame();
                insets = Insets.max(source.calculateInsets(displayFrame, displayFrame, false),
                        insets);
            }
            mBounds.set(insetsState.getDisplayFrame());
            mBounds.inset(insets);
        }
    }
}
