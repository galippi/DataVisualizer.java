package DataVisualizer;

import utils.dbg;

public class DataVisualizerUI extends javax.swing.JFrame
{
    public DataVisualizerUI()
    {
        
    }

    public void windowClose(java.awt.event.WindowEvent e)
    {
      dbg.println(9, "windowClose");
      //IgcViewerPrefs.put("MainWindowX", getX());
      System.exit(0);
    }

}
