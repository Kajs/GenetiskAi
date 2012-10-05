package view;

import java.awt.Color;
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
	private ArrayList<ArrayList<Path2D>> allPath2DColumns = new ArrayList<ArrayList<Path2D>>();
	private ArrayList<Path2D> columnPath2D;
	private Graphics2D g2;
	
	
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    board = new Board(new Coordinate(50, 10), rows, columns, 50);
    hexMatrix = board.getHexMatrix();
    g2 = (Graphics2D)g;
    ai = new Ai(new Coordinate(1,1));
    
    
    for(int column = 0; column < columns; column++) {
    	ArrayList<Hex> currentColumn = hexMatrix.get(column);
    	System.out.println("current column size is: " + Integer.toString(currentColumn.size()));
    	columnPath2D = new ArrayList<Path2D>();
    	for(int row = 0; row < rows; row++) {
    		Hex hex = currentColumn.get(row);
    		Coordinate pos = hex.getStartPosition();
    		String posX = Double.toString(pos.getX());
    		String posY = Double.toString(pos.getY());
    		String cString = Integer.toString(column);
    		String rString = Integer.toString(row);
    		System.out.println("hex at (" + rString + ", " + cString + ") : " + posX + ", " + posY);
    		Path2D hexPath = drawHex(currentColumn.get(row), g2);
    		columnPath2D.add(row, hexPath);
    	}
    	allPath2DColumns.add(columnPath2D);
    }
    colorShape(new Coordinate(1,1), Color.red);
    colorShape(ai.moveAction(), Color.green);
    colorShape(ai.moveAction(), Color.green);
    }
  
  public Path2D drawHex(Hex hexIn, Graphics2D g2) {
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
	    return hexPath;
	  
  }
  
  public void colorShape(Coordinate position, Color color) {
	  Double col = position.getX();
	  Double row = position.getY();
	  ArrayList<Path2D> path2DArray = allPath2DColumns.get(col.intValue());
	  Path2D hexShape = path2DArray.get(row.intValue());
	  g2.setColor(color);
	  g2.fill(hexShape);
	  
  }
  
}
