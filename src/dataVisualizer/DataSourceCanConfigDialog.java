package dataVisualizer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import lippiWare.utils.dbg;

public class DataSourceCanConfigDialog extends JDialog {

    public DataSourceCanConfigDialog(Object o) {

        if (o == null)
            return;

        parent = (DataPanelMain)o;

        JLabel l = new JLabel("lll");

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
        cp.add(bOkCancel);

        layout.putConstraint(SpringLayout.NORTH, l, 4, SpringLayout.NORTH, cp);
        layout.putConstraint(SpringLayout.WEST,  l, 5, SpringLayout.WEST,  cp);

        layout.putConstraint(SpringLayout.NORTH,             bOkCancel, 5, SpringLayout.SOUTH, l);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, bOkCancel,  0, SpringLayout.HORIZONTAL_CENTER, cp);
        layout.putConstraint(SpringLayout.SOUTH,             bOkCancel, -5, SpringLayout.SOUTH, cp);

        pack();

        Point pt = parent.getLocationOnScreen();
        int pw = parent.getWidth();
        setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
        setLocation(DataVisualizerPrefs.get("DataSourceCanConfigDialogX", 0), DataVisualizerPrefs.get("DataSourceCanConfigDialogY", 0));
        setSize(DataVisualizerPrefs.get("DataSourceCanConfigDialogW", 350), DataVisualizerPrefs.get("DataSourceCanConfigDialogH", 300));
        this.setMinimumSize(new Dimension(350, 400));

        addEscapeListener();
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
    private static final long serialVersionUID = 3669321701872360642L;
}
