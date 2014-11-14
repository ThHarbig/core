package mayday.interpreter.rinterpreter.gui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import mayday.core.gui.MaydayDialog;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RFunctionParser;
import mayday.interpreter.rinterpreter.core.RFunctionParser.RFunction;

/**
 * Parse an R source file for function declarations
 * and let the user choose one.
 * 
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class FunctionChooserDialog extends MaydayDialog
{
	private File sourceFile;
	private JList funList;
	private JButton selectButton;
	private JButton cancelButton;
	private ArrayList<RFunction> functions;
	private String description;
    		
	private int result=RDefaults.Actions.CANCEL;
	private RFunction function;
	private RFunctionParser funParser;
	
	/**
	 * Constructor.
	 * 
	 * @param owner
	 * @param f, R source file
	 * @throws IOException
	 */
	public FunctionChooserDialog(Window owner, File f) throws IOException
	{
		super(owner);
		sourceFile=f;
		this.setTitle(RDefaults.Titles.FUNCTIONCHOOSER+"; "+sourceFile.getName());
		init();
	}
	
	/**
	 * Constructor.
	 * 
	 * 
	 * @param owner
	 * @param f
	 * @throws IOException
	 */
	/*
	public FunctionChooserDialog(Dialog owner, File f) throws IOException
	{
		super(owner);
		sourceFile=f;
		this.setTitle(RDefaults.Titles.FUNCTIONCHOOSER+"; "+sourceFile.getName());
		init();
	}//*/
    		
	private void init() throws IOException
	{
		funList=new JList();
		funList.setPrototypeCellValue("0123456789 123456789 123456789 123456789 123456789");
		funList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		selectButton=new JButton(new SelectAction());
		cancelButton=new JButton(new CancelAction());
    			
		//create the content
		JPanel panel=new JPanel();
    			
		Box sourceButtonBox=Box.createVerticalBox();
		sourceButtonBox.setBorder(BorderFactory.createEmptyBorder(5,10,5,5));
		sourceButtonBox.add(cancelButton);
		sourceButtonBox.add(selectButton);

		Box sourceBox=Box.createHorizontalBox();
		sourceBox.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Functions"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
		);
		sourceBox.add(new JScrollPane(funList));
		sourceBox.add(sourceButtonBox);
				
				
		//parse the sourceFile and add the functions found to the sourceList
		RFunctionParser parser=new RFunctionParser(sourceFile);
		functions=parser.parse();
		description=parser.parseDescription();
		
		JPanel text=new JPanel();
		text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS));
		text.add(new JLabel(
				RDefaults.FULLNAME
					+ " will create the description file "
					+"for '"
					+ sourceFile.getName() + "'. "
		));
		text.add(new JLabel(
			"Choose an applicable function." 
		));
		text.add(new JLabel(
			" "
		));

		funList.setListData(functions.toArray(new RFunction[functions.size()]));
		funList.setSelectedIndex(0);
		
		Box globalBox=Box.createVerticalBox();
		globalBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		Box textBox=Box.createHorizontalBox();
		textBox.add(text);
		textBox.add(Box.createHorizontalGlue());
		globalBox.add(textBox);		
		globalBox.add(sourceBox);
		
		panel.add(globalBox);
		
		this.setResizable(false);
		
		this.getContentPane().add(panel);
		
		pack();
		
		Dimension dim=new Dimension(getContentPane().getPreferredSize());
		this.setSize((int)dim.getWidth(),(int)dim.getHeight()+30);
		this.setModal(true);		
	}
	
	/**
	 * Show the dialog and return the return value
	 * if any action disposes it.
	 * 
	 * @return the return value
	 */
	public int showDialog()
	{
		// 081231 show only if there is more than one function to select from
		if (functions.size()>1) {
			this.setVisible(true);
			return result;
		} else {
			return 0;
		}
	}
	
	
	/**
	 * Get the RFunction.
	 * @return function
	 */
	public RFunction getRFunction()
	{
		return function;
	}
	
	public RFunctionParser getRFunctionParser()
	{
		return this.funParser;
	}
    		
	/**
	 * Function selected action.
	 * The test, whether the selected function satisfies the
	 * "applicable function"-definition is not jet implemented.
	 * The dialog will be disposed.
	 * 
	 * @author Matthias
	 *
	 */
	private class SelectAction extends AbstractAction
	{
		public SelectAction()
		{
			super(RDefaults.ActionNames.SELECT);
		}
    			
		public void actionPerformed(ActionEvent event)
		{
			function=(RFunction)funList.getSelectedValue();
			
			if(function==null)
			{
				RDefaults.messageGUI(
					"Select a function!",
					RDefaults.Messages.Type.ERROR
				);
			}else
			{
				result=RDefaults.Actions.SELECT;
				dispose();
			}
		}
	}
    		
	/**
	 * Set the result of this dialog to <tt>RDefaults.Actions.CANCEL</tt>
	 * and dispose the dialog.
	 * 
	 * @author Matthias
	 *
	 */
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super(RDefaults.ActionNames.CANCEL);
		}
    			
		public void actionPerformed(ActionEvent event)
		{
			result=RDefaults.Actions.CANCEL;
			dispose();
		}
	}
	
	
	/**
	 * Get the description of the RFunction object.
	 * This has been initialized with a comment satisfying the
	 * "function description comment" specification.
	 * <br>
	 * See <tt>RFunctionParser.parseDescription</tt>
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}
}
