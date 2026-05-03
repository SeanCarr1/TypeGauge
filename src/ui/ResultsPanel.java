package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.SessionStats;

/**
 * Results dashboard displayed after a typing session ends.
 *
 * <p>Shows core metrics (speed, accuracy, errors, time).
 *
 * <p>Supports in-memory CRUD annotations per session:
 * <ul>
 *   <li>Create — write a note via "Add Note"</li>
 *   <li>Read   — saved note shown below the stats cards; past sessions
 *                browsable via the "Session History" dropdown</li>
 *   <li>Update — reopen editor with existing text via "Edit Note"</li>
 *   <li>Delete — remove the note via "Clear Note"</li>
 * </ul>
 *
 * <p>Sessions are stored in an {@link ArrayList} and notes are keyed by
 * session index so past sessions are always reachable regardless of whether
 * the {@link SessionStats} object is the same reference.
 */
public class ResultsPanel extends JPanel {

	// ── stat labels ──────────────────────────────────────────────────────────────
	private final JLabel wpmValueLabel;
	private final JLabel accuracyValueLabel;
	private final JLabel errorsValueLabel;
	private final JLabel timeValueLabel;
	private final JLabel difficultyValueLabel;
	private final JLabel charsValueLabel;
	private final JLabel sessionQualityValueLabel;
	private final JLabel previousWpmValueLabel;
	private final JLabel previousAccuracyValueLabel;
	private final JLabel bestWpmValueLabel;
	private final JLabel bestAccuracyValueLabel;
	private final JLabel performanceGradeValueLabel;
	private final JLabel lettersErrorValueLabel;
	private final JLabel numbersErrorValueLabel;
	private final JLabel punctuationErrorValueLabel;
	private final JLabel spacingErrorValueLabel;

	// ── annotation widgets ───────────────────────────────────────────────────────
	private final JPanel noteSection;
	private JLabel noteDisplayLabel;
	private JTextArea noteTextArea;

	// ── session history ──────────────────────────────────────────────────────────
	/** All sessions in insertion order. Index == stable note key. */
	private final List<SessionStats> sessionHistory = new ArrayList<>();
	/** Notes keyed by session index (stable even across retries). */
	private final Map<Integer, String> sessionNotes  = new HashMap<>();
	/** Index of the session currently shown in the UI (-1 = none). */
	private int currentIndex = -1;

	private final JComboBox<String> historyCombo;
	private final DefaultComboBoxModel<String> historyModel = new DefaultComboBoxModel<>();

	// ── existing fields ──────────────────────────────────────────────────────────
	private int bestWpm;
	private int bestAccuracy;

	// ────────────────────────────────────────────────────────────────────────────

