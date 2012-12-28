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
import javax.swing.WindowConstants;

import model.HeapSort;

import view.BoardRenderer;

import control.Controller;
import control.Launcher;

public class WindowManager {
	final Controller controller;
	final HeapSort heapSort = new HeapSort();
	
	
	public WindowManager(int width, int height, BoardRenderer boardRenderer, Controller controller) {
		this.controller = controller;
		final JFrame frame = new JFrame();
	    frame.setTitle("GenetiskAIRollespil");
	    frame.setSize(width, height);
	    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    frame.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		boolean previousState = Launcher.isPaused;
	    		Launcher.isPaused = true;
	    		
                int confirm = JOptionPane.showOptionDialog(frame,
                        "Are You Sure to Close this Application?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) { System.exit(1); }
                
                Launcher.isPaused = previousState;
            }
	    });
	    
	    Container contentPane = frame.getContentPane();
	    contentPane.add(boardRenderer);
	   
	   JMenuItem newBestTeamGame = new JMenuItem("Run single best team game");
	   newBestTeamGame.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String str = JOptionPane.showInputDialog(null, "Game Number : ", "New Best Team Game", 1);
	    		if(str != null && !str.equals("") && checkIntString(str)) {
	    			int bestTeam = new Integer(str);
	    			Controller.singleBestTeamNumber = bestTeam - 1;
	    			Controller.runSingleBestTeamGame = true;
	    		}
	    	}
	    });
	   
	   JMenuItem runBestTeamGames = new JMenuItem("Run all best team games");
	   runBestTeamGames.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.runBestTeamGames = true;
	    	}
	    });
	   
	   JMenuItem jumpToBestTeam = new JMenuItem("Jump to best team");
	   jumpToBestTeam.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String str = JOptionPane.showInputDialog(null, "Best team number", "New best team", 1);
	    		if(str != null && !str.equals("") && checkIntString(str)) {
	    			int newBestTeam = new Integer(str);
	    			Launcher.switchBestTeamNumber = newBestTeam - 1;
	    			Launcher.switchBestTeam = true;
	    		}
	    	}
	    });
	   
	   JMenuItem sortBestTeamsLowToHigh = new JMenuItem("Sort best teams (low to high)");
	   sortBestTeamsLowToHigh.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		heapSort.heapSortLow(Controller.bestTeams, Controller.bestTeamsFitness, Controller.generationsCompleted);
	    	}
	    });
	   
	   JMenuItem sortBestTeamsHighToLow = new JMenuItem("Sort best teams (high to low)");
	   sortBestTeamsHighToLow.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		heapSort.heapSortHigh(Controller.bestTeams, Controller.bestTeamsFitness, Controller.generationsCompleted);
	    	}
	    });
	   
//______________________________Output
	   
	   JMenuItem toggleActionOutput = new JMenuItem("Game - Action");
	   toggleActionOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowActionOutput = !(Launcher.allowActionOutput);
	    	}
	    });
	   
	   JMenuItem toggleAreaDamageOutput = new JMenuItem("Game - Area damage");
	   toggleAreaDamageOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowAreaDamageOutput = !(Launcher.allowAreaDamageOutput);
	    	}
	    });
	   
	   JMenuItem toggleBoostOutput = new JMenuItem("Game - Boost");
	   toggleBoostOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowBoostOutput = !(Launcher.allowBoostOutput);
	    	}
	    });
	   
	   JMenuItem toggleBestTeamsFitnessOutput = new JMenuItem("Game - Fitness");
	   toggleBestTeamsFitnessOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowBestTeamsFitnessOutput = !(Launcher.allowBestTeamsFitnessOutput);
	    	}
	    });
	   
	   JMenuItem toggleHealOutput = new JMenuItem("Game - Heal");
	   toggleHealOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowHealOutput = !(Launcher.allowHealOutput);
	    	}
	    });
	   
	   JMenuItem toggleHpOutput = new JMenuItem("Game - Hp");
	   toggleHpOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowHpOutput = !(Launcher.allowHpOutput);
	    	}
	    });
	   
	   JMenuItem toggleNormalDamageOutput = new JMenuItem("Game - Normal damage");
	   toggleNormalDamageOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowNormalDamageOutput = !(Launcher.allowNormalDamageOutput);
	    	}
	    });
	   
	   JMenuItem toggleRoundSeparator = new JMenuItem("Game - Round separator");
	   toggleRoundSeparator.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.toggleRoundSeparator = !(Launcher.toggleRoundSeparator);
	    	}
	    });
	   
	   JMenuItem toggleShieldOutput = new JMenuItem("Game - Shield");
	   toggleShieldOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowShieldOutput = !(Launcher.allowShieldOutput);
	    	}
	    });
	   
	   JMenuItem toggleStunOutput = new JMenuItem("Game - Stun");
	   toggleStunOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowStunOutput = !(Launcher.allowStunOutput);
	    	}
	    });
	   
	   JMenuItem testAdjacentAiOutput = new JMenuItem("Test - Adjacent ais");
	   testAdjacentAiOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowAdjacentAiOutput = !(Launcher.allowAdjacentAiOutput);
	    	}
	    });
	   
	   JMenuItem testAngleOutput = new JMenuItem("Test - Angle");
	   testAngleOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowAngleOutput = !(Launcher.allowAngleOutput);
	    	}
	    });
	   
	   JMenuItem testFitnessSubsetOutput = new JMenuItem("Test - Fitness subset (scaled)");
	   testFitnessSubsetOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.testFitnessSubset = !(Launcher.testFitnessSubset);
	    	}
	    });
	   
	   JMenuItem testMutateLikelihoodOutput = new JMenuItem("Test - Mutate likelihood");
	   testMutateLikelihoodOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.testMutateLikelihood = !(Launcher.testMutateLikelihood);
	    	}
	    });
	   
	   JMenuItem testPrintCurrentGameOutput = new JMenuItem("Test - Print current game");
	   testPrintCurrentGameOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.testPrintCurrentGame = !(Launcher.testPrintCurrentGame);
	    	}
	    });
	   
	   JMenuItem testUnscaledFitnessOutput = new JMenuItem("Test - Unscaled fitness");
	   testUnscaledFitnessOutput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.testUnscaledFitness = !(Launcher.testUnscaledFitness);
	    	}
	    });
	   
	   JMenuItem printLine = new JMenuItem("Print new line");
	   printLine.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		System.out.println();
	    	}
	    });
	   
	   
