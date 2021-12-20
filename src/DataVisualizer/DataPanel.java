package dataVisualizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import dataCache.DataCache_File;
import utils.Sprintf;
import utils.dbg;

class MyPopupMenu extends java.awt.PopupMenu
{
  MyPopupMenu(String label)
  {
    super(label);
  }
  @Override
  public void show(Component origin, int x, int y)
  {
    this.x = x; this.y = y;
    super.show(origin, x, y);
  }
  int x;
  int y;
}

public class DataPanel extends javax.swing.JPanel implements ActionListener, DataChannelListProvider
{
    DataPanelContainer dpc;
    public DataPanel(DataPanelContainer dpc, DataPanelMain _parent, DataCache_File _file, DataChannelList dcl) {
        this.dpc = dpc;
        parent = _parent;
        dataFile = _file;
        dataChannelList = dcl;
        dataChannelList.addActionListener(this);
        windowIdx = windowIdxMax;
        windowIdxMax++;
        for (int i = 0; i < cursors.length; i++)
            cursors[i] = new Cursor(this);
        for (int i = 0; i < zoomCursors.length; i++)
            zoomCursors[i] = new Cursor(this);
        dataImage = new DataImage(this, dataFile, dcl);
        cursorDefault = getCursor();

        //Register for mouse-wheel events on the map area.
        addMouseWheelListener(new MouseWheelListener() {
          public void mouseWheelMoved(MouseWheelEvent e) {
            mouseWheelMovedHandler(e);
          }
        });
        //Register for mouse-wheel events on the map area.
        addMouseMotionListener(new MouseMotionListener() {
          public void mouseMoved(MouseEvent e) {
            mouseHandler(e);
          }
          public void mouseDragged(MouseEvent e) {
            mouseHandler(e);
          }
        });      //Register for mouse events on the map area.
        addMouseListener(new MouseListener() {
          public void mouseMoved(MouseEvent e) {
            mouseHandler(e);
          }
          public void mouseClicked(MouseEvent e) {
            mouseHandler(e);
          }
          public void mousePressed(MouseEvent e) {
            mouseHandler(e);
          }
          public void mouseReleased(MouseEvent e) {
            mouseHandler(e);
          }
          public void mouseEntered(MouseEvent e) {
            mouseHandler(e);
          }
          public void mouseExited(MouseEvent e) {
            mouseHandler(e);
          }
        });
//        addEventHandler(new EventHandler<ZoomEvent>() {
//          public void zoomIn(ZoomEvent e) {
//              zoomHandler(e);
//          }
//        });
        popup = new MyPopupMenu("demo");
        java.awt.MenuItem item;
        java.awt.event.ActionListener popupMenuListener;
        popupMenuListener = new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent event) {
            dbg.println(9, "Popup menu item ["
                + event.getActionCommand() + "] was pressed.");
            popupMenuHandler(event);
          }
        };
        popup.add(item = new java.awt.MenuItem("Manage signals"));
        item.addActionListener(popupMenuListener);
        popup.add(item = new java.awt.MenuItem("New window"));
        item.addActionListener(popupMenuListener);
        popup.add(item = new java.awt.MenuItem("Close window"));
        item.addActionListener(popupMenuListener);
        popup.add(item = new java.awt.MenuItem("About"));
        item.addActionListener(popupMenuListener);
        add(popup);
        }

    public void mouseWheelMovedHandler(MouseWheelEvent e) {
        dbg.println(19, "DataPanel.mouseWheelMovedHandler="+e.getWheelRotation() + " x=" + e.getX() + " y=" + e.getY());
        int xPosMiddle = e.getX();
        int hPosMiddle = dataImage.getHPos(xPosMiddle);
        int hPosMin = dataImage.dcl.pointIndexMin;
        int hPosMax = dataImage.dcl.pointIndexMax;
        int dH = (hPosMax - hPosMin);
        int hMax = -1;
        try
        {
            hMax = dataFile.getLength();
        } catch (Exception e1)
        {
            dbg.println(1, "DataPanel.mouseWheelMovedHandler e="+e1.toString());
            return; /* do nothing */
        }
        int hPosMinNew;
        int hPosMaxNew;
        if (e.getWheelRotation() < 0)
        { // zoom in
            if (dH < 250)
                return; // over zoomed -> do nothing
            hPosMinNew = hPosMiddle - (dH / 4);
            hPosMaxNew = hPosMiddle + (dH / 4);
            if (hPosMinNew < 0)
            {
                hPosMaxNew = hPosMaxNew + (-hPosMinNew);
                hPosMinNew = 0;
            }
            if (hPosMaxNew >= hMax)
            {
                int diff = hPosMaxNew - hMax + 1;
                hPosMaxNew -= diff;
                hPosMinNew -= diff;
            }
            if (hPosMinNew < 0)
                hPosMinNew = 0;
            if (hPosMaxNew >= hMax)
                hPosMaxNew = hMax - 1;
        }else
        { // zoom out
            hPosMinNew = hPosMiddle - dH;
            hPosMaxNew = hPosMiddle + dH;
            if (hPosMinNew < 0)
            {
                hPosMaxNew = hPosMaxNew + (-hPosMinNew);
                hPosMinNew = 0;
            }
            if (hPosMaxNew >= hMax)
            {
                int diff = hPosMaxNew - hMax + 1;
                hPosMaxNew -= diff;
                hPosMinNew -= diff;
            }
            if (hPosMinNew < 0)
                hPosMinNew = 0;
            if (hPosMaxNew >= hMax)
                hPosMaxNew = hMax - 1;
        }
        dataImage.dcl.pointIndexMin = hPosMinNew;
        dataImage.dcl.pointIndexMax = hPosMaxNew;
        if (parent.cursorsTogether)
        {
            parent.setHorizontalZoom(hPosMinNew, hPosMaxNew);
        }else
            dataImage.repaint();
    }

    //  public void zoomHandler(ZoomEvent e) {
