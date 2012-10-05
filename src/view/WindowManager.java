package view;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class WindowManager {
	
	public WindowManager(int width, int height, BoardRenderer board) {
		JFrame frame = new JFrame();
	    frame.setTitle("GenetiskAIRollespil");
	    frame.setSize(width, height);
	    frame.addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        System.exit(0);
	      }
	    });
	    
	    Container contentPane = frame.getContentPane();
	    contentPane.add(board);
	    
	    frame.setVisible(true);
	}
	
}
