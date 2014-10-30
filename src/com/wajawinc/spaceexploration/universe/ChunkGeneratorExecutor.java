package com.wajawinc.spaceexploration.universe;

import java.util.concurrent.LinkedBlockingQueue;
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

    private static ChunkGeneratorExecutor instance;
    
    public static ChunkGeneratorExecutor getExecutor() {
    	if (instance == null) {
    		return new ChunkGeneratorExecutor();
    	}
    	return instance;
    }

    private ChunkGeneratorExecutor() {
        super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
        instance = this;
    }

    public void generateChunk(final Planet planet, final Number3D chunkLocation, final int lodLevel) {
        this.execute(new Runnable() {
            public void run()
            {
            	if (planet.getChunks().containsKey(chunkLocation)) {
            		Chunk c = planet.getChunks().get(chunkLocation);
					c.tessellate(lodLevel);
            	} else {
            		planet.generateChunk(chunkLocation, lodLevel);
            	}
            }
        });
    }

}
