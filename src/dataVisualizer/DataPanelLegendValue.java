package dataVisualizer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import dataCache.DataCache_File;

public class DataPanelLegendValue extends DataPanelLegendBase
{
    //headers for the table
    final String[] columnNames = new String[] {
        "Signal name",
        "value",
        "unit",
    };
    private final int colSignalName = 0;
    private final int colValue = 1;
    private final int colUnit = 2;

    JTable table;

    public DataPanelLegendValue(DataPanelMain parent, DataCache_File file, DataChannelList dcl)
    {
        super(parent, file, dcl);

        //create table with data
        table = new JTable(new DefaultTableModel(1, 3));
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
        fillSignalList();

        //add the table to the frame
        add(new JScrollPane(table));
    }

    private void fillSignalList() {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(dataChannelList.size());
        for (int i = 0; i < dataChannelList.size(); i++)
        {
            table.setValueAt(dataChannelList.getChName(i), i, colSignalName);
            table.setValueAt(dataChannelList.get(i).ch.getUnit(), i, colUnit);
            table.setValueAt("value "+i, i, colValue);
        }
    }

    private static final long serialVersionUID = -1531090392973265388L;
}
