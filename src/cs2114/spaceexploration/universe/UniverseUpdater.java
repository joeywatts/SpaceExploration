package cs2114.spaceexploration.universe;

// Class depends upon the Rajawali 3D library (stable v0.9).

import cs2114.spaceexploration.entity.Player;

// -------------------------------------------------------------------------
/**
 * UniverseUpdater is a background thread for updating the Universe.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class UniverseUpdater
    extends Thread
{

    private Universe universe;
    private Player   player;


    /**
     * Instantiates a new UniverseUpdater.
     *
     * @param universe
     *            the Universe.
     * @param player
     *            the Player
     */
    public UniverseUpdater(Universe universe, Player player)
    {
        this.universe = universe;
        this.player = player;
    }


    public void run()
    {
        while (!this.isInterrupted())
        {
            universe.update(player);
            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
