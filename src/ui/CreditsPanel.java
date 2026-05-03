package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
 * Credits screen listing project contributors and course context.
 *
 * <p>Uses the same card treatment as the instructions screen and exposes a
 * single action to return to the main menu.
 */
public class CreditsPanel extends JPanel {

	private final TypeGaugeFrame frame;
	private final JScrollPane scrollPane;

	public CreditsPanel(TypeGaugeFrame frame) {
		this.frame = frame;

		setOpaque(false);
		setLayout(new BorderLayout());
		// Widened the layout by reducing side margins from 400 to 250
		setBorder(new EmptyBorder(40, 250, 40, 250));

		// Refactored to use GlassCardPanel for consistency with the rest of the UI
		JPanel card = new GlassCardPanel(32, new Color(25, 25, 25, 220));
		card.setLayout(new BorderLayout());
		card.setBorder(BorderFactory.createCompoundBorder(
			new LineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(24, 32, 24, 32)));

		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		JLabel titleLabel = new JLabel("Credits & Lead Developer");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 32f));
		titleLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerPanel.add(titleLabel);

		headerPanel.add(Box.createVerticalStrut(8));

		JLabel subtitleLabel = new JLabel("Core engineering and architectural design.");
		subtitleLabel.setForeground(Color.LIGHT_GRAY);
		subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(16f));
		subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
		headerPanel.add(subtitleLabel);

		// Main content area to hold all the structured CV sections
		JPanel cvContentPanel = new JPanel();
		cvContentPanel.setOpaque(false);
		cvContentPanel.setLayout(new BoxLayout(cvContentPanel, BoxLayout.Y_AXIS));
		cvContentPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Padding below header

		// --- Profile Header (Avatar + Identity) ---
		JPanel profileHeader = new JPanel(new BorderLayout());
		profileHeader.setOpaque(false);
		profileHeader.setAlignmentX(LEFT_ALIGNMENT);

		// Load and scale 2x2.jpg avatar using classpath resource
		URL avatarUrl = getClass().getResource("/images/2x2.jpg");
		if (avatarUrl != null) {
			ImageIcon rawIcon = new ImageIcon(avatarUrl);
			Image scaled = rawIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
			JLabel avatarLabel = new JLabel(new ImageIcon(scaled));
			avatarLabel.setBorder(new EmptyBorder(0, 0, 0, 24));
			profileHeader.add(avatarLabel, BorderLayout.WEST);
		}

		JPanel identityPanel = new JPanel();
		identityPanel.setOpaque(false);
		identityPanel.setLayout(new BoxLayout(identityPanel, BoxLayout.Y_AXIS));

		JLabel nameLabel = new JLabel("Sean Carr Geoffrey M. Tenedero");
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 28f));
		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		identityPanel.add(nameLabel);
		identityPanel.add(Box.createVerticalStrut(8));

		JLabel contactLabel = new JLabel("<html>(+63) 999-339-5659<br>seancarrgeoffrey.tenedero@gmail.com<br>github.com/SeanCarr1</html>");
		contactLabel.setForeground(new Color(180, 180, 180));
		contactLabel.setFont(contactLabel.getFont().deriveFont(16f));
		contactLabel.setAlignmentX(LEFT_ALIGNMENT);
		identityPanel.add(contactLabel);

		profileHeader.add(identityPanel, BorderLayout.CENTER);
		cvContentPanel.add(profileHeader);
		cvContentPanel.add(Box.createVerticalStrut(30));

		// --- Relevant Skills Card ---
		cvContentPanel.add(createSkillsCard());
		cvContentPanel.add(Box.createVerticalStrut(20));

		// --- Experience Card ---
		cvContentPanel.add(createExperienceCard());
		cvContentPanel.add(Box.createVerticalStrut(20));

		// --- Projects Card ---
		cvContentPanel.add(createProjectsCard());
		cvContentPanel.add(Box.createVerticalStrut(20));

		// --- Education Card ---
		cvContentPanel.add(createEducationCard());
		cvContentPanel.add(Box.createVerticalStrut(20));

		// --- Other Technologies Card ---
		cvContentPanel.add(createOtherTechnologiesCard());
		cvContentPanel.add(Box.createVerticalStrut(20));

		scrollPane = new JScrollPane(cvContentPanel);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null); // Remove default scroll pane border
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
		scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		// Keep scroll pane from expanding too far
		// scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500)); // Removed as cvContentPanel defines height

		JButton backButton = UiButtons.createPrimaryButton("Back to Main Menu");
		backButton.addActionListener(e -> frame.showMainUi());
		backButton.setAlignmentX(LEFT_ALIGNMENT);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
		bottomPanel.add(backButton);

		JPanel mainContentWrapper = new JPanel(new BorderLayout());
		mainContentWrapper.setOpaque(false);
		mainContentWrapper.add(headerPanel, BorderLayout.NORTH);
		mainContentWrapper.add(scrollPane, BorderLayout.CENTER); // Scrollable CV content
		mainContentWrapper.add(bottomPanel, BorderLayout.SOUTH);

		card.add(mainContentWrapper, BorderLayout.CENTER);
		add(card, BorderLayout.CENTER);
	}

	public void resetScroll() {
		// Use invokeLater to ensure layout is complete before resetting scroll position
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
	}

	private JPanel createSkillsCard() {
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 180));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(16, 20, 16, 20)));

		JLabel title = new JLabel("Relevant Skills");
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
		title.setAlignmentX(LEFT_ALIGNMENT);
		card.add(title);
		card.add(Box.createVerticalStrut(10));

		JLabel proficient = new JLabel("<html><b>Proficient:</b> Python, Django, SQL, Git</html>");
		proficient.setForeground(new Color(200, 200, 200));
		proficient.setFont(proficient.getFont().deriveFont(15f));
		proficient.setAlignmentX(LEFT_ALIGNMENT);
		card.add(proficient);
		card.add(Box.createVerticalStrut(5));

		JLabel familiar = new JLabel("<html><b>Familiar:</b> TypeScript, AWS EC2, FastAPI, Linux</html>");
		familiar.setForeground(new Color(200, 200, 200));
		familiar.setFont(familiar.getFont().deriveFont(15f));
		familiar.setAlignmentX(LEFT_ALIGNMENT);
		card.add(familiar);

		return card;
	}

	private JPanel createExperienceCard() {
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 180));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(16, 20, 16, 20)));

		JLabel title = new JLabel("Experience");
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
		title.setAlignmentX(LEFT_ALIGNMENT);
		card.add(title);
		card.add(Box.createVerticalStrut(15));

		// Freelance Web Developer/Designer
		card.add(createExperienceEntry(
			"Freelance Web Developer/Designer",
			"July 2025 – March 2026",
			"- Designed and developed custom WordPress websites for clients in the finance and real estate sectors, resulting in a 25% increase in lead generation.\n" +
			"- Resolved MySQL database connection errors in cPanel, ensuring reliable site performance.\n" +
			"- Provided ongoing support and training for clients, maintaining a 95% website uptime."
		));

		return card;
	}

	private JPanel createExperienceEntry(String role, String duration, String description) {
		JPanel entryPanel = new JPanel();
		entryPanel.setOpaque(false);
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
		entryPanel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel roleLabel = new JLabel("<html><b>" + role + "</b> — <i>" + duration + "</i></html>");
		roleLabel.setForeground(new Color(0, 220, 140)); // Highlight role
		roleLabel.setFont(roleLabel.getFont().deriveFont(Font.BOLD, 16f));
		roleLabel.setAlignmentX(LEFT_ALIGNMENT);
		entryPanel.add(roleLabel);
		entryPanel.add(Box.createVerticalStrut(5));

		JTextArea descArea = new JTextArea(description);
		descArea.setEditable(false);
		descArea.setOpaque(false);
		descArea.setLineWrap(true);
		descArea.setWrapStyleWord(true);
		descArea.setForeground(new Color(200, 200, 200));
		descArea.setFont(descArea.getFont().deriveFont(14f));
		descArea.setAlignmentX(LEFT_ALIGNMENT);
		descArea.setCaretPosition(0);
		entryPanel.add(descArea);
		entryPanel.add(Box.createVerticalStrut(15)); // Space between entries

		return entryPanel;
	}

	private JPanel createProjectsCard() {
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 180));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(16, 20, 16, 20)));

		JLabel title = new JLabel("Projects");
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
		title.setAlignmentX(LEFT_ALIGNMENT);
		card.add(title);
		card.add(Box.createVerticalStrut(15));

		card.add(createProjectEntry(
			"Django Web Scraper",
			"- Built a client-server model Django web app with Playwright-based scraping and Django REST Framework API for automated data extraction, categorization, and management.\n" +
			"- Integrated public video metadata APIs and used SQLite for backend storage."
		));
		card.add(createProjectEntry(
			"Moviebit (Movie Investment Platform)",
			"- Collaborating with 3 developers on an ongoing project, leveraging Git for version control and Scrum methodology for agile teamwork.\n" +
			"- Building a full-stack web app with Node.js, TypeScript, Prisma, React, Vite, and PostgreSQL; implementing secure authentication, admin/investor dashboards, test-driven development and Dockerized deployment."
		));
		card.add(createProjectEntry(
			"Carr Tech Crunch (Interactive Coding Tutorial Platform)",
			"- Built a full-stack web platform using PHP, MySQL, and JavaScript for coding tutorials and exercises with user authentication, progress tracking, and interactive feedback."
		));
		card.add(createProjectEntry(
			"FastAPI CI/CD Pipeline Project",
			"- Built and tested a RESTful API using FastAPI, Python, and pytest; containerized the application with Docker for consistent deployment.\n" +
			"- Automated CI/CD workflows with GitHub Actions, integrating code checkout, dependency installation, testing, and Docker image builds."
		));
		card.add(createProjectEntry(
			"Geospatial Fire Risk Assessment for San Francisco",
			"- Built a geospatial ML pipeline in Python (Pandas, GeoPandas, scikit-learn) to assess and visualize San Francisco fire risk zones using multi-source incident, district, and facility data.\n" +
			"- Engineered spatial features and trained a Random Forest classifier; delivered interactive Folium maps for actionable emergency response and urban planning insights."
		));
		card.add(createProjectEntry(
			"Unemployed (Job Board Platform)",
			"- Built a full-stack job board app with Django (backend) and Vue.js + TypeScript/Pinia (frontend), implementing secure JWT auth and tested REST APIs (DRF, Vitest).\n" +
			"- Deployed and managed the app using Docker Compose for consistent development and production environments."
		));

		return card;
	}

	private JPanel createProjectEntry(String name, String description) {
		JPanel entryPanel = new JPanel();
		entryPanel.setOpaque(false);
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
		entryPanel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel nameLabel = new JLabel("<html><b>" + name + "</b></html>");
		nameLabel.setForeground(new Color(90, 150, 255)); // Highlight project name
		nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 16f));
		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		entryPanel.add(nameLabel);
		entryPanel.add(Box.createVerticalStrut(5));

		JTextArea descArea = new JTextArea(description);
		descArea.setEditable(false);
		descArea.setOpaque(false);
		descArea.setLineWrap(true);
		descArea.setWrapStyleWord(true);
		descArea.setForeground(new Color(200, 200, 200));
		descArea.setFont(descArea.getFont().deriveFont(14f));
		descArea.setAlignmentX(LEFT_ALIGNMENT);
		descArea.setCaretPosition(0);
		entryPanel.add(descArea);
		entryPanel.add(Box.createVerticalStrut(15)); // Space between entries

		return entryPanel;
	}

	private JPanel createEducationCard() {
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 180));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(16, 20, 16, 20)));

		JLabel title = new JLabel("Education");
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
		title.setAlignmentX(LEFT_ALIGNMENT);
		card.add(title);
		card.add(Box.createVerticalStrut(15));

		JLabel universityLabel = new JLabel("<html><b>Southern Luzon State University</b></html>");
		universityLabel.setForeground(new Color(0, 220, 140));
		universityLabel.setFont(universityLabel.getFont().deriveFont(Font.BOLD, 16f));
		universityLabel.setAlignmentX(LEFT_ALIGNMENT);
		card.add(universityLabel);
		card.add(Box.createVerticalStrut(5));

		JLabel degreeLabel = new JLabel("Bachelor of Science in Computer Engineering");
		degreeLabel.setForeground(new Color(200, 200, 200));
		degreeLabel.setFont(degreeLabel.getFont().deriveFont(14f));
		degreeLabel.setAlignmentX(LEFT_ALIGNMENT);
		card.add(degreeLabel);

		return card;
	}

	private JPanel createOtherTechnologiesCard() {
		JPanel card = new GlassCardPanel(24, new Color(25, 25, 25, 180));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80, 160), 1, true),
			new EmptyBorder(16, 20, 16, 20)));

		JLabel title = new JLabel("Other Technologies");
		title.setForeground(Color.WHITE);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
		title.setAlignmentX(LEFT_ALIGNMENT);
		card.add(title);
		card.add(Box.createVerticalStrut(15));

		// Mimic table layout with GridLayout
		JPanel tablePanel = new JPanel(new GridLayout(4, 2, 10, 5)); // 4 rows, 2 columns, hgap, vgap
		tablePanel.setOpaque(false);
		tablePanel.setAlignmentX(LEFT_ALIGNMENT);

		addTableRow(tablePanel, "Database & ORM", "MySQL, SQLite, PostgreSQL, Django ORM, Prisma");
		addTableRow(tablePanel, "Tools", "Docker, Jira, JWT, REST APIs, Tailwind, Playwright, GH Actions");
		addTableRow(tablePanel, "Data", "Pandas, Matplotlib, NumPy, Apache (Kafka, Spark, Airflow), dbt, Snowflake");
		addTableRow(tablePanel, "Cloud", "AWS (EC2), GCP (Cloud Compute Engine)");

		card.add(tablePanel);

		return card;
	}

	private void addTableRow(JPanel parent, String category, String technologies) {
		JLabel categoryLabel = new JLabel("<html><b>" + category + "</b></html>");
		categoryLabel.setForeground(new Color(0, 220, 140));
		categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, 15f));
		categoryLabel.setHorizontalAlignment(JLabel.LEFT);
		parent.add(categoryLabel);

		JTextArea techArea = new JTextArea(technologies);
		techArea.setEditable(false);
		techArea.setOpaque(false);
		techArea.setLineWrap(true);
		techArea.setWrapStyleWord(true);
		techArea.setForeground(new Color(200, 200, 200));
		techArea.setFont(techArea.getFont().deriveFont(14f));
		parent.add(techArea);
	}

	/**
	 * Custom ScrollBar UI for a sleek, modern look.
	 */
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
			jbutton.setMinimumSize(new Dimension(0, 0));
			jbutton.setMaximumSize(new Dimension(0, 0));
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
			// Use a very dark, semi-transparent color for the track to match the theme
			g2.setColor(new Color(5, 5, 5, 120));
			g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
			g2.dispose();
		}
	}
}
