package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import no.uio.ifi.management.ManagementFilteredSearch;
import no.uio.ifi.models.search.FilteredSearch;

public class JsoupSearchGui extends FilterGui{

	public JsoupSearchGui(ManagementFilteredSearch mng) {
		super(mng);
	}
	
	@Override
	public void init(){
		System.out.println("Initialize the jsoup gui");
		this.setPreferredSize(FilteredSearchGui.CONTENT_PANE_SIZE);
		this.setLayout(new BorderLayout());
//		add(getNumberOfVideosPanel(), BorderLayout.PAGE_START);
		this.add(drawSearchMenu(), BorderLayout.PAGE_END);
		this.add(drawFilterMenu(), BorderLayout.CENTER);
		onTextFieldChange();
		String infoText="The JSoup crawler does not use the YouTube APi, and will provide a result that more reflect what the user is met by";
//		add(getSearchAlternative());
		
//		add(new JLabel(), BorderLayout.AFTER_LAST_LINE);
	
	}
	public JPanel getSearchAlternative(){
		JPanel panel = new JPanel();
//		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//		panel.add(new JLabel("Choose start page"));
//		panel.add(new JTextField("VideoId or URL"));
//		panel.add(new JButton("Apply"));
		return panel;
	}
	
	@Override 
	public JPanel drawFilterMenu() {
		JPanel panel =  new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));


	//	filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
		filterPanel.setLayout(new GridLayout(1, 2));
		
		filterPanel.add(filterAddPanel);
		filterPanel.add(filterActivePanel);
	
		filtersAppliedText.setBackground(Color.WHITE);
		filterAddPanel.setLayout(new GridLayout(10, 1));
		filterAddPanel.setPreferredSize(new Dimension(100, 500));
		filterAddPanel.add(getNumberOfVideosPanel());
		filterAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		filtersAppliedText.setPreferredSize(new Dimension(PANEL_WIDTH/2, PANEL_HEIGHT-1));

		filterAddPanel.setPreferredSize(new Dimension(400, 500));
		filtersAppliedText.setBorder(BorderFactory.createTitledBorder("Applied filters"));
		filterActivePanel.add(filtersAppliedText);
		filtersAppliedText.setBackground(new Color(238,238,238));
		panel.add(filterPanel);
		return panel;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800226228629567624L;

}
