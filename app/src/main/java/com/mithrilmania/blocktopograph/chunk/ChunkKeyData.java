package com.mithrilmania.blocktopograph.chunk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mithrilmania.blocktopograph.map.Dimension;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author NguyenDuck
 */
public class ChunkKeyData {
    /**
     * Chunk position in the world
     */
    public Vector<Integer> chunkPos;

    /**
     * Lowest and Highest height of the world
     */
    public final HashMap<String, Integer> chunkHeight = new HashMap<>(2);

    /**
     * Dimension of chunk in the world
     */
    public final Dimension dimension;

    /**
     * Chunk version in the world
     */
    public final Version version;

    public ChunkKeyData(@NonNull Vector<Integer> chunkPos) {
        this.chunkPos = chunkPos;
        chunkHeight.put("Lowest", 0);
        chunkHeight.put("Highest", 256);
        dimension = Dimension.OVERWORLD;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    public ChunkKeyData(
            @NonNull Vector<Integer> chunkPos,
            @NonNull HashMap<String, Integer> chunkHeight
    ) {
        this.chunkPos = chunkPos;
        this.chunkHeight.putAll(chunkHeight);
        dimension = Dimension.OVERWORLD;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    public ChunkKeyData(
            @NonNull Vector<Integer> chunkPos,
            @NonNull HashMap<String, Integer> chunkHeight,
            @NonNull Dimension dimension
    ) {
        this.chunkPos = chunkPos;
        this.chunkHeight.putAll(chunkHeight);
        this.dimension = dimension;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    public ChunkKeyData(
            @NonNull Vector<Integer> chunkPos,
            @NonNull HashMap<String, Integer> chunkHeight,
            @NonNull Dimension dimension,
            @NonNull Version version) {
        this.chunkPos = chunkPos;
        this.chunkHeight.putAll(chunkHeight);
        this.dimension = dimension;
        this.version = version;
    }

    public ChunkKeyData(
            @NonNull Vector<Integer> chunkPos,
            @NonNull Integer chunkHeightLowest,
            @NonNull Integer chunkHeightHighest
    ) {
        this.chunkPos = chunkPos;
        this.chunkHeight.put("Lowest", chunkHeightLowest);
        this.chunkHeight.put("Highest", chunkHeightHighest);
        dimension = Dimension.OVERWORLD;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    public ChunkKeyData(
            @NonNull Vector<Integer> chunkPos,
            @NonNull Integer chunkHeightLowest,
            @NonNull Integer chunkHeightHighest,
            @NonNull Dimension dimension
    ) {
        this.chunkPos = chunkPos;
        this.chunkHeight.put("Lowest", chunkHeightLowest);
        this.chunkHeight.put("Highest", chunkHeightHighest);
        this.dimension = dimension;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    public ChunkKeyData(
            @NonNull Vector<Integer> chunkPos,
            @NonNull Integer chunkHeightLowest,
            @NonNull Integer chunkHeightHighest,
            @NonNull Dimension dimension,
            @NonNull Version version
    ) {
        this.chunkPos = chunkPos;
        this.chunkHeight.put("Lowest", chunkHeightLowest);
        this.chunkHeight.put("Highest", chunkHeightHighest);
        this.dimension = dimension;
        this.version = version;
    }

    public ChunkKeyData(
            @NonNull Integer chunkPositionX,
            @NonNull Integer chunkPositionZ,
            @NonNull Integer chunkHeightLowest,
            @NonNull Integer chunkHeightHighest
    ) {
        this.setChunkPos(chunkPositionX, chunkPositionZ);
        this.chunkHeight.put("Lowest", chunkHeightLowest);
        this.chunkHeight.put("Highest", chunkHeightHighest);
        dimension = Dimension.OVERWORLD;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    public ChunkKeyData(
            @NonNull Integer chunkPositionX,
            @NonNull Integer chunkPositionZ,
            @NonNull Integer chunkHeightLowest,
            @NonNull Integer chunkHeightHighest,
            @NonNull Dimension dimension
    ) {
        this.setChunkPos(chunkPositionX, chunkPositionZ);
        this.chunkHeight.put("Lowest", chunkHeightLowest);
        this.chunkHeight.put("Highest", chunkHeightHighest);
        this.dimension = dimension;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    public ChunkKeyData(
            @NonNull Integer chunkPositionX,
            @NonNull Integer chunkPositionZ,
            @NonNull Integer chunkHeightLowest,
            @NonNull Integer chunkHeightHighest,
            @NonNull Dimension dimension,
            @NonNull Version version
    ) {
        this.setChunkPos(chunkPositionX, chunkPositionZ);
        this.chunkHeight.put("Lowest", chunkHeightLowest);
        this.chunkHeight.put("Highest", chunkHeightHighest);
        this.dimension = dimension;
        this.version = version;
    }

    public ChunkKeyData(
            @NonNull Integer chunkPositionX,
            @NonNull Integer chunkPositionZ,
            @NonNull Dimension dimension
    ) {
        this.setChunkPos(chunkPositionX, chunkPositionZ);
        this.chunkHeight.put("Lowest", dimension.chunkHeighLowest);
        this.chunkHeight.put("Highest", dimension.chunkHeighHighest);
        this.dimension = dimension;
        version = Version.LATEST_SUPPORTED_VERSION;
    }

    /**
     * Set chunk position
     * @param chunkPositionX chunk position dimension x
     * @param chunkPositionZ chunk position dimension z
     */
    public final void setChunkPos(@NonNull Integer chunkPositionX, @NonNull Integer chunkPositionZ) {
        if (this.chunkPos == null) {
            this.chunkPos = new Vector<>(2);
        }
        if (this.chunkPos.get(0) == null || this.chunkPos.get(1) == null) {
            this.chunkPos.add(chunkPositionX);
            this.chunkPos.add(chunkPositionZ);
        }
    }

    /**
     * Get chunk 2D position in the world
     * @param dimensionIndex 0 for x, 1 for z
     * @return chunk position in X or Z
     */
    public final Integer getChunkPos(@NonNull Integer dimensionIndex) {
        return this.chunkPos.get(dimensionIndex);
    }

    /**
     * Set chunk height lowest and highest
     * @param chunkHeightLowest chunk height lowest
     * @param chunkHeightHigest chunk height highest
     */
    public final void setChunkHeight(@NonNull Integer chunkHeightLowest, @NonNull Integer chunkHeightHigest) {
        this.chunkHeight.clear();
        this.chunkHeight.put("Lowest", chunkHeightLowest);
        this.chunkHeight.put("Highest", chunkHeightHigest);
    }

    /**
     * Get chunk height highest
     * @return chunk height highest
     */
    public final Integer getChunkHeight() {
        return this.chunkHeight.get("Highest");
    }

    /**
     * Get chunk height lowest or highest
     * @param loh Lowest or Highest, 0 for Lowest, default is Highest
     * @return chunk height lowest or highest
     */
    public final Integer getChunkHeight(@Nullable Integer loh) {
        return (loh == null || loh.equals(0) ? this.chunkHeight.get("Lowest") : this.chunkHeight.get("Highest"));
    }
}
