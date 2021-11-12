/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataVisualizer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import dataCache.DataCache_File;
import utils.FileNameExtension;
import utils.dbg;

/**
 *
 * @author liptakok
 */
public class ChannelSelectorDialog extends JDialog {
  /**
     * 
     */
    private static final long serialVersionUID = 3001210750112112397L;

JComboBox<String> cb;

  DataCache_File file;
  DataChannelList colArray;
    ChannelListEditorTable myTable; 

  ChannelSelectorDialog(JFrame parent, DataCache_File _file, DataChannelList _colArray)
  {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    file = _file;
    colArray = _colArray;
    this.setTitle("Select signals to be displayed");
    
    myTable = new ChannelListEditorTable(this, file, colArray);

    JLabel l2 = new JLabel("Select signals to be displayed");

    JButton bOk = new JButton("OK");
    bOk.setHorizontalAlignment(SwingConstants.LEFT);
    bOk.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            okHandler();
        }
    });

    JButton bCancel = new JButton("Cancel");
    bCancel.setHorizontalAlignment(SwingConstants.RIGHT);
    bCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    });

    JPanel jpanel = new JPanel();
    JScrollPane scrollableTable = new JScrollPane(myTable);
    //JScrollPane scrollableTable = myTable;

    JPanel jpProperties = new JPanel(new GridLayout(3, 2));
    jpProperties.setMinimumSize(new Dimension(200, 60));
    jpProperties.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black));
    jpProperties.add(new JLabel("Signal name:"));
    lSignalName = new JLabel("not_selected");
    jpProperties.add(lSignalName);
    jpProperties.add(new JLabel("Signal color:"));
    jpSignalColor = new JPanel();
    jpProperties.add(jpSignalColor);
    jpProperties.add(new JLabel("Signal group:"));
    jcSignalGroup = new JComboBox<String>();
    jcSignalGroup.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
            String groupName = (String)jcSignalGroup.getSelectedItem();
            updateSignalGroupName(groupName);
        }
    });
    jpProperties.add(jcSignalGroup);

    JLabel l3 = new JLabel("Signal of horizontal axle:");

    String s1[] = new String[file.getChannelNumber()];
    String horizontalAxleChName = colArray.horizontalAxle.getName();
    int toBeSelected = -1;
    for(int i = 0; i < file.getChannelNumber(); i++)
    {
        String chName = file.getChannel(i).getName();
        s1[i] = chName;
        if (chName.contentEquals(horizontalAxleChName))
            toBeSelected  = i;
    }
    cb = new JComboBox<String>(s1);
    cb.setSelectedIndex(toBeSelected);

    JPanel bOkCancel = new JPanel();
    bOkCancel.add(bOk);
    bOkCancel.add(bCancel);

    Container cp = getContentPane();
    SpringLayout layout = new SpringLayout();
    cp.setLayout(layout);
    cp.add(l2);
    cp.add(scrollableTable);
    cp.add(jpProperties);
    cp.add(l3);
    cp.add(cb);
    cp.add(bOkCancel);

    layout.putConstraint(SpringLayout.WEST,  l2, 5, SpringLayout.WEST,  cp);
    layout.putConstraint(SpringLayout.NORTH, l2, 5, SpringLayout.NORTH, cp);

    layout.putConstraint(SpringLayout.WEST,  scrollableTable, 5, SpringLayout.WEST,  cp);
    layout.putConstraint(SpringLayout.EAST,  scrollableTable, -5, SpringLayout.EAST,  cp);
    layout.putConstraint(SpringLayout.NORTH, scrollableTable, 5, SpringLayout.SOUTH, l2);
    layout.putConstraint(SpringLayout.SOUTH, scrollableTable, -5, SpringLayout.NORTH, jpProperties);

    layout.putConstraint(SpringLayout.WEST,  jpProperties,  5, SpringLayout.WEST,  cp);
    layout.putConstraint(SpringLayout.EAST,  jpProperties, -5, SpringLayout.EAST,  cp);
    layout.putConstraint(SpringLayout.SOUTH, jpProperties, -5, SpringLayout.NORTH, l3);

    layout.putConstraint(SpringLayout.WEST,  l3,  5, SpringLayout.WEST,  cp);
    layout.putConstraint(SpringLayout.NORTH, l3,  0, SpringLayout.NORTH, cb);

    layout.putConstraint(SpringLayout.WEST,  cb,  5, SpringLayout.EAST,  l3);
    layout.putConstraint(SpringLayout.SOUTH, cb, -5, SpringLayout.NORTH, bOkCancel);

    layout.putConstraint(SpringLayout.HORIZONTAL_CENTER,  bOkCancel,  0, SpringLayout.HORIZONTAL_CENTER,  cp);
    layout.putConstraint(SpringLayout.SOUTH, bOkCancel, -5, SpringLayout.SOUTH, cp);

    pack();

    Point pt = parent.getLocationOnScreen();
    int pw = parent.getWidth();
    setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
    this.setMinimumSize(new Dimension(350, 400));
    updateButtons();
    addEscapeListener();
  }
  final void updateButtons()
  {
  }

  void okHandler()
  {
    dbg.println(9, "ColumnSelectorDialog.okHandler");
    setVisible(false);
    if (false)
    {
        javax.swing.JTable lbSelected = new javax.swing.JTable();
        DefaultListModel mSelected = (DefaultListModel)lbSelected.getModel();
        colArray.clear();
        for (int i = 0; i < mSelected.size(); i++)
        {
          String colName = mSelected.getElementAt(i).toString();
          colArray.addSignal(colName);
          dbg.println(11, "  colArray["+i+"]=" + colName + "!");
        }
        colArray.setHorizontalAxle((String)cb.getSelectedItem());
        colArray.updateCallbacksExecute();
        //callBackParent.columnSelectorDialogOkHandler(colArray);
        DataVisualizerLayoutFileLoader.saveLayoutFile(FileNameExtension.set(file.getName(), "dvl"), colArray);
    }
  }

    void addEscapeListener() {
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        getRootPane().registerKeyboardAction(escListener,
              KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
              JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    void setSignalProperties(String name, Color color, String groupName, String[] groupNames)
    {
        lSignalName.setText(name);
        jpSignalColor.setBackground(color);
        jcSignalGroup.removeAllItems();
        for (int i = 0; i < groupNames.length; i++)
        {
            jcSignalGroup.addItem(groupNames[i]);
        }
        jcSignalGroup.setSelectedItem(groupName);
    }

    private void updateSignalGroupName(String groupName) {
        myTable.setSignalGroupName(groupName);
    }

    JLabel lSignalName;
    JPanel jpSignalColor;
    JComboBox<String> jcSignalGroup;
}
