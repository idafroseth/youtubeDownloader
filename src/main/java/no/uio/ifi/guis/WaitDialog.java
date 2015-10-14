package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitDialog extends JDialog{
	public WaitDialog(){
		JLabel updating = new JLabel("          Wait while updating filters");
		this.setVisible(true);
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(300, 100));
		p.setLayout(new BorderLayout());
		p.add(updating, BorderLayout.CENTER);
		this.add(p);
		this.setVisible(true);
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 4 - getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 4 - getHeight() / 2);
	
		this.pack();
	}

}
