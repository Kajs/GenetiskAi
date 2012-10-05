package control;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import view.BoardRenderer;

public class Launcher {
	private static int windowWidth;
    private static int windowHeight;

	public static void main(String[] args) {
		windowWidth = 800;
		windowHeight = 600;
		Controller control = new Controller(windowWidth, windowHeight);
	    
	  }
}
