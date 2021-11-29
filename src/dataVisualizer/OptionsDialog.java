/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataVisualizer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import utils.dbg;

/**
 *
 * @author liptakok
 */
public class OptionsDialog extends JDialog {

  JTextField debugLevel;
  JTextField SRTM_cacheFolder;
  OptionsDialog(JFrame parent)
  {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    this.setTitle("Options");

    JLabel l2 = new JLabel("Debug level:");
    l2.setHorizontalAlignment(JTextField.LEFT);
    debugLevel = new JTextField("" + dbg.get(), 5);
    //debugLevel.setSize(100,20);
    debugLevel.setHorizontalAlignment(JTextField.TRAILING);
    //debugLevel.

    JPanel jpDebugLevel = new JPanel();
    //jpDebugLevel.setLayout(new GroupLayout(jpDebugLevel));
    jpDebugLevel.add(l2);
    jpDebugLevel.add(debugLevel);

    JButton bOk = new JButton("Ok");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bOk.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            okHandler();
        }
    });
    JButton bCancel = new JButton("Cancel");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cancelHandler();
        }
    });
    JPanel bOkCancel = new JPanel();
    bOkCancel.add(bOk);
    bOkCancel.add(bCancel);
    Container cp = getContentPane();
    // add label, text field and button one after another into a single column
    cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    cp.add(jpDebugLevel);
    //cp.add(jpSrtm);
    cp.add(bOkCancel, BorderLayout.SOUTH);

    Point pt = parent.getLocationOnScreen();
    int pw = parent.getWidth();
    setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
    this.setMinimumSize(new Dimension(350, 300));
    addEscapeListener();
  }
  void okHandler()
  {
    boolean closable = true;
    try {
      int level = Integer.parseInt(debugLevel.getText());
      DataVisualizerPrefs.put("Debug level", level);
      dbg.set(level);
    }catch (NumberFormatException e)
    {
      dbg.println(2, "OptionDialog.okHandler.NumberFormatException="+e.toString());
      closable = false;
    }
    if (closable)
    {
        //IgcViewerPrefs.setSrtmCache(SRTM_cacheFolder.getText());
        setVisible(false);
    }
  }

  void cancelHandler()
  {
    setVisible(false);
    //dispose();
  }

  void addEscapeListener() {
      ActionListener escListener = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              cancelHandler();
          }
      };
      getRootPane().registerKeyboardAction(escListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  private static final long serialVersionUID = -4777973355120139808L;
}
