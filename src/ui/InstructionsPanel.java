package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class InstructionsPanel extends JPanel {

	private final TypeGaugeFrame frame;

	public InstructionsPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(80, 400, 40, 400));

		JPanel card = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(25, 25, 25, 220));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		card.setOpaque(false);
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(24, 32, 24, 32)));

		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		JLabel titleLabel = new JLabel("Instructions");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerPanel.add(titleLabel);

		headerPanel.add(Box.createVerticalStrut(8));

		JLabel subtitleLabel = new JLabel("How to use TypeGauge in a single session.");
		subtitleLabel.setForeground(Color.LIGHT_GRAY);
		subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(14f));
		subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerPanel.add(subtitleLabel);

		JPanel bodyPanel = new JPanel();
		bodyPanel.setOpaque(false);
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
		bodyPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

		JLabel step1 = new JLabel("1. Choose a difficulty level from the Home screen.");
		JLabel step2 = new JLabel("2. Read the sample text and start typing in the test area.");
		JLabel step3 = new JLabel("3. Focus on both speed and accuracy while you type.");
		JLabel step4 = new JLabel("4. Review your WPM, accuracy, and errors in the Results and Accuracy panels.");
		JLabel step5 = new JLabel("5. Read the Feedback panel for a short assessment and tips.");

		JLabel[] steps = { step1, step2, step3, step4, step5 };
		for (JLabel label : steps) {
			label.setForeground(Color.LIGHT_GRAY);
			label.setFont(label.getFont().deriveFont(14f));
			label.setAlignmentX(LEFT_ALIGNMENT);
			bodyPanel.add(label);
			bodyPanel.add(Box.createVerticalStrut(6));
		}

		JButton backButton = UiButtons.createPrimaryButton("Back to Main Menu");
		backButton.addActionListener(e -> frame.showMainUi());
		backButton.setAlignmentX(LEFT_ALIGNMENT);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
		bottomPanel.add(backButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(headerPanel);
		contentPanel.add(bodyPanel);
		contentPanel.add(bottomPanel);

		card.add(contentPanel, BorderLayout.CENTER);
		add(card, BorderLayout.CENTER);
	}
}
