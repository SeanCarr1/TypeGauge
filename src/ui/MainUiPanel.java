package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Landing screen shown when the app opens.
 *
 * <p>Presents the product identity and primary navigation actions: start,
 * instructions, credits, and exit.
 */
public class MainUiPanel extends JPanel {

	private final TypeGaugeFrame frame;

	public MainUiPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setOpaque(false);
		setLayout(new GridBagLayout());
		// Slightly reduce outer margins so the main card appears larger.
		setBorder(new EmptyBorder(40, 320, 40, 320));

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
			// Reduce inner padding a bit so more space is used for content.
			new EmptyBorder(32, 48, 32, 48)));

		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		ImageIcon logoIcon = UiIcons.loadIcon("/ui/icons/logo.png", 64, 64);
		if (logoIcon != null) {
			JLabel logoLabel = new JLabel(logoIcon);
			logoLabel.setAlignmentX(CENTER_ALIGNMENT);
			headerPanel.add(logoLabel);
			headerPanel.add(Box.createVerticalStrut(16));
		}

		JLabel titleLabel = new JLabel("TypeGauge");
		titleLabel.setForeground(Color.WHITE);
		// Make the title slightly larger to feel more prominent.
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 38f));
		titleLabel.setAlignmentX(CENTER_ALIGNMENT);
		headerPanel.add(titleLabel);

		headerPanel.add(Box.createVerticalStrut(8));

		JLabel subtitleLabel = new JLabel("Your Typing, Measured Instantly");
		subtitleLabel.setForeground(Color.LIGHT_GRAY);
		subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(18f));
		subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
		headerPanel.add(subtitleLabel);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		// Bring buttons closer to the card edges horizontally so the stack looks larger.
		buttonsPanel.setBorder(new EmptyBorder(32, 72, 24, 72));

		JButton startButton = UiButtons.createPrimaryButton("Start");
		JButton instructionsButton = UiButtons.createPrimaryButton("Instructions");
		JButton creditsButton = UiButtons.createPrimaryButton("Credits");
		JButton exitButton = UiButtons.createPrimaryButton("Exit");

		JButton[] buttons = { startButton, instructionsButton, creditsButton, exitButton };
		for (JButton button : buttons) {
			button.setAlignmentX(CENTER_ALIGNMENT);
			// Allow buttons to be a bit taller for stronger presence.
			button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
		}

		buttonsPanel.add(startButton);
		buttonsPanel.add(Box.createVerticalStrut(12));
		buttonsPanel.add(instructionsButton);
		buttonsPanel.add(Box.createVerticalStrut(12));
		buttonsPanel.add(creditsButton);
		buttonsPanel.add(Box.createVerticalStrut(12));
		buttonsPanel.add(exitButton);

		JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		content.add(headerPanel, BorderLayout.NORTH);
		content.add(buttonsPanel, BorderLayout.CENTER);

		card.add(content, BorderLayout.CENTER);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(card, gbc);

		startButton.addActionListener(e -> frame.showFeatureHub());
		instructionsButton.addActionListener(e -> frame.showInstructions());
		creditsButton.addActionListener(e -> frame.showCredits());
		exitButton.addActionListener(e -> frame.exitApplication());
	}
}
