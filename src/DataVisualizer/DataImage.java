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
        int w = img.getWidth();
        int h = img.getHeight();
        while (!(file.isReady() && (dcl != null) && dcl.isReady()))
        {
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                dbg.dprintf(9, "DataImage.Drawing wait exception e=%s!\n", e.toString());
            }
        }
        {
            final int hScaleHeight = 20;
            if (dbg.get(11))
            {
                g.setColor(Color.red);
                g.fillOval(img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2 - 5, img.getHeight() / 2 - 5);
                g.setColor(Color.BLACK);
                g.drawString("DataImage - Drawing!", 40, 40);
            }
            final int hMin = dcl.getDataPointIndexMin();
            final int hMax = dcl.getDataPointIndexMax();
            final int hNum = hMax - hMin;
            if (dbg.get(11))
            {
                g.setColor(Color.BLACK);
                g.drawString("hMin="+hMin, 0, img.getHeight() - 10);
                g.drawString("hMax="+hMax, img.getWidth() - 60, img.getHeight() - 10);
            }
            final int hStep = (hMax - hMin) / 10;
            DataCache_ChannelBase chHor = dcl.getHorizontalAxle();
            g.setColor(Color.BLACK);
            g.drawString(chHor.getName(), 0, img.getHeight() - 20);
            for(int hIdx = hMin + hStep; hIdx < hMax; hIdx += hStep)
            {
                g.drawString("hIdx="+hIdx, w * (hIdx-hMin) / hNum, h - (hScaleHeight / 2));
                try {
                    g.drawString(""+chHor.getDouble(hIdx), w * (hIdx-hMin) / hNum, h - hScaleHeight);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            for(int i = 0; i < dcl.size(); i++)
            {
                DataChannelListItem dcli = dcl.get(i);
                DataChannelGroup dcg = dcli.group;
                Color color = dcli.color;
                g.setColor(color);
                final double hScale = -(h - hScaleHeight) / dcl.groupCnt;
                final int vOffset = h - hScaleHeight;
                int x0 = 0;
                int y0 = (int)(((dcli.getDouble(hMin) - dcg.offset) * dcg.factor) * hScale + 0.5);
                for(int hIdx = hMin + 1; hIdx < hMax; hIdx++)
                {
                    double val = dcli.getDouble(hIdx);
                    int x = (hIdx - hMin) * w / hNum;
                    int y = (int)(((val - dcg.offset) * dcg.factor) * hScale + 0.5) + vOffset;
                    g.drawOval(x, y, 2, 2);
                    g.drawLine(x0, y0, x, y);
                    x0 = x;
                    y0 = y;
                }
            }
        }
        g.dispose();
    }
    DataCache_File file;
    DataChannelList dcl;
}
