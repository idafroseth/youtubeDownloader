package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.google.api.services.youtube.model.SearchResult;

import no.uio.ifi.management.ManagementFilteredSearch;
import no.uio.ifi.models.search.RandomVideoIdGenerator;

public class DownloadProgressBar extends JDialog  implements PreformingSearchDialog {
	JProgressBar progressBar;
	ManagementFilteredSearch mng;
	ThreadGroup tg;
	StopListener mouseListener = new StopListener();
	
	public DownloadProgressBar(ManagementFilteredSearch mng, int max, String title, ThreadGroup tg){
		this.tg = tg;
		this.mng = mng;
		this.setTitle(title);
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setMaximum(max);
		progressBar.setStringPainted(true);
	
		JButton cancelButton = new JButton("Stop and save");
		
		cancelButton.addActionListener(mouseListener);
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
		this.setPreferredSize(new Dimension(500, 150));
		this.add(content);
		this.setVisible(true);
		this.pack();
	}
	@Override
	public void setVideosToRetrieve(int numbOfVideosToSearch) {
		progressBar.setMaximum(numbOfVideosToSearch);
	}

	@Override
	public void updateProgressBar(int numberOfVideosRetrived) {
		progressBar.setValue(numberOfVideosRetrived);
	}
	class StopListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			tg.interrupt();
		}
	}
}
