package com.mithrilmania.blocktopograph.chunk.terrain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mithrilmania.blocktopograph.Log;
import com.mithrilmania.blocktopograph.WorldData;
import com.mithrilmania.blocktopograph.block.Block;
import com.mithrilmania.blocktopograph.block.BlockTemplate;
import com.mithrilmania.blocktopograph.block.OldBlock;
import com.mithrilmania.blocktopograph.map.Dimension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TerrainSubChunk {

//    private final WeakReference<OldBlockRegistry> mBlockRegistry;

    boolean mHasSkyLight;
    boolean mHasBlockLight;
    boolean mIsError;

    protected TerrainSubChunk() {
//        mBlockRegistry = new WeakReference<>(oldBlockRegistry);
    }

    @Nullable
    public static TerrainSubChunk create(@NonNull byte[] rawData) {
        LogActivity.logInfo(TerrainSubChunk.class, Arrays.toString(rawData));
        TerrainSubChunk subChunk;
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawData);
        switch (rawData[0]) {
            case 1:
            case 8:
                subChunk = new V1d2d13TerrainSubChunk(byteBuffer);
                break;
            default:
                subChunk = null;
        }
        return subChunk;
    }

    @Nullable
    public static TerrainSubChunk createEmpty(Integer preferredVersion) {
        TerrainSubChunk subChunk;
        switch (preferredVersion) {
            case 1:
            case 8:
                subChunk = new V1d2d13TerrainSubChunk();
                break;
            default:
                subChunk = null;
        }
        return subChunk;
    }

//    @NonNull
//    protected OldBlock wrapKnownBlock(KnownBlockRepr knownBlock) {
//        // TODO: This would be not efficiency for old saves, add corresponding oldBlock to known blocks.
//        return mBlockRegistry.get().createBlock(knownBlock);
//    }

    @NonNull
    abstract public BlockTemplate getBlockTemplate(Integer x, Integer y, Integer z, Integer layer);

    @NonNull
    abstract public Block getBlock(Integer x, Integer y, Integer z, Integer layer);

    abstract public void setBlock(Integer x, Integer y, Integer z, Integer layer, @NonNull Block block);

    abstract public Integer getBlockLightValue(Integer x, Integer y, Integer z);

    abstract public Integer getSkyLightValue(Integer x, Integer y, Integer z);

    protected static Integer getOffset(Integer x, Integer y, Integer z) {
        return (((x << 4) | z) << 4) | y;
    }

    public final Boolean hasBlockLight() {
        return mHasBlockLight;
    }


    public final Boolean isError() {
        return mIsError;
    }

//    @Nullable
//    protected OldBlockRegistry getBlockRegistry() {
//        OldBlockRegistry oldBlockRegistry = mBlockRegistry.get();
//        if (oldBlockRegistry == null) {
//            mIsError = true;
//        }
//        return oldBlockRegistry;
//    }

    abstract public void save(WorldData worldData, ChunkKeyData chunkKeyData, Integer which) throws WorldData.WorldDBException, IOException;

}
