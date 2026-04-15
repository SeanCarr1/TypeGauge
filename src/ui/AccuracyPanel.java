package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import model.SessionStats;

import ui.GlassCardPanel;
import ui.UiButtons;

/**
 * Accuracy analysis screen shown after a session is completed.
 *
 * <p>Displays high-level precision metrics plus a character-level visualization
 * derived from {@code SessionStats}. Navigation leads back to results or main menu.
 */
public class AccuracyPanel extends JPanel {

	private final TypeGaugeFrame frame;
	private final JTextArea analysisArea;
	private SessionStats lastStats;

	public AccuracyPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(200, 400, 40, 400));
		setOpaque(false);

		// Header: Accuracy Analyzer / Precision Breakdown + Back button
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		JPanel headerLeft = new JPanel(new GridLayout(2, 1));
		headerLeft.setOpaque(false);

		JLabel smallTitle = new JLabel("ACCURACY ANALYZER", SwingConstants.LEFT);
		smallTitle.setForeground(new Color(90, 150, 255));
		smallTitle.setFont(smallTitle.getFont().deriveFont(14f));
		headerLeft.add(smallTitle);

		JLabel title = new JLabel("Precision Breakdown", SwingConstants.LEFT);
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(36f));
		headerLeft.add(title);

		header.add(headerLeft, BorderLayout.WEST);

		JButton backButton = UiButtons.createPrimaryButton("Back to Results");
		backButton.addActionListener(e -> frame.showResults());
		JButton returnToMainButton = UiButtons.createPrimaryButton("Return to Main");
		returnToMainButton.addActionListener(e -> frame.showMainUi());
		JPanel headerButtons = new JPanel();
		headerButtons.setOpaque(false);
		headerButtons.add(backButton);
		headerButtons.add(returnToMainButton);
		header.add(headerButtons, BorderLayout.EAST);

		add(header, BorderLayout.NORTH);

		// Container to keep the main card from stretching to the bottom
		JPanel center = new JPanel();
		center.setOpaque(false);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		// Main analysis card
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(20, 24, 20, 24)));
		// Cap the card height so it doesn't fill the entire window
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 460));

		// Top row inside card: Overall Accuracy and Total Errors
		JPanel topRow = new JPanel(new BorderLayout());
		topRow.setOpaque(false);
		topRow.setBorder(new EmptyBorder(0, 0, 16, 0));

		JPanel overallPanel = new JPanel(new GridLayout(2, 1));
		overallPanel.setOpaque(false);

		JLabel overallLabel = new JLabel("OVERALL ACCURACY", SwingConstants.LEFT);
		overallLabel.setForeground(new Color(160, 160, 160));
		overallLabel.setFont(overallLabel.getFont().deriveFont(12f));
		overallPanel.add(overallLabel);

		JLabel overallValue = new JLabel("0%", SwingConstants.LEFT);
		overallValue.setForeground(new Color(0, 220, 140));
		overallValue.setFont(overallValue.getFont().deriveFont(48f));
		overallPanel.add(overallValue);

		topRow.add(overallPanel, BorderLayout.WEST);

		JPanel errorsPanel = new JPanel(new GridLayout(2, 1));
		errorsPanel.setOpaque(false);
		errorsPanel.setBorder(new EmptyBorder(0, 0, 0, 8));

		JLabel errorsLabel = new JLabel("TOTAL ERRORS", SwingConstants.RIGHT);
		errorsLabel.setForeground(new Color(160, 160, 160));
		errorsLabel.setFont(errorsLabel.getFont().deriveFont(12f));
		errorsPanel.add(errorsLabel);

		JLabel errorsValue = new JLabel("0", SwingConstants.RIGHT);
		errorsValue.setForeground(new Color(240, 80, 80));
		errorsValue.setFont(errorsValue.getFont().deriveFont(48f));
		errorsPanel.add(errorsValue);

		topRow.add(errorsPanel, BorderLayout.EAST);

		card.add(topRow, BorderLayout.NORTH);

		// Middle: Character-by-character analysis area
		JPanel middle = new JPanel(new BorderLayout());
		middle.setOpaque(false);
		middle.setBorder(new EmptyBorder(8, 0, 16, 0));

		JLabel cbcLabel = new JLabel("Character-by-Character Analysis", SwingConstants.LEFT);
		cbcLabel.setForeground(Color.WHITE);
		cbcLabel.setFont(cbcLabel.getFont().deriveFont(18f));
		middle.add(cbcLabel, BorderLayout.NORTH);

		analysisArea = new JTextArea();
		analysisArea.setEditable(false);
		analysisArea.setLineWrap(true);
		analysisArea.setWrapStyleWord(true);
		analysisArea.setFont(analysisArea.getFont().deriveFont(24f));
		analysisArea.setForeground(new Color(0, 220, 140));
		analysisArea.setOpaque(false);
		analysisArea.setBorder(new EmptyBorder(16, 16, 16, 16));

		JScrollPane analysisScroll = new JScrollPane(analysisArea);
		analysisScroll.setOpaque(false);
		analysisScroll.getViewport().setOpaque(false);
		analysisScroll.setBorder(null);

		middle.add(analysisScroll, BorderLayout.CENTER);

		card.add(middle, BorderLayout.CENTER);

		// Bottom: Strengths and Error Hotspots cards
		JPanel bottomRow = new JPanel(new GridLayout(1, 2, 16, 0));
		bottomRow.setOpaque(false);
		bottomRow.setBorder(new EmptyBorder(8, 0, 0, 0));

		JPanel strengthsCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		strengthsCard.setLayout(new BorderLayout());
		strengthsCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel strengthsTitle = new JLabel("Strengths");
		strengthsTitle.setForeground(new Color(0, 220, 140));
		strengthsTitle.setFont(strengthsTitle.getFont().deriveFont(16f));
		strengthsCard.add(strengthsTitle, BorderLayout.NORTH);

		JTextArea strengthsText = new JTextArea();
		strengthsText.setEditable(false);
		strengthsText.setOpaque(false);
		strengthsText.setLineWrap(true);
		strengthsText.setWrapStyleWord(true);
		strengthsText.setForeground(new Color(200, 200, 200));
		strengthsText.setFont(strengthsText.getFont().deriveFont(14f));
		strengthsText.setText("Consistent rhythm on common character sequences\n"
			+ "High accuracy on vowel-heavy words\n"
			+ "Strong performance on the first half of the text");

		strengthsCard.add(strengthsText, BorderLayout.CENTER);

		JPanel hotspotsCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		hotspotsCard.setLayout(new BorderLayout());
		hotspotsCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel hotspotsTitle = new JLabel("Error Hotspots");
		hotspotsTitle.setForeground(new Color(240, 80, 80));
		hotspotsTitle.setFont(hotspotsTitle.getFont().deriveFont(16f));
		hotspotsCard.add(hotspotsTitle, BorderLayout.NORTH);

		JTextArea hotspotsText = new JTextArea();
		hotspotsText.setEditable(false);
		hotspotsText.setOpaque(false);
		hotspotsText.setLineWrap(true);
		hotspotsText.setWrapStyleWord(true);
		hotspotsText.setForeground(new Color(200, 200, 200));
		hotspotsText.setFont(hotspotsText.getFont().deriveFont(14f));
		hotspotsText.setText("Punctuation and special characters\n"
			+ "Double-letter transitions\n"
			+ "End-of-sentence fatigue detected");

		hotspotsCard.add(hotspotsText, BorderLayout.CENTER);

		bottomRow.add(strengthsCard);
		bottomRow.add(hotspotsCard);

		card.add(bottomRow, BorderLayout.SOUTH);

		// Stack card vertically and push extra space below it
		center.add(Box.createVerticalStrut(8));
		card.setAlignmentX(LEFT_ALIGNMENT);
		center.add(card);
		center.add(Box.createVerticalGlue());

		add(center, BorderLayout.CENTER);

		// Store labels we need to update later via showStats
		this.lastStats = null;
	}

	public void showStats(SessionStats stats) {
		// Keep last stats reference for potential future enhancements.
		lastStats = stats;
		if (stats == null) {
			analysisArea.setText("");
			return;
		}
		// Build a simple character-by-character string: correct chars as typed, incorrect shown from target
		StringBuilder sb = new StringBuilder();
		String target = stats.getTargetText() != null ? stats.getTargetText() : "";
		String input = stats.getUserInput() != null ? stats.getUserInput() : "";
		int length = Math.min(target.length(), input.length());
		for (int i = 0; i < length; i++) {
			char c = input.charAt(i);
			if (c == target.charAt(i)) {
				sb.append(c);
			} else {
				// Use target char for visual clarity when showing mistakes
				sb.append(target.charAt(i));
			}
		}
		if (target.length() > length) {
			sb.append(target.substring(length));
		}
		analysisArea.setText(sb.toString());
	}
}
