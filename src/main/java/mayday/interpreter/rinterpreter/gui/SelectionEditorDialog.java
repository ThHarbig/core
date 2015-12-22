/*
 * Created on 24.05.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import mayday.core.gui.MaydayDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.ParameterType;


@SuppressWarnings("serial")
public class SelectionEditorDialog 
extends MaydayDialog
{
    private ParameterType type;
    private JTextArea text;
    private int result=RDefaults.Actions.CANCEL;
    
    public SelectionEditorDialog(DescriptionEditorDialog dialog, Component relative, ParameterType type)
    {
        super(dialog,
              type.id());
        setModal(true);
        
        
        this.type=type;
        this.text=new JTextArea(
            RDefaults.toString(type.values(),"\n"),
            10, //rows
            20  //cols
        );
        this.text.setCaretPosition(0);
        this.text.setLineWrap(false);
        
        Box box=Box.createVerticalBox();
        box.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createTitledBorder(
                    BorderFactory.createEmptyBorder(5,5,5,5),
                    this.type.id()
                )
            )
        );
       
        Box buttonBox=Box.createHorizontalBox();
        buttonBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        buttonBox.add(new JButton(new CancelAction()));
        buttonBox.add(new JLabel(" "));
        buttonBox.add(new JButton(new OkAction()));
        
        box.add(new JScrollPane(text));
        box.add(buttonBox);      
        
        this.getContentPane().add(box);
        this.setSize(this.getContentPane().getPreferredSize());
        this.setAlwaysOnTop(true);
        this.setUndecorated(true);
        this.setLocationRelativeTo(relative);
    }
    
    public int showDlg()
    {
        this.setVisible(true);
        return result;
    }
    
    
    private class OkAction
    extends AbstractAction
    {
        public OkAction()
        {
            super(RDefaults.ActionNames.OK);
        }
        
        public void actionPerformed(ActionEvent event)
        {
           type.setValues(
               text.getText().split("[\\n]+")
           );
           result=RDefaults.Actions.OK;
           SelectionEditorDialog.this.setVisible(false);
        }
    }
    
    private class CancelAction
    extends AbstractAction
    {
        public CancelAction()
        {
            super(RDefaults.ActionNames.CANCEL);
        }
        
        public void actionPerformed(ActionEvent event)
        {
           result=RDefaults.Actions.CANCEL;
           SelectionEditorDialog.this.setVisible(false);
        }            
    }
}