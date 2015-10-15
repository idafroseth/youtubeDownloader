package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.google.api.services.youtube.model.SearchResult;

import no.uio.ifi.management.ManagementFilteredSearch;
import no.uio.ifi.models.RandomVideoIdGenerator;

public class DownloadProgressBar  implements PreformingSearchDialog{
	JProgressBar progressBar;
	public DownloadProgressBar(int max, String title){
		// We should move this into a separate class
		JDialog dialog = new JDialog();
		dialog.setTitle(title);
		
		progressBar = new JProgressBar(0, max);
		progressBar.setValue(0);
		progressBar.setMaximum(max);
		progressBar.setStringPainted(true);
	
		JButton cancelButton = new JButton("Cancel");
		JPanel content = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel progressPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 10));
		progressPanel.setLayout(new FlowLayout());// (FlowLayout.CENTER, 3,
													// 10));
		content.setLayout(new BorderLayout());
		progressBar.setPreferredSize(new Dimension(300, 50));
		progressBar.setVisible(true);
		progressBar.setValue(40);
		progressBar.setBorderPainted(true);
		content.setPreferredSize(new Dimension(500, 150));
		buttonPanel.add(cancelButton);
		content.add(buttonPanel, BorderLayout.PAGE_END);
	
		progressPanel.add(progressBar);
		content.add(progressPanel, BorderLayout.CENTER);
		dialog.setPreferredSize(new Dimension(500, 150));
		dialog.add(content);
		dialog.setVisible(true);
		dialog.pack();
	}
	@Override
	public void setVideosToRetrieve(int numbOfVideosToSearch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProgressBar(int numberOfVideosRetrived) {
		progressBar.setValue(numberOfVideosRetrived);
		
	}
	
	public void run() {
		try {
		
			Thread.sleep(1);

		} catch (InterruptedException v) {
			System.out.println("Thread error");
			System.out.println(v);
		}
	}
	

}
