package view;

import java.awt.event.WindowEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import control.Controller;

/**
 * This demo shows a simple bar chart created using the {@link XYSeriesCollection} dataset.
 *
 */
public class XyWeightChart extends ApplicationFrame {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
	
	public void windowClosing(final WindowEvent evt){
		 if(evt.getWindow() == this){ dispose(); }
	}
	
    public XyWeightChart(final String title, double[][] dataMatrix, String[] names) {
        super(title);
        IntervalXYDataset dataset = createDataset(dataMatrix, names);
        JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);     
    }
    
    public void showResults() {
    	pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
    }
    
    /**
     * Creates a sample dataset.
     * 
     * @return A sample dataset.
     */
    private IntervalXYDataset createDataset(double[][] dataMatrix, String[] names) {
    	final XYSeriesCollection dataset = new XYSeriesCollection();
    	for (int fitResultPos = 0; fitResultPos < dataMatrix.length; fitResultPos++) {
    		final XYSeries series = new XYSeries(names[fitResultPos]);
    		
    		for (int fitPos = 0; fitPos < dataMatrix[fitResultPos].length; fitPos++) {
    			series.add((double) fitPos, dataMatrix[fitResultPos][fitPos]);
    		}
    		
    		dataset.addSeries(series);
    	}
        
        return dataset;
    }

    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return A sample chart.
     */
    private JFreeChart createChart(IntervalXYDataset dataset) {
        final JFreeChart chart = ChartFactory.createXYBarChart(
            "Weight Values",
            "weight", 
            false,
            "value", 
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < Controller.choices + 1; i++) {
        	renderer.setSeriesLinesVisible(i, true);
            renderer.setSeriesShapesVisible(i, true);
        }
        plot.setRenderer(renderer);
        return chart;    
    }
}
