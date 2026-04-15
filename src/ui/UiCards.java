package ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Factory for reusable card widgets used on metric-heavy screens.
 */
public final class UiCards {

	private UiCards() {
	}

	public static JPanel createGlassStatCard(String title, String subtitle, String iconPath, JLabel valueLabel) {
		// Shared metric card layout: header, large value, optional subtitle.
		JPanel panel = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(12, 16, 12, 16)));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);

		if (iconPath != null) {
			ImageIcon icon = UiIcons.loadIcon(iconPath, 18, 18);
			if (icon != null) {
				JLabel iconLabel = new JLabel(icon);
				iconLabel.setBorder(new EmptyBorder(0, 0, 0, 8));
				header.add(iconLabel, BorderLayout.WEST);
			}
		}

		JLabel titleLabel = new JLabel(title.toUpperCase());
		titleLabel.setForeground(new Color(140, 140, 140));
		titleLabel.setFont(titleLabel.getFont().deriveFont(12f));
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		header.add(titleLabel, BorderLayout.CENTER);

		panel.add(header, BorderLayout.NORTH);

		valueLabel.setForeground(Color.WHITE);
		valueLabel.setFont(valueLabel.getFont().deriveFont(48f));
		panel.add(valueLabel, BorderLayout.CENTER);

		if (subtitle != null && !subtitle.isEmpty()) {
			JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.LEFT);
			subtitleLabel.setForeground(new Color(150, 150, 150));
			subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(12f));
			panel.add(subtitleLabel, BorderLayout.SOUTH);
		}

		return panel;
	}
}
