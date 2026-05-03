package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
 * <p>Shows core metrics (speed, accuracy, errors, time) and links to deeper
 * analysis screens.
 *
 * <p>Supports in-memory CRUD annotations per session:
 * <ul>
 *   <li>Create — write a note for the current session via "Add Note"</li>
 *   <li>Read   — saved note is shown below the stats cards</li>
 *   <li>Update — reopen the editor with the existing text via "Edit Note"</li>
 *   <li>Delete — remove the note entirely via "Clear Note"</li>
 * </ul>
 */
public class ResultsPanel extends JPanel {

	// ── stat labels ─────────────────────────────────────────────────────────────
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
	/** Outer container that holds either the editor or the display view. */
	private final JPanel noteSection;
	/** Label used to display saved notes with HTML wrapping. */
	private JLabel noteDisplayLabel;
	/** Text area used for creating and editing notes. */
	private JTextArea noteTextArea;

	// ── session state ────────────────────────────────────────────────────────────
	/** In-memory store: SessionStats identity → annotation text. */
	private final Map<SessionStats, String> sessionNotes = new HashMap<>();
	private SessionStats currentSession;

	// ── existing fields ──────────────────────────────────────────────────────────
	private final JButton analyzeButton;
	private SessionStats previousStats;
	private SessionStats currentPendingStats;
	private int bestWpm;
	private int bestAccuracy;
	private int animationTick = 0;
	private final Timer analysisTimer;

	// ────────────────────────────────────────────────────────────────────────────

