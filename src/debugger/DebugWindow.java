package debugger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DebugWindow {
	final static JFrame frame = new JFrame("Debugger");
	final static JScrollPane scrPane;
	final static JTextArea text = new JTextArea();
	final static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	
	static {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(275, screenSize.height-200);
		frame.setLocation(0,100);
		scrPane = new JScrollPane(text);
		frame.getContentPane().add(scrPane);
		
		text.setBackground(new Color(230,230,230));
		text.setEditable(false);
	}
	
	public static void show() {
		frame.setVisible(true);
	}
	
	public static void hide() {
		frame.setVisible(false);
	}
	
	public static void destroy () {
		frame.dispose();
	}
	
	public static void clear () {
		text.setText("");
	}
	
	public static void write (String classAndMethod, String str) {
		text.append("\n"+classAndMethod+"\n"+"--------------------------\n");
		text.append(str);
	}
}
