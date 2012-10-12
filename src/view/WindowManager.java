package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import view.BoardRenderer;

import control.Controller;

public class WindowManager {
	final Controller controller;
	public WindowManager(int width, int height, BoardRenderer boardRenderer, Controller controller) {
		this.controller = controller;
		JFrame frame = new JFrame();
	    frame.setTitle("GenetiskAIRollespil");
	    frame.setSize(width, height);
	    frame.addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        System.exit(0);
	      }
	    });
	    
	    Container contentPane = frame.getContentPane();
	    contentPane.add(boardRenderer);	    
	    
	   JMenuItem newRound = new JMenuItem("New Round");
	   newRound.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.gameState.newRound();
	    	}
	    });
	   
	   JMenu Round = new JMenu("Round");
	   Round.add(newRound);
	   JMenuBar menuBar = new JMenuBar();
	   menuBar.add(Round);
	   frame.setJMenuBar(menuBar);
	   
	    
	    frame.setVisible(true);
	}
	
}
