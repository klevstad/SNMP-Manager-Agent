package assignment1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.*;


public class Printer extends ApplicationFrame{

	private static final long serialVersionUID = 1L;

	public Printer(String title, ArrayList<Integer> samples) {
		super(title);
		
		for(Integer sample : samples)
		{
			System.out.println(sample);
		}
		
	    XYSeriesCollection data = createSampleData(samples);
	    ChartPanel chart = createDemoPanel(data);
	    this.add(chart, BorderLayout.CENTER);
	}
	
	public static void main(final String[] args)
	{
		LogFileReader logFileReader = new LogFileReader();
	    try{
	    	Printer printer = new Printer("Use Case 1.4 - Helping Adam the Admin", logFileReader.getM_samples());
	    	printer.pack();
		    RefineryUtilities.centerFrameOnScreen(printer);
		    printer.setVisible(true);
	    }
	    catch(NullPointerException e)
	    {
	    	System.out.println("The log file does not contain any valid datasamples.");
	    }
	}	
	
	 private XYSeriesCollection createSampleData(ArrayList<Integer> samples) {
	        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
	        XYSeries series = new XYSeries("Samples");
	        for (int i = 0; i < samples.size(); i++) {
	            series.add(i, samples.get(i));
	        }
	        xySeriesCollection.addSeries(series);
	        return xySeriesCollection;
	    }
	 
	 private ChartPanel createDemoPanel(XYSeriesCollection sampleData) {
	        JFreeChart jfreechart = ChartFactory.createScatterPlot("Graph of traffic", "Time [minutes]", "Traffic [datagrams/minute]", sampleData,
	        		PlotOrientation.VERTICAL, true, true, false);
	        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
	        xyPlot.setDomainCrosshairVisible(true);
	        xyPlot.setRangeCrosshairVisible(true);
	        XYItemRenderer renderer = xyPlot.getRenderer();
	        renderer.setSeriesPaint(0, Color.blue);
	        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
	        domain.setRange(0, sampleData.getDomainBounds(true).getLength());
	        domain.setTickUnit(new NumberTickUnit(1));
	        domain.setVerticalTickLabels(true);
	        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
	        range.setRange((sampleData.getRangeLowerBound(true)-500), sampleData.getRangeUpperBound(true)+500);
	        range.setTickUnit(new NumberTickUnit(500));
	        return new ChartPanel(jfreechart);
	    }
}
