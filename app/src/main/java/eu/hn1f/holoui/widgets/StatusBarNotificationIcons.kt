package eu.hn1f.holoui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.service.notification.StatusBarNotification
import android.util.AttributeSet
import android.view.View
import eu.hn1f.holoui.R

class StatusBarNotificationIcons(context: Context, attrs: AttributeSet?): View(context, attrs) {
    // unhardcode this later
    val MAX_ICONS = 4
    val iconWidthHeight = context.resources.getDimensionPixelSize(R.dimen.statusbar_height)-4
    private var icons: Array<StatusBarNotification> = emptyArray()

    // Set notification icons to render in the status bar
    fun setIcons(newIcons: Array<StatusBarNotification>) {
        icons = newIcons
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension((iconWidthHeight+2)*MAX_ICONS, iconWidthHeight)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(0f, 2f)
        for(i in 0..icons.size) {
            val icon = icons.getOrNull(i) ?: break

            if(i != 0) canvas.translate((iconWidthHeight+2f), 0f)

            val drawable = icon.notification.smallIcon.loadDrawable(context)
            drawable!!.setBounds(0, 0, iconWidthHeight, iconWidthHeight)
            drawable.setTint(Color.WHITE)
            drawable.draw(canvas)

            if(i > MAX_ICONS) break;
        }
        canvas.restore()
        super.onDraw(canvas)
    }

}