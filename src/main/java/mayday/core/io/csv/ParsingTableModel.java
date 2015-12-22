package mayday.core.io.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import mayday.core.DelayedUpdateTask;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskStateEvent;

@SuppressWarnings("serial")
public class ParsingTableModel extends AbstractTableModel {

	protected ParserSettings parserSettings = new ParserSettings();
	private int colCount = -1;
	
	protected List<ParsedLine> lines = Collections.synchronizedList(new ArrayList<ParsedLine>());
	protected List<ParsedLine> linesWithoutComments = Collections.synchronizedList(new ArrayList<ParsedLine>());
	
	private List<TableModelListener> listeners = Collections.synchronizedList(new LinkedList<TableModelListener>());
	
	private DelayedUpdateTask parserTask = new DelayedUpdateTask("Parsing CSV file", 200) {

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			if (parserThread!=null) { 
				if (parserThread.getTaskState()==TaskStateEvent.TASK_RUNNING) {
					parserThread.cancel();
					parserThread.waitFor();
				}
			}
			parserThread = new ParserThread();
			parserThread.start();
			parserThread.waitFor();
		}
		
	}; 
	
	private ParserThread parserThread = null;
	
	public Object getValueAt(int row, int column) {
		int theRow = mapRowIndex(row);
		ParsedLine theLine = linesWithoutComments.get(theRow);
		return theLine.get(column);
	}
	
	protected long fileSize=0, readSize; // for estimating progress;
	
	public ParsingTableModel(File f) throws FileNotFoundException {
		this(new FileInputStream(f), f.length());
	}
	
	public ParsingTableModel(InputStream is) {
		this(is, 0);
	}
	
	protected boolean parseCondition(BufferedReader bfr) throws Exception{
		return bfr.ready();
	}
	
	protected ParsingTableModel() {}
	
	public ParsingTableModel(InputStream is, long streamSize) {
		doParse(is, streamSize);
	}
	
	protected void doParse(InputStream is, long streamSize) {
		final BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
		fileSize = streamSize;
		AbstractTask readFile = new AbstractTask("Reading file") {

			@Override
			protected void doWork() throws Exception {
				setProgress(-1);
				try {
					while (parseCondition(bfr)) {		
						String nextLine = bfr.readLine();
						readSize += nextLine.length();
						lines.add(new ParsedLine(nextLine, parserSettings));
						int count = lines.size();
						if (count%100==0) {
							if (fileSize!=0)
								setProgress((int)((readSize*10000)/fileSize), count+" lines");
							else 
								setProgress(0, count+" lines");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void initialize() {}

		};
		readFile.start();
		readFile.waitFor();
	}

	private int mapRowIndex(int row) {
		return row+parserSettings.skipLines+(parserSettings.hasHeader?1:0);
	}
	
	private void reparse() {
		parserTask.trigger();
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		if (colCount==-1) {
			return 0;
		}
		return colCount;
	}

	public String getColumnName(int columnIndex) {
		int headerIndex = mapRowIndex(0)-1;
		
		String name=""; 
		
		if (parserSettings.hasHeader && headerIndex<linesWithoutComments.size()) {
			ParsedLine pl = linesWithoutComments.get(headerIndex);
			
			// test for r-style header lines (missing the first separator char)
			if (pl.size()==getColumnCount()-1) {
				if (columnIndex==0) 
					name="Probe ID";
				else 
					name=pl.get(columnIndex-1);
			} else {
				name=pl.get(columnIndex);
			}
		} 
		if (name==null || name.trim().length()==0)
			name = "X"+columnIndex;
			
		return name;
	}

	public int getRowCount() {
		return (linesWithoutComments.size())-parserSettings.skipLines-(parserSettings.hasHeader?1:0);
	}

	public int getFileRowCount() {
		return lines.size();
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}
	
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);	
	}
	
	public void fireTableModelChanged(TableModelEvent tme) {
		for (TableModelListener tml : listeners) {
			tml.tableChanged(tme);
		}
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		throw new RuntimeException("FlexibleTableModel does not allow modification.");		
	}
	
	private class ParserThread extends AbstractTask {

		public ParserThread() {
			super("Parsing file");	
		}
		
		@Override
		protected void doWork() throws Exception {
			setColumnCount(-1);
			linesWithoutComments.clear();
			int totalRows = lines.size();

			for (int i=0; i!=totalRows && !hasBeenCancelled(); ++i) {
				//reparse the current line (if needed)
				ParsedLine pl = lines.get(i);
				// if this line is not a comment
				if (!pl.isCommentLine()) {// this triggers reparse 
					// update column count
					setColumnCount(Math.max(colCount, pl.size()));
					addLineToModel(pl);
					if (linesWithoutComments.size()==parserSettings.skipLines+1) {
						// header may need to be updated
						fireTableModelChanged(new TableModelEvent(ParsingTableModel.this, TableModelEvent.HEADER_ROW));
					}
				}				
				setProgress((i*10000)/totalRows);
			}
			if (hasBeenCancelled())
				processingCancelRequest();
		}

		@Override
		protected void initialize() {
					
		}
		
	};
	
	private void setColumnCount(int columnCount) {
		if (colCount!=columnCount) {
			colCount = columnCount;
			fireTableModelChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
		}
	}
	
	private void addLineToModel(ParsedLine pl) {
		int startindex = linesWithoutComments.size();
		linesWithoutComments.add(pl);
		fireTableModelChanged(new TableModelEvent(this, startindex, startindex+1, 
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}
	
	private void invalidateParsedLines() {
		parserSettings.version++;
	}
	
	
	public void setSkipLines(int skipLines) {
		if (parserSettings.skipLines!=skipLines) {			
		  parserSettings.skipLines = skipLines;
		  reparse();
		}
	}
	
	public void setHasHeader(boolean hasHeader) {
		if (parserSettings.hasHeader!=hasHeader) {
			parserSettings.hasHeader=hasHeader;
			fireTableModelChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
		}
	}
	
	public void setCommentChars(String commentChars) {
		if (!parserSettings.commentChars.equals(commentChars)) {
			parserSettings.commentChars = commentChars;
			invalidateParsedLines();
			reparse();
		}
	}
	
	public void setSeparatorChars(String separatorChars) {
		if (!parserSettings.separator.equals(separatorChars)) {
			parserSettings.separator = separatorChars;
			invalidateParsedLines();
			reparse();
		}
	}
	
	public void setQuoteChar(char quoteChar) {
		if (parserSettings.quote!=quoteChar) {
			parserSettings.quote = quoteChar;
			invalidateParsedLines();
			reparse();
		}
	}
	
	public void setEverything(char quoteChar, String separatorChars, String commentChars, int skipLines, boolean hasHeader) {
		parserSettings.quote=quoteChar;
		parserSettings.separator=separatorChars;
		parserSettings.commentChars=commentChars;
		parserSettings.skipLines=skipLines;
		parserSettings.hasHeader=hasHeader;
		invalidateParsedLines();
		reparse();
	}
	
	public void finish() {
		// let the parser finish
		if (parserThread!=null && parserThread.getTaskState()==TaskStateEvent.TASK_RUNNING) 
			parserThread.waitFor();
		
		// make sure everything is parsed correctly		
		if (parserThread==null || parserThread.getTaskState()==TaskStateEvent.TASK_FAILED) {
			System.out.println("Restarting parser.");
			reparse();
			finish();
		}
	}
	
	public void dropComments() {
		lines.clear();
		lines = null;
	}
	
	public void dropRow(int row) {
		row = mapRowIndex(row);
		linesWithoutComments.set(row, null);
	}
}
