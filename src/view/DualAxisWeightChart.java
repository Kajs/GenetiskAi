package view;

import java.awt.Color;
import java.awt.event.WindowEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import control.Controller;

/**
 * A simple demonstration application showing how to create a dual axis chart based on data
 * from two {@link CategoryDataset} instances.
 *
 */
public class DualAxisWeightChart extends ApplicationFrame {

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
	
    public DualAxisWeightChart(final String title, double[][] dataMatrix, String[] names, String type) {
        super(title);

        final CategoryDataset dataset1 = createDataset(dataMatrix, names);

        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Dual Axis Weight Chart " + "(" + type + ")",  // chart title
            "Weight",         // domain axis label
            "Value",            // range axis label
            dataset1,           // data
            PlotOrientation.VERTICAL,
            true,               // include legend
            true,
            false
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(0xCC, 0xFF, 0xCC));
//        chart.getLegend().setAnchor(Legend.SOUTH);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        plot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);  
        
        final CategoryItemRenderer renderer1 = plot.getRenderer();
        for (int i = 0; i < Controller.information; i++) { renderer1.setSeriesPaint(i, Color.red); }

        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1280, 800));
        setContentPane(chartPanel);

    }
    
    /**
     * Creates a sample dataset.
     *
     * @return  The dataset.
     */
    private CategoryDataset createDataset(double[][] dataMatrix, String[] names) {

        // column keys...
        int information = Controller.information;
        int choices = Controller.choices;
        String[] category = new String[information];
        for (int i = 0; i < information; i++) { category[i] = "Weight " + i + 1;}

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int c = 0; c < choices + 1; c++) {
        	for (int i = 0; i < information; i++) { dataset.addValue(dataMatrix[c][i], Integer.toString(i + 1), names[c]); }
        }

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public void showResults() {
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
    }

}
