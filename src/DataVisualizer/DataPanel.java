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

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_ChannelBasePointBased;
import dataCache.DataCache_FileBase;
import dataVisualizer.interfaces.DataChannelListChangeEventHandler;
import lippiWare.utils.Sprintf;
import lippiWare.utils.dbg;

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
  private static final long serialVersionUID = 1493714264465501020L;
}

public class DataPanel extends javax.swing.JPanel implements ActionListener, DataChannelListProvider, DataChannelListChangeEventHandler
{
    DataPanelContainer dpc;
    public DataPanel(DataPanelContainer dpc, DataPanelMain _parent, DataCache_FileBase _file, DataChannelList dcl) {
        this.dpc = dpc;
        parent = _parent;
        dataFile = _file;
        dataChannelList = dcl;
        dataChannelList.addActionListener(this);
        dataChannelList.addDataChannelListChangeEventHandler(this);
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
        double hPosMiddle = dataImage.getHPos(xPosMiddle);
        double hPosMin = dataImage.dcl.pointIndexMin;
        double hPosMax = dataImage.dcl.pointIndexMax;
        double dH = (hPosMax - hPosMin);
        int hMax = -1;
        try
        {
            hMax = dataFile.getLength();
        } catch (Exception e1)
        {
            dbg.println(1, "DataPanel.mouseWheelMovedHandler e="+e1.toString());
            return; /* do nothing */
        }
        double hPosMinNew;
        double hPosMaxNew;
        if (e.getWheelRotation() < 0)
        { // zoom in
            //if (dH < 250)
            //    return; // over zoomed -> do nothing
            hPosMinNew = hPosMiddle - (dH / 4);
            hPosMaxNew = hPosMiddle + (dH / 4);
            if (hPosMinNew < 0)
            {
                hPosMaxNew = hPosMaxNew + (-hPosMinNew);
                hPosMinNew = 0;
            }
            if (hPosMaxNew >= hMax)
            {
                double diff = hPosMaxNew - hMax + 1;
                hPosMaxNew -= diff;
                hPosMinNew -= diff;
            }
            if (hPosMinNew < 0)
                hPosMinNew = 0;
            if (hPosMaxNew >= hMax)
                hPosMaxNew = hMax - 1;
        }else
        { // zoom out
            final double zoomOutLimit = DataVisualizerPrefs.getZoomOutLimit();
            hPosMinNew = hPosMiddle - dH;
            hPosMaxNew = hPosMiddle + dH;
            if (hPosMinNew < -zoomOutLimit)
            {
                hPosMinNew = -zoomOutLimit;
                hPosMaxNew = hPosMinNew + 2 * dH;
            }
            if (hPosMaxNew >= (hMax + zoomOutLimit))
            {
                hPosMaxNew = (hMax + zoomOutLimit);
                hPosMinNew = hPosMaxNew - 2 * dH;
            }
            hPosMinNew = Math.max(hPosMinNew, -zoomOutLimit);
            hPosMaxNew = Math.min(hPosMaxNew, hMax + zoomOutLimit);
        }
        dbg.println(9, " DataPanel.mouseWheelMovedHandler hPosMinNew="+hPosMinNew + " hPosMaxNew=" + hPosMaxNew);
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
                            double h0 = dataImage.getHPos(x);
                            double h1 = cursors[i].hPos;
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
                        double hPosMinNew = dataImage.getHPos(zoomCursors[0].xPos);
                        double hPosMaxNew = dataImage.getHPos(zoomCursors[1].xPos);
                        double dH = hPosMaxNew - hPosMinNew;
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

    public void setHorizontalZoom(double hPosMinNew, double hPosMaxNew)
    {
        dataImage.dcl.pointIndexMin = hPosMinNew;
        dataImage.dcl.pointIndexMax = hPosMaxNew;
        dataImage.repaint();
    }

    void popupMenuHandler(java.awt.event.ActionEvent event)
    {
      dbg.println(9, "popupMenuHandler event="+event.toString());
      //java.awt.Point pt = new java.awt.Point(popup.x, popup.y);
      //int colAtPoint = columnAtPoint(pt);
      //int rowAtPoint = rowAtPoint(pt);
      dbg.println(9, "popupMenuHandler windowIdx="+windowIdx+" event.getActionCommand="+event.getActionCommand());
      switch(event.getActionCommand())
      {
        case "Manage signals":
          ChannelSelectorDialog csd = new ChannelSelectorDialog(getMainFrame(), dataFile, dataChannelList, parent.dvlf);
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

    Color getContrastColor(Color color, Color bgColor) {
        int[] colorDiff = {
            Math.abs(color.getRed() - bgColor.getRed()),
            Math.abs(color.getGreen() - bgColor.getGreen()),
            Math.abs(color.getBlue() - bgColor.getBlue()),
        };
        int colorDiffMax = Math_max(colorDiff);
        if (colorDiffMax > 100)
            return bgColor;
        if (bgColor.getRed() < 100)
            return new Color(255, bgColor.getGreen(), bgColor.getBlue());
        if (bgColor.getRed() > 155)
            return new Color(  0, bgColor.getGreen(), bgColor.getBlue());
        if (bgColor.getGreen() < 100)
            return new Color(bgColor.getRed(), 255, bgColor.getBlue());
        if (bgColor.getGreen() > 155)
            return new Color(bgColor.getRed(),   0, bgColor.getBlue());
        if (bgColor.getBlue() < 100)
            return new Color(bgColor.getRed(), bgColor.getGreen(), 255);
        if (bgColor.getBlue() > 155)
            return new Color(bgColor.getRed(), bgColor.getGreen(),   0);
        throw new Error("getContrastColor - not yet implemented case!");
    }

    private int Math_max(int[] values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++)
            max = Math.max(max, values[i]);
        return max;
    }
    int ctr = 0;

    public DataChannelList getDataChannelList()
    {
        return dataChannelList;
    }

    @Override
    public void dataChannelListChangeEventHandler(DataChannelList dcl)
    {
        dataImage.repaint();
        repaint();
    }

    DataPanelMain parent;
    DataImage dataImage;
    boolean repaintNeeded;
    DataCache_FileBase dataFile;
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
