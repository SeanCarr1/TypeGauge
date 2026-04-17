package ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.Difficulty;
import model.FeedbackResult;
import model.FeedbackService;
import model.SessionStats;
import model.StatsUtil;

/**
 * Main application window and UI coordinator.
 *
 * <p>Owns global navigation, session lifecycle state, timing updates, and
 * cross-panel data propagation after each typing run.
 */
public class TypeGaugeFrame extends JFrame {

	private static final String CARD_MAIN = "main";
	private static final String CARD_HOME = "home";
	private static final String CARD_FEATURE_HUB = "featureHub";
	private static final String CARD_TEST = "test";
	private static final String CARD_RESULTS = "results";
	private static final String CARD_ACCURACY = "accuracy";
	private static final String CARD_FEEDBACK = "feedback";
	private static final String CARD_ABOUT = "about";
	private static final String CARD_INSTRUCTIONS = "instructions";
	private static final String CARD_CREDITS = "credits";

	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private final FooterPanel footerPanel;
	private final SidebarPanel sidebarPanel;
	private final JPanel backgroundPanel;
	private final Image backgroundImage;

	private final MainUiPanel mainUiPanel;
	private final HomePanel homePanel;
	private final FeatureHubPanel featureHubPanel;
	private final TestPanel testPanel;
	private final ResultsPanel resultsPanel;
	private final AccuracyPanel accuracyPanel;
	private final FeedbackPanel feedbackPanel;
	private final AboutPanel aboutPanel;
	private final InstructionsPanel instructionsPanel;
	private final CreditsPanel creditsPanel;

	private Difficulty currentDifficulty = Difficulty.BEGINNER;
	private String currentTargetText;
	private long startTimeMillis;
	private Timer timer;
	private int elapsedSeconds;
	private SessionStats currentStats;

