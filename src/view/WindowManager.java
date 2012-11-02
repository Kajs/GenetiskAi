package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import model.HeapSort;

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
	    
	   JMenuItem newRound = new JMenuItem("New round");
	   newRound.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.gameState.newRound();
	    	}
	    });
	   
	   JMenuItem simulateTen = new JMenuItem("10 rounds");
	   simulateTen.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		int x = 0;
	    		while (x<10) {
		    		Controller.gameState.newRound();
		    		x++;
	    		}
	    	}
	    });
	   
	   JMenuItem newBestTeamGame = new JMenuItem("Chose best team");
	   newBestTeamGame.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String str = JOptionPane.showInputDialog(null, "Game Number : ", "New Best Team Game", 1);
	    		if(str != null) {
	    			int bestTeam = new Integer(str);
	    			Controller.newBestTeamGame(bestTeam - 1);
	    		}
	    	}
	    });
	   
	   JMenuItem runBestTeamGames = new JMenuItem("Run best team games");
	   runBestTeamGames.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.runBestTeamGames = true;
	    	}
	    });
	   
	   JMenuItem sortBestTeamsLowToHigh = new JMenuItem("Sort best teams (low to high)");
	   sortBestTeamsLowToHigh.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		HeapSort.heapSortLow(Controller.bestTeams[0], copyArrayList(Controller.bestTeamsFitness));
	    		HeapSort.heapSortLow(Controller.bestTeams[1], copyArrayList(Controller.bestTeamsFitness));
	    		HeapSort.heapSortLow(Controller.bestTeams[2], Controller.bestTeamsFitness);
	    	}
	    });
	   
	   JMenuItem sortBestTeamsHighToLow = new JMenuItem("Sort best teams (high to low)");
	   sortBestTeamsHighToLow.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		HeapSort.heapSortHigh(Controller.bestTeams[0], copyArrayList(Controller.bestTeamsFitness));
	    		HeapSort.heapSortHigh(Controller.bestTeams[1], copyArrayList(Controller.bestTeamsFitness));
	    		HeapSort.heapSortHigh(Controller.bestTeams[2], Controller.bestTeamsFitness);
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
	   
	   JMenuItem toggleActionOutput = new JMenuItem("Toggle action output");
	   toggleActionOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowActionOutput = !(Launcher.allowActionOutput);
	    	}
	    });
	   
	   JMenuItem toggleBestTeamsFitnessOutput = new JMenuItem("Toggle best teams fitness output");
	   toggleBestTeamsFitnessOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowBestTeamsFitnessOutput = !(Launcher.allowBestTeamsFitnessOutput);
	    	}
	    });
	   
	   JMenuItem toggleHpOutput = new JMenuItem("Toggle hp output");
	   toggleHpOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowHpOutput = !(Launcher.allowHpOutput);
	    	}
	    });
	   
	   JMenuItem toggleShieldOutput = new JMenuItem("Toggle shield output");
	   toggleShieldOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowShieldOutput = !(Launcher.allowShieldOutput);
	    	}
	    });
	   
	   JMenuItem toggleStunOutput = new JMenuItem("Toggle stun output");
	   toggleStunOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowStunOutput = !(Launcher.allowStunOutput);
	    	}
	    });
	   
	   JMenuItem pause = new JMenuItem("Pause");
	   pause.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.isPaused = !(Launcher.isPaused);
	    	}
	    });
	   
	   JMenuItem toggleRoundSeparator = new JMenuItem("Toggle round separator");
	   toggleRoundSeparator.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.toggleRoundSeparator = !(Launcher.toggleRoundSeparator);
	    	}
	    });
	   
	   JMenuItem printLine = new JMenuItem("Print new line");
	   printLine.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		System.out.println();
	    	}
	    });
	   
	   JMenuItem toggleRoundDelay = new JMenuItem("Toggle round delay");
	   toggleRoundDelay.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowRoundDelay = !(Launcher.allowRoundDelay);
	    	}
	    });
	   
	   JMenuItem stop = new JMenuItem("Stop");
	   stop.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.stop = true;
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
	   automatic.add(runBestTeamGames);
	   automatic.add(sortBestTeamsLowToHigh);
	   automatic.add(sortBestTeamsHighToLow);
	   
	   JMenu output = new JMenu("Output");
	   output.add(toggleActionOutput);
	   output.add(toggleBestTeamsFitnessOutput);
	   output.add(toggleHpOutput);
	   output.add(toggleShieldOutput);
	   output.add(toggleStunOutput);
	   output.add(toggleRoundSeparator);
	   output.add(printLine);
	   JMenuBar menuBar = new JMenuBar();
	   
	   JMenu speed = new JMenu("Speed");
	   speed.add(fast);
	   speed.add(normal);
	   speed.add(slow);
	   speed.add(pause);
	   speed.add(stop);
	   speed.add(setSpeed);
	   speed.add(toggleRoundDelay);
	   
	   menuBar.add(manual);
	   menuBar.add(automatic);
	   menuBar.add(output);
	   menuBar.add(speed);
	   frame.setJMenuBar(menuBar);
	   
	    
	    frame.setVisible(true);
	}
	
	public ArrayList<Double> copyArrayList(ArrayList<Double> orgArrayList) {
		ArrayList<Double> copy = new ArrayList<Double>();
		for (int i = 0; i < orgArrayList.size(); i++) {
			copy.add(orgArrayList.get(i));
		}
		return copy;
	}
}
