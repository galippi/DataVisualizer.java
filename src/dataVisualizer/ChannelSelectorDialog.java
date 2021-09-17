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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dataCache.DataCache_File;
import utils.dbg;

/**
 *
 * @author liptakok
 */
public class ChannelSelectorDialog extends JDialog {
  JList lbSelected;
  JList lbDeselected;
  JButton bAddAll;
  JButton bAdd;
  JButton bRemove;
  JButton bRemoveAll;
  JButton bUp;
  JButton bDown;
  //IgcFileTable callBackParent;
  DataCache_File file;
  DataChannelList colArray;
  ChannelSelectorDialog(JFrame parent, DataCache_File _file, DataChannelList _colArray)
  {
    super(parent, Dialog.ModalityType.APPLICATION_MODAL);
    file = _file;
    colArray = _colArray;
    this.setTitle("Select signals to be displayed");

    JLabel l2 = new JLabel("Select signals to be displayed");
    l2.setHorizontalAlignment(SwingConstants.CENTER);

    JButton b2 = new JButton("OK");
    b2.setHorizontalAlignment(SwingConstants.LEFT);
    b2.addActionListener(new ActionListener() {
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

    DefaultListModel lmSelected = new DefaultListModel();
    for(int i = 0; i < colArray.size(); i++)
    {
      lmSelected.addElement(colArray.get(i).getSignalName());
    }
    lbSelected = new JList(lmSelected);
    lbSelected.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        updateButtons();
      }
    });