//----------------------------------Output
	   

//______________________________Speed
	   
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
	   
	   JMenuItem pause = new JMenuItem("Pause");
	   pause.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.isPaused = !(Launcher.isPaused);
	    	}
	    });
	   
	   JMenuItem setSpeed = new JMenuItem("Set speed");
	   setSpeed.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String str = JOptionPane.showInputDialog(null, "Time in milliseconds", "Automatic mode with visual", 1);
	    		if(str != null && !str.equals("") && checkIntString(str)) {
	    			int roundDelay = new Integer(str);
	    			Controller.roundDelay = roundDelay;
	    		}
	    	}
	    });
	   
	   JMenuItem stop = new JMenuItem("Stop");
	   stop.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.stop = true;
	    	}
	    });
	   
	   JMenuItem toggleRoundDelay = new JMenuItem("Toggle round delay");
	   toggleRoundDelay.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Launcher.allowRoundDelay = !(Launcher.allowRoundDelay);
	    	}
	    });

	   
//----------------------------------Speed
	   
	   
//______________________________Charts
	   
	   
	   JMenuItem showXyChart = new JMenuItem("Show xy chart");
	   showXyChart.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.showFitnessXyChart();
	    	}
	    });
	   
	   JMenuItem showXySplineChart = new JMenuItem("Show xy spline chart");
	   showXySplineChart.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.showFitnessXySplineChart();
	    	}
	    });
	   
	   JMenuItem showXyDeviationChart = new JMenuItem("Show xy deviation chart");
	   showXyDeviationChart.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Controller.showFitnessXyDeviationChart();
	    	}
	    });
	   
	   JMenuItem showDualAxisWeightChart = new JMenuItem("Show dual axis weight chart");
	   showDualAxisWeightChart.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String str = JOptionPane.showInputDialog(null, "Game Number : ", "Dual axis weight chart", 1);
	    		if(str != null && !str.equals("") && checkIntString(str)) {
	    			int bestTeam = new Integer(str) - 1;
	    			Controller.showDualAxisWeightChart(bestTeam);
	    		}
	    	}
	    });
	   
//----------------------------------Charts
	   
	   JMenu automatic = new JMenu("Automatic");
	   automatic.add(newBestTeamGame);
	   automatic.add(runBestTeamGames);
	   automatic.add(jumpToBestTeam);
	   automatic.add(sortBestTeamsLowToHigh);
	   automatic.add(sortBestTeamsHighToLow);
	   
	   JMenu output = new JMenu("Output");
	   output.add(toggleActionOutput);
	   output.add(toggleAreaDamageOutput);
	   output.add(toggleBestTeamsFitnessOutput);
	   output.add(toggleBoostOutput);
	   output.add(toggleHealOutput);
	   output.add(toggleHpOutput);
	   output.add(toggleNormalDamageOutput);
	   output.add(toggleShieldOutput);
	   output.add(toggleStunOutput);
	   output.add(toggleRoundSeparator);
	   output.add(testAdjacentAiOutput);
	   output.add(testAngleOutput);
	   output.add(testFitnessSubsetOutput);
	   output.add(testMutateLikelihoodOutput);
	   output.add(testPrintCurrentGameOutput);
	   output.add(testUnscaledFitnessOutput);
	   output.add(printLine);
	   
	   JMenu speed = new JMenu("Speed");
	   speed.add(fast);
	   speed.add(normal);
	   speed.add(slow);
	   speed.add(pause);
	   speed.add(stop);
	   speed.add(setSpeed);
	   speed.add(toggleRoundDelay);
	   
	   JMenu charts = new JMenu("Charts");
	   charts.add(showXyChart);
	   charts.add(showXySplineChart);
	   charts.add(showXyDeviationChart);
	   charts.add(showDualAxisWeightChart);
	   
	   JMenuBar menuBar = new JMenuBar();
	   menuBar.add(automatic);
	   menuBar.add(output);
	   menuBar.add(speed);
	   menuBar.add(charts);
	   frame.setJMenuBar(menuBar);
	   
	    
	    frame.setVisible(true);
	}
	
	public double[] copyArray(double[] orgArray) {
		int length = orgArray.length;
		double[] copy = new double[length];
		for (int i = 0; i < length; i++) {
			copy[i] = orgArray[i];
		}
		return copy;
	}
	
	public boolean checkIntString(String str) {
		boolean isInteger = true;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9') {
				continue;
			}
			else {
				isInteger = false;
				break;
			}
		}
		if(!isInteger) { System.out.println("Input was not a positive integer"); }
		return isInteger;
	}
}
