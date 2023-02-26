package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_FileBase;
import utils.Sprintf;
import utils.dbg;
import utils.threadImage;

public class DataImage extends threadImage
{
    DataPanel parent;
    public DataImage(DataPanel _parent, DataCache_FileBase _file, DataChannelList _dcl)
    {
      super(_parent);
      parent = _parent;
      file = _file;
      dcl = _dcl;
    }

    public void setDcl(DataChannelList _dcl)
    {
        dcl = _dcl;
        repaint();
    }

    public int getHPos(int xPos)
    {
        int hNum = dcl.pointIndexMax - dcl.pointIndexMin;
        return ((xPos - hOffset) * hNum / diagramWidth) + dcl.pointIndexMin;
    }

    public int getY(DataChannelGroup dcg, double val) {
        return diagHeight - (int)(((val - dcg.offset) * dcg.factor) * diagHeight + 0.5);
    }

    public int getY(DataChannelListItem dcli, double val) {
        DataChannelGroup dcg = dcl.getGroup(dcli.group);
        return getY(dcg, val);
    }

    public int getY(DataChannelListItem dcli, DataChannelGroup dcg, int hPos) {
        return getY(dcg, dcli.getDouble(hPos));
    }

    public int getY(DataChannelListItem dcli, int hPos) {
        DataChannelGroup dcg = dcl.getGroup(dcli.group);
        return  getY(dcli, dcg, hPos);
    }

    @Override
    protected void Drawing()
    { /* drawing function */
        java.awt.Graphics2D g = img.createGraphics();
        final int imgWidth = img.getWidth();
        final int imgHeight = img.getHeight();
        g.setColor(parent.parent.bgColor);
        g.fillRect(0, 0, imgWidth, imgHeight);
        while (!(file.isReady() && (dcl != null) && dcl.isReady()))
        {
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                dbg.dprintf(9, "DataImage.Drawing wait exception e=%s!\n", e.toString());
            }
        }
        {
            diagHeight = imgHeight - hScaleHeight;
            hOffset = 0;
            diagramWidth = imgWidth - hOffset;
            if (dbg.get(19))
            {
                g.setColor(Color.red);
                g.fillOval(imgWidth / 2, imgHeight / 2, imgWidth / 2 - 5, imgHeight / 2 - 5);
                g.setColor(Color.BLACK);
                g.drawString("DataImage - Drawing!", 40, 40);
            }
            final int hMin = dcl.getDataPointIndexMin();
            final int hMax = dcl.getDataPointIndexMax();
            final int hNum = hMax - hMin;
            final int hStep = (hMax - hMin) / 10;
            if (dbg.get(11))
            {
                g.setColor(Color.BLACK);
                g.drawString("hMin="+hMin, 0, diagHeight);
                g.drawString("hMax="+hMax, imgWidth - 60, diagHeight);
                for(int hIdx = hMin + hStep; hIdx < hMax; hIdx += hStep)
                    g.drawString("hIdx="+hIdx, imgWidth * (hIdx-hMin) / hNum, diagHeight);
            }
            DataCache_ChannelBase chHor = dcl.getHorizontalAxle();
            g.setColor(Color.BLACK);
            g.drawString(chHor.getName(), imgWidth / 2 - 16, imgHeight);
            for(int hIdx = hMin + hStep; hIdx < hMax; hIdx += hStep)
            {
                try {
                    g.drawString(""+chHor.getDouble(hIdx), imgWidth * (hIdx-hMin) / hNum, imgHeight - hScaleHeight / 2);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            for(int i = 0; i < dcl.size(); i++)
            {
                DataChannelListItem dcli = dcl.get(i);
                DataChannelGroup dcg = dcl.getGroup(dcli.group);
                Color color = dcli.color;
                int x0 = hOffset;
                int y0 = getY(dcli, dcg, hMin);
                if (dbg.get(11))
                {
                    g.setColor(Color.BLACK);
                    String dbgStr = Sprintf.sprintf("%s: fac=%8f offs=%8f", dcli.getSignalName(), dcg.factor, dcg.offset);
                    try
                    {
                        dbgStr += " min="+dcli.ch.getDoubleMin()+" max="+dcli.ch.getDoubleMax();
                    } catch (Exception e)
                    {
                        dbgStr += " min/max exception";
                    }
                    g.drawString(dbgStr, 0, i * 12 + 50);
                }
                g.setColor(color);
                for(int hIdx = hMin + 1; hIdx < hMax; hIdx++)
                {
                    int x = (hIdx - hMin) * diagramWidth / hNum + hOffset;
                    int y = getY(dcli, dcg, hIdx);
                    g.drawOval(x - 1, y - 1, 3, 3);
                    g.drawLine(x0, y0, x, y);
                    x0 = x;
                    y0 = y;
                }
            }
        }
        g.dispose();
    }
    DataCache_FileBase file;
    DataChannelList dcl;
    int hOffset = 0;
    int diagramWidth = 1;
    final int hScaleHeight = 16;
    int diagHeight = 0;
}
