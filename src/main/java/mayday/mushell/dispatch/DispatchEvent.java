package mayday.mushell.dispatch;

import java.util.EventObject;

@SuppressWarnings("serial")
public class DispatchEvent extends EventObject {

	public enum event {BEFORE_DISPATCH, AFTER_DISPATCH, NOW_BUSY, NOW_READY};
	
	protected event when;
	protected String command;
	protected Boolean result;
	
	public DispatchEvent(Dispatcher source, event When, String Command, Boolean Result) {
		super(source);
		when=When;
		command=Command;
		result=Result;
	}

	public event getWhen() {
		return when;
	}

	public String getCommand() {
		return command;
	}

	public Boolean getResult() {
		return result;
	}
	
	public Dispatcher getSource() {
		return (Dispatcher)super.getSource();
	}
	
}
