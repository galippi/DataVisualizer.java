package dataVisualizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import dataCache.DataCache_File;
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

//class EventHandler<T>
//{
//  
//}
//
//class ZoomEvent
//{
//  
//}

public class DataPanel extends javax.swing.JPanel implements ActionListener, DataChannelListProvider
{
    public DataPanel(DataPanelMain _parent, DataCache_File _file, DataChannelList dcl) {
        parent = _parent;
        dataFile = _file;
        dataChannelList = dcl;
        dataChannelList.addActionListener(this);
        windowIdx = windowIdxMax;
        windowIdxMax++;
        dataImage = new DataImage(this, dataFile, dcl);

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
        dbg.println(9, "mouseWheelMovedHandler="+e.getWheelRotation() + " x=" + e.getX() + " y=" + e.getY());
        /*
         * double zoom_new = (e.getWheelRotation() < 0) ? (gu.zoom * zoom_factor) :
         * (gu.zoom / zoom_factor); if (((zoom_new > 40) || (e.getWheelRotation() < 0))
         * && ((zoom_new < 13000) || (e.getWheelRotation() > 0))) { // limit the zooming
         * gu.Zoom(e.getX(), e.getY(), zoom_new); repaint(); }
         */
        repaint();
    }

    //  public void zoomHandler(ZoomEvent e) {
//      
//  }

    public void mouseHandler(MouseEvent e) {
        dbg.println(19, "DataPanel.mouseHandler "+e.toString()+" x=" + e.getX() + " y=" + e.getY() + " button=" + e.getButton());
        if (e.getID() == MouseEvent.MOUSE_PRESSED)
        {
            dbg.println(9, "DataPanel.mouseHandler "+e.toString()+" x=" + e.getX() + " y=" + e.getY() + " button=" + e.getButton());
            if (e.getButton() == MouseEvent.BUTTON3)
                popup.show(this, e.getX(), e.getY());
        }else
        if ((e.getID() == MouseEvent.MOUSE_MOVED) || (e.getID() == MouseEvent.MOUSE_DRAGGED))
        {
                //dx = e.getX() - x_start;
                //dy = e.getY() - y_start;
        }else
        if (e.getID() == MouseEvent.MOUSE_RELEASED)
        {
        }else
        { // not used event
        }
    }
    boolean m_capture = false;
    int x_start, y_start;

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

        boolean repaintNeeded = false;
        dbg.println(9, "DataPanel - paintComponent windowIdx="+windowIdx+" windowIdxMax="+windowIdxMax);
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());
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
        }
        // cursor drawing
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

    /**
     * 
     */
    private static final long serialVersionUID = 1062958030431493625L;
}
