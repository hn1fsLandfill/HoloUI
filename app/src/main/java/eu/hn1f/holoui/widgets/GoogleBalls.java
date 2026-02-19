package eu.hn1f.holoui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GoogleBalls extends View {
    private final BallCanvas balls;

    Handler handler = new Handler(Looper.getMainLooper());

    Runnable refresh = this::invalidate;

    public GoogleBalls(Context context, AttributeSet attrs) {
        super(context, attrs);
        balls = new BallCanvas();
        balls.update();
    }

    @SuppressLint("CanvasSize")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        balls.width = canvas.getWidth();
        balls.height = canvas.getHeight();
        balls.update();
        balls.draw(canvas);
        handler.postDelayed(refresh, 32);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        balls.setMouse((int)event.getX(), (int)event.getY());
        return true;
    }

    class BallCanvas {
        private final Point[] points;
        private int mouseX, mouseY;
        private int width, height;
        private final Paint paint;

        // Base dimensions the logo was designed for
        private static final int BASE_WIDTH = 360;
        private static final int BASE_HEIGHT = 130;

        public BallCanvas() {
            width = getWidth();
            height = getHeight();
            paint = new Paint();

            // Calculate scale factor to fit screen
            /*float scaleX = (float) width / BASE_WIDTH;
            float scaleY = (float) height / BASE_HEIGHT;
            float scaleFactor = Math.min(scaleX, scaleY);

            // Don't scale up, only down
            if (scaleFactor > 1.0f) scaleFactor = 1.0f; */
            float scaleFactor = 1.0f;

            // Initialize points with scaling
            // Google logo ball data (x, y, size, color) - base coordinates for ~400x300
            int[][] BALL_DATA = {
                    {202, 78, 9, 0xED9D33}, {348, 83, 9, 0xD44D61}, {256, 69, 9, 0x4F7AF2},
                    {214, 59, 9, 0xEF9A1E}, {265, 36, 9, 0x4976F3}, {300, 78, 9, 0x269230},
                    {294, 59, 9, 0x1F9E2C}, {45, 88, 9, 0x1C48DD}, {268, 52, 9, 0x2A56EA},
                    {73, 83, 9, 0x3355D8}, {294, 6, 9, 0x36B641}, {235, 62, 9, 0x2E5DEF},
                    {353, 42, 8, 0xD53747}, {336, 52, 8, 0xEB676F}, {208, 41, 8, 0xF9B125},
                    {321, 70, 8, 0xDE3646}, {8, 60, 8, 0x2A59F0}, {180, 81, 8, 0xEB9C31},
                    {146, 65, 8, 0xC41731}, {145, 49, 8, 0xD82038}, {246, 34, 8, 0x5F8AF8},
                    {169, 69, 8, 0xEFA11E}, {273, 99, 8, 0x2E55E2}, {248, 120, 8, 0x4167E4},
                    {294, 41, 8, 0x0B991A}, {267, 114, 8, 0x4869E3}, {78, 67, 8, 0x3059E3},
                    {294, 23, 8, 0x10A11D}, {117, 83, 8, 0xCF4055}, {137, 80, 8, 0xCD4359},
                    {14, 71, 8, 0x2855EA}, {331, 80, 8, 0xCA273C}, {25, 82, 8, 0x2650E1},
                    {233, 46, 8, 0x4A7BF9}, {73, 13, 8, 0x3D65E7}, {327, 35, 6, 0xF47875},
                    {319, 46, 6, 0xF36764}, {256, 81, 6, 0x1D4EEB}, {244, 88, 6, 0x698BF1},
                    {194, 32, 6, 0xFAC652}, {97, 56, 6, 0xEE5257}, {105, 75, 6, 0xCF2A3F},
                    {42, 4, 6, 0x5681F5}, {10, 27, 6, 0x4577F6}, {166, 55, 6, 0xF7B326},
                    {266, 88, 6, 0x2B58E8}, {178, 34, 6, 0xFACB5E}, {100, 65, 6, 0xE02E3D},
                    {343, 32, 6, 0xF16D6F}, {59, 5, 6, 0x507BF2}, {27, 9, 6, 0x5683F7},
                    {233, 116, 6, 0x3158E2}, {123, 32, 6, 0xF0696C}, {6, 38, 6, 0x3769F6},
                    {63, 62, 6, 0x6084EF}, {6, 49, 6, 0x2A5CF4}, {108, 36, 6, 0xF4716E},
                    {169, 43, 6, 0xF8C247}, {137, 37, 6, 0xE74653}, {318, 58, 6, 0xEC4147},
                    {226, 100, 5, 0x4876F1}, {101, 46, 5, 0xEF5C5C}, {226, 108, 5, 0x2552EA},
                    {17, 17, 5, 0x4779F7}, {232, 93, 5, 0x4B78F1}
            };
            points = new Point[BALL_DATA.length];
            int offsetX = width / 2 - (int)(BASE_WIDTH * scaleFactor / 2);
            int offsetY = height / 2 - (int)(BASE_HEIGHT * scaleFactor / 2);

            for (int i = 0; i < BALL_DATA.length; i++) {
                float x = (BALL_DATA[i][0] * scaleFactor) + offsetX;
                float y = (BALL_DATA[i][1] * scaleFactor) + offsetY;
                float size = Math.max(2, BALL_DATA[i][2] * scaleFactor);
                int color = BALL_DATA[i][3];
                points[i] = new Point(x, y, size, color);
            }

            //mouseX = width / 2;
            //mouseY = height / 2;
            mouseX = 0;
            mouseY = 0;
        }

        private void update() {
            for (Point point : points) {
                point.update(mouseX, mouseY, width, height);
            }
        }

        protected void draw(Canvas canvas) {
            // Clear screen with white
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, width, height, paint);

            // Draw all points
            for (Point point : points) {
                point.draw(canvas, paint);
            }
        }

        // Touch/pointer event handling
        protected void setMouse(int x, int y) {
            mouseX = x;
            mouseY = y;
        }
    }

    static class Point {
        private final float origX;
        private final float origY;
        private float curX, curY;
        private float targetX, targetY;
        private float velX, velY;
        private float radius;
        private final float size;
        private final int color;
        private float curZ;
        private float targetZ;
        private float velZ;

        // Fixed point math (multiply by 256 for precision)
        private static final float FRICTION = 0.8f; // 0.8 * 256
        private static final float SPRING = 0.1f;    // 0.1 * 256

        public Point(float x, float y, float size, int color) {
            this.origX = x;
            this.origY = y;
            this.curX = x;
            this.curY = y;
            this.targetX = x;
            this.targetY = y;
            this.size = size;
            this.radius = size;
            this.color = color;
            this.velX = 0;
            this.velY = 0;
            this.curZ = 0;
            this.targetZ = 0;
            this.velZ = 0;
        }

        public void update(int mouseX, int mouseY, int width, int height) {
            float screenX = ((float)width/2)-(-curX);
            float screenY = ((float)height/2)-(-curY);
            float dx = mouseX - screenX;
            float dy = mouseY - screenY;
            float distSq = dx * dx + dy * dy;

            // If mouse is within 150 pixels, move away
            if (distSq < 22500) {
                targetX = curX - dx;
                targetY = curY - dy;
            } else {
                targetX = origX;
                targetY = origY;
            }

            // Update X position
            float dxPos = targetX - curX;
            float ax = dxPos * SPRING;
            velX += ax;
            velX = velX * FRICTION;
            curX += velX;

            // Update Y position
            float dyPos = targetY - curY;
            float ay = dyPos * SPRING;
            velY += ay;
            velY = velY * FRICTION;
            curY += velY;

            // Calculate Z based on distance from origin
            float dox = origX - curX;
            float doy = origY - curY;
            float dd = dox * dox + doy * doy;
            float d = (float)Math.sqrt(dd);

            targetZ = d / 100;
            float dz = targetZ - curZ;
            float az = dz * SPRING;
            velZ += az;
            velZ = velZ * FRICTION;
            curZ += velZ;

            // Update radius based on Z
            radius = size * curZ;
            if(radius < 5) radius *= 10;
            radius = Math.max(7, radius);
        }

        public void draw(Canvas canvas, Paint paint) {
            float x = ((float)canvas.getWidth()/2)-(-curX);
            float y = ((float)canvas.getHeight()/2)-(-curY);
            //paint.setColor(Color.BLACK);
            //canvas.drawText("Yo gurt"+x+" "+y+" "+curX+" "+curY, x, y, paint);
            paint.setColor(color | 0xFF000000);
            canvas.drawCircle(x, y, radius, paint);
            // canvas.translate(-x,-y);
        }
    }
}
