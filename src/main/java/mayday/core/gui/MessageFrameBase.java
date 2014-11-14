package mayday.core.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

@SuppressWarnings("serial")
public class MessageFrameBase extends MaydayFrame
{
	protected JTextPane textArea;
	
	public MessageFrameBase(String title)
	{
		super();
		setTitle(title);
		compose();
	}
	
	public OutputStream addOutputStream(Color col) {
		return new MessageOutputStream(textArea, col);
	}

	protected void compose()
	{
		getContentPane().add(new MessageComponent());
		pack();

		setSize(new Dimension(
				getContentPane().getPreferredSize().width + getInsets().left + getInsets().right,
				getContentPane().getPreferredSize().height + getInsets().top + getInsets().bottom
		));
		
	}

	protected class MessageComponent
	extends Box
	{
		public MessageComponent()
		{
			super(BoxLayout.Y_AXIS);

			textArea = new JTextPane();
			textArea.setEditable(false);
			textArea.setPreferredSize(new Dimension(
					Math.min(1000, Toolkit.getDefaultToolkit().getScreenSize().width*3/4),
					Math.min(700, Toolkit.getDefaultToolkit().getScreenSize().height*1/2)
			));

			add(new JScrollPane(
					textArea,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
			));
		}

		public JTextPane getTextArea()
		{
			return textArea;
		}

	}



	public static class MessageOutputStream
	extends OutputStream
	{
		/*
		 * a few settings
		 */
		private MutableAttributeSet attributes = new SimpleAttributeSet();  
		private JTextPane textArea;
		//private Color color;


		/*
		 * internal buffer to collect some bytes before creating a String
		 */
		private byte[] buffer;

		/*
		 * buffer end pointer
		 */
		private int end = 0; //buffer end pointer

		public void scroll() {
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}

		public MessageOutputStream(JTextPane textArea, int bufferLength, Color color)
		{
			super();

			StyleConstants.setForeground(attributes, color);
			StyleConstants.setFontFamily(attributes, "Monospaced");

			this.textArea = textArea;

			this.buffer = new byte[bufferLength];
		}

		public MessageOutputStream(JTextPane textArea, int bufferLength)
		{
			this(textArea, bufferLength, Color.BLACK);
		}

		public MessageOutputStream(JTextPane textArea, Color color)
		{
			this(textArea, 80, color);
		}


		public MutableAttributeSet getAttributes()
		{
			return attributes;
		}

		public void setAttributes(MutableAttributeSet attributes)
		{
			this.attributes = attributes;
		}

		public JTextComponent getTextArea()
		{
			return textArea;
		}

		public void setTextPane(JTextPane textArea)
		{
			this.textArea = textArea;
		}

		public void write(int b) throws IOException
		{	
			if( end==buffer.length )
			{
				flush();
			}

			buffer[end++]=(byte)b;
			scroll();
		}


		/*
		 * The following implementation omits calling the method write(int)
		 * on each byte. 
		 * 
		 */
		public void write(byte[] b, int off, int len) throws IOException
		{
			if(b==null) return;
			if(off<0 || len<0 || off+len>b.length) return;

			synchronized(b)
			{
				flush();
				try
				{
					flushDocument(b, off, len);

				}catch(Exception ex)
				{
					JOptionPane.showMessageDialog(null,ex.getClass().getName() + ": " + ex.getMessage());
				}
			}
		}

		protected void flushDocument(byte[] b, int off, int len)
		throws Exception
		{
			synchronized (textArea)
			{   
				textArea.getDocument().insertString(
						textArea.getDocument().getLength(),
						new String(b,off,len),
						attributes
				);
				scroll();
			}
		}

		public void flush() throws IOException
		{
			synchronized (buffer)
			{
				try
				{				
					if(end!=0) flushDocument(buffer, 0, end);

				}catch(Exception ex)
				{
					JOptionPane.showMessageDialog(null,ex.getClass().getName() + ": " + ex.getMessage());
				}			

				//reset the buffer end pointer
				end = 0;
			}
		}

		public synchronized void setBufferSize(int newSize)
		throws IOException
		{
			flush();            
			this.buffer = new byte[newSize];
		}
	}

}
