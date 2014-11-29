package cs2114.spaceexploration;

import com.wajawinc.spaceexploration.R;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

// -------------------------------------------------------------------------
/**
 * SpaceExplorationMenuActivity is an Activity for the Main Menu.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class SpaceExplorationMenuActivity
    extends Activity
    implements OnClickListener
{

    private Button newGame;
    private Button continueGame;
    private Button options;
    private Button other;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
        newGame = (Button)findViewById(R.id.newGame);
        continueGame = (Button)findViewById(R.id.continueGame);
        options = (Button)findViewById(R.id.options);
        other = (Button)findViewById(R.id.other);
        newGame.setOnClickListener(this);
        continueGame.setOnClickListener(this);
        options.setOnClickListener(this);
        other.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        if (v == newGame)
        {
            Intent intent = new Intent(this, SpaceExplorationActivity.class);
            startActivity(intent);
        }
        else if (v == continueGame)
        {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
        }
        else if (v == options)
        {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
        }
        else if (v == other)
        {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
        }

    }

}
