package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Plot extends JFrame {

	public Plot() {
	}

	private void initUI(JFreeChart chart) {

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartPanel.setBackground(Color.white);
		add(chartPanel);

		pack();
		setTitle("");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private XYDataset create2dPlotDataset(long[] values) {

		XYSeries series = new XYSeries("Hybrid");
		for (int i = 0; i < values.length; i++) {
			series.add(values[i], i + 1);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		return dataset;
	}

	public void create2dPlot(long[] timeSeries) {
		XYDataset dataset = create2dPlotDataset(timeSeries);

		JFreeChart chart = ChartFactory.createXYLineChart(
				"# of mups found / time(ms)", "time (ms)", "# of mups found",
				dataset, PlotOrientation.VERTICAL, true, true, false);

		XYPlot plot = chart.getXYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(0.1f));

		plot.setRenderer(renderer);
		plot.setBackgroundPaint(Color.white);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		chart.getLegend().setFrame(BlockBorder.NONE);

		chart.setTitle(new TextTitle("Mup found vs time chart",
				new Font("Serif", java.awt.Font.BOLD, 18)));

		initUI(chart);

	}
	
	public void createBarchart(long[] values, int maxLevel) {
		createBarchart(values, maxLevel, false);
	}

	public void createBarchart(long[] values, int maxLevel, boolean ifPrintCounter) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<Long, Integer> counter = new HashMap<Long, Integer>();
		for (int i = 0; i <= maxLevel; i++) {
			counter.put((long) i, 0);
		}

		for (long value : values) {
			counter.put(value, counter.get(value) + 1);
		}

		for (int i = 0; i <= maxLevel; i++) {
			dataset.setValue(counter.get((long) i), "Count", i + "");
			if (ifPrintCounter) 
				System.out.println(i + "," + counter.get((long) i));
		}

		String plotTitle = "# of Mups at each level";
		String xAxis = "Level";
		String yAxis = "# of Mups";
		PlotOrientation orientation = PlotOrientation.VERTICAL;

		boolean show = false;
		boolean toolTips = false;
		boolean urls = false;
		JFreeChart chart = ChartFactory.createBarChart(plotTitle, xAxis, yAxis,
				dataset, orientation, show, toolTips, urls);

		chart.setBackgroundPaint(Color.white);

		initUI(chart);
	}

	public static void main(String[] args) {
//
//		 Plot ex = new Plot(new long[]{1, 3, 4}, "2d");
//		 ex.setVisible(true);
	}
}
