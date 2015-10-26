package no.uio.ifi.guis;

import java.awt.BorderLayout;

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
		this.add(drawSearchMenu(), BorderLayout.PAGE_END);
	//	this.add(drawFilterMenu(), BorderLayout.CENTER);
		onTextFieldChange();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800226228629567624L;

}
