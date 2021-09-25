package dataVisualizer;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
//javax.swing.table.DefaultTableModel

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

    public ChannelListEditorTable()
    {
        super(new MyTableModel());
        javax.swing.table.TableColumnModel columnModel = getColumnModel();
        //columnModel.addColumn(new javax.swing.table.TableColumn());
        //columnModel.addColumn(new javax.swing.table.TableColumn());
        //columnModel.addColumn(new javax.swing.table.TableColumn());
    }

    /**
     * 
     */
    private static final long serialVersionUID = 274626894788601964L;
}
