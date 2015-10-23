package no.uio.ifi.guis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.google.api.services.youtube.model.Video;

public class Statistics extends JPanel {
	//ChartFactory myChartFactory = new ChartFactory();
	HashMap<String, ChartPanel> chartFactory = new HashMap<String, ChartPanel>();
	JPanel contentPane = new JPanel();
	JPanel counterPanel = new JPanel();
	NumberFormat formatter = NumberFormat.getInstance(); // get instance
	
	
	public Statistics (){
		formatter.setMaximumFractionDigits(2);
		contentPane.setLayout(new GridLayout(2,2));
		JScrollPane scrollPane = new JScrollPane(contentPane);
		scrollPane.setPreferredSize(FilteredSearchGui.CONTENT_PANE_SIZE);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		counterPanel.setLayout(new BoxLayout(counterPanel, BoxLayout.PAGE_AXIS));
		contentPane.add(counterPanel);
		add(scrollPane);
	    setVisible(true);
		
	}
	private void changeBarColor(JFreeChart plot){
	//	Plot plot.getPlot();
	}

	public void addCount(BigInteger count, String countName){
		JLabel counter = new JLabel("Average " +countName+ " per video is: " + count);
		counterPanel.add(counter);
	}
	/**
	 * Add a barChart to the GUI
	 * @param categoryMap a MAP with the bar name and freq
	 * @param chartTitle the title of the chart
	 */
	public void addBarChart(Map<String, Integer> categoryMap, String chartTitle){
		
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "", "%", createDataset(categoryMap), PlotOrientation.HORIZONTAL, false, false, false);
		barChart.getTitle().setFont(new Font("Areal", Font.PLAIN, 17));
	
		//Add the value above each bar
		CategoryPlot plot=(CategoryPlot)barChart.getPlot();
		BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setSeriesItemLabelsVisible(0,true);
		ChartPanel panel = new ChartPanel(barChart);
		panel.setPreferredSize(new Dimension(400,300));
		chartFactory.put(chartTitle, panel);
		contentPane.add(panel);
//		pack();
	}
	/**
	 * 
	 * @param categoryMap
	 * @param chartTitle
	 * @param totalNumberOfVideos should reflect the what to divide on to get the precentage
	 */
	public void addBarChart(Map<String, BigInteger> categoryMap, String chartTitle, Integer totalNumberOfVideos){
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "", "Average per video", createBigDataset(categoryMap, totalNumberOfVideos), PlotOrientation.HORIZONTAL, false, false, false);
		barChart.getTitle().setFont(new Font("Areal", Font.PLAIN, 17));
	
		//Add the value above each bar
		CategoryPlot plot=(CategoryPlot)barChart.getPlot();
		BarRenderer renderer = (BarRenderer) barChart.getCategoryPlot().getRenderer();
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setSeriesItemLabelsVisible(0,true);
		ChartPanel panel = new ChartPanel(barChart);
		panel.setPreferredSize(new Dimension(400,300));
		chartFactory.put(chartTitle, panel);
		contentPane.add(panel);
