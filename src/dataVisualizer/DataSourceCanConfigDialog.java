package dataVisualizer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import lippiWare.utils.dbg;

class TreeNodeChannel extends DefaultMutableTreeNode {
    public TreeNodeChannel(String name) {
        super(name);
    }

    private static final long serialVersionUID = -612150313211373948L;
}

class TreeNodeDbc extends DefaultMutableTreeNode {
    public TreeNodeDbc(String name) {
        super(name);
    }

    private static final long serialVersionUID = 6008994166012788905L;
}

public class DataSourceCanConfigDialog extends JDialog {

    public DataSourceCanConfigDialog(Object o) {

        if (o == null)
            return;

        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        parent = (DataPanelMain)o;
        DataVisualizerLayoutFileLoader dvlf = parent.dvlf;

        JLabel l = new JLabel("lll");

        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("Channels");
        tree = new JTree(treeRoot);
        Vector<Integer> channels = parent.file.getDataSourceChannelIndexArray();
        for(int i = 0; i < channels.size(); i++) {
            int chIdx = channels.get(i).intValue();
            String chName = "Ch " + chIdx;
            TreeNodeChannel node = new TreeNodeChannel(chName);
            treeRoot.add(node);
            String dbc;
            int fileIdx = 0;
            while ((dbc = dvlf.getDbcName(chIdx, fileIdx)) != null)
            {
                TreeNodeDbc dbcNode = new TreeNodeDbc(dbc);
                node.add(dbcNode);
                fileIdx++;
            }
        }

        tree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseHandler(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // do nothing
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // do nothing
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // do nothing
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // do nothing
            }
        });
        JScrollPane treePane = new JScrollPane(tree);

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

        JPanel bOkCancel = new JPanel();
        bOkCancel.add(bOk);
        bOkCancel.add(bCancel);

        Container cp = getContentPane();
        SpringLayout layout = new SpringLayout();
        cp.setLayout(layout);
        cp.add(l);
        cp.add(treePane);
        cp.add(bOkCancel);

        layout.putConstraint(SpringLayout.NORTH, l, 4, SpringLayout.NORTH, cp);
        layout.putConstraint(SpringLayout.WEST,  l, 5, SpringLayout.WEST,  cp);

        layout.putConstraint(SpringLayout.NORTH, treePane, 4, SpringLayout.SOUTH, l);
        layout.putConstraint(SpringLayout.WEST,  treePane, 5, SpringLayout.WEST,  cp);
        layout.putConstraint(SpringLayout.EAST,  treePane, 5, SpringLayout.EAST,  cp);

        layout.putConstraint(SpringLayout.NORTH,             bOkCancel, -40, SpringLayout.SOUTH,             cp);
        layout.putConstraint(SpringLayout.SOUTH,             treePane,    5, SpringLayout.NORTH,             bOkCancel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, bOkCancel,   0, SpringLayout.HORIZONTAL_CENTER, cp);
        layout.putConstraint(SpringLayout.SOUTH,             bOkCancel,  -5, SpringLayout.SOUTH,             cp);

        pack();

        Point pt = parent.getLocationOnScreen();
        int pw = parent.getWidth();
        setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
        setLocation(DataVisualizerPrefs.get("DataSourceCanConfigDialogX", 0), DataVisualizerPrefs.get("DataSourceCanConfigDialogY", 0));
        setSize(DataVisualizerPrefs.get("DataSourceCanConfigDialogW", 350), DataVisualizerPrefs.get("DataSourceCanConfigDialogH", 300));
        this.setMinimumSize(new Dimension(350, 400));

        addEscapeListener();
    }

    protected void mouseHandler(MouseEvent e) {
        dbg.println(9, "DataSourceCanConfigDialog.mouseHandler e=" + e.toString());
        if ((e.getID() == MouseEvent.MOUSE_CLICKED) && (e.getButton() == MouseEvent.BUTTON3))
        {
            //Component c = tree.getComponentAt(e.getX(), e.getY());
            TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
            int selRow = tree.getRowForLocation(e.getX(), e.getY());
            dbg.println(9, "TreePath=" + path.toString() + " selRow=" + selRow);
            if (selRow >= 0) {
                tree.setSelectionRow(selRow);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();
                dbg.println(9, " node=" + ((node == null) ? "(null)" : node.toString()));
                if (node.getClass() == TreeNodeChannel.class)
                    dbg.println(9, " node=TreeNodeChannel");
                else if (node.getClass() == TreeNodeDbc.class)
                    dbg.println(9, " node=TreeNodeDbc");
            }
        }
    }

    protected void okHandler() {
        dbg.println(9, "DataSourceCanConfigDialog.okHandler");
        setVisible(false);

        DataVisualizerPrefs.put("DataSourceCanConfigDialogX", getX());
        DataVisualizerPrefs.put("DataSourceCanConfigDialogY", getY());
        DataVisualizerPrefs.put("DataSourceCanConfigDialogH", getHeight());
        DataVisualizerPrefs.put("DataSourceCanConfigDialogW", getWidth());
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

    DataPanelMain parent;
    JTree tree;

    private static final long serialVersionUID = 3669321701872360642L;
}
