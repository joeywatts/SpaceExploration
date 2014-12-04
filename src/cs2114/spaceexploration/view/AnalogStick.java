package cs2114.spaceexploration.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// -------------------------------------------------------------------------
/**
 * AnalogStick is a custom Android View that defines the virtual analog stick.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class AnalogStick
    extends View
{

    /*
     * Current Location of the analog stick relative to the center (resting
     * location)
     */
    private float stickLocationX;
    private float stickLocationY;

    /* Paint object */
    private Paint paint;


    /**
     * Instantiates a new AnalogStick.
     *
     * @param context
     *            the Android context.
     * @param attrs
     *            the AttributeSet
     * @param defStyleAttr
     *            defines style attribute
     */
    public AnalogStick(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * Instantiates a new AnalogStick.
     *
     * @param context
     *            the Android context.
     */
    public AnalogStick(Context context)
    {
        super(context);
        init();
    }


    /**
     * Instantiates a new AnalogStick.
     *
     * @param context
     *            the Android context.
     * @param attrs
     *            the AttributeSet
     */
    public AnalogStick(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    private void init()
    {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        /* Draw clear outer circle */
        paint.setColor(Color.argb(50, 255, 255, 255));
        canvas.drawCircle(
            getWidth() / 2,
            getHeight() / 2,
            getWidth() / 2,
            paint);
        /* Draw analog stick */
        float radius = getWidth() * .10f;
        paint.setColor(Color.WHITE);
        canvas.drawCircle(getWidth() / 2 + stickLocationX, getHeight() / 2
            + stickLocationY, radius, paint);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            stickLocationX = 0;
            stickLocationY = 0;
        }
        else
        {
            stickLocationX = event.getX() - getWidth() / 2;
            stickLocationY = event.getY() - getHeight() / 2;
            if (stickLocationX * stickLocationX + stickLocationY
                * stickLocationY > getWidth() * getHeight() / 4)
            {
                float normX = getNormalizedX();
                float normY = getNormalizedY();
                stickLocationX = normX * getWidth() * .8f / 2;
                stickLocationY = normY * (getHeight() - getWidth() * .2f) / 2;
            }
        }
        invalidate();
        return true;
    }


    /**
     * Gets the x component of the normalized vector that this analog stick
     * represents.
     *
     * @return the x component of the normalized vector.
     */
    public float getNormalizedX()
    {
        if (stickLocationX == 0 && stickLocationY == 0)
        {
            return 0;
        }
        return stickLocationX
            / (float)Math.sqrt(stickLocationX * stickLocationX + stickLocationY
                * stickLocationY);
    }


    /**
     * Gets the y component of the normalized vector that this analog stick
     * represents.
     *
     * @return the y component of the normalized vector.
     */
    public float getNormalizedY()
    {
        if (stickLocationX == 0 && stickLocationY == 0)
        {
            return 0;
        }
        return stickLocationY
            / (float)Math.sqrt(stickLocationX * stickLocationX + stickLocationY
                * stickLocationY);
    }


    /**
     * Gets the ratio.
     *
     * @return the ratio
     */
    public float getRatio()
    {
        float radius = getWidth() / 2;
        return ((float)Math.sqrt(stickLocationX * stickLocationX
            + stickLocationY * stickLocationY))
            / radius;
    }
}
