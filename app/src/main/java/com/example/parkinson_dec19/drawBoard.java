package com.example.parkinson_dec19;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.suyati.telvin.drawingboard.DrawingBoardEventHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class drawBoard extends View {
    private static final float STROKE_WIDTH = 6.0F;
    private static final float HALF_STROKE_WIDTH = 3.0F;
    private final RectF dirtyRect = new RectF();
    boolean isToched = false;
    private Paint paint = new Paint();
    private Path path = new Path();
    private float lastTouchX;
    private float lastTouchY;
    private Context mContext;
    private int bgColor = 17170443;
    private String baseFilePath = "/drawingboard";

    public String getBaseFilePath() {
        return this.baseFilePath;
    }

    public void setBaseFilePath(String baseFilePath) {
        this.baseFilePath = baseFilePath;
    }

    public drawBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.paint.setAntiAlias(true);
        this.paint.setColor(-65536);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeJoin(Join.ROUND);
        this.paint.setStrokeWidth(6.0F);
        this.setBackgroundColor(this.getResources().getColor(17170443));
    }

    public void setPenColor(int color) {
        int myColor = this.getResources().getColor(color);
        this.paint.setColor(myColor);
        this.invalidate();
    }

    public void setStyle(Style style) {
        this.paint.setStyle(style);
        this.invalidate();
    }

    public void setStrokeJoin(Join join) {
        this.paint.setStrokeJoin(join);
        this.invalidate();
    }

    public void setPenWidth(Float width) {
        this.paint.setStrokeWidth(width);
        this.invalidate();
    }

    public void setCanvasColor(int bgColor) {
        this.bgColor = bgColor;
        this.setBackgroundColor(this.getResources().getColor(bgColor));
        this.invalidate();
    }

    public int getCanvasColor() {
        return this.bgColor;
    }

    public Bitmap getBitMapSignature() {
        Bitmap bitmap = null;
        if (bitmap == null) {
            int width = this.getWidth();
            int height = this.getHeight();
            if (width == 0 && height == 0) {
                width = 200;
                height = 200;
            }

            bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        }

        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public void clearBoard() {
        this.path.reset();
        this.invalidate();
        this.isToched = false;
    }

    public boolean isDraw() {
        return this.isToched;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawPath(this.path, this.paint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        this.isToched = true;
        switch(event.getAction()) {
            case 0:
                this.path.moveTo(eventX, eventY);
                this.lastTouchX = eventX;
                this.lastTouchY = eventY;
                return true;
            case 1:
            case 2:
                this.resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();

                for(int i = 0; i < historySize; ++i) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    this.expandDirtyRect(historicalX, historicalY);
                    this.path.lineTo(historicalX, historicalY);
                }

                this.path.lineTo(eventX, eventY);
                this.invalidate((int)(this.dirtyRect.left - 3.0F), (int)(this.dirtyRect.top - 3.0F), (int)(this.dirtyRect.right + 3.0F), (int)(this.dirtyRect.bottom + 3.0F));
                this.lastTouchX = eventX;
                this.lastTouchY = eventY;
                return true;
            default:
                return false;
        }
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < this.dirtyRect.left) {
            this.dirtyRect.left = historicalX;
        } else if (historicalX > this.dirtyRect.right) {
            this.dirtyRect.right = historicalX;
        }

        if (historicalY < this.dirtyRect.top) {
            this.dirtyRect.top = historicalY;
        } else if (historicalY > this.dirtyRect.bottom) {
            this.dirtyRect.bottom = historicalY;
        }

    }

    private void resetDirtyRect(float eventX, float eventY) {
        this.dirtyRect.left = Math.min(this.lastTouchX, eventX);
        this.dirtyRect.right = Math.max(this.lastTouchX, eventX);
        this.dirtyRect.top = Math.min(this.lastTouchY, eventY);
        this.dirtyRect.bottom = Math.max(this.lastTouchY, eventY);
    }
}