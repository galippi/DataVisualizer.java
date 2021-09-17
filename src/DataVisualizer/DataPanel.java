package dataVisualizer;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import dataCache.DataCache_File;
import utils.dbg;

//class EventHandler<T>
//{
//  
//}
//
//class ZoomEvent
//{
//  
//}

public class DataPanel extends javax.swing.JPanel
{
    public DataPanel(DataPanelMain _parent, DataCache_File _file)
    {
        parent = _parent;
        dataFile = _file;
        dataImage = new DataImage(this, dataFile);

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
        dbg.println(19, "mouseHandler "+e.toString()+" x=" + e.getX() + " y=" + e.getY() + " button=" + e.getButton());
        if (e.getID() == MouseEvent.MOUSE_PRESSED)
        {
            if ((!m_capture) && (e.getButton() == MouseEvent.BUTTON1))
            {
                m_capture = true;
                //CaptureMouse();
                x_start = e.getX();
                y_start = e.getY();
            }
        }else
        if ((e.getID() == MouseEvent.MOUSE_MOVED) || (e.getID() == MouseEvent.MOUSE_DRAGGED))
        {
            if (m_capture)
            {
                int dx, dy;
                dx = e.getX() - x_start;
                dy = e.getY() - y_start;
                if ((dx != 0) || (dy != 0))
                {
                    //x_pos += dx;
                    //y_pos += dy;
                    x_start = e.getX();
                    y_start = e.getY();
                    //AutoPos = false;
                    repaint();
                }
                dbg.dprintf(9, "DataPanel::OnMouse(EVT_MOTION x_pos=%d y_pos=%d)\n", 0, 0);
            }
        }else
        if (e.getID() == MouseEvent.MOUSE_RELEASED)
        {
              if (m_capture)
              {
                //ReleaseMouse();
                m_capture = false;
                repaint();
              }
        }else
        { // not used event
        }
    }
    boolean m_capture = false;
    int x_start, y_start;

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        boolean repaintNeeded = false;
        dbg.println(9, "DataPanel - paintComponent");
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());
        ctr++;
        if (dataImage.setImage(getWidth(), getHeight()))
        {
            repaintNeeded = true;
        }
        if (repaintNeeded)
            dataImage.repaint();
        String state = dataFile.getStateString();
        g.setColor(Color.BLACK);
        g.drawString(state, 5, getHeight() / 2);
        if (dataImage.isReady())
        {
          dbg.dprintf(21, "dataPanel - paintComponent(%d, %d)\n", 0, 0);
          g.drawImage(dataImage.getImage(), 0, 0, null);
        }
        // cursor drawing
        g.setColor(Color.BLACK);
        g.drawString("dataPanel ctr=" + ctr, 5, 10);
    }
    int ctr = 0;

    DataPanelMain parent;
    DataImage dataImage;
    boolean repaintNeeded;
    DataCache_File dataFile;

    /**
     * 
     */
    private static final long serialVersionUID = 1062958030431493625L;
}
