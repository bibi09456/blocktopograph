package com.mithrilmania.blocktopograph.map.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.mithrilmania.blocktopograph.WorldData;
import com.mithrilmania.blocktopograph.chunk.Chunk;
import com.mithrilmania.blocktopograph.chunk.ChunkKeyData;
import com.mithrilmania.blocktopograph.chunk.Version;
import com.mithrilmania.blocktopograph.map.Dimension;


public class HeightmapRenderer implements MapRenderer {

    public void renderToBitmap(Chunk chunk, Canvas canvas, Dimension dimension, int chunkX, int chunkZ, int pX, int pY, int pW, int pL, Paint paint, WorldData worldData) throws Version.VersionException {

        Chunk dataW = worldData.getChunk(new ChunkKeyData(chunkX - 1, chunkZ, dimension));
        Chunk dataN = worldData.getChunk(new ChunkKeyData(chunkX, chunkZ - 1, dimension));

        boolean west = dataW != null && !dataW.isVoid(),
                north = dataN != null && !dataN.isVoid();

        int x, y, z, color, tX, tY;
        int yW, yN;
        int r, g, b;
        float yNorm, yNorm2, heightShading;

        for (z = 0, tY = pY; z < 16; z++, tY += pL) {
            for (x = 0, tX = pX; x < 16; x++, tX += pW) {


                //smooth step function: 6x^5 - 15x^4 + 10x^3
                y = chunk.getHeightMapValue(x, z);

                if (y < 0) continue;

                yNorm = (float) y / (float) dimension.chunkHeighHighest;
                yNorm2 = yNorm * yNorm;
                yNorm = ((6f * yNorm2) - (15f * yNorm) + 10f) * yNorm2 * yNorm;

                yW = (x == 0) ? (west ? dataW.getHeightMapValue(dimension.chunkWidth - 1, z) : y)//chunk edge
                        : chunk.getHeightMapValue(x - 1, z);//within chunk
                yN = (z == 0) ? (north ? dataN.getHeightMapValue(x, dimension.chunkLength - 1) : y)//chunk edge
                        : chunk.getHeightMapValue(x, z - 1);//within chunk

                heightShading = SatelliteRenderer.getHeightShading(y, yW, yN);


                r = (int) (yNorm * heightShading * 256f);
                g = (int) (70f * heightShading);
                b = (int) (256f * (1f - yNorm) / (yNorm + 1f));

                r = r < 0 ? 0 : Math.min(r, 255);
                g = g < 0 ? 0 : Math.min(g, 255);
                b = b < 0 ? 0 : Math.min(b, 255);


                color = (r << 16) | (g << 8) | b | 0xff000000;

                paint.setColor(color);
                canvas.drawRect(new Rect(tX, tY, tX + pW, tY + pL), paint);


            }
        }
    }

}
