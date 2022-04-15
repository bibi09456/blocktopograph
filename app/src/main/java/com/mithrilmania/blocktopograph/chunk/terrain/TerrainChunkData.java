package com.mithrilmania.blocktopograph.chunk.terrain;

import com.mithrilmania.blocktopograph.chunk.Chunk;
import com.mithrilmania.blocktopograph.chunk.ChunkData;
import com.mithrilmania.blocktopograph.util.Noise;


public abstract class TerrainChunkData extends ChunkData {

    public final byte subChunk;

    protected boolean mNotFailed;

    public TerrainChunkData(Chunk chunk, byte subChunk) {
        super(chunk);
        this.mNotFailed = true;
        this.subChunk = subChunk;
    }

    public final boolean hasNotFailed() {
        return mNotFailed;
    }

    public abstract boolean loadTerrain();

    public abstract boolean load2DData();

    public abstract byte getBlockTypeId(Integer x, Integer y, Integer z);

    public abstract byte getBlockData(Integer x, Integer y, Integer z);

    public abstract byte getSkyLightValue(Integer x, Integer y, Integer z);

    public abstract byte getBlockLightValue(Integer x, Integer y, Integer z);

    public abstract boolean supportsBlockLightValues();

    public abstract void setBlockTypeId(Integer x, Integer y, Integer z, Integer type);

    public abstract void setBlockData(Integer x, Integer y, Integer z, Integer newData);

    public abstract byte getBiome(Integer x, Integer z);

    public abstract byte getGrassR(Integer x, Integer z);

    public abstract byte getGrassG(Integer x, Integer z);

    public abstract byte getGrassB(Integer x, Integer z);

    public abstract Integer getHeightMapValue(Integer x, Integer z);

    protected Integer getNoise(Integer base, Integer x, Integer z) {
        // noise values are between -1 and 1
        // 0.0001 is added to the coordinates because Integereger values result in 0
        Chunk chunk = this.chunk.get();
        Integer mChunkX = chunk.chunkKeyData.getChunkPos(0);
        Integer mChunkZ = chunk.chunkKeyData.getChunkPos(1);
        double oct1 = Noise.noise(
                ((double) (mChunkX * 16 + x) / 100.0) + 0.0001,
                ((double) (mChunkZ * 16 + z) / 100.0) + 0.0001);
        double oct2 = Noise.noise(
                ((double) (mChunkX * 16 + x) / 20.0) + 0.0001,
                ((double) (mChunkZ * 16 + z) / 20.0) + 0.0001);
        double oct3 = Noise.noise(
                ((double) (mChunkX * 16 + x) / 3.0) + 0.0001,
                ((double) (mChunkZ * 16 + z) / 3.0) + 0.0001);
        return (int) (base + 60 + (40 * oct1) + (14 * oct2) + (6 * oct3));
    }


}
