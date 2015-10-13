package no.uio.ifi.guis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		private ArrayList<String> filters = new ArrayList<String>(10);
		private HashMap<String, String> selectedFilters = new HashMap<String, String>(10);
		
		public FilteredSearchGui(){
			this.add(filterPanel);
			this.setTitle("Filtered Search");
			this.setVisible(true);
		}
		public boolean addFilterBox(Map<String, String> filter, String filterName){
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
				categoryList.setName(filterName);
				categoryList.setSelectedIndex(0);
				categoryList.addActionListener(filterListener);
				filters.add(filterName);
				JLabel filterLabel = new JLabel(filterName);
				filterPanel.add(filterLabel);
				filterPanel.add(categoryList);
				pack();
				return true;
			}
		
		}
		private class SelectorListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			
				JComboBox comboBox = (JComboBox)e.getSource();
				comboBox.getName();
		        String category = (String)comboBox.getSelectedItem();
		    	if(selectedFilters.containsKey(category)){
		    		selectedFilters.
		    	}
		        System.out.println(category);
			}
			
		}
//		public static void main(String[] args){
//
//		}
}
