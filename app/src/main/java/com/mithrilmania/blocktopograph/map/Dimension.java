package com.mithrilmania.blocktopograph.map;


import androidx.annotation.StringRes;

import com.mithrilmania.blocktopograph.R;
import com.mithrilmania.blocktopograph.map.renderer.MapType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Dimension {

    OVERWORLD(0, "Overworld", 16, 16, -64, 320, 1, MapType.OVERWORLD_SATELLITE),
    NETHER(1, "Nether", 16, 16, 0, 128, 1, MapType.NETHER),
    END(2, "End", 16, 16, 0, 128, 1, MapType.END_SATELLITE);

    public final Integer id;
    public final Integer chunkWidth, chunkLength, chunkHeighLowest, chunkHeighHighest;
    public final Integer dimensionScale;
    public final String dataName, name;
    public final MapType defaultMapType;

    Dimension(
            Integer id,
            String name,
            Integer chunkWidth,
            Integer chunkLength,
            Integer chunkHeighLowest,
            Integer chunkHeighHighest,
            Integer dimensionScale,
            MapType defaultMapType) {
        this.id = id;
        this.dataName = name.toLowerCase(Locale.ROOT);
        this.name = name;
        this.chunkWidth = chunkWidth;
        this.chunkLength = chunkLength;
        this.chunkHeighLowest = chunkHeighLowest;
        this.chunkHeighHighest = chunkHeighHighest;
        this.dimensionScale = dimensionScale;
        this.defaultMapType = defaultMapType;
    }

    public String getName() {
        return this.name;
    }

    private static final Map<String, Dimension> dimensionMap = new HashMap<>();

    static {
        for (Dimension dimension : Dimension.values()) {
            dimensionMap.put(dimension.dataName, dimension);
        }
    }

    public static Dimension getDimension(String dataName) {
        if (dataName == null) return null;
        return dimensionMap.get(dataName);
    }

    public static Dimension getDimension(Integer id) {
        for (Dimension dimension : values()) {
            if (dimension.id.equals(id)) return dimension;
        }
        return null;
    }

}
