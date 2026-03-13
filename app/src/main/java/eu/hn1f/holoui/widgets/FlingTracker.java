/*
 * Copyright (C) 2012 The Android Open Source Project
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

package eu.hn1f.holoui.widgets;

import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * A very simple low-pass velocity filter for motion events; not nearly as sophisticated as
 * VelocityTracker but optimized for the kinds of gestures we expect to see in status bar
 * panels.
 */
public class FlingTracker {
    static final boolean DEBUG = true;
    private static final boolean DEBUG_NAN = true;
    final int MAX_EVENTS = 8;
    final float DECAY = 0.75f;
    ArrayDeque<MotionEventCopy> mEventBuf = new ArrayDeque<MotionEventCopy>(MAX_EVENTS);
    float mVX, mVY = 0;
    private static class MotionEventCopy {
        public MotionEventCopy(float x2, float y2, long eventTime) {
            this.x = x2;
            this.y = y2;
            this.t = eventTime;
        }
        public float x, y;
        public long t;
    }
    public FlingTracker() {
    }
    public void addMovement(MotionEvent event) {
        if (mEventBuf.size() == MAX_EVENTS) {
            mEventBuf.remove();
        }
        mEventBuf.add(new MotionEventCopy(event.getX(), event.getY(), event.getEventTime()));
    }
    public void computeCurrentVelocity(long timebase) {
        if (FlingTracker.DEBUG) {
            Log.v("FlingTracker", "computing velocities for " + mEventBuf.size() + " events");
        }
        mVX = mVY = 0;
        MotionEventCopy last = null;
        int i = 0;
        float totalweight = 0f;
        float weight = 10f;
        for (final Iterator<MotionEventCopy> iter = mEventBuf.iterator();
             iter.hasNext();) {
            final MotionEventCopy event = iter.next();
            if (last != null) {
                final float dt = (float) (event.t - last.t) / timebase;
                final float dx = (event.x - last.x);
                final float dy = (event.y - last.y);
                if (FlingTracker.DEBUG) {
                    Log.v("FlingTracker", String.format(
                            "   [%d] (t=%d %.1f,%.1f) dx=%.1f dy=%.1f dt=%f vx=%.1f vy=%.1f",
                            i, event.t, event.x, event.y,
                            dx, dy, dt,
                            (dx/dt),
                            (dy/dt)
                    ));
                }
                if (event.t == last.t) {
                    // Really not sure what to do with events that happened at the same time,
                    // so we'll skip subsequent events.
                    if (DEBUG_NAN) {
                        Log.v("FlingTracker", "skipping simultaneous event at t=" + event.t);
                    }
                    continue;
                }
                mVX += weight * dx / dt;
                mVY += weight * dy / dt;
                totalweight += weight;
                weight *= DECAY;
            }
            last = event;
            i++;
        }
        if (totalweight > 0) {
            mVX /= totalweight;
            mVY /= totalweight;
        } else {
            if (DEBUG_NAN) {
                Log.v("FlingTracker", "computeCurrentVelocity warning: totalweight=0",
                        new Throwable());
            }
            // so as not to contaminate the velocities with NaN
            mVX = mVY = 0;
        }

        if (FlingTracker.DEBUG) {
            Log.v("FlingTracker", "computed: vx=" + mVX + " vy=" + mVY);
        }
    }
    public float getXVelocity() {
        if (Float.isNaN(mVX) || Float.isInfinite(mVX)) {
            if (DEBUG_NAN) {
                Log.v("FlingTracker", "warning: vx=" + mVX);
            }
            mVX = 0;
        }
        return mVX;
    }
    public float getYVelocity() {
        if (Float.isNaN(mVY) || Float.isInfinite(mVX)) {
            if (DEBUG_NAN) {
                Log.v("FlingTracker", "warning: vx=" + mVY);
            }
            mVY = 0;
        }
        return mVY;
    }
    public void recycle() {
        mEventBuf.clear();
    }

    public void trackMovement(MotionEvent event) {
        // Add movement to velocity tracker using raw screen X and Y coordinates instead
        // of window coordinates because the window frame may be moving at the same time.
        float deltaX = event.getRawX() - event.getX();
        float deltaY = event.getRawY() - event.getY();
        event.offsetLocation(deltaX, deltaY);
        this.addMovement(event);
        event.offsetLocation(-deltaX, -deltaY);
    }

    static FlingTracker sTracker;
    static FlingTracker obtain() {
        if (sTracker == null) {
            sTracker = new FlingTracker();
        }
        return sTracker;
    }
}
