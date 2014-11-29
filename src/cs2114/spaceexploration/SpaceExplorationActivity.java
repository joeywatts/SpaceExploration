package cs2114.spaceexploration;

// Class depends upon the Rajawali 3D library (stable v0.7).

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import rajawali.RajawaliActivity;

// -------------------------------------------------------------------------
/**
 *  SpaceExplorationActivity is the activity that starts the game.
 *
 *  @author Joey
 *  @version Nov 28, 2014
 */
public class SpaceExplorationActivity
    extends RajawaliActivity
{

    private SpaceExplorationRenderer mRenderer;
    private RelativeLayout           layout;
    private TextView                 fpsTextView;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        layout = new RelativeLayout(this);
        layout.setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        fpsTextView = new TextView(this);
        fpsTextView.setTextColor(Color.WHITE);
        fpsTextView.setLayoutParams(new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT));
        layout.addView(fpsTextView);
        mLayout.addView(layout);
        if (mRenderer == null)
        {
            mRenderer = new SpaceExplorationRenderer(this);
        }
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
                fpsTextView.setText("FPS: " + String.format("%.2f", fps));
            }
        });
    }
}
