package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.Difficulty;
import ui.UiIcons;
import ui.UiButtons;

public class HomePanel extends JPanel {

	private final TypeGaugeFrame frame;

	public HomePanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(200, 400, 40, 400));
		setOpaque(false);

		buildTitleSection();
		buildCenterSection();
	}

	private void buildTitleSection() {
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);

		JLabel precisionLabel = new JLabel("Precision", SwingConstants.LEFT);
		precisionLabel.setFont(precisionLabel.getFont().deriveFont(64f));
		precisionLabel.setForeground(Color.WHITE);

		JLabel performanceLabel = new JLabel("Performance.", SwingConstants.LEFT);
		performanceLabel.setFont(performanceLabel.getFont().deriveFont(64f));
		performanceLabel.setForeground(new Color(80, 150, 255));

		JPanel headingPanel = new JPanel(new GridLayout(2, 1));
		headingPanel.setOpaque(false);
		headingPanel.add(precisionLabel);
		headingPanel.add(performanceLabel);

		JLabel subtitleLabel = new JLabel(
			"The ultimate typing diagnostic tool. Measure your speed, \nanalyze your accuracy, and master your keystrokes.");
		subtitleLabel.setForeground(Color.LIGHT_GRAY);
		subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(16f));

		titlePanel.add(headingPanel, BorderLayout.NORTH);
		titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

		add(titlePanel, BorderLayout.NORTH);
	}

	private void buildCenterSection() {
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);

		JPanel cardsPanel = buildCardsPanel();
		JPanel bottomPanel = buildBottomPanel();

		JPanel contentWrapper = new JPanel(new BorderLayout());
		contentWrapper.setOpaque(false);
		contentWrapper.add(cardsPanel, BorderLayout.CENTER);
		contentWrapper.add(bottomPanel, BorderLayout.SOUTH);

		centerPanel.add(contentWrapper, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
	}

	private JPanel buildCardsPanel() {
		JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
		cardsPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
		cardsPanel.setOpaque(false);
		cardsPanel.setPreferredSize(new Dimension(0, 285));

		Difficulty[] difficulties = {
			Difficulty.BEGINNER,
			Difficulty.INTERMEDIATE,
			Difficulty.ADVANCED
		};
		String[] titles = {
			"Beginner",
			"Intermediate",
			"Advanced"
		};
		String[] descriptions = {
			"Perfect for warming up. Simple sentences and common vocabulary.",
			"Challenge yourself with longer quotes and varied punctuation.",
			"The ultimate test. Complex technical terms and philosophical texts."
		};
		String iconPath = "/ui/icons/flash.png"; // reuse same base icon for now

		for (int i = 0; i < difficulties.length; i++) {
			JButton card = createDifficultyCard(titles[i], descriptions[i], difficulties[i], iconPath);
			cardsPanel.add(card);
		}

		return cardsPanel;
	}

	private JPanel buildBottomPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);
		bottomPanel.setPreferredSize(new Dimension(0, 180));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

		JLabel calloutTitleLabel = new JLabel("Ready to beat your record?");
		calloutTitleLabel.setForeground(Color.WHITE);
		calloutTitleLabel.setFont(calloutTitleLabel.getFont().deriveFont(Font.BOLD, 16f));

		JLabel calloutDescLabel = new JLabel("Select a difficulty above to begin your diagnostic session.");
		calloutDescLabel.setForeground(Color.LIGHT_GRAY);
		calloutDescLabel.setFont(calloutDescLabel.getFont().deriveFont(13f));

		JPanel statsPreviewPanel = new JPanel(new GridLayout(1, 2, 40, 0));
		statsPreviewPanel.setOpaque(false);

		JLabel avgSpeedLabel = new JLabel("<html><div style='text-align:right;'>AVG SPEED<br>--</div></html>",
			SwingConstants.RIGHT);
		avgSpeedLabel.setForeground(Color.LIGHT_GRAY);

		JLabel accuracyLabel = new JLabel("<html><div style='text-align:right;'>ACCURACY<br>--</div></html>",
			SwingConstants.RIGHT);
		accuracyLabel.setForeground(Color.LIGHT_GRAY);

		statsPreviewPanel.add(avgSpeedLabel);
		statsPreviewPanel.add(accuracyLabel);

		JPanel readyCard = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(25, 25, 25, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		readyCard.setOpaque(false);
		readyCard.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 120), 1, true),
			new EmptyBorder(16, 24, 16, 24)));

		JPanel leftSection = new JPanel(new BorderLayout());
		leftSection.setOpaque(false);

		ImageIcon recapIcon = UiIcons.loadIcon("/ui/icons/flash.png", 20, 20);
		if (recapIcon != null) {
			JPanel iconContainer = new JPanel(new BorderLayout()) {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(new Color(56, 120, 230));
					int size = Math.min(getWidth(), getHeight());
					g2.fillOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
					g2.dispose();
					super.paintComponent(g);
				}
			};
			iconContainer.setOpaque(false);
			iconContainer.setBorder(new EmptyBorder(8, 8, 8, 8));
			Dimension circleSize = new Dimension(40, 40);
			iconContainer.setPreferredSize(circleSize);
			iconContainer.setMinimumSize(circleSize);
			iconContainer.setMaximumSize(circleSize);

			JLabel iconLabel = new JLabel(recapIcon);
			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
			iconLabel.setVerticalAlignment(SwingConstants.CENTER);
			iconContainer.add(iconLabel, BorderLayout.CENTER);

			leftSection.add(iconContainer, BorderLayout.WEST);
		}

		JPanel textPanel = new JPanel();
		textPanel.setOpaque(false);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setBorder(new EmptyBorder(0, 16, 0, 0));
		textPanel.add(calloutTitleLabel);
		textPanel.add(Box.createVerticalStrut(4));
		textPanel.add(calloutDescLabel);

		leftSection.add(textPanel, BorderLayout.CENTER);

		JPanel leftWrapper = new JPanel(new GridBagLayout());
		leftWrapper.setOpaque(false);
		leftWrapper.add(leftSection);

		JPanel statsWrapper = new JPanel(new GridBagLayout());
		statsWrapper.setOpaque(false);
		statsWrapper.add(statsPreviewPanel);

		readyCard.add(leftWrapper, BorderLayout.WEST);
		readyCard.add(statsWrapper, BorderLayout.EAST);

		bottomPanel.add(readyCard, BorderLayout.CENTER);

		JButton returnToMainButton = UiButtons.createPrimaryButton("Return to Main");
		returnToMainButton.addActionListener(e -> frame.showMainUi());
		JPanel buttonRow = new JPanel(new BorderLayout());
		buttonRow.setOpaque(false);
		buttonRow.setBorder(new EmptyBorder(8, 0, 0, 0));
		buttonRow.add(returnToMainButton, BorderLayout.WEST);
		bottomPanel.add(buttonRow, BorderLayout.SOUTH);
		return bottomPanel;
	}

	private JButton createDifficultyCard(String title, String description, Difficulty difficulty, String iconPath) {
		JButton button = new JButton() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(25, 25, 25, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		button.setLayout(new BorderLayout());
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setOpaque(false);

		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(Box.createVerticalStrut(16));

		if (iconPath != null) {
			ImageIcon icon = UiIcons.loadIcon(iconPath, 20, 20);
			if (icon != null) {
				JPanel iconContainer = new JPanel(new BorderLayout());
				iconContainer.setOpaque(true);
				iconContainer.setBackground(new Color(56, 120, 230));
				iconContainer.setBorder(new EmptyBorder(8, 8, 8, 8));
				iconContainer.setMaximumSize(new Dimension(40, 40));
				iconContainer.setAlignmentX(LEFT_ALIGNMENT);

				JLabel iconLabel = new JLabel(icon);
				iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
				iconLabel.setVerticalAlignment(SwingConstants.CENTER);
				iconContainer.add(iconLabel, BorderLayout.CENTER);

				content.add(iconContainer);
				content.add(Box.createVerticalStrut(16));
			}
		}

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		content.add(titleLabel);
		content.add(Box.createVerticalStrut(8));

		JLabel descLabel = new JLabel("<html>" + description + "</html>");
		descLabel.setForeground(Color.LIGHT_GRAY);
		descLabel.setFont(descLabel.getFont().deriveFont(14f));
		descLabel.setAlignmentX(LEFT_ALIGNMENT);
		content.add(descLabel);

		button.add(content, BorderLayout.CENTER);
		button.addActionListener(e -> frame.startSession(difficulty));
		return button;
	}

	private ImageIcon loadIcon(String path, int width, int height) {
		java.net.URL url = getClass().getResource(path);
		if (url == null) {
			return null;
		}
		ImageIcon icon = new ImageIcon(url);
		Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}

	public void resetSelection() {
		// No explicit selection to clear when using cards that start sessions directly.
	}
}
