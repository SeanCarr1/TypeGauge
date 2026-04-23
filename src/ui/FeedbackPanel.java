package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import model.FeedbackResult;
import model.SessionStats;

/**
 * Feedback screen that translates numeric stats into coaching-oriented guidance.
 *
 * <p>This panel receives both {@code SessionStats} and {@code FeedbackResult}
 * from the frame coordinator and renders actionable goals for the next run.
 */
public class FeedbackPanel extends JPanel {

	private final TypeGaugeFrame frame;
	private final JLabel analysisIntroLabel;
	private final JLabel feedbackMessageLabel;
	private final JLabel nextSpeedValueLabel;
	private final JLabel nextAccuracyValueLabel;
	private final JLabel nextSpeedDeltaLabel;
	private final JLabel nextAccuracyDeltaLabel;
	private final JTextArea summaryTextArea;

	public FeedbackPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(20, 400, 40, 400));
		setOpaque(false);

		// Header: Self-Assessment & Feedback / Expert Guidance + Back button
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(200, 0, 20, 0));

		JPanel headerLeft = new JPanel(new GridLayout(2, 1));
		headerLeft.setOpaque(false);

		JLabel smallTitle = new JLabel("SELF-ASSESSMENT & FEEDBACK", SwingConstants.LEFT);
		smallTitle.setForeground(new Color(90, 150, 255));
		smallTitle.setFont(smallTitle.getFont().deriveFont(14f));
		headerLeft.add(smallTitle);

		JLabel titleLabel = new JLabel("Feedback Generator", SwingConstants.LEFT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(36f));
		headerLeft.add(titleLabel);

		header.add(headerLeft, BorderLayout.WEST);

		JButton backButton = UiButtons.createPrimaryButton("Return to Dashboad");
		backButton.addActionListener(e -> frame.showFeatureHub());
		JButton returnToMainButton = UiButtons.createPrimaryButton("Return to Main");
		returnToMainButton.addActionListener(e -> frame.showMainUi());
		JPanel headerButtons = new JPanel();
		headerButtons.setOpaque(false);
		headerButtons.add(backButton);
		headerButtons.add(returnToMainButton);
		header.add(headerButtons, BorderLayout.EAST);

		add(header, BorderLayout.NORTH);

		// Center vertical container so cards don't stretch to the bottom
		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		// Row: left analysis card + right goal/summary column
		JPanel center = new JPanel(new BorderLayout());
		center.setOpaque(false);
		center.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

		// Left: Personalized Analysis card
		JPanel analysisCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		analysisCard.setLayout(new BorderLayout());
		analysisCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel analysisTitle = new JLabel("Personalized Analysis", SwingConstants.LEFT);
		analysisTitle.setForeground(Color.WHITE);
		analysisTitle.setFont(analysisTitle.getFont().deriveFont(24f));
		analysisCard.add(analysisTitle, BorderLayout.NORTH);

		JPanel analysisCenter = new JPanel(new BorderLayout());
		analysisCenter.setOpaque(false);
		analysisCenter.setBorder(new EmptyBorder(12, 0, 0, 0));

		analysisIntroLabel = new JLabel("", SwingConstants.LEFT);
		analysisIntroLabel.setForeground(new Color(200, 200, 200));
		analysisIntroLabel.setFont(analysisIntroLabel.getFont().deriveFont(16f));
		analysisIntroLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
		analysisCenter.add(analysisIntroLabel, BorderLayout.NORTH);

		feedbackMessageLabel = new JLabel("", SwingConstants.LEFT);
		feedbackMessageLabel.setForeground(new Color(220, 220, 220));
		feedbackMessageLabel.setFont(feedbackMessageLabel.getFont().deriveFont(18f));
		feedbackMessageLabel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(60, 60, 80)),
			new EmptyBorder(12, 12, 12, 12)));
		analysisCenter.add(feedbackMessageLabel, BorderLayout.CENTER);

		JPanel suggestionsPanel = new JPanel(new GridLayout(3, 1, 0, 12));
		suggestionsPanel.setOpaque(false);
		suggestionsPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

		JPanel speedCard = createSuggestionCard(
			new Color(0, 140, 100, 200),
			"⚡",
			"Speed Strategy",
			"Try to look ahead at the next word while finishing the current one to maintain momentum.");
		JPanel errorCard = createSuggestionCard(
			new Color(180, 60, 60, 200),
			"●",
			"Error Reduction",
			"Slow down by 5–10% on complex words to ensure 100% accuracy before speeding up again.");
		JPanel progressCard = createSuggestionCard(
			new Color(40, 90, 180, 200),
			"⟳",
			"Progress Encouragement",
			"Your speed is consistent. Focus on longer passages to build endurance.");

		suggestionsPanel.add(speedCard);
		suggestionsPanel.add(errorCard);
		suggestionsPanel.add(progressCard);

		JLabel suggestionsTitle = new JLabel("Constructive Suggestions", SwingConstants.LEFT);
		suggestionsTitle.setForeground(Color.WHITE);
		suggestionsTitle.setFont(suggestionsTitle.getFont().deriveFont(16f));

		JPanel suggestionsWrapper = new JPanel(new BorderLayout());
		suggestionsWrapper.setOpaque(false);
		suggestionsWrapper.add(suggestionsTitle, BorderLayout.NORTH);
		suggestionsWrapper.add(suggestionsPanel, BorderLayout.CENTER);

		analysisCenter.add(suggestionsWrapper, BorderLayout.SOUTH);

		analysisCard.add(analysisCenter, BorderLayout.CENTER);

		center.add(analysisCard, BorderLayout.CENTER);

		// Right column: Goal Setting and Summary of Strengths
		JPanel rightColumn = new JPanel(new BorderLayout());
		rightColumn.setOpaque(false);
		rightColumn.setBorder(new EmptyBorder(0, 16, 0, 0));

		// Goal Setting card
		JPanel goalCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		goalCard.setLayout(new BorderLayout());
		goalCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel goalTitle = new JLabel("Goal Setting", SwingConstants.LEFT);
		goalTitle.setForeground(Color.WHITE);
		goalTitle.setFont(goalTitle.getFont().deriveFont(20f));
		goalCard.add(goalTitle, BorderLayout.NORTH);

		JPanel goalBody = new JPanel(new GridLayout(2, 2, 8, 8));
		goalBody.setOpaque(false);
		goalBody.setBorder(new EmptyBorder(12, 0, 12, 0));

		JLabel nextSpeedLabel = new JLabel("Next Target Speed");
		nextSpeedLabel.setForeground(new Color(180, 180, 180));
		nextSpeedLabel.setFont(nextSpeedLabel.getFont().deriveFont(12f));
		nextSpeedValueLabel = new JLabel("0", SwingConstants.RIGHT);
		nextSpeedValueLabel.setForeground(Color.WHITE);
		nextSpeedValueLabel.setFont(nextSpeedValueLabel.getFont().deriveFont(30f));
		nextSpeedDeltaLabel = new JLabel("+0 WPM", SwingConstants.LEFT);
		nextSpeedDeltaLabel.setForeground(new Color(0, 220, 140));
		nextSpeedDeltaLabel.setFont(nextSpeedDeltaLabel.getFont().deriveFont(14f));
		JPanel speedValuePanel = new JPanel(new BorderLayout());
		speedValuePanel.setOpaque(false);
		speedValuePanel.add(nextSpeedValueLabel, BorderLayout.WEST);
		speedValuePanel.add(nextSpeedDeltaLabel, BorderLayout.EAST);

		JLabel nextAccuracyLabel = new JLabel("Accuracy Goal");
		nextAccuracyLabel.setForeground(new Color(180, 180, 180));
		nextAccuracyLabel.setFont(nextAccuracyLabel.getFont().deriveFont(12f));
		nextAccuracyValueLabel = new JLabel("0%", SwingConstants.RIGHT);
		nextAccuracyValueLabel.setForeground(Color.WHITE);
		nextAccuracyValueLabel.setFont(nextAccuracyValueLabel.getFont().deriveFont(30f));
		nextAccuracyDeltaLabel = new JLabel("+0%", SwingConstants.LEFT);
		nextAccuracyDeltaLabel.setForeground(new Color(0, 220, 140));
		nextAccuracyDeltaLabel.setFont(nextAccuracyDeltaLabel.getFont().deriveFont(14f));
		JPanel accuracyValuePanel = new JPanel(new BorderLayout());
		accuracyValuePanel.setOpaque(false);
		accuracyValuePanel.add(nextAccuracyValueLabel, BorderLayout.WEST);
		accuracyValuePanel.add(nextAccuracyDeltaLabel, BorderLayout.EAST);

		goalBody.add(nextSpeedLabel);
		goalBody.add(speedValuePanel);
		goalBody.add(nextAccuracyLabel);
		goalBody.add(accuracyValuePanel);

		goalCard.add(goalBody, BorderLayout.CENTER);

		JButton practiceButton = UiButtons.createPrimaryButton("Start Practice");
		practiceButton.addActionListener(e -> frame.retry());
		goalCard.add(practiceButton, BorderLayout.SOUTH);

		rightColumn.add(goalCard, BorderLayout.NORTH);

		// Summary of Strengths card
		JPanel summaryCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		summaryCard.setLayout(new BorderLayout());
		summaryCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel summaryTitle = new JLabel("Summary of Strengths", SwingConstants.LEFT);
		summaryTitle.setForeground(Color.WHITE);
		summaryTitle.setFont(summaryTitle.getFont().deriveFont(14f));
		summaryCard.add(summaryTitle, BorderLayout.NORTH);

		summaryTextArea = new JTextArea();
		summaryTextArea.setEditable(false);
		summaryTextArea.setOpaque(false);
		summaryTextArea.setLineWrap(true);
		summaryTextArea.setWrapStyleWord(true);
		summaryTextArea.setForeground(new Color(200, 200, 200));
		summaryTextArea.setFont(summaryTextArea.getFont().deriveFont(12f));

		summaryCard.add(summaryTextArea, BorderLayout.CENTER);

		rightColumn.add(summaryCard, BorderLayout.CENTER);

		center.add(rightColumn, BorderLayout.EAST);

		// Stack the row and push extra space below it
		content.add(Box.createVerticalStrut(8));
		center.setAlignmentX(LEFT_ALIGNMENT);
		content.add(center);
		content.add(Box.createVerticalGlue());

		add(content, BorderLayout.CENTER);
	}

	private JPanel createSuggestionCard(Color accentColor, String iconText, String title, String body) {
		// Small reusable card used in the constructive suggestions section.
		JPanel card = new GlassCardPanel(18, new Color(20, 20, 20, 200));
		card.setLayout(new BorderLayout());
		card.setBorder(new EmptyBorder(12, 16, 12, 16));

		JPanel iconWrapper = new JPanel(new BorderLayout());
		iconWrapper.setOpaque(false);
		JLabel iconLabel = new JLabel(iconText, SwingConstants.CENTER);
		iconLabel.setForeground(accentColor);
		iconLabel.setFont(iconLabel.getFont().deriveFont(18f));
		iconWrapper.setBorder(new EmptyBorder(0, 0, 0, 12));
		iconWrapper.add(iconLabel, BorderLayout.CENTER);

		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.setOpaque(false);
		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(textPanel.getFont().deriveFont(Font.BOLD, 14f));
		JLabel bodyLabel = new JLabel("<html>" + body + "</html>");
		bodyLabel.setForeground(new Color(200, 200, 200));
		bodyLabel.setFont(bodyLabel.getFont().deriveFont(12f));
		textPanel.add(titleLabel, BorderLayout.NORTH);
		textPanel.add(bodyLabel, BorderLayout.CENTER);

		card.add(iconWrapper, BorderLayout.WEST);
		card.add(textPanel, BorderLayout.CENTER);
		return card;
	}

	public void showFeedback(SessionStats stats, FeedbackResult feedback) {
		// Defensive guard: clear fields if data is unavailable.
		if (stats == null || feedback == null) {
			analysisIntroLabel.setText("");
			feedbackMessageLabel.setText("");
			nextSpeedValueLabel.setText("0");
			nextAccuracyValueLabel.setText("0%");
			summaryTextArea.setText("");
			return;
		}

		String difficultyText = stats.getDifficulty() != null ? stats.getDifficulty().name() : "your";
		analysisIntroLabel.setText("Based on your session at " + difficultyText
			+ " difficulty, our Rule-Based Analysis Engine has generated the following assessment:");

		feedbackMessageLabel.setText("\"" + feedback.getMessage() + "\"");

		int nextWpm = stats.getWpm() + 5;
		int nextAccuracy = Math.min(100, stats.getAccuracy() + 2);
		nextSpeedValueLabel.setText(String.valueOf(nextWpm));
		nextAccuracyValueLabel.setText(nextAccuracy + "%");
		nextSpeedDeltaLabel.setText("+" + (nextWpm - stats.getWpm()) + " WPM");
		nextAccuracyDeltaLabel.setText("+" + (nextAccuracy - stats.getAccuracy()) + "%");

		summaryTextArea.setText("Your ability to handle " + difficultyText
			+ " level text shows a strong foundation in touch typing. Your primary strength is maintaining a steady pace even when encountering unfamiliar words.");
	}
}
