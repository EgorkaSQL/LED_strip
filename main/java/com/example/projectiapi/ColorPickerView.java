package com.example.projectiapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View
{
    private Bitmap bitmap;
    private Paint paint;
    private OnColorSelectedListener listener;

    public ColorPickerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        generateColorWheel(w, h);
    }

    private void generateColorWheel(int width, int height)
    {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        float radius = width / 2f;
        float centerX = width / 2f;
        float centerY = height / 2f;

        Shader shader = new RadialGradient(centerX, centerY, radius,
                new int[]{
                        0xFFFF0000,  // Красный
                        0xFFFFFF00,  // Желтый
                        0xFF00FF00,  // Зеленый
                        0xFF00FFFF,  // Голубой
                        0xFF0000FF,  // Синий
                        0xFFFF00FF,  // Фиолетовый
                        0xFFFF0000   // Красный (для плавного перехода)
                },
                null, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (bitmap != null)
        {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
        {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (x >= 0 && x < bitmap.getWidth() && y >= 0 && y < bitmap.getHeight())
            {
                int pixel = bitmap.getPixel(x, y);
                if (listener != null)
                {
                    listener.onColorSelected(pixel);
                }
            }
        }
        return true;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener)
    {
        this.listener = listener;
    }

    public interface OnColorSelectedListener
    {
        void onColorSelected(int color);
    }
}
