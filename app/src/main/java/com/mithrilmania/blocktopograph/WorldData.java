package com.mithrilmania.blocktopograph;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.litl.leveldb.DB;
import com.litl.leveldb.Iterator;
import com.mithrilmania.blocktopograph.block.OldBlockRegistry;
import com.mithrilmania.blocktopograph.chunk.Chunk;
import com.mithrilmania.blocktopograph.chunk.ChunkKeyData;
import com.mithrilmania.blocktopograph.chunk.ChunkTag;
import com.mithrilmania.blocktopograph.chunk.Version;
import com.mithrilmania.blocktopograph.map.Dimension;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.IntStream;

/**
 * Wrapper around level.dat world spec en levelDB database.
 */
public class WorldData {

    //another method for debugging, makes it easy to print a readable byte array
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public DB db;

    private final WeakReference<World> world;
    private final LruCache<Key, Chunk> chunks = new ChunkCache(this, 256);
    public final OldBlockRegistry mOldBlockRegistry;

    public WorldData(World world) {
        this.world = new WeakReference<>(world);
        this.mOldBlockRegistry = new OldBlockRegistry(2048);
    }

    static String bytesToHex(byte[] bytes, int start, int end) {
        char[] hexChars = new char[(end - start)];
        for (int j = start; j < end; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[(j - start)] = hexArray[v >>> 4];
            hexChars[(j - start) + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] getChunkDataKey(ChunkKeyData chunkKeyData, ChunkTag type, byte subChunk, boolean asSubChunk) {
        byte[] key;
        Integer x = chunkKeyData.chunkPos.get(0);
        Integer z = chunkKeyData.chunkPos.get(1);
        if (chunkKeyData.dimension == Dimension.OVERWORLD) {
            key = new byte[asSubChunk ? 10 : 9];
            System.arraycopy(getReversedBytes(x), 0, key, 0, 4);
            System.arraycopy(getReversedBytes(z), 0, key, 4, 4);
            key[8] = type.dataID;
            if (asSubChunk) key[9] = subChunk;
        } else {
            key = new byte[asSubChunk ? 14 : 13];
            System.arraycopy(getReversedBytes(x), 0, key, 0, 4);
            System.arraycopy(getReversedBytes(z), 0, key, 4, 4);
            System.arraycopy(getReversedBytes(chunkKeyData.dimension.id), 0, key, 8, 4);
            key[12] = type.dataID;
            if (asSubChunk) key[13] = subChunk;
        }
        return key;
    }

    private static byte[] getReversedBytes(int i) {
        return new byte[]{
                (byte) i,
                (byte) (i >> 8),
                (byte) (i >> 16),
                (byte) (i >> 24)
        };
    }

    //load db when needed (does not load it!)
    @SuppressLint({"SetWorldReadable", "SetWorldWritable"})
    public void load() throws WorldDataLoadException {

        if (db != null) return;

        World world = this.world.get();

        File dbFile = new File(world.worldFolder, "db");
        if (!dbFile.canRead()) {
            if (!dbFile.setReadable(true, false))
                throw new WorldDataLoadException("World-db folder is not readable! World-db folder: " + dbFile.getAbsolutePath());
        }
        if (!dbFile.canWrite()) {
            if (!dbFile.setWritable(true, false))
                throw new WorldDataLoadException("World-db folder is not writable! World-db folder: " + dbFile.getAbsolutePath());
        }

        if (dbFile.listFiles() == null)
            throw new WorldDataLoadException("Failed loading world-db: cannot list files in worldfolder");

        try {
            this.db = new DB(dbFile);
        } catch (Exception e) {
            LogActivity.logError(this.getClass(), e);
        }
    }

    //open db to make it available for this app
    public void openDB() throws WorldDBException {
        if (this.db == null)
            throw new WorldDBException("DB is null!!! (db is not loaded probably)");

        if (this.db.isClosed()) {
            try {
                this.db.open();
            } catch (Exception e) {
                LogActivity.logError(this.getClass(), e);
            }
        }

    }

    //close db to make it available for other apps (Minecraft itself!)
    public void closeDB() throws WorldDBException {
        if (this.db == null)
            return;
        //Why bother throw an exception, isn't it good enough being able to skip closing as it's null?
        try {
            this.db.close();
        } catch (Exception e) {
            //db was already closed (probably)
            e.printStackTrace();
        }
    }

    public byte[] getChunkData(ChunkKeyData chunkKeyData, ChunkTag type, byte subChunk, boolean asSubChunk) throws WorldDBException, WorldDBLoadException {

        //ensure that the db is opened
        this.openDB();

        byte[] chunkKey = getChunkDataKey(chunkKeyData, type, subChunk, asSubChunk);
        LogActivity.logInfo(this.getClass(), "Getting cX: "+chunkKeyData.getChunkPos(0)+" cZ: "+chunkKeyData.getChunkPos(1)+ " with key: "+bytesToHex(chunkKey, 0, chunkKey.length));
        return db.get(chunkKey);
    }

    public byte[] getChunkData(ChunkKeyData chunkKeyData, ChunkTag type) throws WorldDBException, WorldDBLoadException {
        return getChunkData(chunkKeyData, type, (byte) 0, false);
    }

    public void writeChunkData(ChunkKeyData chunkKeyData, ChunkTag type, byte subChunk, boolean asSubChunk, byte[] chunkData) throws WorldDBException {
        //ensure that the db is opened
        this.openDB();

        db.put(getChunkDataKey(chunkKeyData, type, subChunk, asSubChunk), chunkData);
    }

    public void removeChunkData(ChunkKeyData chunkKeyData, ChunkTag type, byte subChunk, boolean asSubChunk) throws WorldDBException {
        //ensure that the db is opened
        this.openDB();

        db.delete(getChunkDataKey(chunkKeyData, type, subChunk, asSubChunk));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeFullChunk(ChunkKeyData chunkKeyData) {
        var iterator = db.iterator();
        int count = 0;
        var compareKey = getChunkDataKey(chunkKeyData, ChunkTag.DATA_2D, (byte) 0, false);
        int baseKeyLength = chunkKeyData.dimension == Dimension.OVERWORLD ? 8 : 12;
        for (iterator.seekToFirst(); iterator.isValid() && count < 800; iterator.next(), count++) {
            byte[] key = iterator.getKey();
            if (key.length > baseKeyLength && key.length <= baseKeyLength + 3 &&
                    IntStream.range(0, baseKeyLength).allMatch(i -> key[i] == compareKey[i]))
                db.delete(key);
        }
        iterator.close();
    }

    public Chunk getChunk(ChunkKeyData chunkKeyData, Boolean createIfMissing) {
        Key key = new Key(chunkKeyData);
        key.createIfMissng = createIfMissing;
        return chunks.get(key);
    }

    public Chunk getChunk(ChunkKeyData chunkKeyData) {
        Key key = new Key(chunkKeyData);
        return chunks.get(key);
    }

    // Avoid using cache for stream like operations.
    // Caller shall lock cache before operation and invalidate cache afterwards.
    public Chunk getChunkStreaming(ChunkKeyData chunkKeyData, Boolean createIfMissing) {
        return Chunk.create(this, chunkKeyData, createIfMissing);
    }

    public void resetCache() {
        this.chunks.evictAll();
    }

    public String[] getNetworkPlayerNames() {
        List<String> players = getDBKeysStartingWith("player_");
        return players.toArray(new String[0]);
    }

    public List<String> getDBKeysStartingWith(String startWith) {
        Iterator it = db.iterator();

        LinkedList<String> items = new LinkedList<>();
        for (it.seekToFirst(); it.isValid(); it.next()) {
            byte[] key = it.getKey();
            if (key == null) continue;
            String keyStr = new String(key);
            if (keyStr.startsWith(startWith)) items.add(keyStr);
        }
        it.close();

        return items;
    }

    private static class ChunkCache extends LruCache<Key, Chunk> {

        private final WeakReference<WorldData> worldData;

        ChunkCache(WorldData worldData, Integer maxSize) {
            super(maxSize);
            this.worldData = new WeakReference<>(worldData);
        }

        @Override
        protected void entryRemoved(boolean evicted, Key key, Chunk oldValue, Chunk newValue) {
            try {
                oldValue.save();
            } catch (Exception e) {
                LogActivity.logError(this.getClass(), e);
            }
        }

        @Nullable
        @Override
        protected Chunk create(Key key) {
            WorldData worldData = this.worldData.get();
            if (worldData == null) return null;
            return Chunk.create(worldData, key.chunkKeyData, key.createIfMissng);
        }
    }

    static class Key {

        public ChunkKeyData chunkKeyData;
        public Boolean createIfMissng;

        Key(@NonNull ChunkKeyData chunkKeyData) {
            this.chunkKeyData = chunkKeyData;
        }

        @Override
        public int hashCode() {
            return Integer.getInteger(
                    chunkKeyData.getChunkPos(0).toString().concat(
                    chunkKeyData.getChunkPos(1).toString()).concat(
                    chunkKeyData.dimension.id.toString()));
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) return false;
            Key another = (Key) obj;
            return (this.hashCode() == another.hashCode());
        }
    }

    public static class WorldDataLoadException extends Exception {
        private static final long serialVersionUID = 659185044124115547L;

        public WorldDataLoadException(String msg) {
            super(msg);
        }
    }

    public static class WorldDBException extends Exception {
        private static final long serialVersionUID = -3299282170140961220L;

        public WorldDBException(String msg) {
            super(msg);
        }
    }

    public static class WorldDBLoadException extends Exception {
        private static final long serialVersionUID = 4412238820886423076L;

        public WorldDBLoadException(String msg) {
            super(msg);
        }
    }

}
