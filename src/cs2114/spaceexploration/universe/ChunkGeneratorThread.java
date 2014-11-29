package cs2114.spaceexploration.universe;

//Class depends upon the Rajawali 3D library (stable v0.7).

import java.util.concurrent.ConcurrentLinkedQueue;
import rajawali.math.Number3D;

// -------------------------------------------------------------------------
/**
 * ChunkGeneratorThread is a background Thread for generating Chunks
 * asynchronously.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class ChunkGeneratorThread
    extends Thread
{

    private static class ChunkUpdate
    {
        Planet   planet;
        Number3D chunkLoc;
        int      lodLevel;
    }

    private ConcurrentLinkedQueue<ChunkUpdate> chunksToGenerate;


    public ChunkGeneratorThread()
    {
        chunksToGenerate = new ConcurrentLinkedQueue<ChunkUpdate>();
    }


    public void generateChunk(Planet planet, Number3D chunk, int lodLevel)
    {
        ChunkUpdate update = new ChunkUpdate();
        update.chunkLoc = chunk;
        update.lodLevel = lodLevel;
        update.planet = planet;
        chunksToGenerate.offer(update);
    }


    @Override
    public void run()
    {
        try
        {
            while (!isInterrupted())
            {
                if (!chunksToGenerate.isEmpty())
                {
                    ChunkUpdate update = chunksToGenerate.poll();
                    update.planet.generateChunk(
                        update.chunkLoc,
                        update.lodLevel);
                }
                else
                {
                    Thread.sleep(300);
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
