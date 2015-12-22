package mayday.core.settings;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import mayday.core.DelayedUpdateTask;
import mayday.core.EventFirer;
import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public abstract class AbstractSetting implements Setting {
	
	protected String name;
	protected String description;
	
	protected EventFirer<SettingChangeEvent, SettingChangeListener> eventfirer = new EventFirer<SettingChangeEvent, SettingChangeListener>() {
		protected void dispatchEvent(SettingChangeEvent event, SettingChangeListener listener) {
			listener.stateChanged(event);
		}
	};
	
	public AbstractSetting(String Name, String Description) {
		name=Name;
		description=Description;
		if (MaydayDefaults.isDebugMode())
			checkClonability(this);
	}	
	
	public AbstractSetting clone() {
		return reflectiveClone();
	}
	
	protected final AbstractSetting reflectiveClone() {
		AbstractSetting clone;
		// begin reflective cloning procedure, if the child class has an empty constructor
		Class<? extends AbstractSetting> clazz = getClass();
		try {
			Constructor<? extends AbstractSetting> ctr = clazz.getConstructor();
			clone = ctr.newInstance();
			clone.fromPrefNode(this.toPrefNode());
//			System.out.println("Successfully reflectively cloned "+clone.getClass());
			return clone;
		} catch (Exception e) {}
		throwCloneException(false);
		return null; // never reached
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String newDescription) {
		description = newDescription;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return getName();
	}


	public boolean fromPrefNode(Preferences prefNode) {
		setValueString(prefNode.Value);
		return true;
	}

	public Preferences toPrefNode() {
		Preferences myNode = Preferences.createUnconnectedPrefTree(getName(), getValueString());
		return myNode;
	}

	public String getValidityHint() {
		return "Your input is not a valid value for \""+getName()+"\"";
	}

	public void addChangeListener(SettingChangeListener changeListener) {
		eventfirer.addListener(changeListener);
	}

	public void removeChangeListener(SettingChangeListener changeListener) {
		eventfirer.removeListener(changeListener);		
	}
	
	protected void fireChanged() {
		fireChanged(new SettingChangeEvent(this));
	}
	
	protected void fireChanged(SettingChangeEvent e) {
		eventfirer.fireEvent(e);
	}

	public Component getMenuItem( final Window parent ) {
		return new SettingDialogMenuItem( this, parent );
	}
	
	
	
	// ============== CLONE CHECKING =============================
	protected final static Set<Class<? extends AbstractSetting>> clonableClasses = new HashSet<Class<? extends AbstractSetting>>();
	protected final static LinkedList<AbstractSetting> objectsInCheck = new LinkedList<AbstractSetting>();
	
	protected final void throwCloneException(boolean withMessage) {
		if (withMessage)
			JOptionPane.showMessageDialog(null, "The "+getClass()+" does not fulfill the requirements for Setting.clone()!\n" +
					"Please see AbstractSetting.checkClonability() for an explanation.", "Missing implementation of clone()",JOptionPane.ERROR_MESSAGE);
		
		throw new RuntimeException("The "+getClass()+" does not fulfill the requirements for Setting.clone()!\n" +
				"Please see AbstractSetting.checkClonability() for an explanation.");
	}
	
	protected final static void checkClonability( AbstractSetting sobj ) {
		/*
		 * ALL setting classes MUST provide a clone function that can create a clone OF THE SAME class.
		 * Example: "Class X extends AbstractSetting" means that X.clone() returns an instance of
		 *          class X and NOT an instance of AbstractSetting.
		 * AbstractSetting tries to implement a generic clone function using reflection. This works if
		 * the derived class has a constructor with no parameters. If your derived class has an empty constructor,
		 * your clone function can look like this (if you don't already inherit it directly from AbstractSetting):
		 * public myType clone() {
		 *   return (myType)reflectiveClone();
		 * }
		 */
		synchronized ( clonableClasses ) {
			if (clonableClasses.contains(sobj.getClass()))
				return;
		}		
		synchronized ( objectsInCheck ) {
			objectsInCheck.add(sobj);
		}
		cloneabilityChecker.trigger();
	}
	
	protected static final DelayedUpdateTask cloneabilityChecker = new DelayedUpdateTask("Settings implementation checker",1000) {

		@Override
		protected boolean needsUpdating() {
			synchronized ( objectsInCheck ) {
				return !objectsInCheck.isEmpty();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void performUpdate() {
			
			while (needsUpdating()) {
				AbstractSetting input;
				synchronized ( objectsInCheck ) {
					input = objectsInCheck.remove(0);
				}
				
				Class<? extends AbstractSetting> thisClass = input.getClass();
				
				boolean toDo = false;
				synchronized ( clonableClasses ) {
					toDo = !clonableClasses.contains(thisClass);
				}		
				
				if (toDo) {
//					System.out.println("Checking Settings Class "+thisClass.getCanonicalName());

					final Class<? extends AbstractSetting>[] clonedClass = new Class[1];
					final AbstractSetting input_ = input;

					try {
						SwingUtilities.invokeAndWait(new Runnable() {

							public void run() {
								clonedClass[0] = input_.clone().getClass();
							}
							
						});
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (!(thisClass==clonedClass[0]))
						input.throwCloneException(true);
					synchronized ( clonableClasses ) {
						clonableClasses.add(thisClass);
					}
				}
			}
			
		}
		
	};
	
}
