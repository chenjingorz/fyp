package com.example.parkinson_dec19;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.myscript.iink.PointerEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class drawBoard extends View {

    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor = Color.BLACK;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth = 35;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    private static int IMAGE_SIZE = 64;
    private static int PIXEL_DIFF = 10;

    private Float xCord;
    private Float yCord;

    private ArrayList<Array> matrixInterval = new ArrayList<Array>();
    ArrayList<PointerEvent> events = new ArrayList<PointerEvent>();


    public drawBoard(Context context) {
        this(context, null);
    }

    public drawBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
    }

    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        int height = this.getHeight();
        int width = this.getWidth();

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);

        this.canvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            this.canvas.drawPath(fp.path, mPaint);

        }

        canvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        saveMatrixInterval(x,y,event);

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                events.add(new PointerEvent().down(x, y));
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                events.add(new PointerEvent().move(x, y));
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                events.add(new PointerEvent().up(x, y));
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    private void saveMatrixInterval(float curX, float curY, MotionEvent event){
        //note: the ending coord of each word written is not saved when "next" is clicked

        if (xCord==null && yCord==null){
            xCord = event.getX();
            yCord = event.getY();
        }

        //check if #pixels moved > threshold
        if (Math.abs(curX-xCord)>PIXEL_DIFF || Math.abs(curY-yCord)>PIXEL_DIFF){
            Bitmap bitmap = getBitmap(false);
            int[] array = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(array, 0, bitmap.getWidth(),0, 0,
                    bitmap.getWidth(), bitmap.getHeight());
            int[] masked = Arrays.stream(array).map(i -> i == -1 ? 0 : 1).toArray();

//            ArrayList<Object> temp = new ArrayList<>();
//            temp.add(masked);
//            temp.add(Calendar.getInstance().getTimeInMillis());
//            matrixInterval.add(temp);

            xCord = curX;
            yCord = curY;
        }
    }

    public Bitmap getBitmap(Boolean resize){

        if (resize) {
            //TODO???? check if its btr to resize?
            bitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, false);
        }
        return bitmap;
    }

    public void eraseEvents(){
        events.clear();
    }
    public  ArrayList<PointerEvent> getPointerEvents(){
        return events;
    }
}