//		pack();
	}
	private CategoryDataset createBigDataset(Map<String, BigInteger> dataContent, Integer totalNumberOfVideos){
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		final String frequency = "Average disribution";
		LinkedList<Map<String, Double>> sortedQueue = sortBigByCategory(dataContent,totalNumberOfVideos);
		while(sortedQueue.size()>0){
			Map<String, Double> sortedMap = sortedQueue.removeFirst();
			for(String key : sortedMap.keySet()){
				dataset.addValue(sortedMap.get(key),frequency, key );
			}
		}
		return dataset;
}
	
	private CategoryDataset createDataset(Map<String, Integer> dataContent){
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			final String frequency = "Average disribution";
			LinkedList<Map<String, Double>> sortedQueue = sortByCategory(dataContent);
			while(sortedQueue.size()>0){
				Map<String, Double> sortedMap = sortedQueue.removeFirst();
				for(String key : sortedMap.keySet()){
					dataset.addValue(sortedMap.get(key),frequency, key );
				}
			}
			return dataset;
	}
	
	private LinkedList<Map<String,Double>> sortByCategory(Map<String,Integer> dataMap){
		LinkedList<Map<String,Double>> sortedQueue = new LinkedList<Map<String, Double>>();
		System.out.println("Trying to sort the map");
		int totalNumberOfVideos = 0;
		for(String key : dataMap.keySet()){
			totalNumberOfVideos += dataMap.get(key);
		}
		System.out.println("Total number of Videos in the categorySet " + totalNumberOfVideos);
		String pointerToLargestValue = "";
		int i = 0;
		while(dataMap.size()>0){
			Map<String, Double> sortedMap = new HashMap<String, Double>(1);
			i++;
			int largestValue = 0;
			for(String key : dataMap.keySet()){
				if(dataMap.get(key)>largestValue){
					largestValue = dataMap.get(key);
					pointerToLargestValue = key;
				}
			}
			System.out.println("Value nb" + i + " has value " + pointerToLargestValue + " With "  +largestValue);
			sortedMap.put(pointerToLargestValue, (double)largestValue*100/(double)totalNumberOfVideos);
			sortedQueue.add(sortedMap);
			dataMap.remove(pointerToLargestValue);
		}		
		return sortedQueue;
	}
	
	private LinkedList<Map<String,Double>> sortBigByCategory(Map<String,BigInteger> dataMap, Integer numVideo){
		LinkedList<Map<String,Double>> sortedQueue = new LinkedList<Map<String, Double>>();
		System.out.println("Trying to sort the map");
		BigInteger totalNumberOfVideos = new BigInteger(numVideo.toString());
		System.out.println("TotalNUmberOfVideos: " + totalNumberOfVideos);
		
		System.out.println("Total number of Videos in the categorySet " + totalNumberOfVideos);
		String pointerToLargestValue = "";
		int i = 0;
		while(dataMap.size()>0){
			Map<String, Double> sortedMap = new HashMap<String, Double>(1);
			i++;
			BigInteger largestValue = new BigInteger("0");
			for(String key : dataMap.keySet()){
				if(dataMap.get(key).compareTo(largestValue)>0){
					largestValue = dataMap.get(key);
					pointerToLargestValue = key;
				}
			}
			sortedMap.put(pointerToLargestValue,largestValue.doubleValue()/totalNumberOfVideos.doubleValue());
			sortedQueue.add(sortedMap);
			dataMap.remove(pointerToLargestValue);
		}		
		return sortedQueue;
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
	/**
	 * This calculated different types of statistics for the dataset so the user can display the dataset
	 * 
	 * @param videoInfoResult
	 */
	public void computeStatistics(Map<String, Video> videoInfoResult, Map<String, String> availableCategoriesReverse ){
		//Likes values 
		HashMap<String, BigInteger> likesStat = new HashMap<String, BigInteger>(2);
		likesStat.put("Likes", new BigInteger("0"));
		likesStat.put("Dislikes", new BigInteger("0"));
		Integer likesVideos = 0;
		
		Map<String, Integer> categoryStats = new HashMap<String, Integer>();
		Map<String, Integer> yearStats = new HashMap<String, Integer>();
		
		HashMap<String, BigInteger> countStat = new HashMap<String, BigInteger>(2);
		countStat.put("Views", new BigInteger("0"));
		//countStat.put("Favorite", new BigInteger("0"));
		//countStat.put("Comments", new BigInteger("0"));
		BigInteger viewCount = new BigInteger("0");
		BigInteger favoritesCount = new BigInteger("0");
		BigInteger commentCount = new BigInteger("0");
	
		
		for(Video video : videoInfoResult.values() ){
			//Like statistics
			if(video.getStatistics()!=null && video.getStatistics().getLikeCount()!= null){
				likesStat.replace("Likes", likesStat.get("Likes").add(video.getStatistics().getLikeCount()));
				likesStat.replace("Dislikes", likesStat.get("Dislikes").add(video.getStatistics().getDislikeCount()));
				likesVideos++;
				
				viewCount = viewCount.add(video.getStatistics().getViewCount());
				favoritesCount = favoritesCount.add(video.getStatistics().getFavoriteCount());
				commentCount = commentCount.add(video.getStatistics().getCommentCount());
			}
			
			
			//Category statistics
			String category = availableCategoriesReverse.get(video.getSnippet().getCategoryId());
			if(categoryStats.containsKey(category)){
				categoryStats.replace(category,categoryStats.get(category)+1);
			}else{
				categoryStats.put(category,1);
			}
			
			//Year statistics
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			String year =df.format(new Date(video.getSnippet().getPublishedAt().getValue()));
			if(yearStats.containsKey(year)){
				//We must update
				yearStats.replace(year,yearStats.get(year)+1);
			}else{
				yearStats.put(year,1);
			}
		
			
			video.getStatistics().getLikeCount();
		}
	//	Statistics stat = gui.getStatWindow();
		addBarChart(likesStat, "Likes", likesVideos);
		addBarChart(categoryStats, "Categories");
		addBarChart(yearStats, "Years");
		System.out.println(viewCount);
		addCount(viewCount.divide(new BigInteger(likesVideos.toString())), "views");
		addCount(favoritesCount.divide(new BigInteger(likesVideos.toString())), "favorites");
		addCount(commentCount.divide(new BigInteger(likesVideos.toString())), "comments");

	}
	
//	public static void main(String[] args){
//		JFrame jf = new JFrame();
//		Statistics chart = new Statistics();
//		
//		Map<String, Integer> categoryMap = new HashMap<String, Integer>();
//		Map<String, Integer> likes = new HashMap<String, Integer>();
//		Map<String, Integer> third = new HashMap<String, Integer>();
//		third.put("Likes", 85);
//		third.put("dislike", 12);
//		categoryMap.put("Sport", 15);
//		categoryMap.put("Entertainment", 80);
//		categoryMap.put("GameShow", 40);
//		likes.put("Likes", 85);
//		likes.put("dislike", 12);
//		chart.addBarChart(categoryMap, "Categories frequency");
//		chart.addBarChart(likes, "LIKES");
//		chart.addBarChart(third, "Third example");
//		jf.setVisible(true);
//		jf.add(chart);
//		jf.pack();
//	}
}
/**
 * I have mainly used SQL and Javascript in different student projects. One project I built a Student System web application using Java, JavaScript and a PostgreSQL database.  Are working on a very interesting project right now where we use SQL to store downloaded metadata and user content from YouTube which later would be used in multimodal analysis in a smart City context.
I have both student project and work experience with Java.  

 */
