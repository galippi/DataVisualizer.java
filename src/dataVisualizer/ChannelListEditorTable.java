package dataVisualizer;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
//javax.swing.table.DefaultTableModel

import dataCache.DataCache_File;
import utils.dbg;

class MyTableModel extends javax.swing.table.DefaultTableModel {
    public MyTableModel(int rowNum, int colNum)
    {
        super(rowNum, colNum);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        dbg.println(9, "isCellEditable rowIndex=" + rowIndex + " columnIndex=" + columnIndex);
        return false;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -504246097662989104L;
}

class Groups
{
    static final String notVisible = "not visible";
    Groups(DataChannelList colArray)
    {
        groupNameArray.add(notVisible);
        groupNameMap.put(notVisible, 0);
        for(int i = 0; i < colArray.size(); i++)
        {
            DataChannelListItem dcli = colArray.get(i);
            if (!groupNameMap.containsKey(dcli.group.name))
            {
                int idx = groupNameArray.size();
                groupNameArray.add(dcli.group.name);
                groupNameMap.put(dcli.group.name, idx);
            }
        }
    }

    String get(int idx)
    {
        return groupNameArray.get(idx);
    }

    int get(String item)
    {
        return groupNameMap.get(item);
    }

    String[] getStringArray(String signalName)
    {
        int size = groupNameArray.size();
        if (!groupNameMap.containsKey(signalName))
            size++;
        String[] result = new String[size];
        for(int i = 0; i < groupNameArray.size(); i++)
            result[i] = groupNameArray.get(i);
        if (size != groupNameArray.size())
            result[groupNameArray.size()] = signalName;
        return result;
    }

    Vector<String> groupNameArray = new Vector<String>();
    Map<String, Integer> groupNameMap = new TreeMap<String, Integer>();
}

public class ChannelListEditorTable extends JTable {
    DataChannelGroup hidden = new DataChannelGroup(Groups.notVisible);
    static final String[] columnNames = new String[]{"Signal name", "Signal color", "Group name"};
    static final int colSignalName = 0;
    static final int colSignalColor = 1;
    static final int colGroupName = 2;
    ChannelSelectorDialog parent;
    Groups groupNames;

    public ChannelListEditorTable(ChannelSelectorDialog _parent, DataCache_File file, DataChannelList colArray)
    {
        super(new MyTableModel(file.getChannelNumber(), columnNames.length));
        parent = _parent;
        groupNames = new Groups(colArray);

        javax.swing.table.TableColumnModel columnModel = this.getColumnModel();
        for (int i = 0; i < columnNames.length; i++)
        {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(10);
            column.setMaxWidth(200);
            column.setWidth(10);
            column.setResizable(true);
            column.setHeaderValue(columnNames[i]);
        }

        //javax.swing.table.DefaultTableModel tableModel = (javax.swing.table.DefaultTableModel)this.getModel();
        for (int i = 0; i < file.getChannelNumber(); i++)
        {
            String chName = file.getChannel(i).getName();
            this.setValueAt(chName, i, colSignalName);
            DataChannelListItem dcli = colArray.get(chName);
            Color color;
            DataChannelGroup dcg;
            if (dcli != null)
            {
                color = dcli.color;
                dcg = dcli.group;
            }else
            {
                color = Color.WHITE;
                dcg = hidden;
            }
            this.setValueAt(color, i, colSignalColor);
            this.setValueAt(dcg.name, i, colGroupName);
        }

        //add(table.getTableHeader());
        //add(table);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseHandler(e);
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
                mouseHandler(e);
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
        getModel().addTableModelListener(
                new javax.swing.event.TableModelListener()
                {
                    public void tableChanged(javax.swing.event.TableModelEvent evt) 
                    {
                      tableChangedHandler(evt);
                    }
        });
        getSelectionModel().addListSelectionListener(
            new ListSelectionListener (){
                @Override
                public void valueChanged(ListSelectionEvent evt)
                {
                    listSelectionIsChanged(evt);
                }
        });
    }

    protected void mouseHandler(MouseEvent evt)
    {
        dbg.println(9, "ChannelListEditorTable - mouseHandler evt=" + evt.toString());
        dbg.println(9, "  findComponentAt="+findComponentAt(evt.getX(), evt.getY()).toString());
        if (evt.getID() == MouseEvent.MOUSE_CLICKED)
        {
            int rowAtPoint = rowAtPoint(evt.getPoint());
            int colAtPoint = columnAtPoint(evt.getPoint());
            dbg.dprintf(9, "  rowAtPoint=%d colAtPoint=%d\n", rowAtPoint, colAtPoint);
            if (rowAtPoint >= 0) {
                setRowSelectionInterval(rowAtPoint, rowAtPoint);
                updateParent(rowAtPoint);
                if (evt.getButton() == MouseEvent.BUTTON3)
                {
                    if (colAtPoint == colSignalColor)
                    {
                        askSignalColor();
                    }
                }
            }
        }
    }

    void updateParent(int row)
    {
        String signalName = (String)getValueAt(row, colSignalName);
        Color color = (Color)getValueAt(row, colSignalColor);
        String groupName = (String)getValueAt(row, colGroupName);
        String[] groups = groupNames.getStringArray(signalName);
        parent.setSignalProperties(signalName, color, groupName, groups);
    }

    protected void tableChangedHandler(TableModelEvent evt)
    {
        dbg.println(9, "tableChangedHandler evt=" + evt);
        dbg.println(19, "  UPDATE=" + TableModelEvent.UPDATE);
    }

    private void listSelectionIsChanged(ListSelectionEvent evt)
    {
        dbg.println(11, "ChannelListEditorTable - ListSelectionListener evt=" + evt.toString());
        int selIdx = getSelectedRow();
        dbg.println(11, "ChannelListEditorTable - ListSelectionListener selIdx=" + selIdx);
//        int firstIdx = evt.getFirstIndex();
//        int lastIdx = evt.getLastIndex();
//        dbg.println(11, "ChannelListEditorTable - ListSelectionListener firstIdx=" + firstIdx + " lastIdx=" + lastIdx);
        //updateParent();
        parent.updateProperties();
    }

    public void setSignalGroupName(String groupName) {
        int row = getSelectedRow();
        setValueAt(groupName, row, colGroupName);
    }

    public void askSignalColor() {
        int row = getSelectedRow();
        java.awt.Color newColor = 
                javax.swing.JColorChooser.showDialog(
                  this,
                    "Choose Signal Color",
                    (Color)getValueAt(row, colSignalColor));
        if (newColor != null) {
            setValueAt(newColor, row, colSignalColor);
            if (((String)getValueAt(row, colGroupName)).equals(Groups.notVisible))
            {
                setValueAt((String)getValueAt(row, colSignalName), row, colGroupName);
            }
            updateParent(row);
          //repaintRequest(true);
        }
    }

    public boolean isSignalVisible(int row) {
        return !((String)getValueAt(row, colGroupName)).equals(Groups.notVisible);
    }

    public String getSignalName(int row) {
        return (String)getValueAt(row, colSignalName);
    }

    public Color getColor(int row) {
        return (Color)getValueAt(row, colSignalColor);
    }

    public String getGroupName(int row) {
        return (String)getValueAt(row, colGroupName);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 274626894788601964L;
}
