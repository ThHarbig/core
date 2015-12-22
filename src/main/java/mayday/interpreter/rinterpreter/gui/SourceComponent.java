/*
 * Created on 18.02.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.Preferences;
import mayday.core.io.StorageNode;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RFileFilter;
import mayday.interpreter.rinterpreter.core.RSource;
import mayday.interpreter.rinterpreter.core.RSourcesList;

import org.xml.sax.SAXException;

/**
 * @author Matthias Zschunke
 *
 */
@SuppressWarnings("serial")
public class SourceComponent 
extends AbstractProblemDialogComponent
{
    //data
    private RSourcesList sources;
    
    //components
    private JList sourceList;
    private JTextArea descriptionArea;
    
    private static final int SPACE=5;
    private static final int BUTTON_SPACE=3;
    
    public SourceComponent()
    {
        super(BoxLayout.X_AXIS);
        setName("Sources");
        
        compose();
    }
    
    private void compose()
    {
        removeAll();        
        
        setBorder(
            BorderFactory.createEmptyBorder(SPACE,SPACE,SPACE,SPACE)
        );
        
        //initialize sources
        sourceList=new JList();
        descriptionArea=new JTextArea(10,60);
        sourceList.setPrototypeCellValue("123456789012345678901234567890");
        sourceList.addListSelectionListener(new DescriptionListener());
        sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sources=readSources();
        sourceList.setListData(sources.toArray());
        
        //set selected index
        int index=RDefaults.getPrefs().getInt(
            RDefaults.Prefs.LASTSOURCESELECTIONINDEX_KEY,
            RDefaults.Prefs.LASTSOURCESELECTIONINDEX_DEFAULT        
        );
        if(index>=sources.size())
        {
            index=0;
        }
        sourceList.setSelectedIndex(index);
        
        //popup menu for 
        sourceList.addMouseListener(new SourcePopupMenu());
        
        
        //initialize description
        descriptionArea.setEditable(false);
        sourceList.addListSelectionListener(new DescriptionListener());
        
        //initialize buttons
        Box buttonBox=Box.createHorizontalBox();
        buttonBox.add(new JButton(new AddAction()));
        buttonBox.add(Box.createHorizontalStrut(BUTTON_SPACE));
        buttonBox.add(new JButton(new RemoveAction()));
        buttonBox.add(Box.createHorizontalStrut(BUTTON_SPACE));
        buttonBox.add(new JButton(new EditAction()));
        buttonBox.add(Box.createHorizontalStrut(BUTTON_SPACE));

        
        Box descriptionBox=Box.createVerticalBox();
        descriptionBox.add(new JScrollPane(
            descriptionArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ));
        descriptionBox.add(Box.createVerticalStrut(SPACE));
        descriptionBox.add(buttonBox);
        
        //add the components
        add(new JScrollPane(
            sourceList,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ));
        add(Box.createHorizontalStrut(SPACE));
        add(descriptionBox);        
    }

    /**
     * Initialize the list of sources with the sources stored in the
     * Preferences.
     * <br>
     * The source file names are read from the Preferences and the
     * RSources are created via the constructor.
     * @see <tt>mayday.interpreter.rinterpreter.RSource</tt>.
     * 
     * @return list of RSources
     * @throws BackingStoreException
     */
    private RSourcesList readSources() //throws BackingStoreException
    {
        Preferences prefs=RDefaults.getPrefs().node(
            RDefaults.Prefs.SOURCES_NODE);
        
        RSourcesList list=new RSourcesList();
        String[] keys;

        keys = prefs.keys();

        
        for(int i=0; i!=keys.length; ++i)
        {
            String filename=prefs.get(keys[i],"");
            try
            {
                RSource src=new RSource(new File(filename), true); //081231 fb: made this silent
                list.add(src);
                
            }catch(FileNotFoundException ex)
            {
                prefs.remove(keys[i]);
                
            }catch(Exception ex)
            {
                ex.printStackTrace();
                
                RDefaults.messageGUI(
                  "Could not add R Source '"+(new File(filename).getName())+"'. \n\n"+
                    ex.getMessage(),RDefaults.Messages.Type.ERROR);                
            }
        }
        return list;
    }
    
    public String toString()
    {
        return RDefaults.Titles.SOURCES;
    }
       
    public ArrayList<Action> getOkActions()
    {
        return new ArrayList<Action>(Arrays.asList(
            new SaveAction()
        ));
    }
    
    public void initializeSources() {
    	// get old sources list
    	try {
    	  sources=readSources();
    	} catch (Throwable t) {
    		System.err.println("RPlugin: Could not read sources list from Prefs");
    		sources= new RSourcesList();
    	};
    	// add all files that are missing in the list
		for (FMFile rfile : PluginManager.getInstance().getFilemanager().getFiles(".*[.]R", true)) {
			// get corresponding xml file
			String xmlName = rfile.Path+"/"+rfile.Name.replace(".R", ".xml");
			FMFile xmlfile = PluginManager.getInstance().getFilemanager().getFile(xmlName);
			if (xmlfile!=null) { 
				/* make sure all required files are extracted on disk, to keep external files in synch with 
				most recent repository version, overwrite local changes */ 			
				rfile.force_extract();  
				xmlfile.force_extract();
	            RSource src;
				try {
					src = new RSource(new File(rfile.getFullPath()),true);
					if (!sources.contains(src))
						sources.add(src);
				} catch (Exception e) {
					System.err.println("RPlugin:SourceComponent:initializeSources: "+rfile.getFullPath()+"\n"
							+e.getMessage());
				}
			}
		}
		try {
			new SaveAction().actionPerformed(null);
		} catch (Throwable t ){
			// whatever, keep old prefs
		}
    }
    
    private class SaveAction extends AbstractAction
    {

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        /**
         * Save the sources list in the BackingStore and in the
         * Mayday-prefs file.
         */
        public void actionPerformed(ActionEvent event)
        {
            //save the sources list
            Preferences prefs=RDefaults.getPrefs().node(RDefaults.Prefs.SOURCES_NODE);

            Iterator<RSource> iter=sources.iterator();
            
            HashSet<String> sourceDescs = new HashSet<String>();
            
            //System.err.println("Saving sources!!");
            while(iter.hasNext()) {
            	RSource src=(RSource)iter.next();
            	//System.err.println("Saving source: "+src.getFilename());
            	prefs.put(src.getDescriptor(),src.getFilename());
            	sourceDescs.add(src.getDescriptor());
            }  
            
            for (StorageNode pnode : prefs.getChildren())
            	if (!(sourceDescs.contains(pnode.Name)))
            		prefs.remove(pnode.Name);

        }        
    }
    

    
    /**
     * Add an RSource to the sources list and handle the exceptions
     * that may occur.
     * 
     * @author Matthias
     *
     */
    private class AddAction extends AbstractAction
    {
        AddAction()
        {
            super(RDefaults.ActionNames.ADD);
        }
        
        public void actionPerformed(ActionEvent event)
        {
            String lastSrcDir=RDefaults.getPrefs().get(RDefaults.Prefs.LASTSRCDIR_KEY,
                                        RDefaults.Prefs.LASTSRCDIR_DEFAULT);
            
            JFileChooser fileDlg=new JFileChooser(lastSrcDir);
            fileDlg.setFileFilter(new RFileFilter());  
            fileDlg.setMultiSelectionEnabled(true); 
            fileDlg.addActionListener(new LastOpenDirListener());       
            
            int returnVal=fileDlg.showOpenDialog((Component)event.getSource());
            if(returnVal==JFileChooser.APPROVE_OPTION)
            {
                File[] files=fileDlg.getSelectedFiles();
                for(int i=0; i!=files.length; ++i)
                {
                    try
                    {
                        RSource src=new RSource(files[i]);
                        sources.add(src); 
                    }catch(IOException ioEx)
                    {
                        ioEx.printStackTrace();
                        
                        RDefaults.messageGUI(
                            "Could not add '"+files[i].getName()+"'. \n\n"+
                            ioEx.getMessage(), RDefaults.Messages.Type.ERROR
                        );
                    }catch(SAXException e)
                    {
                        //RDefaults.messageGUI(e.getMessage(),RDefaults.Messages.Type.ERROR);                       
                        
                        Object[] options={RDefaults.ActionNames.SKIP,
                                          RDefaults.ActionNames.EDIT,
                                          RDefaults.ActionNames.DELETE};
                        
                        int result =JOptionPane.showOptionDialog(
                            getParent(),
                            "Could not read R Description File '"+
                            RSource.getRSDescFileName(files[i].getName())+
                            "'.\n\n"+
                            e.getMessage(),
                            RDefaults.Titles.XMLPARSEEXCEPTIONDIALOG,
                            JOptionPane.DEFAULT_OPTION,  //optionType
                            JOptionPane.ERROR_MESSAGE,
                            null,                       //ICON
                            options,                    //Object[] options
                            RDefaults.ActionNames.EDIT  //initial value
                        );
                        
                        File descFile=new File(RSource.getRSDescFileName(files[i].getAbsolutePath()));
                        
                        if(result==1) //EDIT
                        {
                            //Start the DescriptionEditorDialog, maybe: here is a little problem, cause src==null if we are here                          
                            RDefaults.startEditor(descFile);
                        } else if(result==2) //DELETE
                        {
                            descFile.delete();
                        } //else: SKIP or Closed
                        
                        RDefaults.messageGUI("The R Source '"+files[i].getName()+"' was not added!",RDefaults.Messages.Type.INFO);
                    }catch(RuntimeException REx)
                    {
                        REx.printStackTrace();
                        
                        RDefaults.messageGUI(
                            "Could not add R Source '"+files[i].getName()+"'.\n\n"+
                            REx.getMessage(),RDefaults.Messages.Type.ERROR
                        );
                    }
                                     
                }
                sourceList.setListData(sources.toArray());
                sourceList.setSelectedIndex(sources.size()-1);
            }       
        }
    }
    
    /**
     * Remove the selected RSource from the list of sources.
     * 
     * @author Matthias
     *
     */
    private class RemoveAction extends AbstractAction
    {
        public RemoveAction()
        {
            super(RDefaults.ActionNames.REMOVE);
        }
        
        public void actionPerformed(ActionEvent event)
        {
            int index=sourceList.getSelectedIndex();
            if(index!=-1 && index<sources.size())
            {
                sources.remove(index);
                sourceList.setListData(sources.toArray());
                sourceList.setSelectedIndex((index==0)?0:index-1);
                if(sources.size()==0)
                    sourceList.clearSelection();
            }else
            {
                RDefaults.messageGUI(
                    "There was no selected Item. Nothing removed.",
                    RDefaults.Messages.Type.INFO);
            }
        }
    }
    
    
    
    /**
     * Sorting the RSources list.
     * @author Matthias
     *
     */
    private class SortAction extends AbstractAction
    {
        public SortAction()
        {
            super(RDefaults.ActionNames.SORT);
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0)
        {
            if(sources.size()==0) return;
            
            String cursor = ((RSource)sourceList.getSelectedValue()).getDescriptor();
            
            sources.sort();            
            sourceList.setListData(sources.toArray());
            
            int i = sources.findFirst(cursor);
            if(cursor!=null && i>=0)
                sourceList.setSelectedIndex(i);   
        }
    }
    
    /**
     * Open the 
     * @see <tt>DescriptionEditorDialog</tt>
     * and read the R source description file again to make
     * the changes available.
     * 
     * @author Matthias
     *
     */
    private class EditAction extends AbstractAction
    {
        public EditAction()
        {
            super(RDefaults.ActionNames.EDIT);
            
            if(!RDefaults.DEBUG)
            {
                this.setEnabled(false);
            }else
            {
                this.setEnabled(true);
            }
        }
        
        public void actionPerformed(ActionEvent event)
        {   
            RSource src=((RSource)sourceList.getSelectedValue());
            
            try
            {
                DescriptionEditorDialog editor=new DescriptionEditorDialog(
                    //dialog,
                    RDefaults.MAYDAY_FRAME(),
                    src);
                int result=editor.showDialog();
                if(result==RDefaults.Actions.OK)
                {
                    // reinit the source:
                    try
                    {
                        src=new RSource(new File(src.getFilename()));
                    }catch(Exception ex)
                    {
                        if(RDefaults.DEBUG) ex.printStackTrace();
                        RDefaults.messageGUI(
                            ex.getMessage(),
                            RDefaults.Messages.Type.ERROR);
                    }
                }   
                
                descriptionArea.setText(src.getInfo());
                descriptionArea.setCaretPosition(0);
            }catch(RuntimeException ex)
            {
                RDefaults.messageGUI(
                    ex.getMessage(),
                    RDefaults.Messages.Type.ERROR
                );
            }
        }
    }
   

    private class SourcePopupMenu 
    extends JPopupMenu 
    implements MouseListener
    {        
        public SourcePopupMenu()
        {
            super();
            
            add(new AddAction());
            add(new RemoveAction());
            add(new EditAction());
            add(new JPopupMenu.Separator());
            add(new SortAction());  
        }
               
        public void mousePressed(MouseEvent e)
        {
            popup(e);
        }
        
        private void popup(MouseEvent e)
        {
            if(!e.isConsumed())
            {
                e.consume();
                if ( e.isPopupTrigger() )
                {
                    show( sourceList, e.getX(), e.getY() );
                }
            }
        }

        public void mouseClicked(MouseEvent arg0)
        {}
        public void mouseReleased(MouseEvent e)
        {
            popup(e);
        }
        public void mouseEntered(MouseEvent arg0)
        {}
        public void mouseExited(MouseEvent arg0)
        {}

    }
    
    /**
     * Listener that updates the 
     * @see <tt>mayday.interpreter.rinterpreter.RInterpreterPanel.descriptionArea</tt>
     * field to show the description of the currently selected RSource. 
     * 
     * @author Matthias
     *
     */
    private class DescriptionListener 
    implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent event)
        {
            int index=sourceList.getSelectedIndex();
            if(index!=-1 && index<sources.size())
            {
                RSource src=(RSource)sourceList.getSelectedValue();
                String text=src.getInfo();
                descriptionArea.setText(text);
                descriptionArea.setCaretPosition(0);
                
                //save the this description to the preferences store
                RDefaults.getPrefs().putInt(
                    RDefaults.Prefs.LASTSOURCESELECTIONINDEX_KEY,
                    index
                );                
            }else
            {
                descriptionArea.setText("");
            }
        }
    }
    
    public RSource getSelectedSource()
    {
        return (RSource)sourceList.getSelectedValue();
    }
}