	public TypeGaugeFrame() {
		super("TypeGauge – Measure Your Typing Skill");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1920, 1000));

		// Load background image for the entire app
		backgroundImage = new ImageIcon("src/images/photo-1550751827-4bd374c3f58b.png").getImage();
		backgroundPanel = new BackgroundPanel(backgroundImage);
		backgroundPanel.setLayout(new BorderLayout());

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.setOpaque(false);
		footerPanel = new FooterPanel();

		mainUiPanel = new MainUiPanel(this);
		homePanel = new HomePanel(this);
		featureHubPanel = new FeatureHubPanel(this);
		testPanel = new TestPanel(this);
		resultsPanel = new ResultsPanel(this::showMainUi, this::retry, this::showAccuracy, this::showFeedback,
			this::showResultsInstructions);
		accuracyPanel = new AccuracyPanel(this);
		feedbackPanel = new FeedbackPanel(this);
		aboutPanel = new AboutPanel(this);
		instructionsPanel = new InstructionsPanel(this);
		creditsPanel = new CreditsPanel(this);

		cardPanel.add(mainUiPanel, CARD_MAIN);
		cardPanel.add(homePanel, CARD_HOME);
		cardPanel.add(featureHubPanel, CARD_FEATURE_HUB);
		cardPanel.add(testPanel, CARD_TEST);
		cardPanel.add(resultsPanel, CARD_RESULTS);
		cardPanel.add(accuracyPanel, CARD_ACCURACY);
		cardPanel.add(feedbackPanel, CARD_FEEDBACK);
		cardPanel.add(aboutPanel, CARD_ABOUT);
		cardPanel.add(instructionsPanel, CARD_INSTRUCTIONS);
		cardPanel.add(creditsPanel, CARD_CREDITS);

		// Sidebar on the left, content in the center
		sidebarPanel = new SidebarPanel(this);
		backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
		backgroundPanel.add(cardPanel, BorderLayout.CENTER);
		backgroundPanel.add(footerPanel, BorderLayout.SOUTH);
		setContentPane(backgroundPanel);
		cardLayout.show(cardPanel, CARD_MAIN);
		sidebarPanel.setActiveScreen(CARD_MAIN);

		pack();
		setLocationRelativeTo(null);
	}

	public void showMainUi() {
		// Keep sidebar selection in sync with currently visible card.
		cardLayout.show(cardPanel, CARD_MAIN);
		sidebarPanel.setActiveScreen(CARD_MAIN);
	}

	public void startSession(Difficulty difficulty) {
		// Session initialization centralizes state reset and target selection.
		Difficulty effectiveDifficulty = difficulty != null ? difficulty : Difficulty.BEGINNER;
		this.currentDifficulty = effectiveDifficulty;
		this.currentTargetText = effectiveDifficulty.getRandomSampleText();
		this.elapsedSeconds = 0;
		this.currentStats = null;

		testPanel.prepareForSession(currentDifficulty, currentTargetText);
		showTest();
	}

	public void startSessionFromSelectedDifficulty() {
		startSession(currentDifficulty);
	}

	public void selectDifficulty(Difficulty difficulty) {
		Difficulty effectiveDifficulty = difficulty != null ? difficulty : Difficulty.BEGINNER;

		// Persist selected difficulty in memory for the current app session only.
		this.currentDifficulty = effectiveDifficulty;
		this.currentTargetText = null;
		showDifficultySelectedDialog(effectiveDifficulty);
	}

	private void showDifficultySelectedDialog(Difficulty difficulty) {
		JDialog dialog = new JDialog(this, "Selection Saved", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JPanel shell = new JPanel(new BorderLayout());
		shell.setBackground(new Color(10, 10, 12));
		shell.setBorder(new EmptyBorder(14, 14, 14, 14));

		JPanel card = new GlassCardPanel(20, new Color(25, 25, 25, 220));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 140), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel title = new JLabel("Difficulty Selected");
		title.setForeground(new Color(90, 150, 255));
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

		JLabel message = new JLabel("You selected: " + difficulty.name() + ". You can start the test anytime.");
		message.setForeground(Color.WHITE);
		message.setBorder(new EmptyBorder(8, 0, 0, 0));

		JButton continueButton = UiButtons.createPrimaryButton("Continue");
		continueButton.addActionListener(e -> dialog.dispose());
		JPanel actions = new JPanel(new BorderLayout());
		actions.setOpaque(false);
		actions.setBorder(new EmptyBorder(14, 0, 0, 0));
		actions.add(continueButton, BorderLayout.EAST);

		card.add(title, BorderLayout.NORTH);
		card.add(message, BorderLayout.CENTER);
		card.add(actions, BorderLayout.SOUTH);

		shell.add(card, BorderLayout.CENTER);
		dialog.setContentPane(shell);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	public void showHome() {
		cardLayout.show(cardPanel, CARD_HOME);
		sidebarPanel.setActiveScreen(CARD_HOME);
	}

	public void showFeatureHub() {
		cardLayout.show(cardPanel, CARD_FEATURE_HUB);
		sidebarPanel.setActiveScreen(CARD_FEATURE_HUB);
	}

	public void showTest() {
		cardLayout.show(cardPanel, CARD_TEST);
		sidebarPanel.setActiveScreen(CARD_TEST);
	}

	public void showResults() {
		cardLayout.show(cardPanel, CARD_RESULTS);
		sidebarPanel.setActiveScreen(CARD_RESULTS);
	}

	public void showAccuracy() {
		cardLayout.show(cardPanel, CARD_ACCURACY);
		sidebarPanel.setActiveScreen(CARD_ACCURACY);
	}

	public void showFeedback() {
		cardLayout.show(cardPanel, CARD_FEEDBACK);
		sidebarPanel.setActiveScreen(CARD_FEEDBACK);
	}

	public void showAbout() {
		cardLayout.show(cardPanel, CARD_ABOUT);
		sidebarPanel.setActiveScreen(CARD_ABOUT);
	}

	public void showInstructions() {
		cardLayout.show(cardPanel, CARD_INSTRUCTIONS);
	}

	public void showCredits() {
		cardLayout.show(cardPanel, CARD_CREDITS);
	}

	public void exitApplication() {
		dispose();
		System.exit(0);
	}

	public void onTypingStarted() {
		// Timer starts only once per session, on the first typed character.
		if (timer != null && timer.isRunning()) {
			return;
		}
		startTimeMillis = System.currentTimeMillis();
		elapsedSeconds = 0;
		timer = new Timer(1000, e -> onTimerTick());
		timer.start();
	}

	private void onTimerTick() {
		elapsedSeconds = (int) ((System.currentTimeMillis() - startTimeMillis) / 1000.0);
		EventQueue.invokeLater(() -> {
			int charsTyped = testPanel.getCurrentInputLength();
			int wpm = StatsUtil.calculateWpm(charsTyped, elapsedSeconds);
			int accuracyLive = testPanel.getSessionAccuracyPercent();
			testPanel.updateLiveStats(elapsedSeconds, wpm, accuracyLive);
		});
	}

	public void finishSession(String finalInput) {
		// Compute immutable session summary then fan out to dependent screens.
		stopTimer();

		if (currentTargetText == null || currentDifficulty == null) {
			showHome();
			return;
		}

		int totalChars = currentTargetText.length();
		int errors = testPanel.getSessionErrorCount();
		int wpm = StatsUtil.calculateWpm(totalChars, elapsedSeconds);
		int accuracy = testPanel.getSessionAccuracyPercent();

		SessionStats stats = new SessionStats();
		stats.setDifficulty(currentDifficulty);
		stats.setTargetText(currentTargetText);
		stats.setUserInput(finalInput);
		stats.setTotalChars(totalChars);
		stats.setErrors(errors);
		stats.setWpm(wpm);
		stats.setAccuracy(accuracy);
		stats.setTimeElapsedSeconds(elapsedSeconds);

		this.currentStats = stats;
		resultsPanel.showStats(stats);
		accuracyPanel.showStats(stats);

		FeedbackResult feedback = FeedbackService.getFeedback(wpm, accuracy);
		feedbackPanel.showFeedback(stats, feedback);
		sidebarPanel.updateForStatsAvailable(true);
		showSessionCompletedDialog(stats);
	}

	private void showSessionCompletedDialog(SessionStats stats) {
		JDialog dialog = new JDialog(this, "Session Complete", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JPanel shell = new JPanel(new BorderLayout());
		shell.setBackground(new Color(10, 10, 12));
		shell.setBorder(new EmptyBorder(14, 14, 14, 14));

		JPanel card = new GlassCardPanel(20, new Color(25, 25, 25, 220));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 140), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel title = new JLabel("Session Completed");
		title.setForeground(new Color(90, 150, 255));
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

		String messageText = "WPM: " + stats.getWpm() + "  |  Accuracy: " + stats.getAccuracy() + "%";
		JLabel message = new JLabel(messageText);
		message.setForeground(Color.WHITE);
		message.setBorder(new EmptyBorder(8, 0, 0, 0));

		JButton continueButton = UiButtons.createPrimaryButton("Return");
		continueButton.addActionListener(e -> {
			dialog.dispose();
			showFeatureHub();
		});
		JPanel actions = new JPanel(new BorderLayout());
		actions.setOpaque(false);
		actions.setBorder(new EmptyBorder(14, 0, 0, 0));
		actions.add(continueButton, BorderLayout.EAST);

		card.add(title, BorderLayout.NORTH);
		card.add(message, BorderLayout.CENTER);
		card.add(actions, BorderLayout.SOUTH);

		shell.add(card, BorderLayout.CENTER);
		dialog.setContentPane(shell);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	public void showTestInstructions() {
		String body = "<html><div style='width:560px;'>"
			+ "<ol style='margin-top:8px;'>"
			+ "<li>(Optional) Select a difficulty in <b>Difficulty Selection</b>. If you skip this, the default is <b>BEGINNER</b>.</li>"
			+ "<li>Press <b>Start</b> to load the typing session text.</li>"
			+ "<li>Click inside the typing area to focus it.</li>"
			+ "<li>Start typing. The timer begins on your <b>first</b> character.</li>"
			+ "<li>Use <b>Backspace</b> to correct mistakes as you type.</li>"
			+ "<li>Finish by typing the full text exactly. You will see a <b>&quot;Session Completed&quot;</b> popup.</li>"
			+ "<li>Press <b>Return</b> to exit the feature.</li>"
			+ "</ol>"
			+ "</div></html>";

		showInstructionsDialog("How to use Test Feature", body);
	}

	public void showDifficultySelectionInstructions() {
		String body = "<html><div style='width:560px;'>"
			+ "<ol style='margin-top:8px;'>"
			+ "<li>Open <b>Difficulty Selection</b> from the Feature Hub.</li>"
			+ "<li>Click a difficulty card (<b>Beginner</b>, <b>Intermediate</b>, or <b>Advanced</b>) to <b>save</b> your selection.</li>"
			+ "<li>Confirm the <b>&quot;Difficulty Selected&quot;</b> popup.</li>"
			+ "<li>When you're ready to type, open the <b>Test</b> feature and press <b>Start</b>.</li>"
			+ "<li>Press <b>Return</b> to exit the feature.</li>"
			+ "</ol>"
			+ "</div></html>";

		showInstructionsDialog("How to use Difficulty Selection Feature", body);
	}

	public void showResultsInstructions() {
		String body = "<html><div style='width:560px;'>"
			+ "<ol style='margin-top:8px;'>"
			+ "<li>Complete a typing session to populate this screen with your latest stats.</li>"
			+ "<li>Read the top cards: <b>Speed</b>, <b>Accuracy</b>, <b>Errors</b>, and <b>Time</b>.</li>"
			+ "<li>Use <b>Detailed Analysis</b> to open the Accuracy breakdown.</li>"
			+ "<li>Use <b>Full Report</b> to open the Feedback screen.</li>"
			+ "<li>Use <b>Retry Test</b> for a new run, or <b>Return to Main</b> to leave this feature.</li>"
			+ "<li>Press <b>Close</b> to exit this popup.</li>"
			+ "</ol>"
			+ "</div></html>";

		showInstructionsDialog("How to use Results Feature", body);
	}

	public void showSessionRequiredDialog() {
		String body = "<html><div style='width:560px;'>"
			+ "Complete at least one test session first to open this feature. "
			+ "Go to <b>Test</b>, press <b>Start</b>, finish the passage, then come back to view Results, Accuracy, or Feedback."
			+ "</div></html>";

		showInstructionsDialog("Session Required", body);
	}

	private void showInstructionsDialog(String titleText, String htmlBody) {
		JDialog dialog = new JDialog(this, "Instructions", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		JPanel shell = new JPanel(new BorderLayout());
		shell.setBackground(new Color(10, 10, 12));
		shell.setBorder(new EmptyBorder(14, 14, 14, 14));

		JPanel card = new GlassCardPanel(20, new Color(25, 25, 25, 220));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 140), 1, true),
			new EmptyBorder(16, 16, 16, 16)));

		JLabel title = new JLabel(titleText);
		title.setForeground(new Color(90, 150, 255));
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

		JLabel message = new JLabel(htmlBody);
		message.setForeground(new Color(230, 230, 230));
		message.setBorder(new EmptyBorder(10, 0, 0, 0));
		message.setFont(message.getFont().deriveFont(14f));

		JButton closeButton = UiButtons.createPrimaryButton("Close");
		closeButton.addActionListener(e -> dialog.dispose());
		JPanel actions = new JPanel(new BorderLayout());
		actions.setOpaque(false);
		actions.setBorder(new EmptyBorder(14, 0, 0, 0));
		actions.add(closeButton, BorderLayout.EAST);

		card.add(title, BorderLayout.NORTH);
		card.add(message, BorderLayout.CENTER);
		card.add(actions, BorderLayout.SOUTH);

		shell.add(card, BorderLayout.CENTER);
		dialog.setContentPane(shell);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(this);

		// ESC closes the modal.
		dialog.getRootPane().registerKeyboardAction(
			e -> dialog.dispose(),
			javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
			javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

		dialog.setVisible(true);
	}

	public void cancelSession() {
		// Cancel drops transient session state and routes user back to Home.
		stopTimer();
		currentStats = null;
		currentTargetText = null;
		currentDifficulty = Difficulty.BEGINNER;
		sidebarPanel.updateForStatsAvailable(false);
		showHome();
	}

	public void retry() {
		// Retry returns to Home to select/start a fresh run.
		currentStats = null;
		currentTargetText = null;
		currentDifficulty = Difficulty.BEGINNER;
		homePanel.resetSelection();
		sidebarPanel.updateForStatsAvailable(false);
		showHome();
	}

	private void stopTimer() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
	}

	public SessionStats getCurrentStats() {
		return currentStats;
	}

	private static class BackgroundPanel extends JPanel {

		private final Image image;

		BackgroundPanel(Image image) {
			this.image = image;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			super.paintComponent(g2);
			if (image != null) {
				// Draw the original image at full opacity
				g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
				// Overlay a semi-transparent black layer to darken it
				g2.setComposite(AlphaComposite.SrcOver.derive(0.6f));
				g2.setColor(new java.awt.Color(0, 0, 0));
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
			g2.dispose();
		}
	}
}
