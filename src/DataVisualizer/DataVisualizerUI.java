package dataVisualizer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import lippiWare.utils.dbg;

class DataFile
{
    DataFile(String _filename)
    {
        
    }

    boolean isValid()
    {
        return true;
    }
}

public class DataVisualizerUI extends javax.swing.JFrame
{
    public DataVisualizerUI()
    {
        setTitle("DataVisualizer - no file is loaded");
        initComponents();

        int nextRecentFile = 0;
        for (int i = 0; i < 10; i++)
        {
          String val = DataVisualizerPrefs.getRecentFile(i, "");
          if (!val.isEmpty())
          {
            javax.swing.JMenuItem jMenuItem = new javax.swing.JMenuItem();
            jMenuItem.setText(nextRecentFile + ": " + val);
            nextRecentFile++;
            jMenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_RecentFileActionPerformed(evt);
              }
            });
            jMenuRecentFiles.add(jMenuItem);
          }
        }

        addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e) {
                dbg.println(9, "DataVisualizerUI.KeyListener e="+e.toString());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                dbg.println(9, "DataVisualizerUI.KeyListener e="+e.toString());
            }

            @Override
            public void keyTyped(KeyEvent e) {
                dbg.println(9, "DataVisualizerUI.KeyListener e="+e.toString());
            }
        });

        setLocation(DataVisualizerPrefs.get("MainWindowX", 0), DataVisualizerPrefs.get("MainWindowY", 0));
        setSize(DataVisualizerPrefs.get("MainWindowW", 600), DataVisualizerPrefs.get("MainWindowH", 400));
        setExtendedState(DataVisualizerPrefs.get("MainWindowState", NORMAL));
    }

    private void m_RecentFileActionPerformed(java.awt.event.ActionEvent evt) {
        dbg.println(9, "m_RecentFileActionPerformed " + evt.toString());
        String val = evt.getActionCommand();
        dbg.dprintf(9, "  val=%s\n", val);
        if (val.charAt(1) == ':')
        {
          int idx = val.charAt(0) - '0';
          String file = val.substring(3);
          dbg.dprintf(9, "  idx=%d file=%s\n", idx, file);
          openDataFile(file);
        }
    }

    DataFile dataFile;
    private void openDataFile(String fileName)
    {
        dataPanelMain.saveDataLayoutFile();
        dataFile = null;
        m_DataSourceConfig.setEnabled(false);
        dataPanelMain.reinit();
        setTitle("DataVisualizer - loading file " + fileName);
        DataFile dataFileTemp = new DataFile(fileName);
        if (!dataFileTemp.isValid())
        { /* error message */
            javax.swing.JOptionPane.showMessageDialog(this, "Unable to load file " + fileName);
        }else
        { /* file is loaded -> add it to the list */
            dataFile = dataFileTemp;
            // init cursors
            // init graph drawing - repaintMap();
            int i;
            for (i = 0; i < 10; i++)
            { /* check the existence of the file on the recent list */
                if (fileName.equals(DataVisualizerPrefs.getRecentFile(i, "")))
                { // the file is already on the recent list -> move it to the first position
                    break;
                }
            }
            if (i != 0)
            {
                for (; i > 0; i--)
                { /* move recent file lower */
                    DataVisualizerPrefs.putRecentFile(i, DataVisualizerPrefs.getRecentFile(i - 1, ""));
                }
                DataVisualizerPrefs.putRecentFile(0, fileName);
            }
            try {
                dataPanelMain.loadFile(fileName);
            } catch (Exception e)
            {
                dbg.println(1, "DataVisualizerUI.openDataFile load file exception e=" + e.toString());
                javax.swing.JOptionPane.showMessageDialog(this, "Unable to load file " + fileName + "! Detailed info: " + e.toString());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataPanelMain = new DataPanelMain(this);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JMenuBar jMenuBarMainMenu = new javax.swing.JMenuBar();

        JMenu jMenuFile = new javax.swing.JMenu("File");

        JMenuItem m_FileOpen = new javax.swing.JMenuItem("File open");
        m_FileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        m_FileOpen.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            m_FileOpenActionPerformed(evt);
          }
        });
        jMenuFile.add(m_FileOpen);

        m_DataSourceConfig = new javax.swing.JMenuItem("Configure data source");
        m_DataSourceConfig.setEnabled(false);
        m_DataSourceConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_DataSourceConfigActionPerformed(evt);
            }
          });
        jMenuFile.add(m_DataSourceConfig);

        jMenuFile.add(new javax.swing.JPopupMenu.Separator());

        jMenuRecentFiles = new javax.swing.JMenu();
        jMenuRecentFiles.setText("Recent Files");
        jMenuRecentFiles.setToolTipText("");
        jMenuRecentFiles.setActionCommand("recentFiles");
        jMenuFile.add(jMenuRecentFiles);

        JMenuItem m_FileExit = new javax.swing.JMenuItem("Exit");
        m_FileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        m_FileExit.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            m_FileExitActionPerformed(evt);
          }
        });
        jMenuFile.add(m_FileExit);

        JMenu jMenuView = new javax.swing.JMenu("View");
        m_ViewChannel = new javax.swing.JMenuItem("Channels");
        m_ViewChannel.setEnabled(false);
        m_ViewChannel.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            m_ViewChannelActionPerformed(evt);
          }
        });
        jMenuView.add(m_ViewChannel);
        m_ViewCursorModeTogether = new JCheckBoxMenuItem("Move data cursors together");
        m_ViewCursorModeTogether.setState(true);
        m_ViewCursorModeTogether.setEnabled(false);
        m_ViewCursorModeTogether.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
              dataPanelMain.setCursorsTogether(m_ViewCursorModeTogether.getState());
          }
        });
        jMenuView.add(m_ViewCursorModeTogether);
        JMenuItem m_ViewPreferences = new javax.swing.JMenuItem("Preferences");
        m_ViewPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_PreferncesActionPerformed(evt);
            }
          });
        jMenuView.add(m_ViewPreferences);

        JMenu jMenuHelp = new javax.swing.JMenu("Help");
        JMenuItem m_HelpAbout = new javax.swing.JMenuItem("About");
        jMenuHelp.add(m_HelpAbout);

        jMenuBarMainMenu.add(jMenuFile);
        jMenuBarMainMenu.add(jMenuView);
        jMenuBarMainMenu.add(new javax.swing.JMenu("Window"));
        jMenuBarMainMenu.add(jMenuHelp);

        setJMenuBar(jMenuBarMainMenu);

        add(dataPanelMain);
    }

    protected void m_DataSourceConfigActionPerformed(ActionEvent evt) {
        dbg.println(9, "m_DataSourceConfigActionPerformed");
        JDialog dlg = dataPanelMain.file.getDataSourceConfigDlg(dataPanelMain);
        dlg.setVisible(true);
    }

    private void m_ViewChannelActionPerformed(java.awt.event.ActionEvent evt)
    {
        dbg.println(9, "m_ViewChannelActionPerformed");
        DataPanelContainer dataPanel = dataPanelMain.dataPanels.get(0);
        ChannelSelectorDialog csd = new ChannelSelectorDialog(this, dataPanelMain.file, dataPanel.getDataChannelList(), dataPanelMain.dvlf);
        csd.setVisible(true);
    }

    private void m_FileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_FileOpenActionPerformed
        dbg.println(9, "m_FileOpenActionPerformed");
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "Diadem DAT file", "dat"));

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "Vector ASC", "asc"));

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "Vector BLF", "blf"));

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
          java.io.File file = fc.getSelectedFile();
          //This is where a real application would open the file.
          dbg.println(9, "Opening: " + file.getName() + ".");
          openDataFile(file.getPath());
        } else
        {
          dbg.println(9, "Open command cancelled by user.");
        }
      }//GEN-LAST:event_m_FileOpenActionPerformed

    private void m_FileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_FileExitActionPerformed
        dbg.println(1, "m_FileExitActionPerformed");
        dispose();
        System.exit(0);
      }//GEN-LAST:event_m_FileExitActionPerformed

    public void windowClose(java.awt.event.WindowEvent e)
    {
      dbg.println(9, "windowClose");
      DataVisualizerPrefs.put("MainWindowX", getX());
      DataVisualizerPrefs.put("MainWindowY", getY());
      DataVisualizerPrefs.put("MainWindowH", getHeight());
      DataVisualizerPrefs.put("MainWindowW", getWidth());
      DataVisualizerPrefs.put("MainWindowState", getExtendedState());
      dataPanelMain.saveDataLayoutFile();
      System.exit(0);
    }

    private void m_PreferncesActionPerformed(ActionEvent evt) {
        OptionsDialog od = new OptionsDialog(this);
        od.setVisible(true);
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        dataPanelMain.setBackgroundColor(backgroundColor);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenuRecentFiles;
    JMenuItem m_DataSourceConfig;
    private DataPanelMain dataPanelMain;
    javax.swing.JMenuItem m_ViewChannel;
    JCheckBoxMenuItem m_ViewCursorModeTogether;

    private static final long serialVersionUID = 4985856149217047613L;
}
