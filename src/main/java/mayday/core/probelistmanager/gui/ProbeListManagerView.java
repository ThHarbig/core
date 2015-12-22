package mayday.core.probelistmanager.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import mayday.core.probelistmanager.ProbeListManager;

public interface ProbeListManagerView 
{

	public ProbeListManager getProbeListManager();
	public void setProbeListManager( ProbeListManager probeListManager );
	
	public JMenu getMenu();
	public JPopupMenu getPopupMenu();
	
	public AbstractAction getRemoveSelectionAction();	
	public AbstractAction getMoveUpAction();	
	public AbstractAction getMoveDownAction();
	
	public void updateMenu();
	public void updateCellRenderer();	
	
	public Component getComponent();
	
	public Container getActionComponent(); 
	
	public Object[] getSelectedValues();	
	public ListSelectionModel getSelectionModel();
	
	public ListModel getModel();
	
	public void addMouseListener(MouseListener list);
	public void removeMouseListener(MouseListener list);	
	
	public void selectIndex(int index);
	public void ensureIndexIsVisible(int index);

	public void addKeyListener(KeyListener kl);
}
