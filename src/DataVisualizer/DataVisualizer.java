package dataVisualizer;

import java.awt.Component;

import lippiWare.utils.dbg;

public class DataVisualizer
{
    public static void main(String[] args)
    {
        dbg.set(DataVisualizerPrefs.get("Debug level", 1));

        setLookAndFeel(null);

          /* Create and display the form */
          java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                  frame = new DataVisualizerUI();
                  frame.setVisible(true);
              // ------------------------------------------------------------
              // Window listener to close application when Window gets closed
              // ------------------------------------------------------------
              frame.addWindowListener(new java.awt.event.WindowAdapter() {
                  public void windowClosing(java.awt.event.WindowEvent e) {
                      dbg.println(9, "windowClosing");
                      frame.windowClose(e);
                  }
              });
              }
          });
    }

    static void setLookAndFeel(String uiManagerName) {
        final String uiManagerKey = "UIManager";
        if (uiManagerName != null) {
            DataVisualizerPrefs.put(uiManagerKey, uiManagerName);
        }else {
            uiManagerName = DataVisualizerPrefs.get(uiManagerKey, "Nimbus");
        }
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        boolean found = false;
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (uiManagerName.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    found = true;
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataVisualizer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            dbg.println(2, "DataVisualizer.setLookAndFeel exception name=" + uiManagerName + " ex=" + ex.toString());
        }
        if (!found)
            dbg.println(3, "DataVisualizer.setLookAndFeel unable to set UIManager name=" + uiManagerName);
    }

    public static Component getMainFrame() {
        return frame;
    }

    static DataVisualizerUI frame;
}
