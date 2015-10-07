package no.uio.ifi.guis;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Statistics extends JFrame {
	//ChartFactory myChartFactory = new ChartFactory();

	public Statistics ( String applicationTitle){
		super(applicationTitle);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setVisible(true);
		
		
	}
	private void changeBarColor(JFreeChart plot){
	//	Plot plot.getPlot();
	}
	
	public void addChart(Map<String, Integer> categoryMap, String chartTitle){
		createDataset(categoryMap);
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Category", "%", createDataset(categoryMap), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel panel = new ChartPanel(barChart);
		add(panel);
		pack();
	}
	
	private CategoryDataset createDataset(Map<String, Integer> dataContent){
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			final String frequency = "Average disribution";
			for(String key : dataContent.keySet()){
				dataset.addValue(dataContent.get(key), frequency, key);
			}
			return dataset;
	}
	private void saveChartAsPNG(JFreeChart barChart, String fileName) throws IOException{//, int width, int height){
		int width = 640; //Width of the image
		int height = 480; //height of the image
		File file = new File(fileName);
		ChartUtilities.saveChartAsPNG(file, barChart, width, height);
	}
	public static void main(String[] args){
		Statistics chart = new Statistics("YT Dataset Stats");
		Map<String, Integer> categoryMap = new HashMap<String, Integer>();
		Map<String, Integer> likes = new HashMap<String, Integer>();
		categoryMap.put("Sport", 15);
		categoryMap.put("Entertainment", 22);
		categoryMap.put("GameShow", 40);
		likes.put("Likes", 85);
		likes.put("dislike", 12);
		chart.addChart(categoryMap, "Categories frequency");
		chart.addChart(likes, "LIKES");
	}
}
