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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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
 *
 * <p>Supports in-memory CRUD personal improvement goals:
 * <ul>
 *   <li>Create — set a target WPM and/or accuracy via the Goals card</li>
 *   <li>Read   — goals are shown with a live pass/fail indicator against the
 *                last analyzed session</li>
 *   <li>Update — "Edit Goals" reopens the form pre-filled with saved values</li>
 *   <li>Delete — "Clear Goals" removes both targets</li>
 * </ul>
 */
public class AccuracyPanel extends JPanel {

	// ── analysis widgets ─────────────────────────────────────────────────────────
	private final JTextPane analysisArea;
	private final JTextArea strengthsTextArea;
	private final JTextArea hotspotsTextArea;

	private SessionStats lastAnalyzedStats;

	// ── goals state ──────────────────────────────────────────────────────────────
	/**
	 * Simple value-object that holds the user's improvement targets.
	 * {@code null} means "no goal set for that metric".
	 */
	private Integer goalAccuracy = null;

	// ── goals widgets ────────────────────────────────────────────────────────────
	private final JPanel goalsSection;

	/** READ — summary row */
	private final JLabel goalAccuracyStatusLabel;

	/** EDITOR — spinner */
	private final JSpinner accuracySpinner;

	// ────────────────────────────────────────────────────────────────────────────

