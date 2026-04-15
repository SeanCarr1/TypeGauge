package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * Lightweight reusable panel that paints a rounded translucent background.
 *
 * <p>Other screens compose this class to get a consistent "glass" card look
 * without duplicating custom paint logic.
 */
public class GlassCardPanel extends JPanel {

	private final int arc;
	private final Color fillColor;

	public GlassCardPanel(int arc, Color fillColor) {
		this.arc = arc;
		this.fillColor = fillColor;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(fillColor);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
		g2.dispose();
		super.paintComponent(g);
	}
}