	public ResultsPanel(Runnable onReturnHome, Runnable onRetry, Runnable onOpenInstructions) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(100, 250, 40, 250));
		setOpaque(false);

		// ── header ───────────────────────────────────────────────────────────────
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		JPanel headerLeft = new JPanel();
		headerLeft.setOpaque(false);
		headerLeft.setLayout(new javax.swing.BoxLayout(headerLeft, javax.swing.BoxLayout.Y_AXIS));

		JLabel diagnosticLabel = new JLabel("DIAGNOSTIC COMPLETE", SwingConstants.LEFT);
		diagnosticLabel.setForeground(new Color(90, 150, 255));
		diagnosticLabel.setFont(diagnosticLabel.getFont().deriveFont(14f));
		diagnosticLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerLeft.add(diagnosticLabel);

		JLabel titleLabel = new JLabel("Results Examiner", SwingConstants.LEFT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(36f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerLeft.add(titleLabel);

		headerLeft.add(javax.swing.Box.createVerticalStrut(12));

		historyCombo = new JComboBox<>(historyModel);
		historyCombo.setBackground(new Color(40, 40, 40));
		historyCombo.setForeground(Color.WHITE);
		historyCombo.setFont(historyCombo.getFont().deriveFont(13f));
		historyCombo.setMaximumSize(new Dimension(240, 32));
		historyCombo.setAlignmentX(LEFT_ALIGNMENT);
		historyCombo.setVisible(false); // hidden until there are >= 2 sessions
		historyCombo.addActionListener(e -> {
			if (!historyCombo.isVisible()) return;
			int sel = historyCombo.getSelectedIndex();
			if (sel >= 0 && sel < sessionHistory.size() && sel != currentIndex) {
				loadSession(sel);
			}
		});
		headerLeft.add(historyCombo);

		header.add(headerLeft, BorderLayout.WEST);

		// Header right: action buttons
		JPanel headerRight = new JPanel();
		headerRight.setOpaque(false);

		JButton homeButton = UiButtons.createPrimaryButton("Return to Dashboard");
		homeButton.addActionListener(e -> { if (onReturnHome != null) onReturnHome.run(); });

		JButton retryButton = UiButtons.createPrimaryButton("Retry Test");
		retryButton.addActionListener(e -> { if (onRetry != null) onRetry.run(); });

		JButton instructionsButton = UiButtons.createPrimaryButton("Instructions");
		instructionsButton.addActionListener(e -> { if (onOpenInstructions != null) onOpenInstructions.run(); });

		headerRight.add(homeButton);
		// headerRight.add(retryButton);
		headerRight.add(instructionsButton);

		header.add(headerRight, BorderLayout.EAST);
		add(header, BorderLayout.NORTH);

		// ── center ───────────────────────────────────────────────────────────────
		JPanel center = new JPanel();
		center.setOpaque(false);
		center.setLayout(new javax.swing.BoxLayout(center, javax.swing.BoxLayout.Y_AXIS));

		// Top stats row
		JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
		statsRow.setOpaque(false);
		statsRow.setBorder(new EmptyBorder(0, 0, 24, 0));
		statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

		JPanel speedCard    = UiCards.createGlassStatCard("SPEED",    "WPM",      null, wpmValueLabel      = new JLabel("0",  SwingConstants.CENTER));
		JPanel accuracyCard = UiCards.createGlassStatCard("ACCURACY", null,       null, accuracyValueLabel  = new JLabel("0%", SwingConstants.CENTER));
		JPanel errorsCard   = UiCards.createGlassStatCard("ERRORS",   null,       null, errorsValueLabel    = new JLabel("0",  SwingConstants.CENTER));
		JPanel timeCard     = UiCards.createGlassStatCard("TIME",     "Duration", null, timeValueLabel      = new JLabel("0s", SwingConstants.CENTER));

		statsRow.add(speedCard);
		statsRow.add(accuracyCard);
		statsRow.add(errorsCard);
		statsRow.add(timeCard);
		statsRow.setAlignmentX(LEFT_ALIGNMENT);
		center.add(statsRow);
		center.add(javax.swing.Box.createVerticalStrut(12));

		// Bottom row: Self-Assessment + Expert Feedback
		JPanel bottomRow = new JPanel(new GridLayout(1, 2, 24, 0));
		bottomRow.setOpaque(false);
		bottomRow.setAlignmentX(LEFT_ALIGNMENT);
		bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

		// Self-Assessment card
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
		selfAssessmentCard.add(selfHeader, BorderLayout.NORTH);

		JPanel selfStats = new JPanel(new GridLayout(8, 2, 8, 8));
		selfStats.setOpaque(false);
		selfStats.setBorder(new EmptyBorder(12, 0, 0, 0));

		difficultyValueLabel       = addStatRow(selfStats, "Difficulty Level",  new Color(90, 150, 255));
		charsValueLabel            = addStatRow(selfStats, "Total Characters",   Color.WHITE);
		sessionQualityValueLabel   = addStatRow(selfStats, "Session Quality",    new Color(0, 220, 140));
		previousWpmValueLabel      = addStatRow(selfStats, "Previous WPM",       Color.WHITE);
		previousAccuracyValueLabel = addStatRow(selfStats, "Previous Accuracy",  Color.WHITE);
		bestWpmValueLabel          = addStatRow(selfStats, "Best WPM",           new Color(0, 220, 140));
		bestAccuracyValueLabel     = addStatRow(selfStats, "Best Accuracy",      new Color(0, 220, 140));
		performanceGradeValueLabel = addStatRow(selfStats, "Performance Grade",  new Color(90, 150, 255));

		selfAssessmentCard.add(selfStats, BorderLayout.CENTER);

		// Expert Feedback card
		JPanel feedbackCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
		feedbackCard.setLayout(new BorderLayout());
		feedbackCard.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel feedbackSummary = new JLabel("Error Category Summary", SwingConstants.LEFT);
		feedbackSummary.setForeground(new Color(0, 220, 140));
		feedbackSummary.setFont(feedbackSummary.getFont().deriveFont(24f));

		JPanel feedbackCenter = new JPanel(new BorderLayout());
		feedbackCenter.setOpaque(false);
		feedbackCenter.add(feedbackSummary, BorderLayout.NORTH);

		JPanel categoryGrid = new JPanel(new GridLayout(4, 2, 8, 8));
		categoryGrid.setOpaque(false);
		categoryGrid.setBorder(new EmptyBorder(12, 0, 0, 0));

		lettersErrorValueLabel     = addStatRow(categoryGrid, "Letters",     Color.WHITE);
		numbersErrorValueLabel     = addStatRow(categoryGrid, "Numbers",     Color.WHITE);
		punctuationErrorValueLabel = addStatRow(categoryGrid, "Punctuation", Color.WHITE);
		spacingErrorValueLabel     = addStatRow(categoryGrid, "Spacing",     Color.WHITE);

		feedbackCenter.add(categoryGrid, BorderLayout.CENTER);
		feedbackCard.add(feedbackCenter, BorderLayout.CENTER);

		bottomRow.add(selfAssessmentCard);
		bottomRow.add(feedbackCard);
		center.add(bottomRow);
		center.add(javax.swing.Box.createVerticalStrut(16));

		// Annotation section
		noteSection = buildNoteSection();
		noteSection.setAlignmentX(LEFT_ALIGNMENT);
		center.add(noteSection);

		center.add(javax.swing.Box.createVerticalGlue());
		add(center, BorderLayout.CENTER);
	}

	// ── public API ────────────────────────────────────────────────────────────────

	/**
	 * Called externally when a session ends. Registers it in history, updates
	 * the combo, and queues the analysis animation.
	 */
	public void showStats(SessionStats stats) {
		if (stats == null) return;

		// Register in history list
		sessionHistory.add(stats);
		int idx = sessionHistory.size() - 1;

		// Add combo entry: "Session N  |  WPM WPM · Accuracy%"
		String label = String.format("Session %d  |  %d WPM · %d%%",
			idx + 1, stats.getWpm(), stats.getAccuracy());
		historyModel.addElement(label);

		// Reveal combo once there is more than one session to choose from
		if (sessionHistory.size() > 1) {
			historyCombo.setVisible(true);
		}

		// Select the new entry without triggering loadSession (index already matches)
		historyCombo.setSelectedIndex(idx);
		currentIndex        = idx;

		applyStatsToUi(stats);
		refreshNoteSection(idx);
	}

	// ── session history navigation ────────────────────────────────────────────────

	/**
	 * Loads an arbitrary past session into the UI without an animation.
	 * Called when the user picks an entry from the history combo.
	 */
	private void loadSession(int idx) {
		currentIndex = idx;
		applyStatsToUi(sessionHistory.get(idx));
		refreshNoteSection(idx);
	}

	// ── annotation section ────────────────────────────────────────────────────────

	private JPanel buildNoteSection() {
		JPanel card = new GlassCardPanel(16, new Color(25, 25, 25, 160));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(16, 16, 16, 16)));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

		JLabel sectionTitle = new JLabel("Session Notes", SwingConstants.LEFT);
		sectionTitle.setForeground(Color.WHITE);
		sectionTitle.setFont(sectionTitle.getFont().deriveFont(18f));
		sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
		card.add(sectionTitle, BorderLayout.NORTH);

		JPanel body = new JPanel(new java.awt.CardLayout());
		body.setOpaque(false);

		// 1. Empty state — CREATE entry point
		JPanel empty = new JPanel(new BorderLayout());
		empty.setOpaque(false);
		JLabel emptyHint = new JLabel("No note for this session.");
		emptyHint.setForeground(new Color(140, 140, 140));
		emptyHint.setFont(emptyHint.getFont().deriveFont(13f));
		JButton addNoteButton = UiButtons.createPrimaryButton("+ Add Note");
		addNoteButton.addActionListener(e -> openEditor(""));
		JPanel emptyRight = new JPanel();
		emptyRight.setOpaque(false);
		emptyRight.add(addNoteButton);
		empty.add(emptyHint, BorderLayout.WEST);
		empty.add(emptyRight, BorderLayout.EAST);

		// 2. Display state — READ + UPDATE/DELETE entry points
		JPanel display = new JPanel(new BorderLayout(12, 0));
		display.setOpaque(false);
		noteDisplayLabel = new JLabel();
		noteDisplayLabel.setForeground(new Color(210, 210, 210));
		noteDisplayLabel.setFont(noteDisplayLabel.getFont().deriveFont(14f));
		noteDisplayLabel.setVerticalAlignment(SwingConstants.TOP);
		display.add(noteDisplayLabel, BorderLayout.CENTER);

		JPanel displayButtons = new JPanel();
		displayButtons.setOpaque(false);
		JButton editNoteButton  = UiButtons.createPrimaryButton("Edit Note");
		JButton clearNoteButton = UiButtons.createPrimaryButton("Clear Note");
		clearNoteButton.setForeground(new Color(255, 100, 100));
		editNoteButton.addActionListener(e -> openEditor(sessionNotes.getOrDefault(currentIndex, "")));
		clearNoteButton.addActionListener(e -> deleteNote());
		displayButtons.add(editNoteButton);
		displayButtons.add(clearNoteButton);
		display.add(displayButtons, BorderLayout.EAST);

		// 3. Editor state — CREATE / UPDATE form
		JPanel editor = new JPanel(new BorderLayout(0, 8));
		editor.setOpaque(false);

		noteTextArea = new JTextArea(3, 40);
		noteTextArea.setBackground(new Color(40, 40, 40));
		noteTextArea.setForeground(Color.WHITE);
		noteTextArea.setCaretColor(Color.WHITE);
		noteTextArea.setFont(noteTextArea.getFont().deriveFont(14f));
		noteTextArea.setLineWrap(true);
		noteTextArea.setWrapStyleWord(true);
		noteTextArea.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(90, 150, 255), 1, true),
			new EmptyBorder(6, 8, 6, 8)));

		JScrollPane scroll = new JScrollPane(noteTextArea);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setBorder(null);

		JPanel editorButtons = new JPanel();
		editorButtons.setOpaque(false);
		JButton saveButton   = UiButtons.createPrimaryButton("Save Note");
		JButton cancelButton = UiButtons.createPrimaryButton("Cancel");
		saveButton.addActionListener(e -> saveNote());
		cancelButton.addActionListener(e -> cancelEdit());
		editorButtons.add(saveButton);
		editorButtons.add(cancelButton);

		editor.add(scroll,         BorderLayout.CENTER);
		editor.add(editorButtons,  BorderLayout.EAST);

		body.add(empty,   "empty");
		body.add(display, "display");
		body.add(editor,  "editor");

		card.add(body, BorderLayout.CENTER);
		((java.awt.CardLayout) body.getLayout()).show(body, "empty");
		card.putClientProperty("noteCardBody", body);
		return card;
	}

	// ── CRUD helpers ──────────────────────────────────────────────────────────────

	private void openEditor(String existing) {
		noteTextArea.setText(existing);
		noteTextArea.requestFocusInWindow();
		switchNoteCard("editor");
	}

	private void saveNote() {
		if (currentIndex < 0) return;
		String text = noteTextArea.getText().trim();
		if (text.isEmpty()) { deleteNote(); return; }
		sessionNotes.put(currentIndex, text);
		noteDisplayLabel.setText(toHtml(text));
		switchNoteCard("display");
	}

	private void deleteNote() {
		sessionNotes.remove(currentIndex);
		noteDisplayLabel.setText("");
		switchNoteCard("empty");
	}

	private void cancelEdit() {
		switchNoteCard(sessionNotes.containsKey(currentIndex) ? "display" : "empty");
	}

	private void refreshNoteSection(int idx) {
		String saved = sessionNotes.get(idx);
		if (saved != null && !saved.isEmpty()) {
			noteDisplayLabel.setText(toHtml(saved));
			switchNoteCard("display");
		} else {
			switchNoteCard("empty");
		}
	}

	private void switchNoteCard(String card) {
		JPanel body = (JPanel) noteSection.getClientProperty("noteCardBody");
		if (body != null) ((java.awt.CardLayout) body.getLayout()).show(body, card);
	}

	private void applyStatsToUi(SessionStats stats) {
		wpmValueLabel.setText(String.valueOf(stats.getWpm()));
		accuracyValueLabel.setText(stats.getAccuracy() + "%");
		errorsValueLabel.setText(String.valueOf(stats.getErrors()));
		timeValueLabel.setText(stats.getTimeElapsedSeconds() + "s");
		difficultyValueLabel.setText(stats.getDifficulty() != null ? stats.getDifficulty().name() : "");
		charsValueLabel.setText(String.valueOf(stats.getTotalChars()));
		lettersErrorValueLabel.setText(String.valueOf(stats.getLetterErrors()));
		numbersErrorValueLabel.setText(String.valueOf(stats.getNumberErrors()));
		punctuationErrorValueLabel.setText(String.valueOf(stats.getPunctuationErrors()));
		spacingErrorValueLabel.setText(String.valueOf(stats.getSpacingErrors()));

		// "Previous" = the session immediately before this one in the history list
		int thisIdx = sessionHistory.indexOf(stats);
		if (thisIdx > 0) {
			SessionStats prev = sessionHistory.get(thisIdx - 1);
			previousWpmValueLabel.setText(String.valueOf(prev.getWpm()));
			previousAccuracyValueLabel.setText(prev.getAccuracy() + "%");
		} else {
			previousWpmValueLabel.setText("-");
			previousAccuracyValueLabel.setText("-");
		}

		bestWpm      = Math.max(bestWpm,      stats.getWpm());
		bestAccuracy = Math.max(bestAccuracy, stats.getAccuracy());
		bestWpmValueLabel.setText(String.valueOf(bestWpm));
		bestAccuracyValueLabel.setText(bestAccuracy + "%");
		performanceGradeValueLabel.setText(
			stats.getPerformanceGrade() != null ? stats.getPerformanceGrade() : "-");

		String quality = "Medium";
		if (stats.getAccuracy() >= 95 && stats.getWpm() >= 40)    quality = "High";
		else if (stats.getAccuracy() < 80 || stats.getWpm() < 25) quality = "Developing";
		sessionQualityValueLabel.setText(quality);
	}

	// ── utilities ─────────────────────────────────────────────────────────────────

	private static JLabel addStatRow(JPanel grid, String labelText, Color valueColor) {
		JLabel lbl = new JLabel(labelText);
		lbl.setForeground(new Color(180, 180, 180));
		lbl.setFont(lbl.getFont().deriveFont(16f));
		JLabel val = new JLabel("-");
		val.setForeground(valueColor);
		grid.add(lbl);
		grid.add(val);
		return val;
	}

	private static String toHtml(String text) {
		return "<html><body style='width:800px'>"
			+ text.replace("&", "&amp;")
			      .replace("<", "&lt;")
			      .replace(">", "&gt;")
			      .replace("\"", "&quot;")
			      .replace("\n", "<br>")
			+ "</body></html>";
	}
}