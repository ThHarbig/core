package mayday.mushell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import mayday.core.LastDirListener;
import mayday.core.MaydayDefaults;
import mayday.core.gui.MaydayFrame;
import mayday.mushell.dispatch.DispatchEvent;
import mayday.mushell.dispatch.DispatchListener;
import mayday.mushell.dispatch.Dispatcher;
import mayday.mushell.history.HistoryField;
import mayday.mushell.queue.CommandQueue;
import mayday.mushell.queue.CommandQueueIndicator;
import mayday.mushell.snippet.SnippetEvent;
import mayday.mushell.snippet.SnippetField;
import mayday.mushell.snippet.SnippetListener;

@SuppressWarnings("serial")
public class Console extends MaydayFrame implements SnippetListener, DispatchListener {
	
	public final static String CONSOLE_SUBMENU = "Interactive Consoles";
	
	protected InputField inputField;
	protected OutputField outputField;
	protected Dispatcher dispatcher;
	protected HistoryField historyField;
	protected DispatcherStateIndicator dispatchIndicator;
	protected CommandQueue queue;
	protected CommandQueueIndicator queueIndicator;
	protected LinkedList<SnippetField> snippetFields;
	protected JScrollPane inputSP;
	
	protected JTabbedPane snippetPane;
	
	private boolean initDone = false;
	
	public Console(String title) {
		inputField = new InputField(this);
		outputField = new OutputField();
		historyField = new HistoryField();
		historyField.addSnippetListener(this);
		queue = new CommandQueue();
		dispatchIndicator = new DispatcherStateIndicator();
		queueIndicator = new CommandQueueIndicator(queue);
		setTitle(title);
		snippetFields = new LinkedList<SnippetField>();
		snippetPane = new JTabbedPane(); 
	}
	
