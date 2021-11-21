package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_File;
import utils.dbg;
import utils.threadImage;

public class DataImage extends threadImage
{
    public DataImage(java.awt.Component parent, DataCache_File _file, DataChannelList _dcl)
    {
      super(parent);
      file = _file;
      dcl = _dcl;
    }

    public void setDcl(DataChannelList _dcl)
    {
        dcl = _dcl;
        repaint();
    }

    @Override
    protected void Drawing()
    { /* drawing function */
        java.awt.Graphics2D g = img.createGraphics();
        while (!(file.isReady() && (dcl != null) && dcl.isReady()))
        {
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                dbg.dprintf(9, "DataImage.Drawing wait exception e=%s!\n", e.toString());
            }
        }
        {
            g.setColor(Color.red);
            g.fillOval(img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2 - 5, img.getHeight() / 2 - 5);
            g.setColor(Color.BLACK);
            g.drawString("DataImage - Drawing!", 40, 40);
            int hMin = dcl.getDataPointIndexMin();
            int hMax = dcl.getDataPointIndexMax();
            g.drawString("hMin="+hMin, 0, img.getHeight() - 10);
            g.drawString("hMax="+hMax, img.getWidth() - 60, img.getHeight() - 10);
            int hStep = (hMax - hMin) / 10;
            DataCache_ChannelBase chHor = dcl.getHorizontalAxle();
            g.drawString(chHor.getName(), 0, img.getHeight() - 20);
            for(int h = hMin + hStep; h < hMax; h+=hStep)
            {
                g.drawString("h="+h, img.getWidth() * (h-hMin) / (hMax - hMin), img.getHeight() - 10);
                try {
                    g.drawString(""+chHor.getDouble(h), img.getWidth() * (h-hMin) / (hMax - hMin), img.getHeight() - 20);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            for(int i = 0; i < dcl.size(); i++)
            {
                for(int h = hMin + hStep; h < hMax; h+=hStep)
                {
                    
                }
            }
        }
        g.dispose();
    }
    DataCache_File file;
    DataChannelList dcl;
}
