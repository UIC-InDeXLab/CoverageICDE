package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Plot extends JFrame {

	public Plot(long[] timeSeries) {

		initUI(timeSeries);
	}

	private void initUI(long[] timeSeries) {

		XYDataset dataset = createDataset(timeSeries);
		JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartPanel.setBackground(Color.white);
		add(chartPanel);

		pack();
		setTitle("Mup found vs time chart");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private XYDataset createDataset(long[] timeSeries) {

		XYSeries series = new XYSeries("Hybrid");
		for (int i = 0; i < timeSeries.length; i++) {
			series.add(timeSeries[i], i + 1);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		return dataset;
	}

	private JFreeChart createChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(
				"# of mups found / time(ms)", "time (ms)", "# of mups found", dataset,
				PlotOrientation.VERTICAL, true, true, false);

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

		return chart;

	}

	public static void main(String[] args) {

		Plot ex = new Plot(new long[]{1,3,4});
		ex.setVisible(true);
	}
}
