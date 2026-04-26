package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import model.Difficulty;

/**
 * Active typing screen that captures keystrokes and streams live session stats.
 *
 * <p>The frame starts/stops timing, while this panel owns text input state,
 * per-character rendering, and completion detection.
 */
public class TestPanel extends JPanel {

	private final TypeGaugeFrame frame;
	private final JLabel difficultyLabel;
	private final JLabel sessionIdLabel;
	private final JLabel timeLabel;
	private final JLabel wpmLabel;
	private final JLabel accuracyLabel;
	private final JTextPane targetPane;

	private String targetText = "";
	private boolean typingStarted = false;
	private String sessionId = "";
	private String currentInput = "";
	private boolean sessionFinished = false;

	// Token-based accuracy model: accuracy drops once per token (word or punctuation).
	// Tokens are derived from the target text and mistakes do not recover after backspace.
	private int[] tokenIndexByChar = new int[0];
	private int tokenCount = 0;
	private final BitSet mistakenTokenIndexes = new BitSet();
	private int letterMistakeCount = 0;
	private int numberMistakeCount = 0;
	private int punctuationMistakeCount = 0;
	private int spacingMistakeCount = 0;
	private final BitSet mistakenCharacterIndexes = new BitSet();

	public TestPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(20, 400, 40, 400));
		setOpaque(false);

		// Header: difficulty pill + session id on the left, cancel on the right
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);

		JPanel leftHeader = new JPanel();
		leftHeader.setOpaque(false);

		difficultyLabel = new JLabel("BEGINNER");
		difficultyLabel.setOpaque(true);
		difficultyLabel.setBackground(new Color(20, 60, 140));
		difficultyLabel.setForeground(Color.WHITE);
		difficultyLabel.setBorder(new EmptyBorder(6, 12, 6, 12));
		difficultyLabel.setFont(difficultyLabel.getFont().deriveFont(14f));
		leftHeader.add(difficultyLabel);

		sessionIdLabel = new JLabel("Session ID: ----");
		sessionIdLabel.setForeground(new Color(160, 160, 160));
		sessionIdLabel.setBorder(new EmptyBorder(0, 12, 0, 0));
		sessionIdLabel.setFont(sessionIdLabel.getFont().deriveFont(14f));
		leftHeader.add(sessionIdLabel);

		headerPanel.add(leftHeader, BorderLayout.WEST);

		JButton startButton = UiButtons.createPrimaryButton("Start");
		startButton.addActionListener(e -> frame.startSessionFromSelectedDifficulty());
		JButton instructionsButton = UiButtons.createPrimaryButton("Instructions");
		instructionsButton.addActionListener(e -> frame.showTestInstructions());
		JButton returnToMainButton = UiButtons.createPrimaryButton("Return");
		returnToMainButton.addActionListener(e -> frame.showFeatureHub());
		JPanel headerRight = new JPanel();
		headerRight.setOpaque(false);
		headerRight.add(startButton);
		headerRight.add(instructionsButton);
		headerRight.add(returnToMainButton);
		headerPanel.add(headerRight, BorderLayout.EAST);

		add(headerPanel, BorderLayout.NORTH);

		// Center: top stats row + large typing card
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);

		JPanel statsRow = new JPanel(new GridLayout(1, 3, 20, 0));
		statsRow.setOpaque(false);
		statsRow.setBorder(new EmptyBorder(20, 0, 20, 0));

		JPanel timeCard = UiCards.createGlassStatCard("TIME", null, "/ui/icons/flash.png",
			timeLabel = new JLabel("0s", SwingConstants.LEFT));
		JPanel wpmCard = UiCards.createGlassStatCard("WPM", null, "/ui/icons/flash.png",
			wpmLabel = new JLabel("0", SwingConstants.LEFT));
		JPanel accuracyCard = UiCards.createGlassStatCard("ACCURACY", null, "/ui/icons/flash.png",
			accuracyLabel = new JLabel("100%", SwingConstants.LEFT));

		statsRow.add(timeCard);
		statsRow.add(wpmCard);
		statsRow.add(accuracyCard);

		centerPanel.add(statsRow, BorderLayout.NORTH);

		// Typing card with integrated typing panel
		targetPane = new JTextPane();
		targetPane.setEditable(false);
		targetPane.setForeground(new Color(220, 220, 220));
		targetPane.setOpaque(false);
		targetPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		targetPane.setFont(targetPane.getFont().deriveFont(32f));
		targetPane.setFocusable(true);
		// Ensure caret is visible and styled even though pane is non-editable
		targetPane.setCaretColor(new Color(80, 200, 140));
		if (targetPane.getCaret() != null) {
			targetPane.getCaret().setVisible(true);
		}
		targetPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
				// Ignore control characters (including backspace). Backspace is handled in keyPressed.
				if (Character.isISOControl(ch)) {
					return;
				}
				handleTypedChar(ch);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					handleBackspace();
					e.consume();
				}
			}
		});

		JPanel typingCard = new GlassCardPanel(40, new Color(25, 25, 25, 160));
		typingCard.setLayout(new BorderLayout());
		typingCard.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(0, 0, 0, 0)));
		typingCard.setPreferredSize(new java.awt.Dimension(0, 1000));
		JScrollPane targetScroll = new JScrollPane(targetPane);
		targetScroll.setOpaque(false);
		javax.swing.JViewport viewport = targetScroll.getViewport();
		if (viewport != null) {
			viewport.setOpaque(false);
		}
		targetScroll.setBorder(BorderFactory.createEmptyBorder());
		typingCard.add(targetScroll, BorderLayout.CENTER);

		JPanel statusRow = new JPanel(new BorderLayout());
		statusRow.setOpaque(false);
		statusRow.setBorder(new EmptyBorder(10, 20, 10, 20));

		JLabel diagnosticsLabel = new JLabel("Diagnostics active. Typing input captured.");
		diagnosticsLabel.setForeground(new Color(150, 150, 150));
		statusRow.add(diagnosticsLabel, BorderLayout.WEST);

		JLabel liveLabel = new JLabel("LIVE TRACKING", SwingConstants.RIGHT);
		liveLabel.setForeground(new Color(0, 200, 120));
		statusRow.add(liveLabel, BorderLayout.EAST);

		typingCard.add(statusRow, BorderLayout.SOUTH);

		centerPanel.add(typingCard, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);
	}

 

	public void prepareForSession(Difficulty difficulty, String targetText) {
		// Reset all transient state so each session starts from a clean slate.
		this.targetText = targetText != null ? targetText : "";
		this.typingStarted = false;
		this.sessionId = generateSessionId();
		this.currentInput = "";
		this.sessionFinished = false;
		this.letterMistakeCount = 0;
		this.numberMistakeCount = 0;
		this.punctuationMistakeCount = 0;
		this.spacingMistakeCount = 0;
			this.mistakenCharacterIndexes.clear();
		buildAccuracyTokens(this.targetText);
		difficultyLabel.setText(difficulty != null ? difficulty.name() : "");
		sessionIdLabel.setText("Session ID: " + sessionId);
		timeLabel.setText("0s");
		wpmLabel.setText("0");
		accuracyLabel.setText("100%");
		renderTargetText("");
		targetPane.setEnabled(true);
		targetPane.requestFocusInWindow();
		// Place caret at the start of the text for a new session
		try {
			targetPane.setCaretPosition(0);
			if (targetPane.getCaret() != null) {
				targetPane.getCaret().setVisible(true);
			}
		} catch (IllegalArgumentException ignored) {
		}
	}

	private void handleTypedChar(char ch) {
		if (sessionFinished || targetText == null || targetText.isEmpty()) {
			return;
		}

		if (!typingStarted) {
			typingStarted = true;
			frame.onTypingStarted();
		}

		if (currentInput.length() >= targetText.length()) {
			return;
		}

		int charIndex = currentInput.length();
		currentInput = currentInput + ch;

		// Persist space mistakes at character level
		// Persist mistakes at character level and token level
		if (charIndex >= 0 && charIndex < targetText.length()) {
			char expected = targetText.charAt(charIndex);
			if (expected == ' ' && ch != ' ') {
				// Mark this character as a mistaken space
				mistakenCharacterIndexes.set(charIndex);
				spacingMistakeCount++;
				// Also register as a token mistake so accuracy is affected
				registerTokenMistakeAt(charIndex);
			} else if (ch != expected) {
				registerTokenMistakeAt(charIndex);
			}
		}
		applyInputUpdate();
	}

	private void handleBackspace() {
		if (sessionFinished || currentInput.isEmpty()) {
			return;
		}
		currentInput = currentInput.substring(0, currentInput.length() - 1);
		applyInputUpdate();
	}

	private void applyInputUpdate() {
		renderTargetText(currentInput);
		// Move caret to current typing position so the cursor follows the user input
		try {
			targetPane.setCaretPosition(currentInput.length());
		} catch (IllegalArgumentException ignored) {
		}

		// Session ends when typed input exactly matches the target text.
		if (!targetText.isEmpty() && currentInput.equals(targetText)) {
			sessionFinished = true;
			targetPane.setEnabled(false);
			frame.finishSession(currentInput);
		}
	}

	private void renderTargetText(String currentInput) {
		// Re-render whole line with per-character color coding.
		// Correct = green, incorrect = red, pending = gray.
		StyledDocument doc = targetPane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (javax.swing.text.BadLocationException ignored) {
		}

		if (targetText == null) {
			return;
		}

		for (int i = 0; i < targetText.length(); i++) {
			char c = targetText.charAt(i);
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			if (i < currentInput.length()) {
				if (currentInput.charAt(i) == c) {
					StyleConstants.setForeground(attrs, new Color(80, 200, 140));
				} else {
					StyleConstants.setForeground(attrs, new Color(230, 80, 80));
				}
			} else {
				StyleConstants.setForeground(attrs, new Color(120, 120, 120));
			}

			// Keep spaces as spaces. If the expected char is a space but the user typed something else,
			// display a visible red dot to show the mistake at that position.
			String displayChar;
			if (c == ' ' && i < currentInput.length() && currentInput.charAt(i) != ' ') {
				displayChar = "·";
			} else {
				displayChar = String.valueOf(c);
			}
			try {
				doc.insertString(doc.getLength(), displayChar, attrs);
			} catch (javax.swing.text.BadLocationException ignored) {
			}
		}
	}

	public int getCurrentInputLength() {
		return currentInput.length();
	}

	public int getCurrentCorrectCharCount() {
		String current = currentInput;
		int length = Math.min(current.length(), targetText.length());
		int correct = 0;
		for (int i = 0; i < length; i++) {
			if (current.charAt(i) == targetText.charAt(i)) {
				correct++;
			}
		}
		return correct;
	}

	public void updateLiveStats(int seconds, int wpm, int accuracy) {
		timeLabel.setText(seconds + "s");
		wpmLabel.setText(String.valueOf(wpm));
		accuracyLabel.setText(accuracy + "%");
	}

	public int getSessionAccuracyPercent() {
		if (tokenCount <= 0) {
			return 100;
		}
		int wrongTokens = mistakenTokenIndexes.cardinality();
		double percent = ((tokenCount - wrongTokens) * 100.0) / tokenCount;
		return (int) Math.max(0, Math.round(percent));
	}

	public int getSessionErrorCount() {
		// Errors follow the same concept as the token-based accuracy model:
		// count each token only once, and do not recover on backspace.
		return mistakenTokenIndexes.cardinality();
	}

	public int getLetterMistakeCount() {
		return letterMistakeCount;
	}

	public int getNumberMistakeCount() {
		return numberMistakeCount;
	}

	public int getPunctuationMistakeCount() {
		return punctuationMistakeCount;
	}

	public int getSpacingMistakeCount() {
		return spacingMistakeCount;
	}

	public BitSet getMistakenCharacterIndexes() {
		return (BitSet) mistakenCharacterIndexes.clone();
	}

	private void markMistakenCharacterRange(int tokenIndex) {
		for (int i = 0; i < tokenIndexByChar.length; i++) {
			if (tokenIndexByChar[i] == tokenIndex) {
				mistakenCharacterIndexes.set(i);
			}
		}
	}

	private void registerTokenMistakeAt(int charIndex) {
		if (tokenCount <= 0 || tokenIndexByChar.length == 0) {
			return;
		}
		if (charIndex < 0 || charIndex >= tokenIndexByChar.length) {
			return;
		}

		int tokenIndex = tokenIndexByChar[charIndex];
		if (tokenIndex < 0) {
			tokenIndex = findNearestTokenIndex(charIndex);
		}
		if (tokenIndex < 0) {
			return;
		}

		if (!mistakenTokenIndexes.get(tokenIndex)) {
			mistakenTokenIndexes.set(tokenIndex);
			markMistakenCharacterRange(tokenIndex);
			trackMistakeCategory(charIndex);
			accuracyLabel.setText(getSessionAccuracyPercent() + "%");
		}
	}

	private void trackMistakeCategory(int charIndex) {
		if (targetText == null || charIndex < 0 || charIndex >= targetText.length()) {
			return;
		}

		char expected = targetText.charAt(charIndex);
		if (Character.isWhitespace(expected)) {
			spacingMistakeCount++;
		} else if (Character.isLetter(expected)) {
			letterMistakeCount++;
		} else if (Character.isDigit(expected)) {
			numberMistakeCount++;
		} else {
			punctuationMistakeCount++;
		}
	}

	private int findNearestTokenIndex(int charIndex) {
		for (int left = charIndex - 1; left >= 0; left--) {
			int tokenIndex = tokenIndexByChar[left];
			if (tokenIndex >= 0) {
				return tokenIndex;
			}
		}
		for (int right = charIndex + 1; right < tokenIndexByChar.length; right++) {
			int tokenIndex = tokenIndexByChar[right];
			if (tokenIndex >= 0) {
				return tokenIndex;
			}
		}
		return -1;
	}

	private void buildAccuracyTokens(String text) {
		mistakenTokenIndexes.clear();
		tokenCount = 0;
		if (text == null || text.isEmpty()) {
			tokenIndexByChar = new int[0];
			return;
		}

		tokenIndexByChar = new int[text.length()];
		Arrays.fill(tokenIndexByChar, -1);

		List<int[]> spans = computeTokenSpans(text);
		tokenCount = spans.size();
		for (int tokenIndex = 0; tokenIndex < spans.size(); tokenIndex++) {
			int[] span = spans.get(tokenIndex);
			for (int i = span[0]; i < span[1] && i < tokenIndexByChar.length; i++) {
				tokenIndexByChar[i] = tokenIndex;
			}
		}
	}

	private static List<int[]> computeTokenSpans(String text) {
		List<int[]> spans = new ArrayList<>();
		int i = 0;
		while (i < text.length()) {
			char ch = text.charAt(i);
			if (Character.isWhitespace(ch)) {
				i++;
				continue;
			}

			if (isWordChar(ch)) {
				int start = i;
				i++;
				while (i < text.length() && isWordChar(text.charAt(i))) {
					i++;
				}
				spans.add(new int[] { start, i });
			} else {
				spans.add(new int[] { i, i + 1 });
				i++;
			}
		}
		return spans;
	}

	private static boolean isWordChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch == '\'' || ch == '\u2019';
	}

	private String generateSessionId() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			int index = (int) (Math.random() * chars.length());
			sb.append(chars.charAt(index));
		}
		return sb.toString();
	}
}
