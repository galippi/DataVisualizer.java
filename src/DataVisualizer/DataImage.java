package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_File;
import utils.threadImage;

public class DataImage extends threadImage
{
    public DataImage(java.awt.Component parent, DataCache_File _file)
    {
      super(parent);
      file = file;
    }

    @Override
    protected void Drawing()
    { /* drawing function */
        java.awt.Graphics2D g = img.createGraphics();
        { // the map could not be loaded
            g.setColor(Color.red);
            g.fillOval(img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2 - 5, img.getHeight() / 2 - 5);
            g.setColor(Color.BLACK);
            g.drawString("MapImage - error loading map!", 40, 40);
        }
        g.dispose();
    }
    DataCache_File file;
}