    DefaultListModel lmDeselected = new DefaultListModel();
    for(int i = 0; i < file.getChannelNumber(); i++)
    {
      int j;
      for(j = 0; j < colArray.size(); j++)
      {
        if (file.getChannel(i) == colArray.get(i).ch)
          break;
      }
      if (j == colArray.size())
        lmDeselected.addElement(file.getChannel(i).getName());
    }
    lbDeselected = new JList(lmDeselected);
    lbDeselected.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        updateButtons();
      }
    });

    Container cp2 = getContentPane();
    // add label, text field and button one after another into a single column
    cp2.setLayout(new BorderLayout());
    cp2.add(l2, BorderLayout.NORTH);
    JPanel p2 = new JPanel();
    //Container cp3 = p2.getContentPane();
    p2.setLayout(new FlowLayout());

    JPanel pUpDown = new JPanel();
    pUpDown.setLayout(new GridLayout(2, 1));

    bUp = new JButton("Up");
    bUp.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          upHandler();
        }
    });
    bDown = new JButton("Down");
    bDown.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          downHandler();
        }
    });
    pUpDown.add(bUp);
    pUpDown.add(bDown);
    p2.add(pUpDown);
    p2.add(new JScrollPane(lbSelected));
    JPanel p3 = new JPanel();
    //p3.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    p3.setLayout(new GridLayout(4, 1));

    bAddAll = new JButton("<<");
    bAddAll.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          addAllHandler();
        }
    });
    p3.add(bAddAll);

    bAdd = new JButton("<");
    bAdd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          addHandler();
        }
    });
    p3.add(bAdd);

    bRemove = new JButton(">");
    bRemove.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          removeHandler();
        }
    });
    p3.add(bRemove);

    bRemoveAll = new JButton(">>");
    bRemoveAll.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          removeAllHandler();
        }
    });
    p3.add(bRemoveAll);

    p2.add(p3);
    p2.add(new JScrollPane(lbDeselected));
    cp2.add(p2, BorderLayout.CENTER);
    JPanel bOkCancel = new JPanel();
    bOkCancel.add(b2);
    bOkCancel.add(bCancel);
    cp2.add(bOkCancel, BorderLayout.SOUTH);
    Point pt = parent.getLocationOnScreen();
    int pw = parent.getWidth();
    setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
    this.setMinimumSize(new Dimension(350, 300));
    updateButtons();
  }
  final void updateButtons()
  {
    if (lbSelected.getModel().getSize() == 0)
    {
      bRemoveAll.setEnabled(false);
    }else
    {
      bRemoveAll.setEnabled(true);
    }
    int[] lbSelectedSelection = lbSelected.getSelectedIndices();
    if (lbSelectedSelection.length == 0)
    {
      bRemove.setEnabled(false);
    }else
    {
      bRemove.setEnabled(true);
    }
    if ((lbSelectedSelection.length > 0) && (lbSelectedSelection[0] != 0))
      bUp.setEnabled(true);
    else
      bUp.setEnabled(false);
    if ((lbSelectedSelection.length > 0) && (lbSelectedSelection[lbSelectedSelection.length - 1] < (lbSelected.getModel().getSize() - 1)))
      bDown.setEnabled(true);
    else
      bDown.setEnabled(false);
    if ((lbDeselected.getModel().getSize() == 0) || (lbSelectedSelection.length >= 2))
    {
      bAddAll.setEnabled(false);
    }else
    {
      bAddAll.setEnabled(true);
    }
    int[] lbDeselectedSelection = lbDeselected.getSelectedIndices();
    if ((lbDeselectedSelection.length == 0) || (lbSelectedSelection.length >= 2))
    {
      bAdd.setEnabled(false);
    }else
    {
      bAdd.setEnabled(true);
    }
  }
  void okHandler()
  {
    dbg.println(9, "ColumnSelectorDialog.okHandler");
    setVisible(false);
    DefaultListModel mSelected = (DefaultListModel)lbSelected.getModel();
    colArray.clear();
    for (int i = 0; i < mSelected.size(); i++)
    {
      String colName = mSelected.getElementAt(i).toString();
      colArray.addSignal(colName);
      dbg.println(11, "  colArray["+i+"]=" + colName + "!");
    }
    colArray.setHorizontalAxle("__pointIndex");
    colArray.updateCallbacksExecute();
    //callBackParent.columnSelectorDialogOkHandler(colArray);
  }

  void upHandler()
  {
    int[] sel = lbSelected.getSelectedIndices();
    DefaultListModel mSelected = (DefaultListModel)lbSelected.getModel();
    for(int i = 0; i < sel.length; i++)
    {
      Object selected = mSelected.getElementAt(sel[i]);
      mSelected.setElementAt(mSelected.getElementAt(sel[i] - 1), sel[i]);
      mSelected.setElementAt(selected, sel[i] - 1);
      sel[i]--;
    }
    lbSelected.setSelectedIndices(sel);
  }

  void downHandler()
  {
    int[] sel = lbSelected.getSelectedIndices();
    DefaultListModel mSelected = (DefaultListModel)lbSelected.getModel();
    for(int i = sel.length - 1; i >= 0 ; i--)
    {
      Object selected = mSelected.getElementAt(sel[i]);
      mSelected.setElementAt(mSelected.getElementAt(sel[i] + 1), sel[i]);
      mSelected.setElementAt(selected, sel[i] + 1);
      sel[i]++;
    }
    lbSelected.setSelectedIndices(sel);
  }

  void addHandler()
  {
    int[] sel = lbDeselected.getSelectedIndices();
    addHandler(sel);
  }
  void addHandler(int[] sel)
  {
    DefaultListModel mDeselected = (DefaultListModel)lbDeselected.getModel();
    DefaultListModel mSelected = (DefaultListModel)lbSelected.getModel();
    Object[] objs = new Object[sel.length];
    for(int i = 0; i < sel.length; i++)
    {
      objs[i] = mDeselected.getElementAt(sel[i]);
      mSelected.addElement(objs[i]);
    }
    for(int i = sel.length - 1; i >= 0; i--)
      mDeselected.removeElementAt(sel[i]);
    int[] selectedList = lbSelected.getSelectedIndices();
    if (selectedList.length == 1)
    {
      //moveUp(selectedList[0], );
      int toIdx = selectedList[0];
      for(int i = mSelected.getSize() - objs.length - 1; i >= toIdx; i--)
      {
        mSelected.setElementAt(mSelected.getElementAt(i), i + objs.length);
      }
      for(int i = 0; i < objs.length; i++)
      {
        mSelected.setElementAt(objs[i], toIdx + i);
      }
    }
    updateButtons();
  }
  void addAllHandler()
  {
    int num = lbDeselected.getModel().getSize();
    int[] sel = new int[num];
    for(int i = 0; i < num; i++)
    {
      sel[i] = i;
    }
    addHandler(sel);
  }
  void removeHandler()
  {
    int[] sel = lbSelected.getSelectedIndices();
    DefaultListModel mDeselected = (DefaultListModel)lbDeselected.getModel();
    DefaultListModel mSelected = (DefaultListModel)lbSelected.getModel();
    for(int i = 0; i < sel.length; i++)
      mDeselected.addElement(mSelected.getElementAt(sel[i]));
    for(int i = sel.length - 1; i >= 0; i--)
      mSelected.removeElementAt(sel[i]);
    updateButtons();
  }
  void removeAllHandler()
  {
    DefaultListModel mDeselected = (DefaultListModel)lbDeselected.getModel();
    DefaultListModel mSelected = (DefaultListModel)lbSelected.getModel();
    for(int i = 0; i < mSelected.getSize(); i++)
      mDeselected.addElement(mSelected.getElementAt(i));
    mSelected.clear();
    updateButtons();
  }
}