	public void setVisible(boolean vis) {
		init();
		super.setVisible(vis);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				inputField.requestFocusInWindow();		
			}
		});
	}
	
	public void setDispatcher(Dispatcher d) {
		dispatcher = d;
		d.connect(outputField);
		d.addDispatchListener(dispatchIndicator);
		d.addDispatchListener(this);
		dispatchNext();
	}
	
	public InputField getInputField() {
		return inputField;
	}
	
	public HistoryField getHistoryField() {
		return historyField;
	}
	
	public void init() {
		if (initDone)
			return;
		
		inputSP = new JScrollPane(inputField);
		inputSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane outputSP = new JScrollPane(outputField);
		outputSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(inputSP, BorderLayout.CENTER);
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.add(dispatchIndicator, BorderLayout.WEST);
		infoPanel.add(queueIndicator, BorderLayout.EAST);
		
		southPanel.add(infoPanel, BorderLayout.NORTH);
		
		JSplitPane jinnersplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputSP, southPanel);
		jinnersplit.setResizeWeight(1.0); // all resizing affects only the output pane
		jinnersplit.setDividerLocation(.75);
		
		updateSnippets();
		
		JSplitPane jrightSplit  = new JSplitPane(JSplitPane.VERTICAL_SPLIT, historyField, snippetPane);
		jrightSplit.setResizeWeight(1.0); // all resizing affects only the history pane
		jrightSplit.setDividerLocation(0.5);
		jrightSplit.setOneTouchExpandable(true);
		
		JSplitPane joutersplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jinnersplit, jrightSplit);
		joutersplit.setResizeWeight(1.0); // all resizing affects only the output pane
		joutersplit.setDividerLocation(0.8);
		joutersplit.setOneTouchExpandable(true);
		
		add(joutersplit, BorderLayout.CENTER);
		
		
		JMenuBar mb = new JMenuBar();
		JMenu historyMenu = new JMenu("History");
		historyMenu.add(new LoadHistoryAction());
		historyMenu.add(new SaveHistoryAction());
		mb.add(historyMenu);
		setJMenuBar(mb);		
		
		pack();
		
		initDone = true;
	}

	public void addSnippetField(SnippetField sf) {
		snippetFields.add(sf);
		sf.addSnippetListener(this);
		updateSnippets();
	}
	
	public void removeSnippetField(SnippetField sf) {
		sf.removeSnippedListener(this);
		snippetFields.remove(sf);
		updateSnippets();
	}
	
	protected void updateSnippets() {
		snippetPane.removeAll();
		for (SnippetField sf : snippetFields) {
			snippetPane.add(sf.getComponent().getName(), sf.getComponent());
		}
	}

	public void snippetSelected(SnippetEvent event) {
		if (event.isReplacement())
			inputField.replaceContent(event.getSnippet());
		else
			inputField.insertSnippet(event.getSnippet());
	}
	
	public synchronized void dispatch(final String command) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				queue.addElement(command);
				historyField.push(command);
				dispatchNext();
			}

		});
	}
	
	protected synchronized void dispatchNext() {
		if (dispatcher!=null && dispatcher.ready()) {
			if (queue.size()>0) {
				String nextCommand = (String)queue.get(0);
				queue.remove(0);
				outputField.print(getPrompt());
				outputField.printWithHighlight(nextCommand, inputField.getTokenizer(), inputField.getSyntaxHighlighter());
				outputField.print("\n");
				dispatcher.dispatchCommand(nextCommand);
			}
		}
	}
	
	public boolean idle() {
		return (dispatcher.ready() && queue.isEmpty()); 
	}
	
	public String getPrompt() {
		return "> ";
	}

	public void dispatching(DispatchEvent evt) {
		switch (evt.getWhen()) {
		case NOW_BUSY:
			inputSP.setBorder(BorderFactory.createLineBorder( Color.red, 2));
			break;
		case NOW_READY:
			inputSP.setBorder(BorderFactory.createLineBorder( Color.green, 2));
			dispatchNext();	
			break;
		}				
	}
	
	
	public class LoadHistoryAction extends AbstractAction {

		public LoadHistoryAction() {
			super("Load History");
		}
		
		public void actionPerformed( ActionEvent event ) 
		{
			String l_defaultFileName = dispatcher.getName()+"_history.txt";
			
			JFileChooser l_chooser=new JFileChooser();
			l_chooser.addActionListener(new LastDirListener());
			String s_lastExportPath=
				MaydayDefaults.Prefs.NODE_PREFS.get(
						MaydayDefaults.Prefs.KEY_LASTOPENDIR,
						MaydayDefaults.Prefs.DEFAULT_LASTOPENDIR
				);
			if(!s_lastExportPath.equals(""))
				l_chooser.setCurrentDirectory(new File(s_lastExportPath));
			l_chooser.setSelectedFile( new File( l_defaultFileName ) );
			int l_option = l_chooser.showOpenDialog( (Component)event.getSource() );

			if ( l_option  == JFileChooser.APPROVE_OPTION ) {
				String l_fileName = l_chooser.getSelectedFile().getAbsolutePath();
				MaydayDefaults.s_lastExportPath = l_chooser.getCurrentDirectory().getAbsolutePath();
				if ( l_fileName == null )
					return;
								
				try {
					historyField.loadFromFile(l_fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}		
	}
	
	public class SaveHistoryAction extends AbstractAction {

		public SaveHistoryAction() {
			super("Save History");
		}
		
		public void actionPerformed( ActionEvent event ) 
		{
			String l_defaultFileName = dispatcher.getName()+"_history.txt";
			
			JFileChooser l_chooser=new JFileChooser();
			l_chooser.addActionListener(new LastDirListener());
			String s_lastExportPath=
				MaydayDefaults.Prefs.NODE_PREFS.get(
						MaydayDefaults.Prefs.KEY_LASTSAVEDIR,
						MaydayDefaults.Prefs.DEFAULT_LASTSAVEDIR
				);
			if(!s_lastExportPath.equals(""))
				l_chooser.setCurrentDirectory(new File(s_lastExportPath));
			l_chooser.setSelectedFile( new File( l_defaultFileName ) );
			int l_option = l_chooser.showSaveDialog( (Component)event.getSource() );

			if ( l_option  == JFileChooser.APPROVE_OPTION ) {
				String l_fileName = l_chooser.getSelectedFile().getAbsolutePath();
				MaydayDefaults.s_lastExportPath = l_chooser.getCurrentDirectory().getAbsolutePath();
				if ( l_fileName == null )
					return;
				
				if (new File(l_fileName).exists() && 
					JOptionPane.showConfirmDialog(null, 
							"Do you really want to overwrite the existing file \""+l_fileName+"\"?",
							"Confirm file overwrite", 
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
							!=JOptionPane.YES_OPTION
				) {
						return;
				}
				
				try {
					historyField.saveToFile(l_fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}		
	}
	
	
	public class DispatcherStateIndicator extends JLabel implements DispatchListener {

		public void dispatching(DispatchEvent evt) {
			String sourceN = evt.getSource().getName();
			switch (evt.getWhen()) {
			case NOW_BUSY:
				setText(sourceN, true);
				break;
			case NOW_READY:
				setText(sourceN, false);
			}		
		}
		
		protected void setText(String sourceN, boolean busy) {
			setText(sourceN+" is "+(busy?"busy":"ready"));
		}
		
		public DispatcherStateIndicator() {
			setText("Console",false);
		}

	}

}
