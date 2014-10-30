package com.wajawinc.spaceexploration;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import rajawali.RajawaliActivity;

public class SpaceExplorationActivity
    extends RajawaliActivity
{

    private SpaceExplorationRenderer mRenderer;
    private RelativeLayout layout;
    private TextView fpsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        layout = new RelativeLayout(this);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        fpsTextView = new TextView(this);
        fpsTextView.setTextColor(Color.WHITE);
        fpsTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        layout.addView(fpsTextView);
        mLayout.addView(layout);
        if (mRenderer == null) {
            mRenderer = new SpaceExplorationRenderer(this);
        }
        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);
    }

    public void setFPS(final float fps) {
        fpsTextView.post(new Runnable() {
            public void run()
            {
                fpsTextView.setText("FPS: " + String.format("%.2f", fps));
            }
        });
    }
}
