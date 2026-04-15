package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * Left-side icon navigation used across all screens.
 *
 * <p>The frame owns the active card state; this sidebar reflects that state and
 * dispatches navigation actions back to the frame.
 */
public class SidebarPanel extends JPanel {

	private final TypeGaugeFrame frame;

	private final JButton mainButton;
	private final JButton homeButton;
	private final JButton testButton;
	private final JButton resultsButton;
	private final JButton accuracyButton;
	private final JButton feedbackButton;
	private final JButton aboutButton;
	private final JButton settingsButton;
	private JButton activeButton;

	public SidebarPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setPreferredSize(new Dimension(80, 0));
		setLayout(new BorderLayout());
		setBackground(new Color(15, 15, 20));
		setBorder(new EmptyBorder(16, 8, 16, 8));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		ImageIcon logoIcon = UiIcons.loadIcon("/ui/icons/logo.png", 32, 32);
		JLabel logoLabel = new JLabel(logoIcon);
		logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logoLabel.setBorder(new EmptyBorder(8, 0, 16, 0));
		topPanel.add(logoLabel, BorderLayout.NORTH);

		JPanel navPanel = new JPanel();
		navPanel.setOpaque(false);
		navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

		mainButton = createNavButton("/ui/icons/logo.png");
		homeButton = createNavButton("/ui/icons/nav_home.png");
		testButton = createNavButton("/ui/icons/nav_target.png");
		resultsButton = createNavButton("/ui/icons/nav_check.png");
		accuracyButton = createNavButton("/ui/icons/nav_bars.png");
		feedbackButton = createNavButton("/ui/icons/nav_history.png");
		aboutButton = createNavButton("/ui/icons/nav_info.png");

		mainButton.setToolTipText("Main menu");
		homeButton.setToolTipText("Home");
		testButton.setToolTipText("Test");
		resultsButton.setToolTipText("Results");
		accuracyButton.setToolTipText("Accuracy");
		feedbackButton.setToolTipText("Feedback");
		aboutButton.setToolTipText("About TypeGauge");

		mainButton.addActionListener(e -> {
			setActiveButton(mainButton);
			frame.showMainUi();
		});
		homeButton.addActionListener(e -> {
			setActiveButton(homeButton);
			frame.showHome();
		});
		testButton.addActionListener(e -> {
			setActiveButton(testButton);
			frame.showTest();
		});
		resultsButton.addActionListener(e -> {
			setActiveButton(resultsButton);
			frame.showResults();
		});
		accuracyButton.addActionListener(e -> {
			setActiveButton(accuracyButton);
			frame.showAccuracy();
		});
		feedbackButton.addActionListener(e -> {
			setActiveButton(feedbackButton);
			frame.showFeedback();
		});
		aboutButton.addActionListener(e -> {
			setActiveButton(aboutButton);
			frame.showAbout();
		});

		navPanel.add(wrapNavButton(mainButton));
		navPanel.add(Box.createVerticalStrut(40));
		navPanel.add(wrapNavButton(homeButton));
		navPanel.add(Box.createVerticalStrut(40));
		navPanel.add(wrapNavButton(testButton));
		navPanel.add(Box.createVerticalStrut(40));
		navPanel.add(wrapNavButton(resultsButton));
		navPanel.add(Box.createVerticalStrut(40));
		navPanel.add(wrapNavButton(accuracyButton));
		navPanel.add(Box.createVerticalStrut(40));
		navPanel.add(wrapNavButton(feedbackButton));
		navPanel.add(Box.createVerticalStrut(40));
		navPanel.add(wrapNavButton(aboutButton));

		topPanel.add(navPanel, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);

		settingsButton = createNavButton("/ui/icons/nav_settings.png");
		settingsButton.setToolTipText("Settings");
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);
		bottomPanel.add(wrapNavButton(settingsButton), BorderLayout.SOUTH);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private JButton createNavButton(String iconPath) {
		JButton button = new JButton() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (this == activeButton) {
					Color blue = new Color(59, 89, 152);
					int inset = 2; // smaller inset => larger pill around icon
					int w = getWidth() - inset * 2;
					int h = getHeight() - inset * 2;
					g2.setColor(blue);
					g2.fillRoundRect(inset, inset, w, h, 24, 24);
				}
				g2.dispose();
				super.paintComponent(g);
			}
		};
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setForeground(Color.LIGHT_GRAY);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		Dimension square = new Dimension(44, 44);
		button.setPreferredSize(square);
		button.setMinimumSize(square);
		button.setMaximumSize(square);
		ImageIcon icon = UiIcons.loadIcon(iconPath, 20, 20);
		if (icon != null) {
			button.setIcon(icon);
		}
		return button;
	}

	private JPanel wrapNavButton(JButton button) {
		// Transparent wrapper with horizontal padding so the blue pill
		// doesn't touch the sidebar edges.
		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		wrapper.setOpaque(false);
		wrapper.setBorder(new EmptyBorder(0, 4, 0, 4));
		wrapper.add(button);
		return wrapper;
	}

	private void setActiveButton(JButton button) {
		if (button == null || !button.isEnabled()) {
			return;
		}
		if (activeButton == button) {
			return;
		}
		activeButton = button;
		repaint();
	}

	public void setActiveScreen(String screenId) {
		if ("main".equals(screenId)) {
			setActiveButton(mainButton);
		} else if ("home".equals(screenId)) {
			setActiveButton(homeButton);
		} else if ("test".equals(screenId)) {
			setActiveButton(testButton);
		} else if ("results".equals(screenId)) {
			setActiveButton(resultsButton);
		} else if ("accuracy".equals(screenId)) {
			setActiveButton(accuracyButton);
		} else if ("feedback".equals(screenId)) {
			setActiveButton(feedbackButton);
		} else if ("about".equals(screenId)) {
			setActiveButton(aboutButton);
		}
	}

	public void updateForStatsAvailable(boolean hasStats) {
		// Disable result-related routes until at least one test has finished.
		resultsButton.setEnabled(hasStats);
		accuracyButton.setEnabled(hasStats);
		feedbackButton.setEnabled(hasStats);
	}
}
