package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.SessionStats;

/**
 * Results dashboard displayed after a typing session ends.
 *
 * <p>Shows core metrics (speed, accuracy, errors, time) and links to deeper
 * analysis screens.
 */
public class ResultsPanel extends JPanel {

	private final JLabel wpmValueLabel;
	private final JLabel accuracyValueLabel;
	private final JLabel errorsValueLabel;
	private final JLabel timeValueLabel;
	private final JLabel difficultyValueLabel;
	private final JLabel charsValueLabel;
	private final JLabel sessionQualityValueLabel;

	public ResultsPanel(Runnable onReturnHome, Runnable onRetry, Runnable onOpenAccuracy, Runnable onOpenFeedback,
		Runnable onOpenInstructions) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(200, 400, 40, 400));
		setOpaque(false);

		// Header row: Diagnostic Complete / Performance Summary + Home / Retry buttons
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		JPanel headerLeft = new JPanel(new GridLayout(2, 1));
		headerLeft.setOpaque(false);

		JLabel diagnosticLabel = new JLabel("DIAGNOSTIC COMPLETE", SwingConstants.LEFT);
		diagnosticLabel.setForeground(new Color(90, 150, 255));
		diagnosticLabel.setFont(diagnosticLabel.getFont().deriveFont(14f));
		headerLeft.add(diagnosticLabel);

		JLabel titleLabel = new JLabel("Performance Summary", SwingConstants.LEFT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(36f));
		headerLeft.add(titleLabel);

		header.add(headerLeft, BorderLayout.WEST);

		JPanel headerButtons = new JPanel();
		headerButtons.setOpaque(false);

		JButton homeButton = UiButtons.createPrimaryButton("Return to Main");
		homeButton.addActionListener(e -> {
			if (onReturnHome != null) {
				onReturnHome.run();
			}
		});
		JButton retryButton = UiButtons.createPrimaryButton("Retry Test");
		retryButton.addActionListener(e -> {
			if (onRetry != null) {
				onRetry.run();
			}
		});
		JButton instructionsButton = UiButtons.createPrimaryButton("Instructions");
		instructionsButton.addActionListener(e -> {
			if (onOpenInstructions != null) {
				onOpenInstructions.run();
			}
		});

		headerButtons.add(homeButton);
		headerButtons.add(retryButton);
		headerButtons.add(instructionsButton);

		header.add(headerButtons, BorderLayout.EAST);

		add(header, BorderLayout.NORTH);

		// Center area: stats row + two summary cards (non-stretching cards)
		JPanel center = new JPanel();
		center.setOpaque(false);
		center.setLayout(new javax.swing.BoxLayout(center, javax.swing.BoxLayout.Y_AXIS));

		// Top stats row: four cards
		JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
		statsRow.setOpaque(false);
		statsRow.setBorder(new EmptyBorder(0, 0, 24, 0));
		// Limit how tall the stats cards can grow
		statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

		JPanel speedCard = UiCards.createGlassStatCard("SPEED", "WPM", null,
			wpmValueLabel = new JLabel("0", SwingConstants.CENTER));
		JPanel accuracyCard = UiCards.createGlassStatCard("ACCURACY", "Precision", null,
			accuracyValueLabel = new JLabel("0%", SwingConstants.CENTER));
		JPanel errorsCard = UiCards.createGlassStatCard("ERRORS", "Keystrokes", null,
			errorsValueLabel = new JLabel("0", SwingConstants.CENTER));
		JPanel timeCard = UiCards.createGlassStatCard("TIME", "Duration", null,
			timeValueLabel = new JLabel("0s", SwingConstants.CENTER));

		statsRow.add(speedCard);
		statsRow.add(accuracyCard);
		statsRow.add(errorsCard);
		statsRow.add(timeCard);

		statsRow.setAlignmentX(LEFT_ALIGNMENT);
		center.add(statsRow);
		center.add(javax.swing.Box.createVerticalStrut(12));

		// Bottom row: Self-Assessment and Expert Feedback (with max height)
		JPanel bottomRow = new JPanel(new GridLayout(1, 2, 24, 0));
		bottomRow.setOpaque(false);
		bottomRow.setAlignmentX(LEFT_ALIGNMENT);
		bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

		JPanel selfAssessmentCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		selfAssessmentCard.setLayout(new BorderLayout());
		selfAssessmentCard.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JPanel selfHeader = new JPanel(new BorderLayout());
		selfHeader.setOpaque(false);
		JLabel selfTitle = new JLabel("Self-Assessment", SwingConstants.LEFT);
		selfTitle.setForeground(Color.WHITE);
		selfTitle.setFont(selfTitle.getFont().deriveFont(20f));
		selfHeader.add(selfTitle, BorderLayout.WEST);
		JButton detailedAnalysisButton = UiButtons.createPrimaryButton("Detailed Analysis ▶");
		detailedAnalysisButton.addActionListener(e -> {
			if (onOpenAccuracy != null) {
				onOpenAccuracy.run();
			}
		});
		selfHeader.add(detailedAnalysisButton, BorderLayout.EAST);
		selfAssessmentCard.add(selfHeader, BorderLayout.NORTH);

		JPanel selfStats = new JPanel(new GridLayout(3, 2, 8, 8));
		selfStats.setOpaque(false);
		selfStats.setBorder(new EmptyBorder(12, 0, 0, 0));

		JLabel difficultyLabel = new JLabel("Difficulty Level");
		difficultyLabel.setForeground(new Color(180, 180, 180));
		difficultyLabel.setFont(difficultyLabel.getFont().deriveFont(16f));
		difficultyValueLabel = new JLabel("-");
		difficultyValueLabel.setForeground(new Color(90, 150, 255));

		JLabel charsLabel = new JLabel("Total Characters");
		charsLabel.setForeground(new Color(180, 180, 180));
		charsLabel.setFont(charsLabel.getFont().deriveFont(16f));
		charsValueLabel = new JLabel("0");
		charsValueLabel.setForeground(Color.WHITE);

		JLabel sessionQualityLabel = new JLabel("Session Quality");
		sessionQualityLabel.setForeground(new Color(180, 180, 180));
		sessionQualityLabel.setFont(sessionQualityLabel.getFont().deriveFont(16f));
		sessionQualityValueLabel = new JLabel("-");
		sessionQualityValueLabel.setForeground(new Color(0, 220, 140));

		selfStats.add(difficultyLabel);
		selfStats.add(difficultyValueLabel);
		selfStats.add(charsLabel);
		selfStats.add(charsValueLabel);
		selfStats.add(sessionQualityLabel);
		selfStats.add(sessionQualityValueLabel);

		selfAssessmentCard.add(selfStats, BorderLayout.CENTER);

		JPanel feedbackCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		feedbackCard.setLayout(new BorderLayout());
		feedbackCard.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JPanel feedbackHeader = new JPanel(new BorderLayout());
		feedbackHeader.setOpaque(false);
		JLabel feedbackTitle = new JLabel("Expert Feedback", SwingConstants.LEFT);
		feedbackTitle.setForeground(Color.WHITE);
		feedbackTitle.setFont(feedbackTitle.getFont().deriveFont(20f));
		feedbackHeader.add(feedbackTitle, BorderLayout.WEST);
		JButton fullReportButton = UiButtons.createPrimaryButton("Full Report ▶");
		fullReportButton.addActionListener(e -> {
			if (onOpenFeedback != null) {
				onOpenFeedback.run();
			}
		});
		feedbackHeader.add(fullReportButton, BorderLayout.EAST);
		feedbackCard.add(feedbackHeader, BorderLayout.NORTH);

		JLabel feedbackSummary = new JLabel("Master Typist", SwingConstants.LEFT);
		feedbackSummary.setForeground(new Color(0, 220, 140));
		feedbackSummary.setFont(feedbackSummary.getFont().deriveFont(24f));

		JLabel feedbackBody = new JLabel("Exceptional speed and precision. You're in the top tier of typists.");
		feedbackBody.setForeground(new Color(200, 200, 200));
		feedbackBody.setFont(feedbackBody.getFont().deriveFont(18f));
		feedbackBody.setBorder(new EmptyBorder(8, 0, 0, 0));

		JPanel feedbackCenter = new JPanel(new BorderLayout());
		feedbackCenter.setOpaque(false);
		feedbackCenter.add(feedbackSummary, BorderLayout.NORTH);
		feedbackCenter.add(feedbackBody, BorderLayout.CENTER);

		feedbackCard.add(feedbackCenter, BorderLayout.CENTER);

		bottomRow.add(selfAssessmentCard);
		bottomRow.add(feedbackCard);

		center.add(bottomRow);
		// Any remaining vertical space goes below the cards, not inside them
		center.add(javax.swing.Box.createVerticalGlue());

		add(center, BorderLayout.CENTER);
	}

	public void showStats(SessionStats stats) {
		// Guard against accidental navigation before any session is available.
		if (stats == null) {
			return;
		}
		wpmValueLabel.setText(String.valueOf(stats.getWpm()));
		accuracyValueLabel.setText(stats.getAccuracy() + "%");
		errorsValueLabel.setText(String.valueOf(stats.getErrors()));
		timeValueLabel.setText(stats.getTimeElapsedSeconds() + "s");
		difficultyValueLabel.setText(stats.getDifficulty() != null ? stats.getDifficulty().name() : "");
		charsValueLabel.setText(String.valueOf(stats.getTotalChars()));

		// Simple quality heuristic similar to web: high if accuracy >= 95 and wpm >= 40
		String quality = "Medium";
		if (stats.getAccuracy() >= 95 && stats.getWpm() >= 40) {
			quality = "High";
		} else if (stats.getAccuracy() < 80 || stats.getWpm() < 25) {
			quality = "Developing";
		}
		sessionQualityValueLabel.setText(quality);
	}
}