	public AccuracyPanel(TypeGaugeFrame frame) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(200, 400, 40, 400));
		setOpaque(false);

		// ── header ───────────────────────────────────────────────────────────────
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

		JPanel headerButtons = new JPanel();
		headerButtons.setOpaque(false);
		headerButtons.add(instructionsButton);
		headerButtons.add(backButton);
		headerButtons.add(returnToMainButton);
		header.add(headerButtons, BorderLayout.EAST);

		add(header, BorderLayout.NORTH);

		// ── center ───────────────────────────────────────────────────────────────
		JPanel center = new JPanel();
		center.setOpaque(false);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		// Main analysis card
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(20, 24, 20, 24)));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 460));

		// Character-by-character analysis area
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

		// Strengths / Hotspots row
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

		center.add(Box.createVerticalStrut(8));
		card.setAlignmentX(LEFT_ALIGNMENT);
		center.add(card);
		center.add(Box.createVerticalStrut(16));

		// ── goals section ─────────────────────────────────────────────────────────
		// Temporary labels/spinners are created here so they can be stored as
		// final fields; buildGoalsSection() wires them together into a card.
		goalAccuracyStatusLabel = new JLabel("-");
		accuracySpinner = new JSpinner(new SpinnerNumberModel(90, 1, 100, 1));

		goalsSection = buildGoalsSection();
		goalsSection.setAlignmentX(LEFT_ALIGNMENT);
		center.add(goalsSection);
		center.add(Box.createVerticalGlue());

		add(center, BorderLayout.CENTER);

		strengthsTextArea.setText("Waiting for a completed session.");
		hotspotsTextArea.setText("Waiting for a completed session.");
	}

	// ── goals section builder ─────────────────────────────────────────────────────

	private JPanel buildGoalsSection() {
		JPanel card = new GlassCardPanel(16, new Color(25, 25, 25, 160));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1, true),
			new EmptyBorder(16, 16, 16, 16)));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

		JLabel sectionTitle = new JLabel("Improvement Goals", SwingConstants.LEFT);
		sectionTitle.setForeground(Color.WHITE);
		sectionTitle.setFont(sectionTitle.getFont().deriveFont(18f));
		sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
		card.add(sectionTitle, BorderLayout.NORTH);

		JPanel body = new JPanel(new java.awt.CardLayout());
		body.setOpaque(false);

		// ── 1. Empty state — no goals set yet ────────────────────────────────────
		JPanel empty = new JPanel(new BorderLayout());
		empty.setOpaque(false);

		JLabel emptyHint = new JLabel("No goals set. Challenge yourself!");
		emptyHint.setForeground(new Color(140, 140, 140));
		emptyHint.setFont(emptyHint.getFont().deriveFont(13f));

		JButton setGoalsButton = UiButtons.createPrimaryButton("+ Set Goals");
		setGoalsButton.addActionListener(e -> openGoalEditor());

		JPanel emptyRight = new JPanel();
		emptyRight.setOpaque(false);
		emptyRight.add(setGoalsButton);

		empty.add(emptyHint,  BorderLayout.WEST);
		empty.add(emptyRight, BorderLayout.EAST);

		// ── 2. Display state — goals shown with pass/fail indicators ─────────────
		JPanel display = new JPanel(new BorderLayout(16, 0));
		display.setOpaque(false);

		JPanel statusGrid = new JPanel(new GridLayout(1, 2, 12, 6));
		statusGrid.setOpaque(false);

		JLabel accGoalLabel = new JLabel("Target Accuracy");
		accGoalLabel.setForeground(new Color(180, 180, 180));
		accGoalLabel.setFont(accGoalLabel.getFont().deriveFont(14f));

		goalAccuracyStatusLabel.setFont(goalAccuracyStatusLabel.getFont().deriveFont(14f));

		statusGrid.add(accGoalLabel);
		statusGrid.add(goalAccuracyStatusLabel);

		display.add(statusGrid, BorderLayout.CENTER);

		JPanel displayButtons = new JPanel();
		displayButtons.setOpaque(false);
		JButton editGoalsButton  = UiButtons.createPrimaryButton("Edit Goals");
		JButton clearGoalsButton = UiButtons.createPrimaryButton("Clear Goals");
		clearGoalsButton.setForeground(new Color(255, 100, 100));
		editGoalsButton.addActionListener(e -> openGoalEditor());
		clearGoalsButton.addActionListener(e -> deleteGoals());
		displayButtons.add(editGoalsButton);
		displayButtons.add(clearGoalsButton);
		display.add(displayButtons, BorderLayout.EAST);

		// ── 3. Editor state — spinner for accuracy ───────────────────────────────
		JPanel editor = new JPanel(new BorderLayout(16, 0));
		editor.setOpaque(false);

		JPanel spinnerGrid = new JPanel(new GridLayout(1, 2, 12, 8));
		spinnerGrid.setOpaque(false);

		JLabel accSpinnerLabel = new JLabel("Target Accuracy (%)");
		accSpinnerLabel.setForeground(new Color(180, 180, 180));
		accSpinnerLabel.setFont(accSpinnerLabel.getFont().deriveFont(14f));

		styleSpinner(accuracySpinner);

		spinnerGrid.add(accSpinnerLabel);
		spinnerGrid.add(accuracySpinner);

		editor.add(spinnerGrid, BorderLayout.CENTER);

		JPanel editorButtons = new JPanel();
		editorButtons.setOpaque(false);
		JButton saveButton   = UiButtons.createPrimaryButton("Save Goals");
		JButton cancelButton = UiButtons.createPrimaryButton("Cancel");
		saveButton.addActionListener(e -> saveGoals());
		cancelButton.addActionListener(e -> cancelGoalEdit());
		editorButtons.add(saveButton);
		editorButtons.add(cancelButton);
		editor.add(editorButtons, BorderLayout.EAST);

		body.add(empty,   "empty");
		body.add(display, "display");
		body.add(editor,  "editor");

		card.add(body, BorderLayout.CENTER);
		((java.awt.CardLayout) body.getLayout()).show(body, "empty");
		card.putClientProperty("goalsCardBody", body);
		return card;
	}

	// ── CRUD helpers ──────────────────────────────────────────────────────────────

	/** CREATE / UPDATE — opens the spinner editor pre-filled with current goals. */
	private void openGoalEditor() {
		if (goalAccuracy != null) accuracySpinner.setValue(goalAccuracy);
		switchGoalCard("editor");
	}

	/** CREATE / UPDATE — persists spinner values and refreshes the display. */
	private void saveGoals() {
		goalAccuracy = (Integer) accuracySpinner.getValue();
		refreshGoalDisplay();
		switchGoalCard("display");
	}

	/** DELETE — clears both targets and returns to the empty state. */
	private void deleteGoals() {
		goalAccuracy = null;
		switchGoalCard("empty");
	}

	/** Cancels an in-progress edit without modifying saved goals. */
	private void cancelGoalEdit() {
		switchGoalCard(goalAccuracy != null ? "display" : "empty");
	}

	/**
	 * Rebuilds the status labels to show each goal value and whether it was
	 * met by {@link #lastAnalyzedStats}. Called after save and after analysis.
	 */
	private void refreshGoalDisplay() {
		int sessionAccuracy = lastAnalyzedStats != null ? lastAnalyzedStats.getAccuracy() : -1;

		if (goalAccuracy != null) {
			boolean met = sessionAccuracy >= 0 && sessionAccuracy >= goalAccuracy;
			String icon = sessionAccuracy < 0 ? "" : (met ? "  ✓" : "  ✗");
			Color  color = sessionAccuracy < 0 ? Color.WHITE
				: (met ? new Color(0, 220, 140) : new Color(240, 80, 80));
			goalAccuracyStatusLabel.setText(goalAccuracy + "%" + icon);
			goalAccuracyStatusLabel.setForeground(color);
		} else {
			goalAccuracyStatusLabel.setText("-");
			goalAccuracyStatusLabel.setForeground(Color.WHITE);
		}
	}

	private void switchGoalCard(String card) {
		JPanel body = (JPanel) goalsSection.getClientProperty("goalsCardBody");
		if (body != null) ((java.awt.CardLayout) body.getLayout()).show(body, card);
	}

	// ── spinner styling ───────────────────────────────────────────────────────────

	private static void styleSpinner(JSpinner spinner) {
		spinner.setBackground(new Color(40, 40, 40));
		spinner.setForeground(Color.WHITE);
		spinner.setFont(spinner.getFont().deriveFont(14f));
		spinner.setPreferredSize(new Dimension(90, 28));
		spinner.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
		editor.getTextField().setBackground(new Color(40, 40, 40));
		editor.getTextField().setForeground(Color.WHITE);
		editor.getTextField().setCaretColor(Color.WHITE);
		editor.getTextField().setBorder(new EmptyBorder(2, 4, 2, 4));

		// Style the arrow buttons to match the dark theme
		for (java.awt.Component comp : spinner.getComponents()) {
			if (comp instanceof JButton) {
				JButton btn = (JButton) comp;
				btn.setBackground(new Color(55, 55, 60));
				btn.setForeground(Color.WHITE);
				btn.setFocusPainted(false);
				btn.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(80, 80, 80)));
			}
		}
	}

	// ── existing analysis logic (unchanged) ──────────────────────────────────────
	public void showStats(SessionStats stats) {
		if (stats == null) return;

		applyStatsToUi(stats);
	}

	private void applyStatsToUi(SessionStats stats) {
		if (stats == null) {
			renderTargetText("", "", new BitSet());
			strengthsTextArea.setText("No session data available.");
			hotspotsTextArea.setText("No session data available.");
			return;
		}

		lastAnalyzedStats = stats;

		renderTargetText(stats.getTargetText(), stats.getUserInput(), stats.getMistakenCharacterIndexes());
		strengthsTextArea.setText(buildStrengthsText(stats, stats.getTargetText()));
		hotspotsTextArea.setText(buildHotspotsText(stats));

		// Refresh goal indicators now that we have fresh session data
		if (goalAccuracy != null) {
			refreshGoalDisplay();
		}
	}

	private void renderTargetText(String targetText, String userInput, BitSet mistakenCharacterIndexes) {
		StyledDocument document = analysisArea.getStyledDocument();
		try {
			document.remove(0, document.getLength());
		} catch (javax.swing.text.BadLocationException ignored) {
		}

		String safeTarget   = targetText != null ? targetText : "";
		String safeInput    = userInput != null ? userInput : "";
		BitSet safeMistakes = mistakenCharacterIndexes != null ? mistakenCharacterIndexes : new BitSet();
		int length = safeTarget.length();

		for (int i = 0; i < length; i++) {
			char targetChar = safeTarget.charAt(i);
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			boolean wasMistaken = safeMistakes.get(i);

			if (wasMistaken) {
				StyleConstants.setForeground(attributes, new Color(230, 80, 80));
			} else if (i < safeInput.length()) {
				StyleConstants.setForeground(attributes,
					safeInput.charAt(i) == targetChar
						? new Color(80, 200, 140)
						: new Color(230, 80, 80));
			} else {
				StyleConstants.setForeground(attributes, new Color(180, 180, 180));
			}

			String displayChar = (targetChar == ' ' && wasMistaken) ? "·" : String.valueOf(targetChar);
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
		if (stats.getWpm() >= 30)    strengths.append("typing pace, ");
		if (stats.getErrors() <= 3)  strengths.append("error control, ");
		strengths.append("and consistent focus.\n");
		strengths.append("Best handled category: ").append(getBestCategoryLabel(stats, targetText)).append("\n");
		strengths.append("Performance grade: ").append(
			stats.getPerformanceGrade() != null ? stats.getPerformanceGrade() : "-");
		return strengths.toString();
	}

	private String buildHotspotsText(SessionStats stats) {
		String targetText = stats.getTargetText();
		int letterCount = 0, numberCount = 0, punctuationCount = 0, spacingCount = 0;
		if (targetText != null) {
			for (int i = 0; i < targetText.length(); i++) {
				char c = targetText.charAt(i);
				if (Character.isLetter(c))         letterCount++;
				else if (Character.isDigit(c))     numberCount++;
				else if (c == ' ')                 spacingCount++;
				else if (!Character.isWhitespace(c)) punctuationCount++;
			}
		}
		StringBuilder hotspots = new StringBuilder();
		hotspots.append("Letters: ")     .append(letterCount > 0      ? stats.getLetterErrors()      : "N/A").append("\n");
		hotspots.append("Numbers: ")     .append(numberCount > 0      ? stats.getNumberErrors()      : "N/A").append("\n");
		hotspots.append("Punctuation: ") .append(punctuationCount > 0 ? stats.getPunctuationErrors() : "N/A").append("\n");
		hotspots.append("Spacing: ")     .append(spacingCount > 0     ? stats.getSpacingErrors()     : "N/A").append("\n\n");
		hotspots.append("Top hotspot: ") .append(getDominantCategoryLabel(stats));
		return hotspots.toString();
	}

	private String getDominantCategoryKey(SessionStats stats) {
		int le = stats.getLetterErrors(), ne = stats.getNumberErrors(),
		    pe = stats.getPunctuationErrors(), se = stats.getSpacingErrors();
		if (stats.getErrors() == 0)                                   return null;
		if (se >= le && se >= ne && se >= pe)                         return "spacing";
		if (pe >= le && pe >= ne)                                      return "punctuation";
		if (ne >= le)                                                  return "numbers";
		return "letters";
	}

	private String getDominantCategoryLabel(SessionStats stats) {
		String key = getDominantCategoryKey(stats);
		if (key == null)                  return "None";
		if ("spacing".equals(key))        return "Spacing";
		if ("punctuation".equals(key))    return "Punctuation";
		if ("numbers".equals(key))        return "Numbers";
		return "Letters";
	}

	private String getBestCategoryLabel(SessionStats stats, String targetText) {
		if (stats.getAccuracy() == 100) return "All (100% Match)";
		int letterCount = 0, numberCount = 0, punctuationCount = 0, spacingCount = 0;
		if (targetText != null) {
			for (int i = 0; i < targetText.length(); i++) {
				char c = targetText.charAt(i);
				if (Character.isLetter(c))         letterCount++;
				else if (Character.isDigit(c))     numberCount++;
				else if (c == ' ')                 spacingCount++;
				else if (!Character.isWhitespace(c)) punctuationCount++;
			}
		}
		java.util.List<String>  present = new java.util.ArrayList<>();
		java.util.List<Integer> errors  = new java.util.ArrayList<>();
		if (letterCount > 0)      { present.add("Letters");     errors.add(stats.getLetterErrors()); }
		if (numberCount > 0)      { present.add("Numbers");     errors.add(stats.getNumberErrors()); }
		if (punctuationCount > 0) { present.add("Punctuation"); errors.add(stats.getPunctuationErrors()); }
		if (spacingCount > 0)     { present.add("Spacing");     errors.add(stats.getSpacingErrors()); }
		if (present.isEmpty()) return "-";
		int minIdx = 0;
		for (int i = 1; i < errors.size(); i++) {
			if (errors.get(i) < errors.get(minIdx)) minIdx = i;
		}
		return present.get(minIdx);
	}
}