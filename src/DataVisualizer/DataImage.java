package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_File;
import utils.dbg;
import utils.threadImage;

public class DataImage extends threadImage
{
    public DataImage(java.awt.Component parent, DataCache_File _file, DataChannelList _dcl)
    {
      super(parent);
      file = file;
      dcl = _dcl;
    }

    @Override
    protected void Drawing()
    { /* drawing function */
        java.awt.Graphics2D g = img.createGraphics();
        while (!(file.isReady() && dcl.isReady()))
        {
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                dbg.dprintf(9, "DataImage.Drawing wait exception e=%s!\n", e.toString());
            }
        }
        { // the map could not be loaded
            g.setColor(Color.red);
            g.fillOval(img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2 - 5, img.getHeight() / 2 - 5);
            g.setColor(Color.BLACK);
            g.drawString("MapImage - error loading map!", 40, 40);
        }
        g.dispose();
    }
    DataCache_File file;
    DataChannelList dcl;
}
