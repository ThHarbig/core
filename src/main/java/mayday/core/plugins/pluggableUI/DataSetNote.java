package mayday.core.plugins.pluggableUI;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.DelayedUpdateTask;
import mayday.core.Mayday;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public class DataSetNote extends AbstractPlugin implements GenericPlugin {

	protected static Component myComponent;
	boolean notext;
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(), 
				"PAS.pluggableUI.datasetNote", 
				null, 
				Constants.MC_PLUGGABLEVIEWS, 
				null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de",
				"Displays dataset-associated editable notes",
				"DataSet note area"
		);
	}

	@Override
	public void run() {
		if (myComponent==null)
			myComponent = new JScrollPane(new NoteElement());
		Mayday.sharedInstance.addPluggableViewElement(myComponent, "DataSet notes");
	}

	@SuppressWarnings("serial")
	protected class NoteElement extends JEditorPane implements ListSelectionListener, DocumentListener {
		
		protected DelayedUpdateTask storer = new DelayedUpdateTask("Store dataset note", 1000) {
		
			@Override
			protected void performUpdate() {
				storeContent();
			}
		
			@Override
			protected boolean needsUpdating() {
				return true;
			}
		};
		
		protected DataSet assoc; 
		
		public NoteElement() {
			loadContent();	
			addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent evt) {
				if (getText().trim().length()==0 && !notext) {
					setText("Click here to enter notes about the current dataset");
					notext=true;
				}
				}
				public void focusGained(FocusEvent evt) {
					if (notext) {
						setText("");
						notext=false;
					}
				}
			});
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			loadContent();
		}
		
		public synchronized void storeContent() {
			String textToStore = getText().trim();
			if (notext)
				textToStore="";
			if (assoc!=null) {
				assoc.getAnnotation().setInfo(textToStore);
			}
		}
		
		public synchronized void loadContent() {
			List<DataSet> sel = DataSetManagerView.getInstance().getSelectedDataSets();
			storeContent();
			notext=false;
			if (sel.size()!=1) {
				setText("< No dataset selected >");
				assoc = null;
				setEnabled(false);
			} else {
				assoc = sel.get(0);
				String s = assoc.getAnnotation().getInfo();
				if (s.trim().length()==0) {
					s = "Click here to enter notes about the current dataset";
					notext=true;
				} 
				setText(s);
				setEnabled(true);
			}
			
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			storer.trigger();			
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			storer.trigger();			
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			storer.trigger();			
		}
		
		public void addNotify() {
			getDocument().addDocumentListener(this);
			DataSetManagerView.getInstance().addSelectionListener(this);
			super.addNotify();
		}
		
		public void removeNotify() {
			getDocument().removeDocumentListener(this);
			DataSetManagerView.getInstance().removeSelectionListener(this);
			super.removeNotify();
		}

	}
}
