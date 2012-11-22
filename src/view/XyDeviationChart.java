package view;

import java.awt.*;
import java.awt.event.WindowEvent;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.*;
import org.jfree.ui.*;

public class XyDeviationChart extends ApplicationFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XyDeviationChart(String s, double[][] dataMatrix, String[] names)
	{
		super(s);
		JPanel jpanel = createDemoPanel(dataMatrix, names);
		jpanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(jpanel);
	}
	
	public void windowClosing(final WindowEvent evt){
		 if(evt.getWindow() == this){ dispose(); }
	}

	private XYDataset createDataset(double[][] dataMatrix, String[] names)
	{
		YIntervalSeriesCollection yintervalseriescollection = new YIntervalSeriesCollection();

		for (int fitResultPos = 0; fitResultPos < dataMatrix.length; fitResultPos++) {
    		final YIntervalSeries yintervalseries = new YIntervalSeries(names[fitResultPos]);
    		double total = 0;
    		double totalSquare = 0;
    		double standardDeviation = 0;
    		
    		for (int fitPos = 0; fitPos < dataMatrix[fitResultPos].length; fitPos++) {
    			double value = dataMatrix[fitResultPos][fitPos];
    			
    			total += value;
    			totalSquare += (value - total/(fitPos + 1)) * (value - total/(fitPos + 1)); // (value - mean)^2
    			standardDeviation = sqrt(totalSquare/(fitPos + 1));
    			yintervalseries.add((double) fitPos, value, value - standardDeviation, value + standardDeviation);
    		}
    		
    		yintervalseriescollection.addSeries(yintervalseries);
    	}

		return yintervalseriescollection;
	}

	private JFreeChart createChart(XYDataset xydataset)
	{
		JFreeChart jfreechart = ChartFactory.createScatterPlot("Pojected Fitness Results", "game", "fitness", xydataset, PlotOrientation.VERTICAL, false, false, false);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		DeviationRenderer deviationrenderer = new DeviationRenderer(true, false);
		xyplot.setRenderer(deviationrenderer);
		return jfreechart;
	}

	public JPanel createDemoPanel(double[][] dataMatrix, String[] names)
	{
		JFreeChart jfreechart = createChart(createDataset(dataMatrix, names));
		return new ChartPanel(jfreechart);
	}

	public void showResults()
	{
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}
}
