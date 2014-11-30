package cs2114.spaceexploration;

// Class depends upon the Rajawali 3D library (stable v0.7).

import rajawali.RajawaliActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cs2114.spaceexploration.view.AnalogStick;

// -------------------------------------------------------------------------
/**
 * SpaceExplorationActivity is the activity that starts the game.
 *
 * @author Joey
 * @version Nov 28, 2014
 */
public class SpaceExplorationActivity extends RajawaliActivity {

	private SpaceExplorationRenderer mRenderer;
	private RelativeLayout layout;
	private TextView fpsTextView;

	private AnalogStick leftAnalogStick;
	private Button accelerateButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.activity_space_exploration, mLayout);
		fpsTextView = (TextView) layout.findViewById(R.id.fpsText);
		leftAnalogStick = (AnalogStick) layout.findViewById(R.id.leftAnalogStick);
		accelerateButton = (Button) layout.findViewById(R.id.accelerate);
		//mLayout.addView(layout);
		if (mRenderer == null) {
			mRenderer = new SpaceExplorationRenderer(this);
		}
		mRenderer.setAnalogStick(leftAnalogStick);
		mRenderer.setAccelerateButton(accelerateButton);
		mRenderer.setSurfaceView(mSurfaceView);
		super.setRenderer(mRenderer);
	}

	/**
	 * Updates the FPS text view to reflect the current FPS.
	 *
	 * @param fps
	 *            the current FPS.
	 */
	public void setFPS(final float fps) {
		fpsTextView.post(new Runnable() {
			public void run() {
				fpsTextView.setText("FPS: " + String.format("%.2f", fps) + " LOC : " + mRenderer.getPlayer().getPosition() );
			}
		});
	}

}
