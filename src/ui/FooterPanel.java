package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FooterPanel extends JPanel {

	public FooterPanel() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 40));
		setBackground(new Color(5, 5, 8));
		setBorder(BorderFactory.createEmptyBorder(4, 32, 4, 32));

		JLabel leftLabel = new JLabel("TypeGauge v2.0");
		leftLabel.setForeground(new Color(180, 180, 190));
		leftLabel.setFont(leftLabel.getFont().deriveFont(Font.PLAIN, 11f));

		JLabel rightLabel = new JLabel("PRIVACY GUARANTEED   •   NO DATA RETENTION   •   LOCAL PROCESSING");
		rightLabel.setForeground(new Color(120, 120, 130));
		rightLabel.setFont(rightLabel.getFont().deriveFont(Font.PLAIN, 10f));
		rightLabel.setHorizontalAlignment(JLabel.RIGHT);

		add(leftLabel, BorderLayout.WEST);
		add(rightLabel, BorderLayout.EAST);
	}
}
