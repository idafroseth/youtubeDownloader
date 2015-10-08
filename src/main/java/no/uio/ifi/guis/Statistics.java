package no.uio.ifi.guis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.renderer.*;

public class Statistics extends JFrame {
	//ChartFactory myChartFactory = new ChartFactory();
	HashMap<String, ChartPanel> chartFactory = new HashMap<String, ChartPanel>();
	JPanel contentPane = new JPanel();


	public Statistics ( String applicationTitle){
		super(applicationTitle);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setLayout(new GridLayout(2,2));
		contentPane.setBackground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(contentPane);
		scrollPane.setPreferredSize(new Dimension(1000,500));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
	    setVisible(true);
		
	}
	private void changeBarColor(JFreeChart plot){
	//	Plot plot.getPlot();
	}
	
	/**
	 * Add a barChart to the GUI
	 * @param categoryMap a MAP with the bar name and freq
	 * @param chartTitle the title of the chart
	 */
	public void addBarChart(Map<String, Integer> categoryMap, String chartTitle){
		
		createDataset(categoryMap);
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Category", "%", createDataset(categoryMap), PlotOrientation.VERTICAL, false, false, false);
		barChart.getTitle().setFont(new Font("Areal", Font.PLAIN, 17));
	
		//Add the value above each bar
		BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setSeriesItemLabelsVisible(0,true);
		
		Color barColor = new Color(255, 5,5, 128);
		renderer.setSeriesPaint(0,barColor);
	//	ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
	

//		final CategoryPlot plot = barChart.getCategoryPlot();
//		((BarRenderer) plot.getRenderer().setBarPainter)(new StandardXYBarPainter());
////		
		
//		final CategoryPlot plots = barChart.getCategoryPlot();
//		BarRenderer.setDefaultBarPainter(new StandardBarPainter());
//		((BarRenderer) plots.getRenderer()).setBarPainter(new StandardBarPainter());
//		
		ChartPanel panel = new ChartPanel(barChart);
		panel.setPreferredSize(new Dimension(400,300));
		chartFactory.put(chartTitle, panel);
		contentPane.add(panel);
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
	private void saveChartAsPNG(String chartTitle, String fileName){
		try {
			int width = 200;
			int height = 200;
			File file = new File(fileName);
			ChartUtilities.saveChartAsPNG(file, chartFactory.get(chartTitle).getChart(), width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void changeColor(String chartTitle){
		try{
			chartFactory.get(chartTitle).getChart().setBackgroundPaint(new Color(0, 0, 0, 0));
		}catch(NullPointerException e){
			System.out.println("Could not set the color because there is not any chart with that title in the chartFactory");
		}
	}
	
	public static void main(String[] args){
		Statistics chart = new Statistics("YT Dataset Stats");
		Map<String, Integer> categoryMap = new HashMap<String, Integer>();
		Map<String, Integer> likes = new HashMap<String, Integer>();
		Map<String, Integer> third = new HashMap<String, Integer>();
		third.put("Likes", 85);
		third.put("dislike", 12);
		categoryMap.put("Sport", 15);
		categoryMap.put("Entertainment", 22);
		categoryMap.put("GameShow", 40);
		likes.put("Likes", 85);
		likes.put("dislike", 12);
		chart.addBarChart(categoryMap, "Categories frequency");
		chart.addBarChart(likes, "LIKES");
		chart.addBarChart(third, "Third example");
//		chart.changeColor("LIKES");
//		chart.changeColor("THIRD");
//		chart.changeColor("Categories frequency");
		
		//chart.saveChartAsPNG("Categories frequency", "secondChart");
	}
}
/**
 * I have mainly used SQL and Javascript in different student projects. One project I built a Student System web application using Java, JavaScript and a PostgreSQL database.  Are working on a very interesting project right now where we use SQL to store downloaded metadata and user content from YouTube which later would be used in multimodal analysis in a smart City context.
I have both student project and work experience with Java.  

 */
