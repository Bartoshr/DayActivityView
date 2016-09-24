package io.github.bartoshr.dayactivityview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bartosh on 20.09.2016.
 */

public class DayActivityView extends View {

    private static final String TAG = DayActivityView.class.getSimpleName();

    Paint enabledPaint;
    Paint disabledPaint;
    Paint currentPaint;

    int enabledColor;
    int disabledColor;

    // size of rect
    private float rectSize;

    //size of tiles arc
    private float arcSize;

    private float strokeSize;

    // Describe size of space between squares
    private float interSize;

    //indicates current tile, which will be marked
    private int currentTile = -1;

    Tile tiles[];

    public interface OnTileClick {
        public void onTileClick(int id);
    }
    public OnTileClick onTileClicklistener;


    static class Tile extends RectF {
        public Tile() {}
        public boolean isEnabled;
    }


    public DayActivityView(Context context) {
        super(context);
    }

    public DayActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DayActivityView,0,0);
        try {
            enabledColor = a.getColor(R.styleable.DayActivityView_enabledColor, 0xffff4081);
            disabledColor = a.getColor(R.styleable.DayActivityView_disabledColor, 0xff3F51B5);
            arcSize = a.getFloat(R.styleable.DayActivityView_arcSize, 10);
            strokeSize = a.getFloat(R.styleable.DayActivityView_strokeSize, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        tiles = new Tile[7];
        for (int i = 0; i < 7; i++)
            tiles[i] = new Tile();

        enabledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        enabledPaint.setStyle(Paint.Style.FILL);
        enabledPaint.setColor(enabledColor);

        currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(strokeSize);
        currentPaint.setColor(0xffffffff);

        Log.d(TAG, "StrokeWidth"+currentPaint.getStrokeWidth());

        disabledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        disabledPaint.setStyle(Paint.Style.FILL);
        disabledPaint.setColor(disabledColor);
    }

    public void setCurrent(int current) {
        this.currentTile = current;
        invalidate();
    }

    /**
     * Enable rectangle specified by postion
     * @param position
     */
    public void enable(int position){
        tiles[position].isEnabled = true;
        invalidate();
    }

    /**
     * Disable rectangle specified by postion
     * @param position
     */
    public void disable(int position){
        tiles[position].isEnabled = false;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int pl = getPaddingLeft();
        int pr = getPaddingRight();

        Log.d(TAG, "onSizeChanged("+w+")");

        // space between tiles is set to be 15 promiles of total width
        interSize = (float)(w*0.015);
        // calculated for width (fits perfectly)
        rectSize =  (w-pl-pr-8*interSize)/7;

        calcRects();
    }

    private void calcRects(){
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();
        float shift;
        for (int i = 0; i < 7; i++) {
            shift = pl+(i+1)*interSize+rectSize*i;
            tiles[i].set(shift, interSize+pt-pb, shift+rectSize, interSize+rectSize+pt-pb);
            Log.d(TAG, "calcRect = "+(rectSize+shift));
        }
    }

    class mListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
    GestureDetector mDetector = new GestureDetector(DayActivityView.this.getContext(), new mListener());

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                for (int i = 0; i < tiles.length; i++) {
                    if(onTileClicklistener != null && tiles[i].contains(event.getX(), event.getY())){
                        onTileClicklistener.onTileClick(i);
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    public void setOnTileClick(OnTileClick listener) {
        this.onTileClicklistener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = getMeasuredWidth();
        int heightSize = getMeasuredHeight();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int height;

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(widthSize/7+(int)interSize, heightSize);
        } else {
            //Be whatever you want
            // for example attack helicopter
            height = (widthSize-(int)interSize)/7+(int)interSize;
        }

        Log.d(TAG,"w="+ widthSize);
        Log.d(TAG,"h="+ heightSize);//

        //MUST CALL THIS
        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint;
        for (int i = 0; i < tiles.length; i++) {
            paint = (tiles[i].isEnabled) ? enabledPaint : disabledPaint;
            canvas.drawRoundRect(tiles[i],arcSize,arcSize, paint);
            if(i==currentTile) canvas.drawRoundRect(tiles[i],arcSize,arcSize, currentPaint);
        }
    }
}
