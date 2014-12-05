package cs2114.spaceexploration.universe;

// Class depends upon the Rajawali 3D library (stable v0.9).

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


    /**
     * Instantiates a new UniverseUpdater.
     *
     * @param universe
     *            the Universe.
     */
    public UniverseUpdater(Universe universe)
    {
        this.universe = universe;
    }


    public void run()
    {
        while (!this.isInterrupted())
        {
            universe.update();
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
