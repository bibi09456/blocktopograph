package com.mithrilmania.blocktopograph.chunk;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.mithrilmania.blocktopograph.Log;
import com.mithrilmania.blocktopograph.WorldData;
import com.mithrilmania.blocktopograph.block.Block;
import com.mithrilmania.blocktopograph.block.BlockTemplate;
import com.mithrilmania.blocktopograph.map.Dimension;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.stream.IntStream;

public abstract class Chunk {

    protected final WeakReference<WorldData> mWorldData;
    public final ChunkKeyData chunkKeyData;
    protected NBTChunkData mEntity;
    protected NBTChunkData mTileEntity;
    Boolean mIsVoid;
    Boolean mIsError;

    Chunk(WorldData worldData, ChunkKeyData chunkKeyData) {
        mWorldData = new WeakReference<>(worldData);
        this.chunkKeyData = chunkKeyData;
        mIsVoid = false;
        mIsError = false;
        try {
            mEntity = chunkKeyData.version.createEntityChunkData(this);
            mTileEntity = chunkKeyData.version.createBlockEntityChunkData(this);
        } catch (Version.VersionException e) {
            Log.d(this, e);
        }
    }

    public static Chunk createEmpty(
            @NonNull WorldData worldData,
            @NonNull ChunkKeyData chunkKeyData
    ) {
        Chunk chunk;
        switch (chunkKeyData.version) {
            case V1_2_PLUS:
                try {
                    worldData.writeChunkData(chunkKeyData, ChunkTag.FINALIZED_STATE, (byte) 0, false, new byte[]{2, 0, 0, 0});
                    worldData.writeChunkData(chunkKeyData, ChunkTag.VERSION_PRE16, (byte) 0, false, new byte[]{0xf});
                    chunk = new BedrockChunk(worldData, chunkKeyData, true);
                } catch (Exception e) {
                    Log.d(Chunk.class, e);
                    chunk = new VoidChunk(worldData, createOfVersion, chunkX, chunkZ, dimension);
                }
                break;
            default:
                chunk = new VoidChunk(worldData, chunkKeyData);
        }
        return chunk;
    }

    public static Chunk create(
            @NonNull WorldData worldData,
            @NonNull ChunkKeyData chunkKeyData,
            Boolean createIfMissing) {
        Version version;
        try {
            byte[] data = worldData.getChunkData(chunkKeyData, ChunkTag.VERSION_PRE16);
            if (data == null)
                data = worldData.getChunkData(chunkKeyData, ChunkTag.VERSION);
            if (data == null && createIfMissing)
                return createEmpty(worldData, chunkKeyData);
            version = Version.getVersion(data);
        } catch (WorldData.WorldDBLoadException | WorldData.WorldDBException e) {
            Log.d(Chunk.class, e);
            version = Version.ERROR;
        }
        Chunk chunk;
        switch (version) {
            case V1_2_PLUS:
                chunk = new BedrockChunk(worldData, chunkKeyData, false);
                break;
            default:
                chunk = new VoidChunk(worldData, chunkKeyData);
        }
        return chunk;
    }

    public final WorldData getWorldData() {
        return mWorldData.get();
    }

    public final Boolean isVoid() {
        return mIsVoid;
    }

    public final Boolean isError() {
        return mIsError;
    }

    abstract public Boolean supportsBlockLightValues();

    abstract public Boolean supportsHeightMap();

    public Integer getHeightLimit() {
        return chunkKeyData.getChunkHeight();
    }

    public Integer getTotalHeight() {
        return getHeightLimit() + chunkKeyData.getChunkHeight(0);
    }

    abstract public Integer getHeightMapValue(Integer x, Integer z);

    abstract public Integer getBiome(Integer x, Integer z);

    abstract public void setBiome(Integer x, Integer z, Integer id);

    abstract public Integer getGrassColor(Integer x, Integer z);

    @NonNull
    public BlockTemplate getBlockTemplate(Integer x, Integer y, Integer z) {
        return getBlockTemplate(x, y, z, 0);
    }

    @NonNull
    abstract public BlockTemplate getBlockTemplate(Integer x, Integer y, Integer z, Integer layer);

    @NonNull
    public Block getBlock(Integer x, Integer y, Integer z) {
        return getBlock(x, y, z, 0);
    }

    @NonNull
    abstract public Block getBlock(Integer x, Integer y, Integer z, Integer layer);

    abstract public void setBlock(Integer x, Integer y, Integer z, Integer layer, @NonNull Block block);

    abstract public Integer getBlockLightValue(Integer x, Integer y, Integer z);

    abstract public Integer getSkyLightValue(Integer x, Integer y, Integer z);

    abstract public Integer getHighestBlockYUnderAt(Integer x, Integer z, Integer y);

    abstract public void save() throws WorldData.WorldDBException, IOException;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteThis() throws NullPointerException {
        // TODO: delete all with given prefix
        WorldData worldData = mWorldData.get();
        if (worldData == null) throw new NullPointerException("World data is null.");
        worldData.removeFullChunk(chunkKeyData);
        // Prevent saving.
        mIsError = true;
    }

    public final NBTChunkData getEntity() {
        return mEntity;
    }

    public final NBTChunkData getBlockEntity() {
        return mTileEntity;
    }
}
