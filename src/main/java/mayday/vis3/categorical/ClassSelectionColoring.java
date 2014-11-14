package mayday.vis3.categorical;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.ClassSelectionModel;
import mayday.core.EventFirer;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ClassSelectionColoring extends HashMap<Object, Color> implements CategoricalColoring {

	protected ViewModel viewModel;
	protected String source;
	protected ClassSelectionModel csm;
	
	protected EventFirer<ChangeEvent, ChangeListener> firer = new EventFirer<ChangeEvent, ChangeListener>() {
		protected void dispatchEvent(ChangeEvent event, ChangeListener listener) {
			listener.stateChanged(event);
		}
	};
	
	protected CategoricalAssignmentComponent categoricalAssignmentComponent;
	
	public ClassSelectionColoring(ClassSelectionModel csm, ViewModel vm, String source) {
		viewModel=vm;
		this.source=source;
		this.csm = csm;
		for (String className:csm.getClassNames()) {
			put(className,csm.getColorForClass(className));
		}
	}

	@Override
	public void replaceCategoricalColor(Object category, Color c) {
		put(category, c);
		firer.fireEvent(new ChangeEvent(this));
	}

	@Override
	public int getNumberOfCategories() {		
		return size();
	}

	@Override
	public Set<Entry<Object, Color>> getCategoricalColoring() {
		return entrySet();
	}

	@Override
	public ViewModel getViewModel() {
		return viewModel;
	}

	@Override
	public String getSourceName() {
		return source;
	}

	public Color getColorForObject(String name) {		
		Color c = get(csm.getClassOf(name));
		if (c==null)
			c=Color.white;
		return c;
	}

	public void showColorAssignmentWindow() {
		if (size()==0)
			return;
		if (categoricalAssignmentComponent==null)
			categoricalAssignmentComponent = new CategoricalAssignmentComponent(this);
		categoricalAssignmentComponent.showWindow(); 					
	}
	
	public void hideColorAssignmentWindow() {
		if (categoricalAssignmentComponent!=null)
			categoricalAssignmentComponent.hideWindow();
	}
	
	public void addListener(ChangeListener cl) {
		firer.addListener(cl);
	}
}
