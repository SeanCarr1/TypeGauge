package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.BitSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import model.SessionStats;

/**
 * Accuracy analysis screen shown after a session is completed.
 *
 * <p>Displays high-level precision metrics plus a character-level visualization
 * derived from {@code SessionStats}. Navigation leads back to results or main menu.
 */
public class AccuracyPanel extends JPanel {

	private final JTextPane analysisArea;
	private final JTextArea strengthsTextArea;
	private final JTextArea hotspotsTextArea;

	private final JButton analyzeButton;
	private SessionStats currentPendingStats;
	private int animationTick = 0;
	private final Timer analysisTimer;

	public AccuracyPanel(TypeGaugeFrame frame) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(200, 400, 40, 400));
		setOpaque(false);

		// Header: Accuracy Analyzer / Precision Breakdown + Back button
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		JPanel headerLeft = new JPanel(new GridLayout(2, 1));
		headerLeft.setOpaque(false);

		JLabel smallTitle = new JLabel("SKILL INSIGHTS", SwingConstants.LEFT);
		smallTitle.setForeground(new Color(90, 150, 255));
		smallTitle.setFont(smallTitle.getFont().deriveFont(14f));
		headerLeft.add(smallTitle);

		JLabel title = new JLabel("Accuracy Analyzer", SwingConstants.LEFT);
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(36f));
		headerLeft.add(title);

		header.add(headerLeft, BorderLayout.WEST);

		JButton backButton = UiButtons.createPrimaryButton("Return to Dashboard");
		backButton.addActionListener(e -> frame.showFeatureHub());
		JButton returnToMainButton = UiButtons.createPrimaryButton("Return to Main");
		returnToMainButton.addActionListener(e -> frame.showMainUi());
		JButton instructionsButton = UiButtons.createPrimaryButton("Instructions");
		instructionsButton.addActionListener(e -> frame.showAccuracyInstructions());

		analyzeButton = UiButtons.createPrimaryButton("Analyze");
		analyzeButton.setVisible(false);
		analyzeButton.addActionListener(e -> startAnalysisAnimation());

		JPanel headerButtons = new JPanel();
		headerButtons.setOpaque(false);
		headerButtons.add(instructionsButton);
		headerButtons.add(analyzeButton);
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

		// Middle: Character-by-character analysis area
		JPanel middle = new JPanel(new BorderLayout());
		middle.setOpaque(false);
		middle.setBorder(new EmptyBorder(8, 0, 16, 0));

		JLabel cbcLabel = new JLabel("Character-by-Character Analysis", SwingConstants.LEFT);
		cbcLabel.setForeground(Color.WHITE);
		cbcLabel.setFont(cbcLabel.getFont().deriveFont(18f));
		middle.add(cbcLabel, BorderLayout.NORTH);

		analysisArea = new JTextPane();
		analysisArea.setEditable(false);
		analysisArea.setOpaque(false);
		analysisArea.setFont(analysisArea.getFont().deriveFont(24f));
		analysisArea.setForeground(new Color(0, 220, 140));
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
		strengthsTextArea = strengthsText;

		strengthsCard.add(strengthsTextArea, BorderLayout.CENTER);

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
		hotspotsTextArea = hotspotsText;

		hotspotsCard.add(hotspotsTextArea, BorderLayout.CENTER);

		bottomRow.add(strengthsCard);
		bottomRow.add(hotspotsCard);

		card.add(bottomRow, BorderLayout.SOUTH);

		// Stack card vertically and push extra space below it
		center.add(Box.createVerticalStrut(8));
		card.setAlignmentX(LEFT_ALIGNMENT);
		center.add(card);
		center.add(Box.createVerticalGlue());

		add(center, BorderLayout.CENTER);
		strengthsTextArea.setText("Waiting for a completed session.");
		hotspotsTextArea.setText("Waiting for a completed session.");

		analysisTimer = new Timer(200, e -> handleAnalysisTick());
	}

	private void startAnalysisAnimation() {
		analyzeButton.setEnabled(false);
		animationTick = 0;
		analysisTimer.start();
	}

	private void handleAnalysisTick() {
		animationTick++;
		String[] dots = { "", ".", "..", "..." };
		analyzeButton.setText("Analyzing" + dots[animationTick % 4]);

		if (animationTick >= 10) {
			analysisTimer.stop();
			analyzeButton.setVisible(false);
			if (currentPendingStats != null) {
				applyStatsToUi(currentPendingStats);
			}
		}
	}

	public void showStats(SessionStats stats) {
		if (stats == null) {
			return;
		}
		this.currentPendingStats = stats;

		// Reset UI to "Processing" state
		renderTargetText("", "", new BitSet());
		strengthsTextArea.setText("Analysis in progress...");
		hotspotsTextArea.setText("Analysis in progress...");

		analyzeButton.setText("Analyze");
		analyzeButton.setEnabled(true);
		analyzeButton.setVisible(true);
	}

	private void applyStatsToUi(SessionStats stats) {
		if (stats == null) {
			renderTargetText("", "", new BitSet());
			strengthsTextArea.setText("No session data available.");
			hotspotsTextArea.setText("No session data available.");
			return;
		}

		// Render the target text itself so the user can compare it against their input.
		renderTargetText(stats.getTargetText(), stats.getUserInput(), stats.getMistakenCharacterIndexes());
		strengthsTextArea.setText(buildStrengthsText(stats, stats.getTargetText()));
		hotspotsTextArea.setText(buildHotspotsText(stats));
	}

	private void renderTargetText(String targetText, String userInput, BitSet mistakenCharacterIndexes) {
		// Rebuild the styled document so the target passage stays visible and history-based mistakes stay red.
		StyledDocument document = analysisArea.getStyledDocument();
		try {
			document.remove(0, document.getLength());
		} catch (javax.swing.text.BadLocationException ignored) {
		}

		String safeTarget = targetText != null ? targetText : "";
		String safeInput = userInput != null ? userInput : "";
		BitSet safeMistakes = mistakenCharacterIndexes != null ? mistakenCharacterIndexes : new BitSet();
		int length = safeTarget.length();
		for (int i = 0; i < length; i++) {
			char targetChar = safeTarget.charAt(i);
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			boolean wasMistaken = safeMistakes.get(i);
			if (wasMistaken) {
				StyleConstants.setForeground(attributes, new Color(230, 80, 80));
			} else if (i < safeInput.length()) {
				if (safeInput.charAt(i) == targetChar) {
					StyleConstants.setForeground(attributes, new Color(80, 200, 140));
				} else {
					StyleConstants.setForeground(attributes, new Color(230, 80, 80));
				}
			} else {
				StyleConstants.setForeground(attributes, new Color(180, 180, 180));
			}

			String displayChar;
			if (targetChar == ' ' && wasMistaken) {
				displayChar = "·";
			} else {
				displayChar = String.valueOf(targetChar);
			}
			try {
				document.insertString(document.getLength(), displayChar, attributes);
			} catch (javax.swing.text.BadLocationException ignored) {
			}
		}
	}

	private String buildStrengthsText(SessionStats stats, String targetText) {
		   StringBuilder strengths = new StringBuilder();
		   strengths.append("Steady control on: ");
		   if (stats.getAccuracy() == 100) {
			   strengths.append("flawless accuracy, ");
		   } else if (stats.getAccuracy() >= 90) {
			   strengths.append("overall accuracy, ");
		   }
		   if (stats.getWpm() >= 30) {
			   strengths.append("typing pace, ");
		   }
		   if (stats.getErrors() <= 3) {
			   strengths.append("error control, ");
		   }
		   strengths.append("and consistent focus.\n");
		   strengths.append("Best handled category: ").append(getBestCategoryLabel(stats, targetText)).append("\n");
		   strengths.append("Performance grade: ").append(stats.getPerformanceGrade() != null ? stats.getPerformanceGrade() : "-");
		   return strengths.toString();
	   }

	private String buildHotspotsText(SessionStats stats) {
		   String targetText = stats.getTargetText();
		   int letterCount = 0, numberCount = 0, punctuationCount = 0, spacingCount = 0;
		   if (targetText != null) {
			   for (int i = 0; i < targetText.length(); i++) {
				   char c = targetText.charAt(i);
				   if (Character.isLetter(c)) letterCount++;
				   else if (Character.isDigit(c)) numberCount++;
				   else if (c == ' ') spacingCount++;
				   else if (!Character.isWhitespace(c)) punctuationCount++;
			   }
		   }
		   StringBuilder hotspots = new StringBuilder();
		   hotspots.append("Letters: ").append(letterCount > 0 ? stats.getLetterErrors() : "N/A").append("\n");
		   hotspots.append("Numbers: ").append(numberCount > 0 ? stats.getNumberErrors() : "N/A").append("\n");
		   hotspots.append("Punctuation: ").append(punctuationCount > 0 ? stats.getPunctuationErrors() : "N/A").append("\n");
		   hotspots.append("Spacing: ").append(spacingCount > 0 ? stats.getSpacingErrors() : "N/A").append("\n\n");
		   hotspots.append("Top hotspot: ").append(getDominantCategoryLabel(stats));
		   return hotspots.toString();
	   }
	
	private String getDominantCategoryKey(SessionStats stats) {
		int letterErrors = stats.getLetterErrors();
		int numberErrors = stats.getNumberErrors();
		int punctuationErrors = stats.getPunctuationErrors();
		int spacingErrors = stats.getSpacingErrors();

		if (stats.getErrors() == 0) {
			return null;
		}
		if (spacingErrors >= letterErrors && spacingErrors >= numberErrors && spacingErrors >= punctuationErrors) {
			return "spacing";
		}
		if (punctuationErrors >= letterErrors && punctuationErrors >= numberErrors) {
			return "punctuation";
		}
		if (numberErrors >= letterErrors) {
			return "numbers";
		}
		return "letters";
	}

	private String getDominantCategoryLabel(SessionStats stats) {
		String category = getDominantCategoryKey(stats);
		if (category == null) {
			return "None";
		}
		if ("spacing".equals(category)) {
			return "Spacing";
		}
		if ("punctuation".equals(category)) {
			return "Punctuation";
		}
		if ("numbers".equals(category)) {
			return "Numbers";
		}
		return "Letters";
	}

	private String getBestCategoryLabel(SessionStats stats, String targetText) {
		   if (stats.getAccuracy() == 100) return "All (100% Match)";
		   // Count presence of each category in the target text
		   int letterCount = 0, numberCount = 0, punctuationCount = 0, spacingCount = 0;
		   if (targetText != null) {
			   for (int i = 0; i < targetText.length(); i++) {
				   char c = targetText.charAt(i);
				   if (Character.isLetter(c)) letterCount++;
				   else if (Character.isDigit(c)) numberCount++;
				   else if (c == ' ') spacingCount++;
				   else if (!Character.isWhitespace(c)) punctuationCount++;
			   }
		   }
		   // Build arrays for present categories and their error counts
		   java.util.List<String> present = new java.util.ArrayList<>();
		   java.util.List<Integer> errors = new java.util.ArrayList<>();
		   if (letterCount > 0) {
			   present.add("Letters");
			   errors.add(stats.getLetterErrors());
		   }
		   if (numberCount > 0) {
			   present.add("Numbers");
			   errors.add(stats.getNumberErrors());
		   }
		   if (punctuationCount > 0) {
			   present.add("Punctuation");
			   errors.add(stats.getPunctuationErrors());
		   }
		   if (spacingCount > 0) {
			   present.add("Spacing");
			   errors.add(stats.getSpacingErrors());
		   }
		   if (present.isEmpty()) return "-";
		   // Find the present category with the lowest error count
		   int minIdx = 0;
		   for (int i = 1; i < errors.size(); i++) {
			   if (errors.get(i) < errors.get(minIdx)) minIdx = i;
		   }
		   return present.get(minIdx);
	   }
}
