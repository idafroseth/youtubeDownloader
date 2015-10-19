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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1192024962932785605L;
	JLabel updating;
	String dialog;
	public WaitDialog(String dia){
		dialog = dia;
		updating = new JLabel(dialog);
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
	public void setText(String newText){
		updating.setText(newText);
	}
	public void appendText(Integer newText){
		updating.setText(dialog + " \n Downloaded: " + newText);
	}


}
