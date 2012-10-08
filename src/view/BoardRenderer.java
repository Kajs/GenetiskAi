package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import model.*;

@SuppressWarnings("serial")
public class BoardRenderer extends JPanel implements Observer {
	private Graphics2D g2D;
	private int rows;
	private int columns;
	private Hex[][] hexMatrix;
	
	
  public BoardRenderer(int row, int col, Hex[][] newHexMatrix) {
	  hexMatrix = newHexMatrix;
	  rows = row;
      columns = col;
}
  
  public void paint(Graphics g) {
	  super.paintComponent(g);
	  g2D = (Graphics2D)g;
	  for(int col = 0; col < columns; col++) {
	      for(int row = 0; row < rows; row++) {
	    	  Hex hex = hexMatrix[col][row];
	    	  g2D.setColor(hex.getColor());
	    	  g2D.fill(hex.getShape());
	    	  g2D.setColor(Color.black);
	    	  g2D.draw(hex.getShape());
	      }
	  }	  
  }
  
  public void update(Observable obs, Object obj) {
	  if (obj == hexMatrix) {
		  hexMatrix = (Hex[][]) obj;
		  repaint();
	  }
  }
}
