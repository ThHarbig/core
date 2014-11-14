package mayday.core.tasks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskManager;
import mayday.core.tasks.TaskStateEvent;

@SuppressWarnings("serial")
public class TaskManagerFrame extends MaydayDialog {
	
	private static TaskManagerFrame singleInstance;
	
    private LinkedList<AbstractTask> keptTasks=new LinkedList<AbstractTask>();
    
    public static TaskManagerFrame getInstance() {
    	if (singleInstance==null)
    		singleInstance = new TaskManagerFrame();
    	return singleInstance;
    }
	
	private JPanel taskPane=new JPanel();
    
    /**
     * Constructs a new TaskManagerDialog containing a
     * ScrollPanel with the tasks rendering components and
     * a &quot;Close&quot; button.
     *
     */
    public TaskManagerFrame()
    {
//        super(Mayday.sharedInstance);
              
        setTitle(TaskManager.TITLE);
        
        Box taskBox=Box.createVerticalBox();
        taskBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));            
        
        taskPane.setLayout(
            new ExcellentBoxLayout(true, 5) //(taskPane,BoxLayout.Y_AXIS)
        );
        taskPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
 
        taskPane.setPreferredSize(taskPane.getMinimumSize());
        taskPane.setBackground(Color.white);
        
        JScrollPane scroll=new JScrollPane(taskPane);
        
        taskBox.add(scroll);
        taskBox.validate();
        
        getContentPane().add(taskBox); 
        
        JButton clearList = new JButton("Clear finished tasks");
        clearList.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		keptTasks.clear();
        		TaskManager.sharedInstance.removeFinished();
        		updateTasklist();
        	}
        });
        taskBox.add(Box.createVerticalStrut(4));
        
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(clearList);
        
        taskBox.add(box);
        
        updateTasklist();
                    
        this.setMinimumSize(new Dimension(600,400));
        this.pack();
        
    }
    
    public void removeTask(AbstractTask task, boolean force) {
    	if (force) {
    		keptTasks.remove(task);
    	} else {
    		if (task.getTaskState() != TaskStateEvent.TASK_FINISHED || task.getGUI().hasLog())
    			if (!keptTasks.contains(task)) 
    				keptTasks.addFirst(task);
    	}
    }
    
    private void addTaskToPane(AbstractTask at) {
    	TaskStateGUI tsg = at.getGUI();
    	taskPane.add(tsg);
//		taskPane.add(Box.createVerticalStrut(5));
//		taskPane.add(new JSeparator());
//		taskPane.add(Box.createVerticalStrut(5));
		tsg.revalidate();
    }
   
    
    private void updateTasklist_internal() {
    	        	
		taskPane.removeAll();
		List<AbstractTask> tasks = TaskManager.sharedInstance.getTasks();
    	
    	// newest task is always at the top
    	for (int i=tasks.size()-1; i>=0; --i) {
    		addTaskToPane(tasks.get(i));
    	}
    	// keep all tasks that failed in the list 
    	for (AbstractTask at : keptTasks) {
    		addTaskToPane(at);    		
    	}
    	
    	if (tasks.isEmpty() && keptTasks.isEmpty()) {
    		taskPane.add(new JLabel("There are no tasks to display."));
    	} 
    	
    	taskPane.invalidate();
    	taskPane.validate();
    	pack();
    	repaint();
    	
        if(tasks.isEmpty() && keptTasks.isEmpty()) 
        	setVisible(false);
    }
    
    public synchronized void updateTasklist() {

    	EventQueue.invokeLater(new Runnable() {
    		public void run() { 
    			try {
    				updateTasklist_internal();
    			} catch (Throwable t) {
    			}
    		}
    	});
    		        	
    }
		

}
