package com.wajawinc.spaceexploration.universe;

import java.util.concurrent.LinkedBlockingQueue;
import com.wajawinc.spaceexploration.SpaceExplorationRenderer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import rajawali.math.Number3D;

public class ChunkGeneratorExecutor
    extends ThreadPoolExecutor
{
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = 1;
    private static final long KEEP_ALIVE_TIME = 1000;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;

    private SpaceExplorationRenderer renderer;

    public ChunkGeneratorExecutor(SpaceExplorationRenderer renderer) {
        super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
        this.renderer = renderer;
    }

    public void generateChunk(final Planet planet, final Number3D chunkLocation) {
        this.execute(new Runnable() {
            public void run()
            {
                planet.generateChunk(chunkLocation);
            }
        });
    }

}
