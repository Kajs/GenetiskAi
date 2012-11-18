package view;

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

/**
 * This demo shows a simple bar chart created using the {@link XYSeriesCollection} dataset.
 *
 */
public class FitnessChart extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public FitnessChart(final String title, double[][] dataMatrix, String[] names) {
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
            "Fitness Results",
            "Game", 
            false,
            "Fitness", 
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, true);
        plot.setRenderer(renderer);
        return chart;    
    }
}
