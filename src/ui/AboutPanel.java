package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import ui.GlassCardPanel;
import ui.UiButtons;

/**
 * About screen describing TypeGauge goals and core principles.
 *
 * <p>This panel follows the same glass-card visual system used by the rest of
 * the UI and provides a single back action to return to the dashboard/home flow.
 */
public class AboutPanel extends JPanel {

	private final TypeGaugeFrame frame;

	public AboutPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setLayout(new BorderLayout());
		// Extra top padding so the content floats closer to the center
		setBorder(new EmptyBorder(200, 400, 40, 400));
		setOpaque(false);

		// Vertical stack for title + main card so it doesn't stretch
		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		JLabel titleLabel = new JLabel("About TypeGauge", SwingConstants.LEFT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(36f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		content.add(titleLabel);
		content.add(Box.createVerticalStrut(16));

		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(20, 24, 20, 24)));
		// Cap the card height so it doesn't reach the bottom
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

		JTextArea description = new JTextArea();
		description.setEditable(false);
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setOpaque(false);
		description.setForeground(new Color(210, 210, 210));
		description.setFont(description.getFont().deriveFont(16f));
		description.setText(
			"TypeGauge is a professional-grade typing diagnostic tool designed for developers, writers, " +
			"and anyone looking to master their digital communication speed.\n\n" +
			"Our core philosophy is built on three pillars: Precision, Analytics, and Privacy. We believe " +
			"that improvement starts with accurate measurement, which is why our engine tracks every " +
			"keystroke with millisecond precision.");

		card.add(description, BorderLayout.CENTER);

		JPanel pillarsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
		pillarsPanel.setOpaque(false);
		pillarsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

		JPanel privacyCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		privacyCard.setLayout(new BorderLayout());
		privacyCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(12, 12, 12, 12)));

		JLabel privacyTitle = new JLabel("Privacy First", SwingConstants.LEFT);
		privacyTitle.setForeground(Color.WHITE);
		privacyTitle.setFont(privacyTitle.getFont().deriveFont(Font.BOLD, 16f));
		privacyCard.add(privacyTitle, BorderLayout.NORTH);

		JTextArea privacyText = new JTextArea("Your typing data never leaves your machine. We don't save or track your sessions.");
		privacyText.setEditable(false);
		privacyText.setOpaque(false);
		privacyText.setLineWrap(true);
		privacyText.setWrapStyleWord(true);
		privacyText.setForeground(new Color(200, 200, 200));
		privacyText.setFont(privacyText.getFont().deriveFont(12f));
		privacyCard.add(privacyText, BorderLayout.CENTER);

		JPanel openCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		openCard.setLayout(new BorderLayout());
		openCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(12, 12, 12, 12)));

		JLabel openTitle = new JLabel("Open Standard", SwingConstants.LEFT);
		openTitle.setForeground(Color.WHITE);
		openTitle.setFont(openTitle.getFont().deriveFont(Font.BOLD, 16f));
		openCard.add(openTitle, BorderLayout.NORTH);

		JTextArea openText = new JTextArea("Built using modern technologies for the best performance and accessibility.");
		openText.setEditable(false);
		openText.setOpaque(false);
		openText.setLineWrap(true);
		openText.setWrapStyleWord(true);
		openText.setForeground(new Color(200, 200, 200));
		openText.setFont(openText.getFont().deriveFont(12f));
		openCard.add(openText, BorderLayout.CENTER);

		pillarsPanel.add(privacyCard);
		pillarsPanel.add(openCard);

		card.add(pillarsPanel, BorderLayout.SOUTH);

		card.setAlignmentX(LEFT_ALIGNMENT);
		content.add(card);

		// Button row directly below the card
		content.add(Box.createVerticalStrut(24));
		JButton backButton = UiButtons.createPrimaryButton("Back to Dashboard");
		backButton.addActionListener(e -> frame.showHome());
		JPanel buttonRow = new JPanel(new BorderLayout());
		buttonRow.setOpaque(false);
		buttonRow.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonRow.add(backButton, BorderLayout.WEST);
		// Limit the row height so BoxLayout doesn't stretch it
		int buttonHeight = backButton.getPreferredSize().height;
		buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonHeight + 8));
		buttonRow.setAlignmentX(LEFT_ALIGNMENT);
		content.add(buttonRow);

		add(content, BorderLayout.CENTER);
	}
}
