package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Instructions screen explaining the expected user flow for one typing session.
 *
 * <p>This panel is informational only and provides a direct navigation action
 * back to the main menu.
 */
public class InstructionsPanel extends JPanel {

	private final TypeGaugeFrame frame;
	private final JScrollPane scrollPane;

	public InstructionsPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(40, 250, 40, 250));

		// Use GlassCardPanel for the main container
		JPanel card = new GlassCardPanel(32, new Color(25, 25, 25, 220));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(24, 32, 24, 32)));

		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel titleLabel = new JLabel("Instructions");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 32f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerPanel.add(titleLabel);

		headerPanel.add(Box.createVerticalStrut(8));

		JLabel subtitleLabel = new JLabel("Master the diagnostic workflow to improve your typing precision.");
		subtitleLabel.setForeground(Color.LIGHT_GRAY);
		subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(16f));
		subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerPanel.add(subtitleLabel);

		// Container for the instruction cards
		JPanel stepContainer = new JPanel();
		stepContainer.setOpaque(false);
		stepContainer.setLayout(new BoxLayout(stepContainer, BoxLayout.Y_AXIS));
		stepContainer.setBorder(new EmptyBorder(20, 0, 0, 0));

		stepContainer.add(createStepCard(
			"01. Difficulty Selection",
			"Navigate to Home and choose between Beginner, Intermediate, or Advanced texts. Each level introduces complex punctuation and technical vocabulary.",
			new Color(90, 150, 255)
		));
		stepContainer.add(Box.createVerticalStrut(16));

		stepContainer.add(createStepCard(
			"02. The Typing Test",
			"Focus your attention on the typing area. The timer starts on your first keystroke. Accuracy is tracked per token; use backspace to fix errors before finishing.",
			new Color(0, 220, 140)
		));
		stepContainer.add(Box.createVerticalStrut(16));

		stepContainer.add(createStepCard(
			"03. Results Examiner",
			"Once finished, use the 'Examine Results' and 'Analyze' buttons. The engine will process your session data to visualize mistake patterns and speed consistency.",
			new Color(255, 215, 0)
		));
		stepContainer.add(Box.createVerticalStrut(16));

		stepContainer.add(createStepCard(
			"04. Accuracy Analyzer",
			"Drill down into the Accuracy Analyzer to see character-level breakdowns. Identify your 'Hotspots'—the specific keys where you lose momentum.",
			new Color(240, 80, 80)
		));
		stepContainer.add(Box.createVerticalStrut(16));

		stepContainer.add(createStepCard(
			"05. Feedback Generator",
			"Visit the Feedback Generator for rule-based suggestions. Review your next session targets for Speed and Accuracy to ensure steady progress.",
			new Color(150, 100, 255)
		));

		scrollPane = new JScrollPane(stepContainer);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
		scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);

		JButton backButton = UiButtons.createPrimaryButton("Back to Main Menu");
		backButton.addActionListener(e -> frame.showMainUi());
		backButton.setAlignmentX(LEFT_ALIGNMENT);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
		bottomPanel.setAlignmentX(LEFT_ALIGNMENT);
		bottomPanel.add(backButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(headerPanel);
		contentPanel.add(scrollPane);
		contentPanel.add(bottomPanel);

		card.add(contentPanel, BorderLayout.CENTER);
		add(card, BorderLayout.CENTER);
	}

	public void resetScroll() {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
	}

	private JPanel createStepCard(String title, String description, Color accentColor) {
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 180));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(16, 20, 16, 20)));
		card.setAlignmentX(LEFT_ALIGNMENT);

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(accentColor);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		card.add(titleLabel);

		card.add(Box.createVerticalStrut(8));

		JTextArea descArea = new JTextArea(description);
		descArea.setEditable(false);
		descArea.setOpaque(false);
		descArea.setLineWrap(true);
		descArea.setWrapStyleWord(true);
		descArea.setForeground(new Color(200, 200, 200));
		descArea.setFont(descArea.getFont().deriveFont(15f));
		descArea.setCaretPosition(0);
		descArea.setAlignmentX(LEFT_ALIGNMENT);
		card.add(descArea);

		return card;
	}

	private static class ModernScrollBarUI extends BasicScrollBarUI {
		@Override
		protected void configureScrollBarColors() {
			this.thumbColor = new Color(80, 80, 80, 150);
		}

		@Override
		protected JButton createDecreaseButton(int orientation) {
			return createZeroButton();
		}

		@Override
		protected JButton createIncreaseButton(int orientation) {
			return createZeroButton();
		}

		private JButton createZeroButton() {
			JButton jbutton = new JButton();
			jbutton.setPreferredSize(new Dimension(0, 0));
			return jbutton;
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(thumbColor);
			g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
			g2.dispose();
		}

		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(5, 5, 5, 120));
			g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
			g2.dispose();
		}
	}
}
