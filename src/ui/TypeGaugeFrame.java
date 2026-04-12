package ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.Difficulty;
import model.FeedbackResult;
import model.FeedbackService;
import model.SessionStats;
import model.StatsUtil;

public class TypeGaugeFrame extends JFrame {

	private static final String CARD_MAIN = "main";
	private static final String CARD_HOME = "home";
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
	private final TestPanel testPanel;
	private final ResultsPanel resultsPanel;
	private final AccuracyPanel accuracyPanel;
	private final FeedbackPanel feedbackPanel;
	private final AboutPanel aboutPanel;
	private final InstructionsPanel instructionsPanel;
	private final CreditsPanel creditsPanel;

	private Difficulty currentDifficulty;
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
		testPanel = new TestPanel(this);
		resultsPanel = new ResultsPanel(this);
		accuracyPanel = new AccuracyPanel(this);
		feedbackPanel = new FeedbackPanel(this);
		aboutPanel = new AboutPanel(this);
		instructionsPanel = new InstructionsPanel(this);
		creditsPanel = new CreditsPanel(this);

		cardPanel.add(mainUiPanel, CARD_MAIN);
		cardPanel.add(homePanel, CARD_HOME);
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
		showMainUi();

		pack();
		setLocationRelativeTo(null);
	}

	public void showMainUi() {
		cardLayout.show(cardPanel, CARD_MAIN);
		sidebarPanel.setActiveScreen(CARD_MAIN);
	}

	public void startSession(Difficulty difficulty) {
		if (difficulty == null) {
			showDifficultyRequiredDialog();
			return;
		}
		this.currentDifficulty = difficulty;
		this.currentTargetText = difficulty.getRandomSampleText();
		this.elapsedSeconds = 0;
		this.currentStats = null;

		testPanel.prepareForSession(currentDifficulty, currentTargetText);
		showTest();
	}

	private void showDifficultyRequiredDialog() {
		JOptionPane.showMessageDialog(this,
			"Please select a difficulty before starting.",
			"Difficulty Required",
			JOptionPane.WARNING_MESSAGE);
	}

	public void showHome() {
		cardLayout.show(cardPanel, CARD_HOME);
		sidebarPanel.setActiveScreen(CARD_HOME);
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
			int correctCharsLive = testPanel.getCurrentCorrectCharCount();
			int accuracyLive = StatsUtil.calculateAccuracy(correctCharsLive, Math.max(charsTyped, 1));
			testPanel.updateLiveStats(elapsedSeconds, wpm, accuracyLive);
		});
	}

	public void finishSession(String finalInput) {
		stopTimer();

		if (currentTargetText == null || currentDifficulty == null) {
			showHome();
			return;
		}

		int totalChars = currentTargetText.length();
		int errors = StatsUtil.countErrors(currentTargetText, finalInput);
		int correctChars = Math.max(0, totalChars - errors);
		int wpm = StatsUtil.calculateWpm(totalChars, elapsedSeconds);
		int accuracy = StatsUtil.calculateAccuracy(correctChars, totalChars);

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

		showResults();
	}

	public void cancelSession() {
		stopTimer();
		currentStats = null;
		currentTargetText = null;
		currentDifficulty = null;
		sidebarPanel.updateForStatsAvailable(false);
		showHome();
	}

	public void retry() {
		currentStats = null;
		currentTargetText = null;
		currentDifficulty = null;
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
