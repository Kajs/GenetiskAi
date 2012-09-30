package view;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.*;

public class BoardRenderer extends JPanel {
	private Board board;
	private int rows = 4;
	private int columns = 4;
	private Hex[][] hexes;
	
	
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    board = new Board(10, 10, rows, columns, 1);
    hexes = board.getHexes();
    Graphics2D g2 = (Graphics2D)g;
    System.out.println("Foer foerste for each");
    for (int r = 0; r < rows; r++ ) {
    	System.out.println("Efter foerste for each");
    	for (int c = 0; c < columns; c++ ) {
    		System.out.println("Fejl ved draw");
    		drawHex(hexes[rows][columns], g2);    		
    	}
    }
  }
  
  public void drawHex(Hex hexIn, Graphics2D g2) {
	  Path2D hexPath = new Path2D.Double();
	  Coordinate startPosition = hexIn.getStartPosition();
	    hexPath.moveTo(startPosition.getX(), startPosition.getY());
	    for (Coordinate coordinate : hexIn.getVertices()) {
	        double x = coordinate.getX();
	    	double y = coordinate.getY();
	    	hexPath.lineTo(x,  y);
	    }
	    hexPath.closePath();
	    g2.draw(hexPath);
	  
  }
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setTitle("DrawPoly");
    frame.setSize(350, 250);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    Container contentPane = frame.getContentPane();
    contentPane.add(new BoardRenderer());

    frame.show();
  }
}
