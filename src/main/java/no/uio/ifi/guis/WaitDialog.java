package no.uio.ifi.guis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitDialog extends JDialog{
	public WaitDialog(String dialog){
		JLabel updating = new JLabel(dialog);
		updating.setHorizontalAlignment((int) Component.CENTER_ALIGNMENT);
		this.setVisible(true);
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(500, 200));
		p.setLayout(new BorderLayout());
		p.add(updating, BorderLayout.CENTER);
		this.add(p);
		this.setVisible(true);
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 4 - getWidth() / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 4 - getHeight() / 2);
	
		this.pack();
	}

}
