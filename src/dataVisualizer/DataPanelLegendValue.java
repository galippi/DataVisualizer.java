package dataVisualizer;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import dataCache.DataCache_FileBase;
import lippiWare.utils.dbg;

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

    public DataPanelLegendValue(DataPanelMain parent, DataCache_FileBase file, DataChannelList dcl)
    {
        super(parent, file, dcl);
        this.setMinimumSize(new Dimension(100, 100));

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

        //table.setMinimumSize(new Dimension(100, 100));

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
            table.setValueAt("", i, colValue);
        }
    }

    //@Override
    //public void setDataCursor(int hPos) {
    //    dbg.println(9, "DataPanelLegendValue.setDataCursor hPos="+hPos);
    //    updateSignalValues(hPos);
    //}

    int hPosLast = -1;
    private void updateSignalValues(int hPos)
    {
        dbg.println(9, "DataPanelLegendValue.updateSignalValues hPos="+hPos);
        hPosLast = hPos;
        if (hPosLast < 0)
        {
            // fill table empty
            for (int i = 0; i < dataChannelList.size(); i++)
            {
                table.setValueAt("", i, colValue);
            }
            return;
        }
        for (int i = 0; i < dataChannelList.size(); i++)
        {
            String value;
            try {
                value = ""+dataChannelList.get(i).ch.getDoubleGlobal(hPos);
            } catch (Exception e) {
                dbg.println(1, "DataPanelLegendValue.updateSignalValue.getDouble("+i+") Exception ch="+dataChannelList.getChName(i)+" hPos="+hPos+" e="+e.toString());
                value = "";
            }
            table.setValueAt(value, i, colValue);
        }
    }

    @Override
    public void dataChannelListChangeEventHandler(DataChannelList dcl)
    {
        repaint();
        fillSignalList();
        if (hPosLast >= 0)
            updateSignalValues(hPosLast);
    }

    @Override
    public void setDataCursor(double hPos) {
        if (hPos < 0)
            updateSignalValues((int)hPos);
    }

    @Override
    public void setLegendValue(int i, String valStr) {
        table.setValueAt(valStr, i, colValue);
    }

    private static final long serialVersionUID = -1531090392973265388L;
}
