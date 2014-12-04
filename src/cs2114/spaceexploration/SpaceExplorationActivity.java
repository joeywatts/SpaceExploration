package cs2114.spaceexploration;

// Class depends upon the Rajawali 3D library (stable v0.9).

import rajawali.RajawaliActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cs2114.spaceexploration.view.AnalogStick;

// -------------------------------------------------------------------------
/**
 * SpaceExplorationActivity is the activity that starts the game. Due to the
 * nature of our project (its dependency on a 3D graphics library, multiple
 * threads, pseudorandomness, and arbitrarily defined functionality), testing
 * most of our classes is unfeasible.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 28, 2014
 */
public class SpaceExplorationActivity
    extends RajawaliActivity
{

    private SpaceExplorationRenderer mRenderer;
    private RelativeLayout           layout;
    private TextView                 fpsTextView;

    private AnalogStick              leftAnalogStick;
    private Button                   accelerateButton;
    private Button                   brakeButton;


    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (layout == null)
        {
            layout =
                (RelativeLayout)getLayoutInflater().inflate(
                    R.layout.activity_space_exploration,
                    null);
            fpsTextView = (TextView)layout.findViewById(R.id.fpsText);
            leftAnalogStick =
                (AnalogStick)layout.findViewById(R.id.leftAnalogStick);
            accelerateButton = (Button)layout.findViewById(R.id.accelerate);
            brakeButton = (Button)layout.findViewById(R.id.brake);
            mLayout.addView(layout);
        }
        if (mRenderer == null)
        {
            mRenderer = new SpaceExplorationRenderer(this);
        }
        mRenderer.setAnalogStick(leftAnalogStick);
        mRenderer.setAccelerateButton(accelerateButton);
        mRenderer.setBrakeButton(brakeButton);
        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);
    }


    /**
     * Updates the FPS text view to reflect the current FPS.
     *
     * @param fps
     *            the current FPS.
     */
    public void setFPS(final float fps)
    {
        fpsTextView.post(new Runnable() {
            public void run()
            {
                fpsTextView.setText("FPS: " + String.format("%.2f", fps)
                    + " LOC : " + mRenderer.getPlayer().getPosition());
            }
        });
    }

}
