package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import no.uio.ifi.management.ManagementFilteredSearch;

/**
 * This Class display a JPanel with three content panes. one contentpane hold
 * all the possible filters, one holding the chosen filters and the last one
 * containing the search button
 * 
 * @author Ida Marie Frøseth
 *
 */
public class FilterGui extends JPanel {

	private JPanel filterPanel = new JPanel();
	private JPanel filterAddPanel = new JPanel();
	private JPanel filterActivePanel = new JPanel();
	private JButton searchButton = new JButton("Search");
	// limited to 10 filters
	private ArrayList<Integer> filters = new ArrayList<Integer>(10);
	private SelectorListener filterListener = new SelectorListener();
	private ButtonListener mouseListener = new ButtonListener();
	private ManagementFilteredSearch mng;

	private HashMap<Integer, String> selectedFilters = new HashMap<Integer, String>(10);

	private JTextArea filtersAppliedText = new JTextArea("No filter");

	/**
	 * Contructor
	 * 
	 * @param mng
	 *            the Managment that has to be alerted when the search button is
	 *            hit.
	 */
	public FilterGui(ManagementFilteredSearch mng) {
		this.mng = mng;
		this.drawFilterMenu();
		this.setPreferredSize(FilteredSearchGui.CONTENT_PANE_SIZE);
		this.setLayout(new BorderLayout());
		this.add(searchButton, BorderLayout.PAGE_END);
		this.add(filterPanel, BorderLayout.CENTER);
	}

	/**
	 * Making the layout of the FilterGui
	 */
	public void drawFilterMenu() {
		searchButton.setActionCommand("SEARCHBUTTON");
		searchButton.addActionListener(mouseListener);
		searchButton.setPreferredSize(new Dimension(100, 30));

		filterPanel.setPreferredSize(new Dimension(1000, 500));
		filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
		filterPanel.setLayout(new GridLayout(1, 2));
		filterPanel.add(filterAddPanel);
		filterPanel.add(filterActivePanel);
		filterAddPanel.setLayout(new GridLayout(15, 1));
		filterAddPanel.setPreferredSize(new Dimension(100, 500));
		filterAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		// JPanel numSearchPanel = new JPanel();
		// numSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 10));
		filterAddPanel.add(new JLabel("# Videos to search"));
		filterAddPanel.add(new JTextField("100 000"));
		// filterAddPanel.add(numSearchPanel);
		filterActivePanel.setBorder(BorderFactory.createTitledBorder("Applied filters"));
		filterAddPanel.setPreferredSize(new Dimension(400, 500));
		filtersAppliedText.setPreferredSize(new Dimension(450, 500));
		filterActivePanel.add(filtersAppliedText);

	}

	/**
	 * Add a filter box to the gui in the filterAddPanel
	 * 
	 * @param filter
	 *            Map contaning all the possible filter values
	 * @param filterName
	 *            The name of the filter, is displayed to the left of the
	 *            JComboBox
	 * @param filterType
	 *            is a defined filtertype that you can find in FilterSearch
	 *            class
	 * @return
	 */
	public boolean addFilterBox(Map<String, String> filter, String filterName, Integer filterType) {
		String[] dropDownList = new String[filter.size() + 1];
		dropDownList[0] = "No " + filterName + " filter";
		int i = 1;
		if (filters.contains(filterName)) {
			System.out.println("A filter with this name is already defined!");
			return false;
		} else {

			for (String key : filter.keySet()) {
				dropDownList[i] = key;
				i++;
			}
			JComboBox categoryList = new JComboBox(dropDownList);
			categoryList.setName(filterType.toString());
			categoryList.setSelectedIndex(0);
			categoryList.addActionListener(filterListener);
			filters.add(filterType);
			JLabel filterLabel = new JLabel(filterName);
			JPanel filterRow = new JPanel();
			filterRow.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 10));
			filterRow.add(filterLabel);
			filterRow.add(categoryList);
			filterAddPanel.add(filterRow);
			return true;
		}
	}

	/**
	 * 
	 * @return a list HashMap<FilterSearch.FILTERTYPE, FilterValue> of all the
	 *         selected filters
	 */
	public HashMap<Integer, String> getSelectedFilters() {
		return this.selectedFilters;
	}

	/**
	 * 
	 * Class that act when a button is clicked
	 * 
	 * @author Ida Marie Frøseth
	 *
	 */
	private class ButtonListener implements ActionListener {

		/**
		 * Alters the management that the search button is clicked and display a
		 * progress bar in a JDialogBox
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			mng.preformSearch();
			// JButton button = (JButton)e.getSource();
			// String action = button.getActionCommand();
			// switch (action){
			// case "SEARCHBUTTON":
			// mng.preformSearch();
			// break;
			// }



		}
	}

	/**
	 * Listen when the user choose one of the filters
	 * For now the user can only chose one filter for each Filter type. 
	 * @author Ida Marie Frøseth
	 *
	 */
	private class SelectorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			JComboBox comboBox = (JComboBox) e.getSource();
			System.out.println(comboBox.getName());
			String category = (String) comboBox.getSelectedItem();
			if (selectedFilters.containsKey(comboBox.getName())) {
				selectedFilters.replace(Integer.parseInt(comboBox.getName()), category);
			} else {
				selectedFilters.put(Integer.parseInt(comboBox.getName()), category);
			}
			String outputText = "";
			for (String filter : selectedFilters.values()) {
				outputText += filter + "; \n";
			}

			filtersAppliedText.setText(outputText);
		}
	}
}