	public ResultsPanel(Runnable onReturnHome, Runnable onRetry, Runnable onOpenInstructions) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(200, 400, 40, 400));
		setOpaque(false);

		// ── header ───────────────────────────────────────────────────────────────
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 20, 0));

		JPanel headerLeft = new JPanel(new GridLayout(2, 1));
		headerLeft.setOpaque(false);

		JLabel diagnosticLabel = new JLabel("DIAGNOSTIC COMPLETE", SwingConstants.LEFT);
		diagnosticLabel.setForeground(new Color(90, 150, 255));
		diagnosticLabel.setFont(diagnosticLabel.getFont().deriveFont(14f));
		headerLeft.add(diagnosticLabel);

		JLabel titleLabel = new JLabel("Results Examiner", SwingConstants.LEFT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(36f));
		headerLeft.add(titleLabel);

		header.add(headerLeft, BorderLayout.WEST);

		JPanel headerButtons = new JPanel();
		headerButtons.setOpaque(false);

		JButton homeButton = UiButtons.createPrimaryButton("Return to Dashboard");
		homeButton.addActionListener(e -> { if (onReturnHome != null) onReturnHome.run(); });

		JButton retryButton = UiButtons.createPrimaryButton("Retry Test");
		retryButton.addActionListener(e -> { if (onRetry != null) onRetry.run(); });

		JButton instructionsButton = UiButtons.createPrimaryButton("Instructions");
		instructionsButton.addActionListener(e -> { if (onOpenInstructions != null) onOpenInstructions.run(); });

		analyzeButton = UiButtons.createPrimaryButton("Examine Results");
		analyzeButton.setVisible(false);
		analyzeButton.addActionListener(e -> startAnalysisAnimation());

		headerButtons.add(analyzeButton);
		headerButtons.add(homeButton);
		headerButtons.add(retryButton);
		headerButtons.add(instructionsButton);

		header.add(headerButtons, BorderLayout.EAST);
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

		JPanel speedCard = UiCards.createGlassStatCard("SPEED", "WPM", null,
			wpmValueLabel = new JLabel("0", SwingConstants.CENTER));
		JPanel accuracyCard = UiCards.createGlassStatCard("ACCURACY", null, null,
			accuracyValueLabel = new JLabel("0%", SwingConstants.CENTER));
		JPanel errorsCard = UiCards.createGlassStatCard("ERRORS", null, null,
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

		difficultyValueLabel   = addStatRow(selfStats, "Difficulty Level",   new Color(90, 150, 255));
		charsValueLabel        = addStatRow(selfStats, "Total Characters",    Color.WHITE);
		sessionQualityValueLabel = addStatRow(selfStats, "Session Quality",   new Color(0, 220, 140));
		previousWpmValueLabel  = addStatRow(selfStats, "Previous WPM",        Color.WHITE);
		previousAccuracyValueLabel = addStatRow(selfStats, "Previous Accuracy", Color.WHITE);
		bestWpmValueLabel      = addStatRow(selfStats, "Best WPM",            new Color(0, 220, 140));
		bestAccuracyValueLabel = addStatRow(selfStats, "Best Accuracy",       new Color(0, 220, 140));
		performanceGradeValueLabel = addStatRow(selfStats, "Performance Grade", new Color(90, 150, 255));

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

		// ── annotation section ───────────────────────────────────────────────────
		noteSection = buildNoteSection();
		noteSection.setAlignmentX(LEFT_ALIGNMENT);
		center.add(noteSection);

		center.add(javax.swing.Box.createVerticalGlue());
		add(center, BorderLayout.CENTER);

		analysisTimer = new Timer(200, e -> handleAnalysisTick());

		// Grab refs to sub-panels (assigned during buildNoteSection)
		// Fields are initialised inside buildNoteSection(); Java requires final
		// fields to be assigned exactly once, so they are set there directly.
	}

	// ── annotation section builder ───────────────────────────────────────────────

	/**
	 * Builds the three-state note section and wires all CRUD buttons.
	 * Assigns {@link #noteTextArea} and {@link #noteDisplayLabel} as side-effects
	 * so they can be referenced by the CRUD methods.
	 */
	private JPanel buildNoteSection() {
		// -- shared card shell --
		JPanel card = new GlassCardPanel(16, new Color(25, 25, 25, 160));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(16, 16, 16, 16)));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

		JLabel sectionTitle = new JLabel("Session Notes", SwingConstants.LEFT);
		sectionTitle.setForeground(Color.WHITE);
		sectionTitle.setFont(sectionTitle.getFont().deriveFont(18f));
		sectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
		card.add(sectionTitle, BorderLayout.NORTH);

		// CardLayout-style: swap between three child panels
		JPanel body = new JPanel(new java.awt.CardLayout());
		body.setOpaque(false);

		// 1. "No note yet" state (CREATE entry point)
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

		// 2. Display state (READ + UPDATE/DELETE entry points)
		JPanel display = new JPanel(new BorderLayout(12, 0));
		display.setOpaque(false);
		noteDisplayLabel = new JLabel();
		noteDisplayLabel.setForeground(new Color(210, 210, 210));
		noteDisplayLabel.setFont(noteDisplayLabel.getFont().deriveFont(14f));
		noteDisplayLabel.setVerticalAlignment(SwingConstants.TOP);
		display.add(noteDisplayLabel, BorderLayout.CENTER);

		JPanel displayButtons = new JPanel();
		displayButtons.setOpaque(false);
		JButton editNoteButton   = UiButtons.createPrimaryButton("Edit Note");
		JButton clearNoteButton  = UiButtons.createPrimaryButton("Clear Note");
		clearNoteButton.setForeground(new Color(255, 100, 100));

		editNoteButton.addActionListener(e -> {
			String existing = sessionNotes.getOrDefault(currentSession, "");
			openEditor(existing);
		});
		clearNoteButton.addActionListener(e -> deleteNote());

		displayButtons.add(editNoteButton);
		displayButtons.add(clearNoteButton);
		display.add(displayButtons, BorderLayout.EAST);

		// 3. Editor state (CREATE / UPDATE form)
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

		editor.add(scroll, BorderLayout.CENTER);
		editor.add(editorButtons, BorderLayout.EAST);

		body.add(empty,   "empty");
		body.add(display, "display");
		body.add(editor,  "editor");

		card.add(body, BorderLayout.CENTER);
		
		// Start in "empty" state
		((java.awt.CardLayout) body.getLayout()).show(body, "empty");

		// Keep a reference to `body` for card switching
		card.putClientProperty("noteCardBody", body);

		return card;
	}

	// ── CRUD helpers ─────────────────────────────────────────────────────────────

	/** CREATE / UPDATE — opens the text editor pre-filled with {@code existing}. */
	private void openEditor(String existing) {
		noteTextArea.setText(existing);
		noteTextArea.requestFocusInWindow();
		switchNoteCard("editor");
	}

	/** CREATE / UPDATE — persists the typed text and switches to the display view. */
	private void saveNote() {
		if (currentSession == null) return;
		String text = noteTextArea.getText().trim();
		if (text.isEmpty()) {
			// Treat save-with-empty as a delete
			deleteNote();
			return;
		}
		sessionNotes.put(currentSession, text);
		noteDisplayLabel.setText("<html><body style='width:800px'>" +
			escapeHtml(text) + "</body></html>");
		switchNoteCard("display");
	}

	/** DELETE — removes the note from the in-memory store and resets the view. */
	private void deleteNote() {
		if (currentSession != null) {
			sessionNotes.remove(currentSession);
		}
		noteDisplayLabel.setText("");
		switchNoteCard("empty");
	}

	/** Cancels an in-progress edit without modifying the store. */
	private void cancelEdit() {
		boolean hasExisting = currentSession != null
			&& sessionNotes.containsKey(currentSession);
		switchNoteCard(hasExisting ? "display" : "empty");
	}

	/** Restores the note section to match whichever note (if any) belongs to {@code stats}. */
	private void refreshNoteSection(SessionStats stats) {
		if (stats == null) {
			switchNoteCard("empty");
			return;
		}
		String saved = sessionNotes.get(stats);
		if (saved != null && !saved.isEmpty()) {
			noteDisplayLabel.setText("<html><body style='width:800px'>" +
				escapeHtml(saved) + "</body></html>");
			switchNoteCard("display");
		} else {
			switchNoteCard("empty");
		}
	}

	/** Retrieves the shared CardLayout body panel and flips to the named card. */
	private void switchNoteCard(String card) {
		JPanel body = (JPanel) noteSection.getClientProperty("noteCardBody");
		if (body != null) {
			((java.awt.CardLayout) body.getLayout()).show(body, card);
		}
	}

	private static void showNoteCard(JPanel body, String card) {
		((java.awt.CardLayout) body.getLayout()).show(body, card);
	}

	// ── animation / existing logic (unchanged) ───────────────────────────────────

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
		if (stats == null) return;
		this.currentPendingStats = stats;
		this.currentSession = stats;

		wpmValueLabel.setText("0");
		accuracyValueLabel.setText("0%");
		errorsValueLabel.setText("0");
		timeValueLabel.setText("0s");
		sessionQualityValueLabel.setText("-");
		performanceGradeValueLabel.setText("-");

		// Restore any previously saved note for this session (READ on load)
		refreshNoteSection(stats);

		analyzeButton.setText("Examine Results");
		analyzeButton.setEnabled(true);
		analyzeButton.setVisible(true);
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

		if (previousStats != null) {
			previousWpmValueLabel.setText(String.valueOf(previousStats.getWpm()));
			previousAccuracyValueLabel.setText(previousStats.getAccuracy() + "%");
		} else {
			previousWpmValueLabel.setText("-");
			previousAccuracyValueLabel.setText("-");
		}

		bestWpm = Math.max(bestWpm, stats.getWpm());
		bestAccuracy = Math.max(bestAccuracy, stats.getAccuracy());
		bestWpmValueLabel.setText(String.valueOf(bestWpm));
		bestAccuracyValueLabel.setText(bestAccuracy + "%");
		performanceGradeValueLabel.setText(stats.getPerformanceGrade() != null ? stats.getPerformanceGrade() : "-");

		String quality = "Medium";
		if (stats.getAccuracy() >= 95 && stats.getWpm() >= 40) {
			quality = "High";
		} else if (stats.getAccuracy() < 80 || stats.getWpm() < 25) {
			quality = "Developing";
		}
		sessionQualityValueLabel.setText(quality);
		previousStats = stats;
	}

	// ── tiny utilities ────────────────────────────────────────────────────────────

	/**
	 * Convenience: adds a label/value pair row to {@code grid} and returns the
	 * value label so it can be stored in a field.
	 */
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

	private static String escapeHtml(String text) {
		return text.replace("&", "&amp;")
		           .replace("<", "&lt;")
		           .replace(">", "&gt;")
		           .replace("\"", "&quot;")
		           .replace("\n", "<br>");
	}
}