//      
//  }

    public void mouseHandler(MouseEvent e) {
        dbg.println(19, "DataPanel.mouseHandler "+e.toString()+" x=" + e.getX() + " y=" + e.getY() + " button=" + e.getButton());
        final int cursorDistance = 10;
        final int x = e.getX();
        switch (e.getID())
        {
            case MouseEvent.MOUSE_PRESSED:
            {
                dbg.println(19, "DataPanel.mouseHandler "+e.toString()+" x=" + e.getX() + " y=" + e.getY() + " button=" + e.getButton());
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    cursorLast = null;
                    for (int i = 0; i < cursors.length; i++)
                    {
                        if (cursors[i].xPos >= 0)
                        {
                            int dx = x - cursors[i].xPos;
                            if (dx < 0)
                                dx = -dx;
                            int h0 = dataImage.getHPos(x);
                            int h1 = cursors[i].hPos;
                            if ((dx < cursorDistance) || (Math.abs(h1 - h0) < 2))
                                cursorLast = cursors[i];
                        }
                    }
                    if (cursorLast != null)
                    {
                        if (x >= dataImage.hOffset)
                        {
                            cursorLast.xPos = x;
                            cursorLast.hPos = dataImage.getHPos(x);
                            repaint();
                        }
                    }else
                    {
                        zoomCursors[0].xPos = x;
                        zoomCursors[1].xPos = x;
                        cursorLast = zoomCursors[1];
                        // invalidate data cursor
                        cursors[0].hPos = -1;
                        repaint();
                    }
                }else
                if (e.getButton() == MouseEvent.BUTTON3)
                    popup.show(this, e.getX(), e.getY());
            }
            break;
            case MouseEvent.MOUSE_DRAGGED:
            {
                if (cursorLast != null)
                {
                    if (x > dataImage.hOffset)
                    {
                        cursorLast.hPos = dataImage.getHPos(x);
                        cursorLast.xPos = x;
                        if ((cursorLast == cursors[0]) || (cursorLast == cursors[1]))
                        {
                            int cursorIdx;
                            if (cursorLast == cursors[0])
                                cursorIdx = 0;
                            else
                                cursorIdx = 1;
                            parent.setDataCursor(getWindowIdx(), cursorIdx, x, cursorLast.hPos);
                        }
                    }
                }
            }
            break;
            case MouseEvent.MOUSE_RELEASED:
                if (cursorLast == zoomCursors[1])
                {
                    int dx = zoomCursors[1].xPos - zoomCursors[0].xPos;
                    if (dx < 0)
                        dx = -dx;
                    if (dx < cursorDistance)
                    { // set the reading cursor
                        cursors[0].xPos = x;
                        cursors[0].hPos = dataImage.getHPos(x);
                        parent.setDataCursor(getWindowIdx(), 0, x, cursors[0].hPos);
                    }else
                    { // zoom the window
                        int hMax = -1;
                        try
                        {
                            hMax = dataFile.getLength();
                        } catch (Exception e1)
                        {
                            dbg.println(1, "DataPanel.mouseHandler dataFile.getLength e="+e1.toString());
                            cursorLast = null;
                            zoomCursors[0].xPos = -9999;
                            repaint();
                            return; /* do nothing */
                        }
                        if (zoomCursors[1].xPos < zoomCursors[0].xPos)
                        {
                            Cursor tmp = zoomCursors[1];
                            zoomCursors[1] = zoomCursors[0];
                            zoomCursors[0] = tmp;
                        }
                        int hPosMinNew = dataImage.getHPos(zoomCursors[0].xPos);
                        int hPosMaxNew = dataImage.getHPos(zoomCursors[1].xPos);
                        int dH = hPosMaxNew - hPosMinNew;
                        if (dH < 20)
                        {
                            cursorLast = null;
                            zoomCursors[0].xPos = -9999;
                            repaint();
                            return; // over zoomed -> do nothing
                        }
                        if (hPosMinNew < 0)
                            hPosMinNew = 0;
                        if (hPosMaxNew >= hMax)
                            hPosMaxNew = hMax - 1;
                        dataImage.dcl.pointIndexMin = hPosMinNew;
                        dataImage.dcl.pointIndexMax = hPosMaxNew;
                        if (parent.cursorsTogether)
                        {
                            parent.setHorizontalZoom(hPosMinNew, hPosMaxNew);
                        }else
                            dataImage.repaint();
                    }
                }
                cursorLast = null;
                zoomCursors[0].xPos = -9999;
                break;
            case MouseEvent.MOUSE_MOVED:
                if ((cursors[0].hPos >= 0) && (Math.abs(cursors[0].xPos - x) < cursorDistance))
                    this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR));
                else
                    this.setCursor(cursorDefault);
                break;
            default:
                break;
        }
    }

    private int getWindowIdx() {
        return dpc.getWindowIdx();
    }

    void setDataCursor(int cursorIdx, int x)
    {
        cursors[cursorIdx].xPos = x;
        cursors[cursorIdx].hPos = dataImage.getHPos(x);
        repaint();
    }

    public void setHorizontalZoom(int hPosMinNew, int hPosMaxNew)
    {
        dataImage.dcl.pointIndexMin = hPosMinNew;
        dataImage.dcl.pointIndexMax = hPosMaxNew;
        dataImage.repaint();
    }

    void popupMenuHandler(java.awt.event.ActionEvent event)
    {
      dbg.println(9, "popupMenuHandler event="+event.toString());
      java.awt.Point pt = new java.awt.Point(popup.x, popup.y);
      //int colAtPoint = columnAtPoint(pt);
      //int rowAtPoint = rowAtPoint(pt);
      dbg.println(9, "popupMenuHandler windowIdx="+windowIdx+" event.getActionCommand="+event.getActionCommand());
      switch(event.getActionCommand())
      {
        case "Manage signals":
          ChannelSelectorDialog csd = new ChannelSelectorDialog(getMainFrame(), dataFile, dataChannelList);
          //csd.addActionListener(this);
          csd.setVisible(true);
          break;
        case "New window":
            parent.createNewWindow();
            break;
        case "Close window":
            dbg.println(9, "popupMenuHandler.closeWindow windowIdx="+windowIdx);
            parent.closeWindow(this);
            break;
        default:
           dbg.println(1, "popupMenuHandler invalid event="+event.toString());
          break;
      }
    }

    private DataVisualizerUI getMainFrame() {
        return parent.getMainFrame();
    }

    /* Callback for ChannelSelectorDialog - ok handler */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        dataImage.repaint();
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        final int windowHeight = getHeight();

        boolean repaintNeeded = false;
        dbg.println(9, "DataPanel - paintComponent windowIdx="+windowIdx+" windowIdxMax="+windowIdxMax);
        Graphics2D g2 = (Graphics2D)g;
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
          dbg.dprintf(21, "dataPanel - paintComponent(%d, %d)\n", 0, 0);
          g.drawImage(dataImage.getImage(), 0, 0, null);

          // cursor drawing
          if (cursors[0].hPos >= 0)
          {
              g.setColor(Color.BLUE);
              final int x = (cursors[0].hPos - dataChannelList.getDataPointIndexMin()) * dataImage.diagramWidth / (dataChannelList.getDataPointIndexMax() - dataChannelList.getDataPointIndexMin()) + dataImage.hOffset;
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
                      g.setColor(dcli.color);
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
                      g.clearRect(xVal, yVal - fontHgt, textWidth, fontHgt);
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
            g.drawString("dataPanel ctr=" + ctr, 5, 10);
        if (dbg.get(9))
            g.drawString("DataPanel - paintComponent windowIdx="+windowIdx+" windowIdxMax="+windowIdxMax, 5, 30);
    }
    int ctr = 0;

    public DataChannelList getDataChannelList()
    {
        return dataChannelList;
    }

    DataPanelMain parent;
    DataImage dataImage;
    boolean repaintNeeded;
    DataCache_File dataFile;
    public DataChannelList dataChannelList;
    MyPopupMenu popup;
    int windowIdx;
    static int windowIdxMax = 0;
    Cursor[] cursors = new Cursor[2];
    Cursor[] zoomCursors = new Cursor[2];
    Cursor cursorLast = null;
    private java.awt.Cursor cursorDefault;

    private static final long serialVersionUID = 1062958030431493625L;
}
