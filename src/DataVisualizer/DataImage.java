package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_ChannelBasePointBased;
import dataCache.DataCache_FileBase;
import dataCache.DataPointBase;
import lippiWare.utils.Sprintf;
import lippiWare.utils.dbg;
import lippiWare.utils.threadImage;

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

    public double getHPos(int xPos)
    {
        double hNum = dcl.pointIndexMax - dcl.pointIndexMin;
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
            DataCache_ChannelBase chHor = dcl.getHorizontalAxle();
            g.setColor(Color.BLACK);
            g.drawString(chHor.getName(), imgWidth / 2 - 16, imgHeight);
            int xLast = -9999;
            double tMin, tMax;
            int hMin;
            int hMax;
            try {
                final int tIdxMax = dcl.file.getLength() - 1;
                double tIdxLow = dcl.getDataPointIndexMin();
                double tIdxHigh = dcl.getDataPointIndexMax();
                int tIdxLowInt = Math.max((int)(tIdxLow + 0.5), 0);
                tMin = chHor.getDouble(tIdxLowInt);
                int tIdxHighInt = Math.min((int)(tIdxHigh + 0.5), tIdxMax);
                tMax = chHor.getDouble(tIdxHighInt);
                double dt = tMax - tMin;
                int dIdxHighInt = tIdxHighInt - tIdxLowInt;
                double factor = dt / dIdxHighInt;
                tMin = tMin - (tIdxLowInt - tIdxLow) * factor;
                tMax = tMax + (tIdxHigh - tIdxHighInt) * factor;
                hMin = Math.max(tIdxLowInt, 0);
                hMax = Math.min(tIdxHighInt, tIdxMax);
            } catch (Exception e1) {
                e1.printStackTrace();
                dbg.println(1, "DataImage.Draw execption tMin/tMax");
                return;
            }
            final int hNum = hMax - hMin;
            final int hStep = (hNum < 10) ? 1 : ((hMax - hMin) / 10);
            double dt = tMax - tMin;
            if (dbg.get(9))
            {
                g.setColor(Color.BLACK);
                g.drawString("hMin="+hMin, 0, diagHeight);
                g.drawString("hMax="+hMax, imgWidth - 60, diagHeight);
                for(int hIdx = hMin + hStep; hIdx < hMax; hIdx += hStep)
                    g.drawString("hIdx="+hIdx, imgWidth * (hIdx-hMin) / hNum, diagHeight);
            }
            {
                int y = imgHeight - hScaleHeight / 2;
                for(int hIdx = hMin; hIdx <= hMax; hIdx += hStep)
                {
                    try {
                        double t = chHor.getDouble(hIdx);
                        double delta = t - tMin;
                        int x = (int)((imgWidth * delta) / dt);
                        if (x > (imgWidth - 40))
                            x = (imgWidth - 40);
                        dbg.println(9, "t=" + t + " x=" + x);
                        if ((x - xLast) > 60) {
                            g.drawString(""+ t, x, y);
                            xLast = x;
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
            for(int i = 0; i < dcl.size(); i++)
            {
                DataChannelListItem dcli = dcl.get(i);
                DataChannelGroup dcg = dcl.getGroup(dcli.group);
                Color color = dcli.color;
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
                if (dcli.isTimeBasedChannel())
                {
                    int x0 = hOffset;
                    int y0 = getY(dcli, dcg, hMin);
                    for(int hIdx = hMin + 1; hIdx < hMax; hIdx++)
                    {
                        int x = (hIdx - hMin) * diagramWidth / hNum + hOffset;
                        int y = getY(dcli, dcg, hIdx);
                        g.drawOval(x - 1, y - 1, 3, 3);
                        g.drawLine(x0, y0, x, y);
                        x0 = x;
                        y0 = y;
                    }
                }else
                {
                    try {
                        dbg.println(19, "DataImage point based tMin=" + tMin + " tMax=" + tMax);
                        DataCache_ChannelBasePointBased dclip = (DataCache_ChannelBasePointBased)dcli.ch;
                        final int maxIdx = dclip.size() - 1;
                        int idxMin = dclip.getPointIdx(tMin);
                        int idxMax = dclip.getPointIdx(tMax);
                        dbg.println(19, "DataImage point based idxMin=" + idxMin + " idxMax=" + idxMax);
                        if ((idxMin < maxIdx) && (idxMax >= 0)) {
                            idxMin = Math.max(idxMin - 1, 0);
                            idxMax = Math.min(idxMax + 1, maxIdx);
                            DataPointBase pt = dclip.getPoint(idxMin);
                            int x0 = (int)((pt.t - tMin) * diagramWidth / dt) + hOffset;
                            int y0 = getY(dcg, pt.getDouble());
                            g.drawOval(x0 - 1, y0 - 1, 3, 3);
                            idxMin++;
                            while(idxMin <= idxMax) {
                                pt = dclip.getPoint(idxMin);
                                int x = (int)((pt.t - tMin) * diagramWidth / dt) + hOffset;
                                int y = getY(dcg, pt.getDouble());
                                g.drawOval(x - 1, y - 1, 3, 3);
                                g.drawLine(x0, y0, x, y);
                                x0 = x;
                                y0 = y;
                                idxMin++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dbg.println(3, "DataImage point based drawing e=" + e.toString());
                    }
                    //throw new Error("Not yet implemented!");
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
