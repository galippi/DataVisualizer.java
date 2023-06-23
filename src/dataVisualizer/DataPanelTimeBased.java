package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_FileBase;
import lippiWare.utils.Sprintf;
import lippiWare.utils.dbg;

public class DataPanelTimeBased extends DataPanel {
    public DataPanelTimeBased(DataPanelContainer dpc, DataPanelMain _parent, DataCache_FileBase _file,
            DataChannelList dcl) {
        super(dpc, _parent, _file, dcl);
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        //final int windowHeight = getHeight();

        boolean repaintNeeded = false;
        dbg.println(9, "DataPanelTimeBased - paintComponent windowIdx="+windowIdx+" windowIdxMax="+windowIdxMax);
        //Graphics2D g2 = (Graphics2D)g;
        //g.setColor(Color.BLUE);
        //g.fillRect(0, 0, getWidth(), getHeight());
        ctr++;
        if (dataImage.setImage(getWidth(), getHeight()))
        {
            repaintNeeded = true;
        }
        if (repaintNeeded)
            dataImage.repaint();
        if (dbg.get(19))
        {
            String state = dataFile.getStateString();
            g.setColor(Color.BLACK);
            g.drawString(state, 5, getHeight() / 2);
        }
        if (dataImage.isReady())
        {
          dbg.dprintf(21, "DataPanelTimeBased - paintComponent(%d, %d)\n", 0, 0);
          g.drawImage(dataImage.getImage(), 0, 0, null);

          // cursor drawing
          if (cursors[0].hPos >= 0)
          {
              g.setColor(Color.BLUE);
              int x = (cursors[0].hPos - dataChannelList.getDataPointIndexMin()) * dataImage.diagramWidth / (dataChannelList.getDataPointIndexMax() - dataChannelList.getDataPointIndexMin()) + dataImage.hOffset;
              g.drawLine(x, 0, x, getHeight());
              cursors[0].xPos = x;
              if (dataChannelList.size() <= DataVisualizerPrefs.getDataCursorMaxChannel())
              { // displaying signal value
                  for (int i = 0; i < dataChannelList.size(); i++)
                  {
                      DataChannelListItem dcli = dataChannelList.get(i);
                      double val = dcli.getDouble(cursors[0].hPos);
                      String unit = dcli.ch.getUnit();
                      if (!unit.isEmpty())
                          unit = " " + unit;
                      String valStr = " ";
                      if ((Math.abs(val - (int)val) < 1e-12) && (Math.abs(val) < 1000000))
                          valStr = valStr + (int)val;
                      else
                          valStr = valStr + Sprintf.sprintf("%4.2f", val);
                      valStr = valStr + unit + " ";
                      java.awt.FontMetrics metrics = g.getFontMetrics();
                      int fontHgt = metrics.getHeight();
                      int textWidth = metrics.stringWidth(valStr);
                      int xVal = x - (textWidth / 2);
                      if (xVal < 0)
                          xVal = 0;
                      else if (xVal > (getWidth() - textWidth))
                          xVal = (getWidth() - textWidth);
                      int yVal = dataImage.getY(dcli, val) + (fontHgt / 2);
                      if (yVal > (getHeight() - 10))
                          yVal = getHeight() - 10;
                      //g.clearRect(xVal, yVal - fontHgt, textWidth, fontHgt);
                      Color contrastColor = getContrastColor(dcli.color, parent.bgColor);
                      g.setColor(contrastColor);
                      g.fillRect(xVal, yVal - fontHgt, textWidth, fontHgt);
                      g.setColor(dcli.color);
                      g.drawRect(xVal, yVal - fontHgt, textWidth + 1, fontHgt);
                      g.drawString(valStr, xVal + 1, yVal - 2);
                  }
              }
          }else
          if (zoomCursors[0].xPos >= 0)
          { // draw zoom cursors
              g.setColor(Color.darkGray);
              g.drawLine(zoomCursors[0].xPos, 0, zoomCursors[0].xPos, getHeight());
              g.drawLine(zoomCursors[1].xPos, 0, zoomCursors[1].xPos, getHeight());
          }
        }
        if (dbg.get(19))
        {
            g.setColor(Color.ORANGE);
            g.drawRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(Color.BLACK);
        if (dbg.get(19))
            g.drawString("DataPanelTimeBased ctr=" + ctr, 5, 10);
        if (dbg.get(9))
            g.drawString("DataPanelTimeBased - paintComponent windowIdx="+windowIdx+" windowIdxMax="+windowIdxMax, 5, 30);
    }

    private static final long serialVersionUID = -6706612346661366767L;
}
