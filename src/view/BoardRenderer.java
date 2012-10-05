package view;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.*;

public class BoardRenderer extends JPanel {
	private Board board;
	private ArrayList<ArrayList<Hex>> hexMatrix;
	private Ai ai;
	private int columns = 4;
	private int rows = 5;
	
	
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    board = new Board(new Coordinate(50, 10), rows, columns, 50);
    hexMatrix = board.getHexMatrix();
    ai = new Ai(new Coordinate(1,1));
    Graphics2D g2 = (Graphics2D)g;
    for(int column = 0; column < columns; column++) {
    	ArrayList<Hex> currentColumn = hexMatrix.get(column);
    	System.out.println("current column size is: " + Integer.toString(currentColumn.size()));
    	for(int row = 0; row < rows; row++) {
    		Hex hex = currentColumn.get(row);
    		Coordinate pos = hex.getStartPosition();
    		String posX = Double.toString(pos.getX());
    		String posY = Double.toString(pos.getY());
    		String cString = Integer.toString(column);
    		String rString = Integer.toString(row);
    		System.out.println("hex at (" + rString + ", " + cString + ") : " + posX + ", " + posY);
    		drawHex(currentColumn.get(row), g2);
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
    frame.setSize(800, 600);
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
