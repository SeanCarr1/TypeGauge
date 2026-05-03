package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Middle navigation screen between Home and feature-specific pages.
 *
 * <p>Provides five cards matching key sidebar features so users can jump
 * directly to Test, Results, Accuracy, Feedback, and About.
 */
public class FeatureHubPanel extends JPanel {

	private final TypeGaugeFrame frame;

	public FeatureHubPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(180, 400, 40, 400));
		setOpaque(false);

		buildHeader();
		buildCards();
	}

	private void buildHeader() {
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		JButton backButton = UiButtons.createPrimaryButton("Return");
		backButton.addActionListener(e -> frame.showMainUi());
		backButton.setAlignmentX(LEFT_ALIGNMENT);
		header.add(backButton);
		header.add(Box.createVerticalStrut(18));

		JPanel titlePanel = new JPanel(new GridLayout(2, 1));
		titlePanel.setOpaque(false);
		titlePanel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel smallTitle = new JLabel("FEATURE HUB", SwingConstants.LEFT);
		smallTitle.setForeground(new Color(90, 150, 255));
		smallTitle.setFont(smallTitle.getFont().deriveFont(14f));

		JLabel title = new JLabel("Choose Your Next Step", SwingConstants.LEFT);
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 40f));

		titlePanel.add(smallTitle);
		titlePanel.add(title);

		header.add(titlePanel);
		add(header, BorderLayout.NORTH);
	}

	private void buildCards() {
		JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 16, 0));
		cardsPanel.setOpaque(false);
		// Keep cards compact so the row does not stretch to the bottom.
		cardsPanel.setPreferredSize(new Dimension(0, 280));

		cardsPanel.add(createFeatureCard(
			"<html>Difficulty<br>Selection</html>",
			"Return to difficulty selection and overview controls.",
			"/ui/icons/nav_home.png",
			() -> frame.showHome()));

		cardsPanel.add(createFeatureCard(
			"<html>Typing Test</html>",
			"Open the typing test and start or continue a session.",
			"/ui/icons/nav_target.png",
			() -> frame.showTest()));

		cardsPanel.add(createFeatureCard(
			"<html>Results<br>Examiner</html>",
			"View your session metrics and summary dashboard.",
			"/ui/icons/nav_check.png",
			() -> openStatsScreen("results")));

		cardsPanel.add(createFeatureCard(
			"<html>Accuracy<br>Analyzer</html>",
			"Inspect character-level precision and error patterns.",
			"/ui/icons/nav_bars.png",
			() -> openStatsScreen("accuracy")));

		cardsPanel.add(createFeatureCard(
			"<html>Feedback<br>Generator</html>",
			"Read coaching guidance and next practice goals.",
			"/ui/icons/nav_history.png",
			() -> openStatsScreen("feedback")));


		JPanel centerWrapper = new JPanel(new BorderLayout());
		centerWrapper.setOpaque(false);
		centerWrapper.add(cardsPanel, BorderLayout.NORTH);

		add(centerWrapper, BorderLayout.CENTER);
	}

	private JButton createFeatureCard(String title, String description, String iconPath, Runnable action) {
		JButton card = new JButton() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(25, 25, 25, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
				g2.dispose();
				super.paintComponent(g);
			}
		};

		card.setLayout(new BorderLayout());
		card.setContentAreaFilled(false);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(16, 16, 16, 16)));
		card.setBorderPainted(false);
		card.setFocusPainted(false);
		card.setOpaque(false);

		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		ImageIcon icon = UiIcons.loadIcon(iconPath, 22, 22);
		if (icon != null) {
			JLabel iconLabel = new JLabel(icon);
			iconLabel.setAlignmentX(LEFT_ALIGNMENT);
			content.add(iconLabel);
			content.add(Box.createVerticalStrut(14));
		}

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		content.add(titleLabel);
		content.add(Box.createVerticalStrut(8));

		JLabel descLabel = new JLabel("<html>" + description + "</html>");
		descLabel.setForeground(new Color(200, 200, 200));
		descLabel.setFont(descLabel.getFont().deriveFont(13f));
		descLabel.setAlignmentX(LEFT_ALIGNMENT);
		content.add(descLabel);

		card.add(content, BorderLayout.CENTER);
		card.addActionListener(e -> action.run());
		return card;
	}

	private void openStatsScreen(String route) {
		if (frame.getCurrentStats() == null) {
			frame.showSessionRequiredDialog();
			return;
		}

		switch (route) {
		case "results" -> frame.showResults();
		case "accuracy" -> frame.showAccuracy();
		case "feedback" -> frame.showFeedback();
		default -> {
		}
		}
	}
}
