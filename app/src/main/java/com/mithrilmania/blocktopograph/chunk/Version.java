package com.mithrilmania.blocktopograph.chunk;


import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mithrilmania.blocktopograph.LogActivity;


public enum Version {

    ERROR("ERROR", "failed to retrieve version number", -2, 16, 16),
    NULL("NULL", "no data", -1, 16, 16),
    V1_2_PLUS("v1.2.13", "Global numeric id replaced with string id and per-chunk numeric id", 7, 16, 16),;
//    V1_16_PLUS("v1.16(17)", "val is replaced by block states",0x16,16,16);

    public static final Version LATEST_SUPPORTED_VERSION = V1_2_PLUS;

    public final String displayName, description;
    public final int id, subChunkHeight, subChunks;


    Version(String displayName, String description, int id, int subChunkHeight, int subChunks) {
        this.displayName = displayName;
        this.description = description;
        this.id = id;
        this.subChunkHeight = subChunkHeight;
        this.subChunks = subChunks;
    }

    private static final SparseArray<Version> versionMap;

    static {
        versionMap = new SparseArray<>();
        for (Version b : Version.values()) {
            versionMap.put(b.id, b);
        }
    }

    @NonNull
    public static Version getVersion(@Nullable byte[] data) {
        //Log.d("Data version: "+ ConvertUtil.bytesToHexStr(data));

        //`data` is supposed to be one byte,
        // but it might grow to contain more data later on, or larger version ids.
        // Looking up the first byte is sufficient for now.
        if (data == null || data.length <= 0) {
            return NULL;
        } else {
            int versionNumber = data[0] & 0xff;

            //fallback version
            if (versionNumber > LATEST_SUPPORTED_VERSION.id) {
                versionNumber = LATEST_SUPPORTED_VERSION.id;
            }

            Version version = versionMap.get(versionNumber);
            LogActivity.logInfo(Version.class, version.toString());
            return version;
        }
    }

    @Nullable
    public NBTChunkData createEntityChunkData(Chunk chunk) throws VersionException {
        switch (this) {
            case ERROR:
            case NULL:
                return null;
            default:
                return new NBTChunkData(chunk, ChunkTag.ENTITY);
        }
    }

    @Nullable
    public NBTChunkData createBlockEntityChunkData(Chunk chunk) throws VersionException {
        switch (this) {
            case ERROR:
            case NULL:
                return null;
            default:
                //use the latest version, like nothing will ever happen...
                return new NBTChunkData(chunk, ChunkTag.BLOCK_ENTITY);
        }
    }

    @NonNull

    @Override
    public String toString() {
        return "[Minecraft version \"" + displayName + "\" (version-code: " + id + ")]";
    }

    public static class VersionException extends Exception {

        VersionException(String msg, @NonNull Version version) {
            super(msg + " " + version);
        }
    }
}
