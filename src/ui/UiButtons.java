package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/**
 * Factory for consistently styled primary action buttons.
 *
 * <p>Centralizing button creation ensures all primary controls share
 * dimensions, typography, cursor, and hover behavior.
 */
public final class UiButtons {

	private UiButtons() {
	}

	public static JButton createPrimaryButton(String text) {
		// Brand-aligned blue palette used throughout the application.
		final Color normalColor = new Color(59, 89, 152);
		final Color hoverColor = new Color(39, 69, 132);

		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Use rollover state to determine background shade so all
				// primary buttons share consistent hover behavior.
				boolean hover = getModel().isRollover() || getModel().isPressed();
				g2.setColor(hover ? hoverColor : normalColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
				g2.dispose();
				super.paintComponent(g);
			}
		};

		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setForeground(Color.WHITE);
		button.setFont(button.getFont().deriveFont(Font.BOLD, 16f));
		// Slightly smaller vertical padding to reduce overall button height
		button.setBorder(new EmptyBorder(6, 24, 6, 24));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setHorizontalAlignment(JButton.CENTER);

		return button;
	}
}
