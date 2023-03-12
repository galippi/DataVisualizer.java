/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataVisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import lippiWare.utils.dbg;

/**
 *
 * @author liptakok
 */
public class OptionsDialog extends JDialog {

  //headers for the table
  final String[] columnNames = new String[] {
      "Property name", "Property value"
  };
  final int rowDebugLevel = 0;
  final int colDebugLevel = 1;
  final int rowBackgroundColor = 1;
  final int colBackgroundColor = 1;
  final int rowDataCursorMaxChannel = 2;

  DataVisualizerUI parent;

  OptionsDialog(DataVisualizerUI _parent)
  {
    super(_parent, Dialog.ModalityType.APPLICATION_MODAL);
    parent = _parent;
    this.setTitle("Options");

    //create table with data
    table = new JTable(new DefaultTableModel(3, 2) {
        @Override
        public boolean isCellEditable(int row, int column)
        {
            return (column == 1);
        }
        private static final long serialVersionUID = 2641690847759012960L;
    });
    javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnNames.length; i++)
    {
        TableColumn column = columnModel.getColumn(i);
        column.setMinWidth(10);
        column.setMaxWidth(200);
        column.setWidth(10);
        column.setResizable(true);
        column.setHeaderValue(columnNames[i]);
    }
    table.setValueAt("Debug level:", 0, 0);
    table.setValueAt("" + dbg.get(), 0, 1);
    table.setValueAt("Background color", 1, 0);
    table.setValueAt(DataVisualizerPrefs.getBackgroundColor(new Color(0, 0, 0)), 1, 1);
    table.setValueAt("Maximum number of channels", rowDataCursorMaxChannel, 0);
    table.setValueAt("" + DataVisualizerPrefs.getDataCursorMaxChannel(), rowDataCursorMaxChannel, 1);

    table.addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            mouseHandlerTable(e);
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            mouseHandlerTable(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        { // not used - do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e)
        { // not used - do nothing
        }

        @Override
        public void mouseExited(MouseEvent e)
        { // not used - do nothing
        }
    });
    table.getModel().addTableModelListener(
            new javax.swing.event.TableModelListener()
            {
                public void tableChanged(javax.swing.event.TableModelEvent evt) 
                {
                  tableChangedHandler(evt);
                }
    });

    //add the table to the frame
    this.add(new JScrollPane(table));

    JButton bOk = new JButton("Ok");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bOk.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            okHandler();
        }
    });
    JButton bCancel = new JButton("Cancel");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cancelHandler();
        }
    });
    JPanel bOkCancel = new JPanel();
    bOkCancel.add(bOk);
    bOkCancel.add(bCancel);
    Container cp = getContentPane();
    // add label, text field and button one after another into a single column
    cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    cp.add(bOkCancel, BorderLayout.SOUTH);

    //Point pt = parent.getLocationOnScreen();
    //int pw = parent.getWidth();
    //setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
    setLocation(DataVisualizerPrefs.get("OptionsDialogX", 0), DataVisualizerPrefs.get("OptionsDialogY", 0));
    setSize(DataVisualizerPrefs.get("OptionsDialogW", 350), DataVisualizerPrefs.get("OptionsDialogH", 300));
    this.setMinimumSize(new Dimension(350, 300));
    addEscapeListener();
  }

  private void mouseHandlerTable(MouseEvent evt)
  {
      dbg.println(9, "OptionsDialog - mouseHandlerTable evt=" + evt.toString());
      dbg.println(9, "  findComponentAt="+findComponentAt(evt.getX(), evt.getY()).toString());
      if (evt.getID() == MouseEvent.MOUSE_CLICKED)
      {
          int rowAtPoint = table.rowAtPoint(evt.getPoint());
          int colAtPoint = table.columnAtPoint(evt.getPoint());
          dbg.dprintf(9, "  rowAtPoint=%d colAtPoint=%d\n", rowAtPoint, colAtPoint);
          if (rowAtPoint >= 0) {
              //setRowSelectionInterval(rowAtPoint, rowAtPoint);
              if ((evt.getButton() == MouseEvent.BUTTON1) || (evt.getButton() == MouseEvent.BUTTON3))
              {
                  if ((rowAtPoint == rowBackgroundColor) && (colAtPoint == colBackgroundColor))
                  {
                      java.awt.Color newColor = 
                              javax.swing.JColorChooser.showDialog(
                                this,
                                  "Choose background color",
                                  (Color)table.getValueAt(rowBackgroundColor, colBackgroundColor));
                      if (newColor != null) {
                          table.setValueAt(newColor, rowBackgroundColor, colBackgroundColor);
                      }
                  }
              }
          }
      }

  }

  private void tableChangedHandler(TableModelEvent evt)
  {
      dbg.println(9, "tableChangedHandler evt=" + evt);
      dbg.println(19, "  UPDATE=" + TableModelEvent.UPDATE);
  }

  void okHandler()
  {
    boolean closable = true;
    int level = -1;
    Color backgroundColor = null;
    int dataCursorMaxChannel = 0;
    try {
      level = Integer.parseInt((String) table.getValueAt(rowDebugLevel, colDebugLevel));
      backgroundColor = (Color)table.getValueAt(rowBackgroundColor, colBackgroundColor);
      dataCursorMaxChannel = Integer.parseInt((String) table.getValueAt(rowDataCursorMaxChannel, 1));
    }catch (NumberFormatException e)
    {
      dbg.println(2, "OptionDialog.okHandler.NumberFormatException="+e.toString());
      closable = false;
    }

    if (closable)
    {
        DataVisualizerPrefs.put("Debug level", level);
        dbg.set(level);

        DataVisualizerPrefs.putBackgroundColor(backgroundColor);
        parent.setBackgroundColor(backgroundColor);

        DataVisualizerPrefs.putDataCursorMaxChannel(dataCursorMaxChannel);

        DataVisualizerPrefs.put("OptionsDialogX", getX());
        DataVisualizerPrefs.put("OptionsDialogY", getY());
        DataVisualizerPrefs.put("OptionsDialogH", getHeight());
        DataVisualizerPrefs.put("OptionsDialogW", getWidth());

        setVisible(false);
    }
  }

  void cancelHandler()
  {
    setVisible(false);
    //dispose();
  }

  void addEscapeListener() {
      ActionListener escListener = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              cancelHandler();
          }
      };
      getRootPane().registerKeyboardAction(escListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  JTable table;
  private static final long serialVersionUID = -4777973355120139808L;
}
