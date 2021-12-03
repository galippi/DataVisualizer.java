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
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dataCache.DataCache_ChannelBase;
import dataCache.DataCache_File;
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
    DataChannelList colArrayLocal; // this will store the visibility data before ok key
    ChannelListEditorTable myTable;
    private JTextField signalNameFilterText;
    private JCheckBox signalVisibilityFilter;
    private JLabel lGroupName;
    private JTextField tGroupFactor;
    private JTextField tGroupOffset; 
    boolean signalDataIsUpdated = false;

  ChannelSelectorDialog(JFrame parent, DataCache_File _file, DataChannelList _colArray)
  {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    file = _file;
    colArray = _colArray;
    colArrayLocal = colArray.copy();
    this.setTitle("Select signals to be displayed");
    
    myTable = new ChannelListEditorTable(this, file, colArray);

    JLabel l2 = new JLabel("Filter:");
    signalNameFilterText = new JTextField();
    signalNameFilterText.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e)
        {
            dbg.println(11, "signalNameFilterText.insertUpdate=" + signalNameFilterText.getText());
            signalVisibilityFilterIsChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e)
        {
            dbg.println(11, "signalNameFilterText.removeUpdate=" + signalNameFilterText.getText());
            signalVisibilityFilterIsChanged();
        }

        @Override
        public void changedUpdate(DocumentEvent e)
        {
            dbg.println(11, "signalNameFilterText.changedUpdate=" + signalNameFilterText.getText());
            signalVisibilityFilterIsChanged();
        }
    });
    signalVisibilityFilter = new JCheckBox("show only visible signals");
    signalVisibilityFilter.addItemListener(new ItemListener() {    
        @Override
        public void itemStateChanged(ItemEvent e)
        {
            signalVisibilityFilterIsChanged();
        }
     });  
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
    //jpProperties.setMinimumSize(new Dimension(200, 60));
    jpProperties.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black));
    jpProperties.add(new JLabel("Signal name:"));
    lSignalName = new JLabel("not_selected");
    jpProperties.add(lSignalName);
    jpProperties.add(new JLabel("Signal color:"));
    jpSignalColor = new JPanel();
    jpSignalColor.addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            myTable.askSignalColor();
        }

        @Override
        public void mouseEntered(MouseEvent e)
        { // not used - do nothing
        }

        @Override
        public void mouseExited(MouseEvent e)
        { // not used - do nothing
        }

        @Override
        public void mousePressed(MouseEvent e)
        { // not used - do nothing
        }

        @Override
        public void mouseReleased(MouseEvent e)
        { // not used - do nothing
        }
    });

    jpProperties.add(jpSignalColor);
    jpProperties.add(new JLabel("Signal group:"));
    jcSignalGroup = new JComboBox<String>();
    jcSignalGroup.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
            String groupName = (String)jcSignalGroup.getSelectedItem();
            updateSignalGroupName(groupName);
            signalDataIsUpdated = true;
        }
    });
    jpProperties.add(jcSignalGroup);

    JPanel jpGroupProperties = new JPanel(new GridLayout(3, 2));
    //jpGroupProperties.setMinimumSize(new Dimension(200, 60));
    jpGroupProperties.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black));
    jpGroupProperties.add(new JLabel("Group name:"));
    lGroupName = new JLabel("not_selected");
    jpGroupProperties.add(lGroupName);
    jpGroupProperties.add(new JLabel("Group factor:"));
    tGroupFactor = new JTextField("<default>");
    jpGroupProperties.add(tGroupFactor);
    jpGroupProperties.add(new JLabel("Group offset:"));
    tGroupOffset = new JTextField("<default>");
    jpGroupProperties.add(tGroupOffset);

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
    cp.add(signalNameFilterText);
    cp.add(signalVisibilityFilter);
    cp.add(scrollableTable);
    cp.add(jpProperties);
    cp.add(jpGroupProperties);
    cp.add(l3);
    cp.add(cb);
    cp.add(bOkCancel);

    layout.putConstraint(SpringLayout.NORTH, l2, 4, SpringLayout.NORTH, signalNameFilterText);
    layout.putConstraint(SpringLayout.WEST,  l2, 5, SpringLayout.WEST,  cp);

    layout.putConstraint(SpringLayout.NORTH, signalNameFilterText,   5, SpringLayout.NORTH, cp);
    layout.putConstraint(SpringLayout.WEST,  signalNameFilterText,   5, SpringLayout.EAST,  l2);
    layout.putConstraint(SpringLayout.EAST,  signalNameFilterText, 100, SpringLayout.WEST,  signalNameFilterText);

    layout.putConstraint(SpringLayout.NORTH, signalVisibilityFilter, 4, SpringLayout.NORTH, signalNameFilterText);
    layout.putConstraint(SpringLayout.WEST,  signalVisibilityFilter, 5, SpringLayout.EAST,  signalNameFilterText);

    layout.putConstraint(SpringLayout.WEST,  scrollableTable, 5, SpringLayout.WEST,  cp);
    layout.putConstraint(SpringLayout.EAST,  scrollableTable, -5, SpringLayout.EAST,  cp);
    layout.putConstraint(SpringLayout.NORTH, scrollableTable, 5, SpringLayout.SOUTH, signalNameFilterText);
    layout.putConstraint(SpringLayout.SOUTH, scrollableTable, -5, SpringLayout.NORTH, jpProperties);

    layout.putConstraint(SpringLayout.WEST,  jpProperties,  5, SpringLayout.WEST,  cp);
    layout.putConstraint(SpringLayout.EAST,  jpProperties, -5, SpringLayout.EAST,  cp);
    layout.putConstraint(SpringLayout.SOUTH, jpProperties, -5, SpringLayout.NORTH, jpGroupProperties);

    layout.putConstraint(SpringLayout.WEST,  jpGroupProperties,  5, SpringLayout.WEST,  cp);
    layout.putConstraint(SpringLayout.EAST,  jpGroupProperties, -5, SpringLayout.EAST,  cp);
    layout.putConstraint(SpringLayout.SOUTH, jpGroupProperties, -5, SpringLayout.NORTH, l3);

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
    setLocation(DataVisualizerPrefs.get("SignalSelectorDialogX", 0), DataVisualizerPrefs.get("SignalSelectorDialogY", 0));
    setSize(DataVisualizerPrefs.get("SignalSelectorDialogW", 350), DataVisualizerPrefs.get("SignalSelectorDialogH", 300));
    this.setMinimumSize(new Dimension(350, 400));

    addEscapeListener();

    fillRowData();
    updateButtons();
    updateProperties();
  }

  final void updateButtons()
  {
  }

    private void signalVisibilityFilterIsChanged()
    {
        dbg.println(11, "ChannelSelectorDialog.signalVisibilityFilterIsChanged");
        myTable.clearSelection();
        if (signalDataIsUpdated)
        {
            updateLocalColArray();
            signalDataIsUpdated = false;
        }
        fillRowData();
//        if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED)
//        { // hide all not visible signals
//            DefaultTableModel model = (DefaultTableModel)myTable.getModel();
//            //for (int i = 0; i < myTable.getRowCount(); i++)
//            for (int i = myTable.getRowCount() - 1; i >= 0; i--)
//            {
//                if (!myTable.isSignalVisible(i))
//                {
//                    //myTable.setRowHeight(0);
//                    model.removeRow(i);
//                }
//            }
//        }else
//        { // show all signals
//            if (signalDataIsUpdated)
//            {
//                updateLocalColArray();
//                signalDataIsUpdated = false;
//            }
//            fillRowData();
//        }
    }

    private void updateLocalColArray()
    {
        updateColArray(colArrayLocal);
    }

    public void fillRowData()
    {
        String filterStr = signalNameFilterText.getText();
        if (filterStr.isEmpty())
            filterStr = null;
        boolean filterActive = signalVisibilityFilter.isSelected();
        dbg.println(11, "fillRowData filterActive="+filterActive + " filterStr="+filterStr);
        DefaultTableModel model = (DefaultTableModel)myTable.getModel();
        DataChannelList colArrayFiltered;
        if (filterStr == null)
            if (filterActive)
                colArrayFiltered = colArrayLocal;
            else
                colArrayFiltered = null;
        else
        {
            if (filterActive)
                colArrayFiltered = filterSignals(colArrayLocal, filterStr);
            else
                colArrayFiltered = filterSignals(null, filterStr);
        }
        int rowCount;
        if (colArrayFiltered == null)
            rowCount = file.getChannelNumber();
        else
            rowCount = colArrayFiltered.size();
        model.setRowCount(rowCount);
        int rowIdx = 0;
        for (int i = 0; i < file.getChannelNumber(); i++)
        {
            String chName = file.getChannel(i).getName();
            if ((colArrayFiltered == null) || (colArrayFiltered.get(chName) != null))
            {
                DataChannelListItem dcli = colArrayLocal.get(chName);
                Color color;
                DataChannelGroup dcg;
                if (dcli != null)
                {
                    color = dcli.color;
                    dcg = dcli.group;
                }else
                {
                    color = Color.WHITE;
                    dcg = myTable.hidden;
                }
                myTable.setValueAt(chName, rowIdx, myTable.colSignalName);
                myTable.setValueAt(color, rowIdx, myTable.colSignalColor);
                myTable.setValueAt(dcg.name, rowIdx, myTable.colGroupName);
                rowIdx++;
            }
        }
    }

    private DataChannelList filterSignals(DataChannelList colArrayParam, String filterStr)
    {
        DataChannelList result = new DataChannelList(file);
        if (colArrayParam == null)
        {
            for (int i = 0; i < file.getChannelNumber(); i++)
            {
                DataCache_ChannelBase ch = file.getChannel(i);
                if (filterSignalName(ch.getName(), filterStr))
                {
                    DataChannelListItem dcli = colArrayLocal.get(ch.getName());
                    Color color;
                    String groupName;
                    if (dcli == null)
                    {
                        color = Color.WHITE;
                        groupName = "not visible";
                    }else
                    {
                        color = dcli.color;
                        groupName = dcli.group.name;
                    }
                    result.addSignal(ch.getName(), color, groupName);
                }
            }
        }else
        {
            for (int i = 0; i < colArrayParam.size(); i++)
            {
                DataChannelListItem dcli = colArrayParam.get(i);
                if (filterSignalName(dcli.getSignalName(), filterStr))
                {
                    result.addSignal(dcli.getSignalName(), dcli.color, dcli.group.name);
                }
            }
        }
        return result;
    }

    private boolean filterSignalName(String name, String filterStr)
    {
        return name.contains(filterStr);
    }

    void okHandler()
    {
        dbg.println(9, "ChannelSelectorDialog.okHandler");
        setVisible(false);

        DataVisualizerPrefs.put("SignalSelectorDialogX", getX());
        DataVisualizerPrefs.put("SignalSelectorDialogY", getY());
        DataVisualizerPrefs.put("SignalSelectorDialogH", getHeight());
        DataVisualizerPrefs.put("SignalSelectorDialogW", getWidth());

        updateColArray(colArray);
        colArray.updateGroupData();
        colArray.updateCallbacksExecute();
        //DataVisualizerLayoutFileLoader.saveLayoutFile(FileNameExtension.set(file.getName(), "dvl"), colArray);
    }

    private void updateColArray(DataChannelList colArray)
    {
        //colArray.clear();
        for (int i = 0; i < myTable.getRowCount(); i++)
        {
            String chName = myTable.getSignalName(i);
            DataChannelListItem dcli = colArray.get(chName);
            if (myTable.isSignalVisible(i))
            {
                Color color = myTable.getColor(i);
                String groupName = myTable.getGroupName(i);
                dbg.println(11, "  colArray["+i+"]=" + chName + "!");
                if (dcli != null)
                    dcli.update(color, groupName);
                else
                    colArray.addSignal(chName, color, groupName);
            }else
                if (dcli != null)
                    colArray.remove(dcli);
        }
        colArray.setHorizontalAxle((String)cb.getSelectedItem());
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

    void updateProperties()
    {
        int selIdx = myTable.getSelectedRow();
        dbg.println(9, "ChannelSelectorDialog.updateProperties selIdx="+selIdx);
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
