package no.uio.ifi.guis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FilteredSearchGui extends JFrame{
		private JPanel filterPanel = new JPanel();
		private JPanel resultPanel;
		private SelectorListener filterListener = new SelectorListener();
		//limited to 10 filters
		private ArrayList<Integer> filters = new ArrayList<Integer>(10);
		private HashMap<Integer, String> selectedFilters = new HashMap<Integer, String>(10);
		
		public FilteredSearchGui(){
			this.add(filterPanel);
			this.setTitle("Filtered Search");
			this.setVisible(true);
		}
		public boolean addFilterBox(Map<String, String> filter, String filterName, Integer filterType){
			String[] dropDownList = new String[filter.size()+1];
			dropDownList[0] = "No "+ filterName +" filter";
			int i = 1;
			if(filters.contains(filterName)){
				System.out.println("A filter with this name is already defined!");
				return false;
			}else{
				
				for(String key : filter.keySet()){
					dropDownList[i] = key;
					i++;
				}
				JComboBox categoryList = new JComboBox(dropDownList);
				categoryList.setName(filterType.toString());
				categoryList.setSelectedIndex(0);
				categoryList.addActionListener(filterListener);
				filters.add(filterType);
				JLabel filterLabel = new JLabel(filterName);
				filterPanel.add(filterLabel);
				filterPanel.add(categoryList);
				pack();
				return true;
			}
		
		}
		public HashMap<Integer,String> getSelectedFilters(){
			return this.selectedFilters;
		}
		private class SelectorListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			
				JComboBox comboBox = (JComboBox)e.getSource();
				System.out.println(comboBox.getName());
		        String category = (String)comboBox.getSelectedItem();
		    	if(selectedFilters.containsKey(comboBox.getName())){
		    		selectedFilters.replace(Integer.parseInt(comboBox.getName()),category);
		    	}else{
		    		selectedFilters.put(Integer.parseInt(comboBox.getName()),category);
		    	}
			}
			
		}
//		public static void main(String[] args){
//
//		}
}
