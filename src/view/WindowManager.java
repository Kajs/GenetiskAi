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
import javax.swing.JOptionPane;

import view.BoardRenderer;

import control.Controller;
import control.Launcher;

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
	   
	   JMenuItem simulateTen = new JMenuItem("10 Rounds");
	   simulateTen.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		int x = 0;
	    		while (x<10) {
		    		Controller.gameState.newRound();
		    		x++;
	    		}
	    	}
	    });
	   
	   JMenuItem newBestTeamGame = new JMenuItem("Chose Best Team");
	   newBestTeamGame.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String str = JOptionPane.showInputDialog(null, "Game Number : ", "New Best Team Game", 1);
	    		if(str != null) {
	    			int bestTeam = new Integer(str);
	    			Controller.newBestTeamGame(bestTeam - 1);
	    		}
	    	}
	    });
	   
	   JMenuItem setSpeed = new JMenuItem("Set speed");
	   setSpeed.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String str = JOptionPane.showInputDialog(null, "Time in milliseconds", "Automatic mode with visual", 1);
	    		if(str != null) {
	    			int roundDelay = new Integer(str);
	    			Controller.roundDelay = roundDelay;
	    		}
	    	}
	    });
	   
	   JMenuItem toggleActionOutput = new JMenuItem("Toggle Action Output");
	   toggleActionOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowActionOutput = !(Launcher.allowActionOutput);
	    	}
	    });
	   
	   JMenuItem toggleRoundDelay = new JMenuItem("Toggle Round Delay");
	   toggleRoundDelay.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowRoundDelay = !(Launcher.allowRoundDelay);
	    	}
	    });
	   
	   JMenuItem pause = new JMenuItem("Pause");
	   pause.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.isPaused = !(Launcher.isPaused);
	    	}
	    });
	   
	   JMenuItem fast = new JMenuItem("Fast");
	   fast.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.roundDelay = 100;
	    	}
	    });
	   
	   JMenuItem normal = new JMenuItem("Normal");
	   normal.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.roundDelay = 1000;
	    	}
	    });
	   
	   JMenuItem slow = new JMenuItem("Slow");
	   slow.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.roundDelay = 5000;
	    	}
	    });
	   
	   JMenu manual = new JMenu("Manual");
	   manual.add(newRound);
	   manual.add(simulateTen);
	   
	   JMenu automatic = new JMenu("Automatic");
	   automatic.add(newBestTeamGame);
	   
	   JMenu output = new JMenu("Output");
	   output.add(toggleActionOutput);
	   JMenuBar menuBar = new JMenuBar();
	   
	   JMenu speed = new JMenu("Speed");
	   speed.add(fast);
	   speed.add(normal);
	   speed.add(slow);
	   speed.add(pause);
	   speed.add(setSpeed);
	   speed.add(toggleRoundDelay);
	   
	   menuBar.add(manual);
	   menuBar.add(automatic);
	   menuBar.add(output);
	   menuBar.add(speed);
	   frame.setJMenuBar(menuBar);
	   
	    
	    frame.setVisible(true);
	}
	
}
