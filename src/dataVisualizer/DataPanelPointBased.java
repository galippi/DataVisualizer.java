package dataVisualizer;

import java.awt.Color;

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_ChannelBasePointBased;
import dataCache.DataCache_Channel_CAN_Base;
import dataCache.DataCache_FileBase;
import dataCache.DataPointBase;
import dataCache.DataPointDouble;
import lippiWare.utils.Sprintf;
import lippiWare.utils.dbg;

// point based stored data file, e.g. CAN log file
public class DataPanelPointBased extends DataPanel {
    public DataPanelPointBased(DataPanelContainer dpc, DataPanelMain _parent, DataCache_FileBase _file,
            DataChannelList dcl) {
        super(dpc, _parent, _file, dcl);
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        //final int windowHeight = getHeight();

        boolean repaintNeeded = false;
        dbg.println(9, "DataPanelPointBased - paintComponent windowIdx="+windowIdx+" windowIdxMax="+windowIdxMax);
        //Graphics2D g2 = (Graphics2D)g;
        //g.setColor(Color.BLUE);
        //g.fillRect(0, 0, getWidth(), getHeight());
        ctr++;
        final int wi = getWidth();
        final int he = getHeight();
        if (dataImage.setImage(wi, he))
        {
            repaintNeeded = true;
        }
        if (repaintNeeded)
            dataImage.repaint();
        if (dbg.get(19))
        {
            String state = dataFile.getStateString();
            g.setColor(Color.BLACK);
            g.drawString(state, 5, he / 2);
        }
        if (dataImage.isReady())
        {
          dbg.dprintf(21, "DataPanelPointBased - paintComponent(%d, %d)\n", 0, 0);
          g.drawImage(dataImage.getImage(), 0, 0, null);

          // cursor drawing
          if (cursors[0].hPos >= 0)
          {
              g.setColor(Color.BLUE);
              HPosData hPosData = DataImageUtil.calcBorders(dataChannelList);
              double dt = hPosData.tMax - hPosData.tMin;
              //int ptMin = dataChannelList.getDataPointIndexMin();
              //int ptMax = dataChannelList.getDataPointIndexMax();
              double t = hPosData.tMin + (cursors[0].xPos * dt) / wi;
              DataCache_ChannelBase chHor = dataChannelList.getHorizontalAxle();
              int ptIdx = chHor.getPointIdx(t);
              double tPt;
              try {
                  tPt = chHor.getDouble(ptIdx);
              } catch (Exception e) {
                  e.printStackTrace();
                  dbg.println(1, "DataPanelPointBased.paintComponent chHor.getDouble(" + ptIdx + ") exception=" + e.toString());
                  return;
              }
              int x = (int)(((tPt - hPosData.tMin) * wi) / dt);
              g.drawLine(x, 0, x, getHeight());
              cursors[0].xPos = x;
              dt = dt * 50 / (wi);
              if (dataChannelList.size() <= DataVisualizerPrefs.getDataCursorMaxChannel())
              { // displaying signal value
                  for (int i = 0; i < dataChannelList.size(); i++)
                  {
                      DataChannelListItem dcli = dataChannelList.get(i);
                      try {
                          double val;
                          try {
                              //double val = dcli.ch.getDoubleGlobal((int)(cursors[0].hPos + 0.5));
                              // Todo: find the closest point, throw an exception, if they would be too far
                              //val = dcli.ch.getDoubleGlobal(ptIdx, tPt, dt);
                              val = dcli.ch.getDoubleGlobal(ptIdx);
                          }catch (Exception e) {
                              DataPointBase result = null;
                              int idxLess = dcli.ch.getIdxLess(ptIdx);
                              if (idxLess >= 0) {
                                  DataPointBase ptLess = dcli.ch.getPointGlobal(idxLess);
                                  if (Math.abs(ptLess.t - tPt) < dt)
                                      result = ptLess;
                              }
                              int idxGreater = dcli.ch.getIdxGreater(ptIdx);
                              if (idxGreater >= 0) {
                                  DataPointBase ptGreater = dcli.ch.getPointGlobal(idxGreater);
                                  if (Math.abs(ptGreater.t - tPt) < dt) {
                                      if (result == null)
                                          result = ptGreater;
                                      else
                                          if (Math.abs(ptGreater.t - tPt) < Math.abs(result.t - tPt))
                                                  result = ptGreater;
                                  }
                              }
                              if (result == null)
                                  throw new Exception("No close point is found");
                              else
                                  val = result.getDouble();
                          }
                          String unit = dcli.ch.getUnit();
                          if (!unit.isEmpty())
                              unit = " " + unit;
                          String valStr = " ";
                          if ((Math.abs(val - (int)val) < 1e-12) && (Math.abs(val) < 1000000))
                              valStr = valStr + (int)val;
                          else
                              valStr = valStr + Sprintf.sprintf("%4.2f", val);
                          dpc.setLegendValue(i, valStr);
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
                      }catch(Exception e) {
                          dbg.println(9, "DataPanelPointBased paint cursor exception e=" + e.toString());
                          dpc.setLegendValue(i, "");
                      }
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
            g.drawString("DataPanelPointBased ctr=" + ctr, 5, 10);
        if (dbg.get(9))
            g.drawString("DataPanelPointBased - paintComponent windowIdx="+windowIdx+" windowIdxMax="+windowIdxMax, 5, 30);
    }

    private static final long serialVersionUID = -3816921731264125797L;
}
