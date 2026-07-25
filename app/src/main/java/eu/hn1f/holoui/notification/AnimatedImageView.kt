/*
 * Copyright (C) 2008 The Android Open Source Project
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

package eu.hn1f.holoui.notification

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.util.AttributeSet
import android.view.RemotableViewMethod
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews


@RemoteViews.RemoteView
class AnimatedImageView : ImageView {
    var mAnim: AnimationDrawable? = null
    var mAttached: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private fun updateAnim() {
        val drawable = if (mAttached) drawable else null
        if (mAttached && mAnim != null) {
            mAnim!!.stop()
        }
        if (drawable is AnimationDrawable) {
            mAnim = drawable
            if (isShown) {
                mAnim!!.start()
            }
        } else {
            mAnim = null
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        updateAnim()
    }

    override fun setImageIcon(icon: Icon?) {
        super.setImageIcon(icon)
        updateAnim()
    }

    @RemotableViewMethod
    override fun setImageResource(resid: Int) {
        super.setImageResource(resid)
        updateAnim()
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAttached = true
        updateAnim()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mAnim != null) {
            mAnim!!.stop()
        }
        mAttached = false
    }

    override fun onVisibilityChanged(changedView: View, vis: Int) {
        super.onVisibilityChanged(changedView, vis)
        if (mAnim != null) {
            if (isShown) {
                mAnim!!.start()
            } else {
                mAnim!!.stop()
            }
        }
    }
}