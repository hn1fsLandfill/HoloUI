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

import static android.window.TransitionInfo.FLAG_CHANGED_INTERACTIVE;
import static android.window.TransitionInfo.FLAG_MOVED_TO_TOP;
import static eu.hn1f.holoui.transitions.WindowManagerConsts.*;

import android.view.WindowManager;
import android.window.TransitionInfo;

public class TransitionUtil {
    public static boolean isOpeningType(int type) {
        return type == TRANSIT_OPEN
                || type == TRANSIT_TO_FRONT
                || type == TRANSIT_KEYGUARD_GOING_AWAY
                || type == TRANSIT_PREPARE_BACK_NAVIGATION;
    }
    public static boolean isClosingType(int type) {
        return type == TRANSIT_CLOSE || type == TRANSIT_TO_BACK;
    }

    public static boolean isOpeningMode(@TransitionInfo.TransitionMode int mode) {
        return mode == TRANSIT_OPEN || mode == TRANSIT_TO_FRONT;
    }

    /** Returns {@code true} if the transition is closing mode. */
    public static boolean isClosingMode(@TransitionInfo.TransitionMode int mode) {
        return mode == TRANSIT_CLOSE || mode == TRANSIT_TO_BACK;
    }

    public static boolean isOpenOrCloseMode(int mode) {
        return isOpeningMode(mode) || isClosingMode(mode);
    }

    public static int rootIndexFor(TransitionInfo.Change change,
                                   TransitionInfo info) {
        int rootIdx = info.findRootIndex(change.getEndDisplayId());
        if (rootIdx >= 0) return rootIdx;
        rootIdx = info.findRootIndex(change.getStartDisplayId());
        if (rootIdx >= 0) return rootIdx;
        return 0;
    }

    /**
     * Returns `true` if a `change` is stationary. Stationary changes are those that do not change
     * task's visual representation and don't need an animation, but do not prevent them either.
     */
    public static boolean isStationary(TransitionInfo.Change change) {
        return change.getMode() == TRANSIT_CHANGE
                && (change.getFlags() & (FLAG_MOVED_TO_TOP | FLAG_CHANGED_INTERACTIVE)) != 0
                && change.getStartAbsBounds().equals(change.getEndAbsBounds())
                && (change.getLastParent() == null
                || change.getLastParent().equals(change.getParent()))
                && (change.getStartRotation() == change.getEndRotation());
    }
}
