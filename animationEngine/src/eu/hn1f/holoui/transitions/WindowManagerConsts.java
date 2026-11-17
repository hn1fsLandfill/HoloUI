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

public class WindowManagerConsts {
    static int TRANSIT_NONE = 0;
    static int TRANSIT_OPEN = 1;
    static int TRANSIT_CLOSE = 2;
    static int TRANSIT_TO_FRONT = 3;
    static int TRANSIT_TO_BACK = 4;
    static int TRANSIT_CHANGE = 6;

    static int TRANSIT_KEYGUARD_GOING_AWAY = 7;
    static int TRANSIT_PREPARE_BACK_NAVIGATION = 13;
    static int TRANSIT_CLOSE_PREPARE_BACK_NAVIGATION = 14;
}
