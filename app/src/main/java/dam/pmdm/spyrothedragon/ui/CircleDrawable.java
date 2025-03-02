package dam.pmdm.spyrothedragon.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CircleDrawable extends Drawable {
    private Paint paint;

    public CircleDrawable() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#9C35BA")); // Set the color of the circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10); // Set the stroke width
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int radius = Math.min(getBounds().width(), getBounds().height()) / 2;
        int cx = getBounds().centerX();
        int cy = getBounds().centerY();
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
