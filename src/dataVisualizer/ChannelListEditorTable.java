package dataVisualizer;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
//javax.swing.table.DefaultTableModel

import dataCache.DataCache_File;

class MyTableModel extends javax.swing.table.DefaultTableModel {
    private String[] columnNames = {"Signal name", "Signal color", "Group name"};
    private String[][] data = {{"Signal 0", "red", "group 0"}};

    public MyTableModel()
    {
        //super(data, columnNames);
        //super(columnNames);
        super();
        for (int i = 0; i < columnNames.length; i++)
        {
            addColumn(columnNames[i]);
        }

        setNumRows(1);
        //addRow(data);
        for (int i=0; i < columnNames.length; i++)
        {
            setValueAt(data[0][i], 0, i);
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = -504246097662989104L;
}

public class ChannelListEditorTable extends JTable {
    //JTable table;
    DataChannelGroup hidden = new DataChannelGroup("not visible");
    static final String[] columnNames = new String[]{"Signal name", "Signal color", "Group name"};
    public ChannelListEditorTable(DataCache_File file, DataChannelList colArray)
    {
        super(new javax.swing.table.DefaultTableModel(file.getChannelNumber(), columnNames.length));
        //setLayout(new GridLayout(2,1));
        //javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel();
        //model.setColumnCount(3);
        //model.setNumRows(file.getChannelNumber());
        //table = new JTable(model);
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

        javax.swing.table.DefaultTableModel tableModel = (javax.swing.table.DefaultTableModel)this.getModel();
        for (int i = 0; i < file.getChannelNumber(); i++)
        {
            String chName = file.getChannel(i).getName();
            this.setValueAt(chName, i, 0);
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
            this.setValueAt(color.toString(), i, 1);
            this.setValueAt(dcg.name, i, 2);
        }

        //add(table.getTableHeader());
        //add(table);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 274626894788601964L;
}
