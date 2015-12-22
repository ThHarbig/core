/*
 * Created on 18.02.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import mayday.core.gui.MaydayDialog;
import javax.swing.JTabbedPane;

import mayday.core.MaydayDefaults;
import mayday.core.gui.PreferencePane;
import mayday.core.gui.abstractdialogs.AbstractStandardDialogComponent;
import mayday.core.pluma.PluginInfo;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RSettings;

/**
 * @author Matthias Zschunke
 *
 */
@SuppressWarnings("serial")
public class RSettingsDialog
extends MaydayDialog
{
    private RSettings settings;
    private ArrayList<AbstractProblemDialogComponent> tabs;
    private JTabbedPane tabbedPane;
    private SourceComponent sourceComponent;
    private FileSettingsComponent filesettingsComponent;
    private Box globalBox;
    
    public RSettingsDialog(RSettings settings) throws BackingStoreException
    {
        super(RDefaults.MAYDAY_FRAME());
        setTitle(RDefaults.Titles.RDIALOG);
        
        this.settings=settings;
        this.tabs=new ArrayList<AbstractProblemDialogComponent>();
        
        compose();
    }
    
    protected void compose() throws BackingStoreException
    {
        tabbedPane = new JTabbedPane();
        globalBox=Box.createVerticalBox();
        Box buttonBox=Box.createHorizontalBox();
        
        NextAction nextAction=new NextAction();
        
        sourceComponent = new SourceComponent();
        tabbedPane.add(sourceComponent.toString(),sourceComponent);
        tabs.add(sourceComponent);
        filesettingsComponent = new FileSettingsComponent();
        tabbedPane.add(filesettingsComponent.toString(),filesettingsComponent);
        tabs.add(filesettingsComponent);
        
        for(AbstractProblemDialogComponent c:tabs)
        {
            c.addProblemListener(nextAction);
        }
        
        //buttonBox.add(new JButton(new AboutAction()));
        //buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(new JButton(new CancelAction()));
        buttonBox.add(Box.createHorizontalStrut(3));
        if (settings!=null) { 
        	buttonBox.add(new JButton(nextAction));
        	//buttonBox.add(Box.createHorizontalStrut(3));
        }
        //buttonBox.add(new JButton(new SaveAction()));
        
        
        
        globalBox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        globalBox.add(tabbedPane);
        globalBox.add(Box.createVerticalStrut(5));
        globalBox.add(buttonBox);
        globalBox.add(Box.createVerticalGlue());
        
        getContentPane().add(globalBox);
        
        pack();
        setSize(new Dimension(
            getContentPane().getPreferredSize().width,
            getContentPane().getPreferredSize().height
            + getInsets().top
        ));
        setModal(true);
    }   
    
    public PreferencePane getAsPane() {
    	PreferencePane pp = new PreferencePane() {
			public void writePreferences() throws BackingStoreException {
				new SaveAction().actionPerformed(null);
			}
    	};
    	pp.add(globalBox);
    	return pp;
    }
    
    private class NextAction
    extends AbstractAction
    implements ProblemListener
    {
        Set<AbstractProblemDialogComponent> problems=new HashSet<AbstractProblemDialogComponent>();
        
        public NextAction()
        {
            super(RDefaults.ActionNames.NEXT);
        }
        
        public void actionPerformed(ActionEvent event)
        {
        	try {
        		new SaveAction().actionPerformed(event);
        	} catch (Throwable t) {};
            
            //apply selections to settings 
            settings.setSource(sourceComponent.getSelectedSource());
            filesettingsComponent.applyValues(settings);
            
            if(settings.getSource()==null) //if no selection
            {
                RDefaults.messageGUI("Please select a function.",RDefaults.Messages.Type.ERROR);
            } else 
            {
                settings.activate();
                setVisible(false);
            }            
        }

        public void problemOccured(ProblemEvent e)
        {
            if(e.getSource() instanceof AbstractProblemDialogComponent)
            {
                problems.add((AbstractProblemDialogComponent)e.getSource());
                tabbedPane.setIconAt(
                    tabbedPane.getSelectedIndex(),
                    PluginInfo.getIcon(RDefaults.ICON_WARNING)
                );
            }
            this.setEnabled(false);
        }

        public void problemSolved(ProblemEvent e)
        {
            if(e.getSource() instanceof AbstractProblemDialogComponent)
            {
                problems.remove((AbstractProblemDialogComponent)e.getSource());
                tabbedPane.setIconAt(
                    tabbedPane.getSelectedIndex(),
                    null
                );
            }
            if(problems.size()==0) this.setEnabled(true);
        }
    }
    
    private class SaveAction
    extends AbstractAction
    {
        public SaveAction()
        {
            super(RDefaults.ActionNames.SAVE);
        }
        
        public void actionPerformed(ActionEvent event)
        {
            for(AbstractStandardDialogComponent tab:tabs)
            {
                for(Action a:tab.getOkActions())
                {
                    a.actionPerformed(event);
                }
            }
            MaydayDefaults.Prefs.save();  
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
            setVisible(false);
        }
    }
    
    /*
    private class AboutAction
    extends AbstractAction
    {
        private AboutDialog dlg;
        
        public AboutAction()
        {
            super(RDefaults.ActionNames.ABOUT);
        }

        public void actionPerformed(ActionEvent e)
        {
            if(dlg==null) dlg=new AboutDialog();
            dlg.setVisible(true);            
        }
    }*/
    
    /*
    private class AboutDialog
    extends MaydayDialog
    {
        public AboutDialog()
        {
            super(RSettingsDialog.this);
            setTitle("About");
            setModal(true);
            compose();
        }
        
        protected void compose()
        {
            Box box=Box.createVerticalBox();
            box.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            Box labelBox2 = Box.createVerticalBox();
            labelBox2.add(new PluginAnnotationViewerComponent(
                RDefaults.ANNOTATION,
                "About"
            ));
            
            Box labelBox = Box.createHorizontalBox();
            labelBox.add(new JLabel(RDefaults.RSPLASH));
            labelBox.add(Box.createHorizontalStrut(10));
            labelBox.add(labelBox2);
            
            Box buttonBox=Box.createHorizontalBox();
            buttonBox.add(Box.createHorizontalGlue());
            buttonBox.add(new JButton(new AbstractAction(RDefaults.ActionNames.OK) {
                public void actionPerformed(ActionEvent e)
                {
                    setVisible(false);                    
                }                
            }));
            
            box.add(labelBox);
            box.add(Box.createVerticalStrut(5));
            box.add(buttonBox);
            
            getContentPane().add(box);
            
            setSize(new Dimension(
                getContentPane().getPreferredSize().width,
                getContentPane().getPreferredSize().height
                + getInsets().top
            ));
            pack();
            
            
            this.setLocationRelativeTo(this.getParent());
            this.setResizable(false);
        }
    }*/
}
