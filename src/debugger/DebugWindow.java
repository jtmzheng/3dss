package debugger;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class DebugWindow {
	final static JFrame frame = new JFrame("Debugger");
	final static JTextArea text = new JTextArea();
	final static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	static {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(300, screenSize.height);
		frame.add(text);
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
		text.append(classAndMethod +": " + str);
	}
}
