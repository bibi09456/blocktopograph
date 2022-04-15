package com.mithrilmania.blocktopograph.chunk;

import androidx.annotation.NonNull;

import com.mithrilmania.blocktopograph.WorldData;
import com.mithrilmania.blocktopograph.block.Block;
import com.mithrilmania.blocktopograph.block.BlockTemplate;
import com.mithrilmania.blocktopograph.block.BlockTemplates;
import com.mithrilmania.blocktopograph.map.Biome;
import com.mithrilmania.blocktopograph.map.Dimension;

import java.io.IOException;


public final class VoidChunk extends Chunk {

    VoidChunk(WorldData worldData, ChunkKeyData chunkKeyData) {
        super(worldData, chunkKeyData);
        mIsVoid = true;
    }

    @Override
    public Boolean supportsBlockLightValues() {
        return false;
    }

    @Override
    public Boolean supportsHeightMap() {
        return false;
    }

    @Override
    public Integer getHeightMapValue(Integer x, Integer z) {
        return 0;
    }

    @Override
    public Integer getBiome(Integer x, Integer z) {
        return 0;
    }

    @Override
    public void setBiome(Integer x, Integer z, Integer id) { }

    @Override
    public Integer getGrassColor(Integer x, Integer z) {
        return 0;
    }

    @NonNull
    @Override
    public BlockTemplate getBlockTemplate(Integer x, Integer y, Integer z, Integer layer) {
        return BlockTemplates.getAirTemplate();
    }

    @NonNull
    @Override
    public Block getBlock(Integer x, Integer y, Integer z, Integer layer) {
        throw new RuntimeException();
    }

    @Override
    public void setBlock(Integer x, Integer y, Integer z, Integer layer, @NonNull Block block) {
        throw new RuntimeException();
    }

    @Override
    public Integer getBlockLightValue(Integer x, Integer y, Integer z) {
        return 0;
    }

    @Override
    public Integer getSkyLightValue(Integer x, Integer y, Integer z) {
        return 0;
    }

    @Override
    public Integer getHighestBlockYUnderAt(Integer x, Integer z, Integer y) {
        return chunkKeyData.getChunkHeight(0) - 1;
    }

    @Override
    public void save() throws WorldData.WorldDBException, IOException { }
}
