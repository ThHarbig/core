/*
 * Created on 29.11.2005
 */
package mayday.core.io.csv;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.gui.abstractdialogs.AbstractStandardDialogComponent;

/**
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 29.11.2005
 *
 */
@SuppressWarnings("serial")
public class CSVImportSettingComponent
extends AbstractStandardDialogComponent
{
    private static final String SKIP_LINES_KEY = "skiplines";
    private static final String HAS_HEADER_KEY = "hasheader";
    private static final String COMMENT_CHAR_KEY = "commentchar";
    private static final String SEPARATORS_KEY = "separators";
    private static final String QUOTES_KEY = "quotes";
    
    private static Character[] QUOTES = new Character[] {'"','\'',' '};
    
    
    private static String tab_sep = "\t";
    private static String comma_sep = ",";
    private static String semi_sep = ";";
    private static String space_sep = " ";
    private String other_sep = "";
    
    private final String[] separatorStrings = new String[]{tab_sep, comma_sep, semi_sep, space_sep};
    
    protected ArrayList<Action> actions = new ArrayList<Action>();
    
    protected int skipLines;
    protected boolean hasHeader;
    protected String commentChar;    
    protected String separators;
    protected char quotes; 
    
    private ParsingTableModel theTableModel;
    
    protected ArrayList<String> readers = new ArrayList<String>();
    
    /**
     * @param direction
     */
    public CSVImportSettingComponent(ParsingTableModel tableModel)
    {
        super(BoxLayout.Y_AXIS);        
        theTableModel = tableModel;
        initFromDefaults();
        compose();
    }
    
    public CSVImportSettingComponent(ParsingTableModel tableModel, ParserSettings settings) {
    	 super(BoxLayout.Y_AXIS);         
         theTableModel = tableModel;
         initFromSettings(settings);
         compose();
    }
    
    
    protected void initFromDefaults() {
    	   /*
         * read out properties
         */
        Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs();
        prefs = prefs.node(this.getClass().getSimpleName());
        skipLines = prefs.getInt(SKIP_LINES_KEY, 0);
        hasHeader = prefs.getBoolean(HAS_HEADER_KEY, false);
        commentChar = prefs.get(COMMENT_CHAR_KEY, "#");
        quotes = prefs.get(QUOTES_KEY, ""+QUOTES[0]).charAt(0); 
        separators = prefs.get(SEPARATORS_KEY, tab_sep);
        for (int i=0; i!=separatorStrings.length; ++i)
        	if (separatorStrings[i].equals(separators))
        		return;
        other_sep = separators;
    }
    
    protected void initFromSettings(ParserSettings settings) {
    	skipLines = settings.skipLines;
    	hasHeader = settings.hasHeader;
    	commentChar = settings.commentChars;
    	separators = settings.separator;
    	other_sep = separators;
    	quotes = settings.quote;
    }

    /**
     * 
     */
    private void compose() 
    {
        removeAll();
        setName("Options ...");
        
        Box p = this; 
        p.setOpaque(true);
        p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        actions.clear();
        
        
        /*
         * skip
         */
        Box b = Box.createHorizontalBox();
        b.setBorder(BorderFactory.createCompoundBorder(            
            BorderFactory.createTitledBorder("Skip"),
            BorderFactory.createEmptyBorder(5,5,5,5)
        ));        
        JPanel b0 = new JPanel(); //Box.createVerticalBox();
        b0.setLayout(new GridLayout(3,2,10,2));
        
        b0.add(new JLabel("Skip Lines (before header)"));
        JSpinner skip = new JSpinner(new SpinnerNumberModel(skipLines,0,theTableModel.getFileRowCount(),1));
        skip.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            skip.getPreferredSize().height+2
        ));
        skip.getModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e)
            {   
                skipLines = (Integer)((SpinnerModel)e.getSource()).getValue();
            	theTableModel.setSkipLines(skipLines);
            }
        });
        b0.add(skip);
        
        b0.add(new JLabel("Has Header Line"));
        JCheckBox hasHeaderCheckBox = new JCheckBox("",hasHeader);
        hasHeaderCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e)
            {
                hasHeader = ((JCheckBox)e.getSource()).isSelected();
                theTableModel.setHasHeader(hasHeader);
            }});
        b0.add(hasHeaderCheckBox);
        
        b0.add(new JLabel("Comment Characters"));
        JTextField commentCharField = new JTextField(commentChar, 10);
        commentCharField.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            commentCharField.getPreferredSize().height+2
        ));
        commentCharField.getDocument().addDocumentListener(new DocumentListener() {
            private void update(DocumentEvent e)
            {
                try
                {
                    commentChar = e.getDocument().getText(0,e.getDocument().getLength());
                    theTableModel.setCommentChars(commentChar);
                    
                }catch(BadLocationException ex)
                {
                    ex.printStackTrace();
                }
            }
            
            public void insertUpdate(DocumentEvent e)
            {
                update(e);
            }

            public void removeUpdate(DocumentEvent e)
            {
                update(e);
            }

            public void changedUpdate(DocumentEvent e)
            {}    
        });
        b0.add(commentCharField);
        
        b.add(b0);
        b.add(Box.createHorizontalGlue());
        p.add(b);
        p.add(Box.createVerticalStrut(5));
        
        /*
         * separators
         */
        b = Box.createHorizontalBox();
        b.setBorder(BorderFactory.createCompoundBorder(            
            BorderFactory.createTitledBorder("Separators"),
            BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        b0 = new JPanel(); //Box.createHorizontalBox();
        b0.setLayout(new GridLayout(4,2,10,3));
        
        ButtonGroup separatorGroup = new ButtonGroup();
        final JRadioButton[] separatorChoices = new JRadioButton[4];        
        
        separatorChoices[0] = new JRadioButton("Tabulator");
        separatorChoices[1] = new JRadioButton("Comma");
        separatorChoices[2] = new JRadioButton("Semicolon");
        separatorChoices[3] = new JRadioButton("Space");
        
        
        final ItemListener separatorListener = new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				int i=0; 
				separators = null;
				for (i=0; i!=separatorChoices.length; ++i)
					if (separatorChoices[i].isSelected())
						separators = separatorStrings[i];
				if (separators == null)
					separators = other_sep;
				theTableModel.setSeparatorChars(separators);
			}
        	
        };
        
        for (int i=0; i!=separatorChoices.length; ++i) {
        	separatorGroup.add(separatorChoices[i]);
        	b0.add(separatorChoices[i]);
        	separatorChoices[i].addItemListener(separatorListener);
        }
        
        
        
        JRadioButton tmp = new JRadioButton("Other (regular expression)", other_sep.length()>0);        
        separatorGroup.add(tmp);
        final JTextField sepField = new JTextField("",10);
        sepField.setText(other_sep);
        sepField.setEnabled(tmp.isSelected());
        tmp.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e)
            {
                boolean b = ((JRadioButton)e.getSource()).isSelected();
                if (b) 
                	other_sep=sepField.getText();
                else 
                	other_sep="";                
                sepField.setEnabled(b);
                separatorListener.itemStateChanged(null);
            }});
        b0.add(tmp);
        
        sepField.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            sepField.getPreferredSize().height+2
        ));
        sepField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e)
            {}
            public void focusLost(FocusEvent e)
            {
            	other_sep=sepField.getText();
            	theTableModel.setSeparatorChars(other_sep);  
            	/*
                    Document d = ((JTextField)e.getSource()).getDocument(); 
                    otherSep = d.getText(0,d.getLength());*/
            }});        
        b0.add(sepField);
        
        boolean bc = true;
        for (int i=0; i!=separatorChoices.length; ++i) {
        	if (separators.equals(separatorStrings[i])) {
        		separatorChoices[i].setSelected(true);
        		bc = false;
        	}
        }
        if (bc)
        	tmp.setSelected(true);
        	
        
        
        b0.add(new JLabel("Quotes"));
        JComboBox combo = new JComboBox(QUOTES);
        combo.setEditable(false);
        combo.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            combo.getPreferredSize().height+2
        ));
        combo.setSelectedItem(quotes);
        combo.setRenderer(new DefaultListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);
                
                setText( value==null || ((Character)value)<=' '?
                        "<html><span style=\"font-style:italic\">None</span>" :
                        ""+value
                );
                
                return this;
            }
            
        });
        combo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e)
            {
                quotes  = (Character)e.getItem();
                theTableModel.setQuoteChar(quotes);
            }});
        b0.add(combo);
        
        b.add(b0);
        b.add(Box.createHorizontalGlue());
        
        p.add(b);
        p.add(Box.createVerticalStrut(3));
                
        
        /*
         * table
         */
        b = Box.createVerticalBox();
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Content"),
            BorderFactory.createEmptyBorder(5,5,5,5)
        ));
        JTable table = new JTable(theTableModel);
        
        table.setColumnModel(new DefaultTableColumnModel() {
            public void moveColumn(int columnIndex, int newIndex)
            {/*do nothing*/}

            public void addColumn(TableColumn aColumn)
            {
                super.addColumn(aColumn);
                aColumn.setMinWidth(100);
            }
            
            
        });
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {   
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                this.setText("<html>"+
                    (value==null || value.toString().equalsIgnoreCase("NA") ||value.toString().length()==0?
                        "<span style=\"color:#ff7f7f;\">NA</span>": //font-style:italic;
                        value.toString()
                ));
                return this;
            }});
        
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        JScrollPane scroll = new JScrollPane(
            table, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            Toolkit.getDefaultToolkit().getScreenSize().height/4
        ));
        scroll.setPreferredSize(new Dimension(
            scroll.getPreferredSize().width,
            scroll.getMaximumSize().height
        ));
        b.add(scroll);
        //b.add(progressBox);
        
        
        p.add(b);
        p.add(Box.createVerticalGlue());
        
        theTableModel.setEverything(quotes, separators, commentChar, skipLines, hasHeader);
        
        /*
         * actions
         */   
        actions.add(new AbstractAction() {
            public void actionPerformed(ActionEvent e)
            {
                putPreferences();
                // make sure everything is parsed correctly
                theTableModel.finish();
            }});
    }    
   

    private void putPreferences()
    {
        Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs();
        prefs = prefs.node(this.getClass().getSimpleName());
        
        prefs.put(SKIP_LINES_KEY, ""+skipLines);
        prefs.put(HAS_HEADER_KEY, ""+hasHeader);
        prefs.put(COMMENT_CHAR_KEY, commentChar);
        prefs.put(SEPARATORS_KEY,""+separators);
        prefs.put(QUOTES_KEY, ""+quotes);
    }

    /* (non-Javadoc)
     * @see mayday.core.gui.AbstractStandardDialogComponent#getOkActions()
     */
    public ArrayList<Action> getOkActions()
    {
        return actions;
    }
        
    /**
     * Returns the resulting table model containing the values
     * as String.
     * 
     * @return the resulting table model or null if the component has been canceled
     */
    public TableModel getTableModel()
    {
        return this.theTableModel;
    }

    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
    }

}
