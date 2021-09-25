package dataVisualizer;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
//javax.swing.table.DefaultTableModel

class MyTableModel extends AbstractTableModel {
    private String[] columnNames = {"Signal name", "Signal color", "Group name"};
    private Object[][] data = {{"Signal 0", "red", "group 0"}};
    //private Object[][] data = null;

    public MyTableModel()
    {
        //super(data, columnNames);
        //super(columnNames);
        super();
        //for (int i = 0; i < columnNames.length; i++)
        //    addColumn(columnNames[i]);
        //addRow(data);
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
        //return 1;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
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
