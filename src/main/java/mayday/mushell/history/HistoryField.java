package mayday.mushell.history;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

import mayday.core.EventFirer;
import mayday.core.structures.StringListModel;
import mayday.mushell.snippet.SnippetEvent;
import mayday.mushell.snippet.SnippetField;
import mayday.mushell.snippet.SnippetListener;

@SuppressWarnings("serial")
public class HistoryField extends JScrollPane implements SnippetField {

	protected StringListModel commandStack;
	protected int currentStackPosition = -2;
	protected String lastSnippet;
	protected JList list;

	public HistoryField() {
		commandStack = new StringListModel();
		list = new JList(commandStack);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON1) {
					lastSnippet = (String)list.getSelectedValue();
					eventfirer.fireEvent(new SnippetEvent(HistoryField.this,lastSnippet,true));
				}
			}
		});
		setViewportView(list);
	}

	protected int lastIndex() {
		return commandStack.size()-1;
	}
	
	public void push(String command) {
		if (!command.equals(lastSnippet)) {
			if (commandStack.size()>0 && commandStack.get(lastIndex()).equals(command))
				return;
			if (commandStack.size()>1 && commandStack.get(commandStack.size()-2).equals(command)
					&& commandStack.get(lastIndex()).length()==0)
				return;
			// The current command is always put in the top stack position
			if (commandStack.size()>0 && commandStack.get(lastIndex()).trim().length()==0)
				commandStack.remove(lastIndex());
			commandStack.add(command);
			commandStack.add("");
			currentStackPosition = lastIndex();
			selectAndScroll(currentStackPosition);
		}
	}
	
	private void selectAndScroll(int idx)
	{
		list.setSelectedIndex(idx);
		list.ensureIndexIsVisible(idx);
	}

	public String getPrevious(String current) {
		// store current editing buffer as replacement of the current command position
		if (current.trim().length()>0)
			commandStack.set(currentStackPosition, current);
		--currentStackPosition;
		if (currentStackPosition<0) { // reached bottom end of stack
			currentStackPosition=0;
			// insert empty command before first if not already done
			if (commandStack.size()==0 || commandStack.get(0).trim().length()>0)
				commandStack.add(0, "");
		}
		selectAndScroll(currentStackPosition);
		return commandStack.get(currentStackPosition);
	}

	public String getNext(String current) {
		// store current editing buffer as replacement of the current command position
		if (current.trim().length()>0)
			commandStack.set(currentStackPosition, current);
		++currentStackPosition;
		if (currentStackPosition>=commandStack.size()) { // reached top end of stack
			// insert new element at the end of the stack if not already done
			if (commandStack.size()==0 || commandStack.get(lastIndex()).trim().length()>0) {
				commandStack.add("");
			}				
			currentStackPosition = lastIndex();
		} 
		selectAndScroll(currentStackPosition);
		return commandStack.get(currentStackPosition);
	}

	public EventFirer<SnippetEvent, SnippetListener> eventfirer = new EventFirer<SnippetEvent, SnippetListener>() {
		protected void dispatchEvent(SnippetEvent event, SnippetListener listener) {
			listener.snippetSelected(event);
		}		
	};

	public void addSnippetListener(SnippetListener l) {
		eventfirer.addListener(l);		
	}

	public void removeSnippedListener(SnippetListener l) {
		eventfirer.removeListener(l);
	}

	public JComponent getComponent() {
		return this;
	}
	
	public void saveToFile(String filename) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		for (int i=0; i!=commandStack.size(); ++i) {
			String s = commandStack.get(i);
			s.replace("\\", "\\\\");
			s.replace("\n", "\\n");
			bw.write(s+"\n");
		}
		bw.flush();
		bw.close();
	}
	
	public void loadFromFile(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		commandStack.clear();		
		while (br.ready()) {
			String line = br.readLine();
			line.replace("\\n", "\n");
			line.replace("\\\\","\\");
			commandStack.add(line);
		}
		currentStackPosition = lastIndex();
	}

}
