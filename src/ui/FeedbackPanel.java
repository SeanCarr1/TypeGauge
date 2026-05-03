package ui;

import java.awt.*;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.FeedbackContext;
import model.FeedbackResult;
import model.FeedbackService;
import model.SessionStats;

public class FeedbackPanel extends JPanel {

    private static final ResourceBundle STRINGS = ResourceBundle.getBundle(
        "ui.resources.FeedbackPanelStrings"
    );

    private final TypeGaugeFrame frame;
    private final JLabel analysisIntroLabel;
    private final JLabel feedbackMessageLabel;
    private final JLabel nextSpeedValueLabel;
    private final JLabel nextAccuracyValueLabel;
    private final JLabel nextSpeedDeltaLabel;
    private final JLabel nextAccuracyDeltaLabel;

    private JPanel suggestionsWrapper;

    public FeedbackPanel(TypeGaugeFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(100, 300, 40, 300));
        setOpaque(false);

        // Header: Self-Assessment & Feedback / Expert Guidance + Back button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(200, 0, 20, 0));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setOpaque(false);

        JLabel smallTitle = new JLabel(
            STRINGS.getString("feedback.header.smallTitle"),
            SwingConstants.LEFT
        );
        smallTitle.setForeground(new Color(90, 150, 255));
        smallTitle.setFont(smallTitle.getFont().deriveFont(14f));
        headerLeft.add(smallTitle);

        JLabel titleLabel = new JLabel(
            STRINGS.getString("feedback.header.title"),
            SwingConstants.LEFT
        );
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(36f));
        headerLeft.add(titleLabel);

        header.add(headerLeft, BorderLayout.WEST);

        JButton backButton = UiButtons.createPrimaryButton(
            "Return to Dashboard"
        );
        backButton.addActionListener(e -> frame.showFeatureHub());
        JButton returnToMainButton = UiButtons.createPrimaryButton(
            "Return to Main"
        );
        returnToMainButton.addActionListener(e -> frame.showMainUi());
        JButton instructionsButton = UiButtons.createPrimaryButton(
            "Instructions"
        );
        instructionsButton.addActionListener(e ->
            frame.showFeedbackInstructions()
        );

        JPanel headerButtons = new JPanel();
        headerButtons.setOpaque(false);
        headerButtons.add(instructionsButton);
        headerButtons.add(backButton);
        headerButtons.add(returnToMainButton);
        header.add(headerButtons, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center vertical container so cards don't stretch to the bottom
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Row: left analysis card + right goal/summary column
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        // Left: Personalized Analysis card
        JPanel analysisCard = new GlassCardPanel(
            24,
            new Color(25, 25, 25, 160)
        );
        analysisCard.setLayout(new BorderLayout());
        analysisCard.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(255, 255, 255, 40),
                    1,
                    true
                ),
                new EmptyBorder(20, 24, 20, 24)
            )
        );

        JLabel analysisTitle = new JLabel(
            "Personalized Analysis",
            SwingConstants.LEFT
        );
        analysisTitle.setForeground(Color.WHITE);
        analysisTitle.setFont(analysisTitle.getFont().deriveFont(24f));
        analysisCard.add(analysisTitle, BorderLayout.NORTH);

        JPanel analysisCenter = new JPanel(new BorderLayout());
        analysisCenter.setOpaque(false);
        analysisCenter.setBorder(new EmptyBorder(16, 0, 0, 0));

        analysisIntroLabel = new JLabel("", SwingConstants.LEFT);
        analysisIntroLabel.setForeground(new Color(200, 200, 200));
        analysisIntroLabel.setFont(
            analysisIntroLabel.getFont().deriveFont(16f)
        );
        analysisIntroLabel.setBorder(new EmptyBorder(0, 0, 16, 0));
        analysisCenter.add(analysisIntroLabel, BorderLayout.NORTH);

        feedbackMessageLabel = new JLabel("", SwingConstants.LEFT);
        feedbackMessageLabel.setForeground(new Color(90, 150, 255));
        feedbackMessageLabel.setFont(
            feedbackMessageLabel.getFont().deriveFont(Font.ITALIC, 22f)
        );
        feedbackMessageLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        analysisCenter.add(feedbackMessageLabel, BorderLayout.CENTER);

        // Suggestions wrapper is initialized here, but content is set in showFeedback
        suggestionsWrapper = new JPanel(new BorderLayout());
        suggestionsWrapper.setOpaque(false);
        suggestionsWrapper.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel suggestionsTitle = new JLabel(
            STRINGS.getString("feedback.suggestions.title"),
            SwingConstants.LEFT
        );
        suggestionsTitle.setForeground(Color.WHITE);
        suggestionsTitle.setFont(suggestionsTitle.getFont().deriveFont(16f));
        suggestionsWrapper.add(suggestionsTitle, BorderLayout.NORTH);
        analysisCenter.add(suggestionsWrapper, BorderLayout.SOUTH);

        analysisCard.add(analysisCenter, BorderLayout.CENTER);

        center.add(analysisCard, BorderLayout.CENTER);

        // Right column: Goal Setting and Summary of Strengths
        JPanel rightColumn = new JPanel(new BorderLayout());
        rightColumn.setOpaque(false);
        rightColumn.setBorder(new EmptyBorder(0, 24, 0, 0));
        rightColumn.setPreferredSize(new Dimension(380, 0));

        // Goal Setting card
        JPanel goalCard = new GlassCardPanel(24, new Color(25, 25, 25, 160));
        goalCard.setLayout(new BorderLayout());
        goalCard.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(255, 255, 255, 40),
                    1,
                    true
                ),
                new EmptyBorder(16, 16, 16, 16)
            )
        );

        JLabel goalTitle = new JLabel("Goal Setting", SwingConstants.LEFT);
        goalTitle.setForeground(Color.WHITE);
        goalTitle.setFont(goalTitle.getFont().deriveFont(20f));
        goalCard.add(goalTitle, BorderLayout.NORTH);

        JPanel goalBody = new JPanel(new GridLayout(2, 2, 8, 8));
        goalBody.setOpaque(false);
        goalBody.setBorder(new EmptyBorder(12, 0, 12, 0));

        JLabel nextSpeedLabel = new JLabel("Next Target Speed");
        nextSpeedLabel.setForeground(new Color(180, 180, 180));
        nextSpeedLabel.setFont(nextSpeedLabel.getFont().deriveFont(12f));
        nextSpeedValueLabel = new JLabel("0", SwingConstants.RIGHT);
        nextSpeedValueLabel.setForeground(Color.WHITE);
        nextSpeedValueLabel.setFont(
            nextSpeedValueLabel.getFont().deriveFont(30f)
        );
        nextSpeedDeltaLabel = new JLabel("+0 WPM", SwingConstants.LEFT);
        nextSpeedDeltaLabel.setForeground(new Color(0, 220, 140));
        nextSpeedDeltaLabel.setFont(
            nextSpeedDeltaLabel.getFont().deriveFont(14f)
        );
        JPanel speedValuePanel = new JPanel(new BorderLayout());
        speedValuePanel.setOpaque(false);
        speedValuePanel.add(nextSpeedValueLabel, BorderLayout.WEST);
        speedValuePanel.add(nextSpeedDeltaLabel, BorderLayout.EAST);

        JLabel nextAccuracyLabel = new JLabel("Accuracy Goal");
        nextAccuracyLabel.setForeground(new Color(180, 180, 180));
        nextAccuracyLabel.setFont(nextAccuracyLabel.getFont().deriveFont(12f));
        nextAccuracyValueLabel = new JLabel("0%", SwingConstants.RIGHT);
        nextAccuracyValueLabel.setForeground(new Color(0, 220, 140));
        nextAccuracyValueLabel.setFont(
            nextAccuracyValueLabel.getFont().deriveFont(30f)
        );
        nextAccuracyDeltaLabel = new JLabel("+0%", SwingConstants.LEFT);
        nextAccuracyDeltaLabel.setForeground(new Color(0, 220, 140));
        nextAccuracyDeltaLabel.setFont(
            nextAccuracyDeltaLabel.getFont().deriveFont(14f)
        );
        JPanel accuracyValuePanel = new JPanel(new BorderLayout());
        accuracyValuePanel.setOpaque(false);
        accuracyValuePanel.add(nextAccuracyValueLabel, BorderLayout.WEST);
        accuracyValuePanel.add(nextAccuracyDeltaLabel, BorderLayout.EAST);

        goalBody.add(nextSpeedLabel);
        goalBody.add(speedValuePanel);
        goalBody.add(nextAccuracyLabel);
        goalBody.add(accuracyValuePanel);

        goalCard.add(goalBody, BorderLayout.CENTER);

        JPanel practiceWrapper = new JPanel(new BorderLayout());
        practiceWrapper.setOpaque(false);
        practiceWrapper.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton practiceButton = UiButtons.createPrimaryButton(
            "Start Practice"
        );
        practiceButton.addActionListener(e -> frame.retry());
        practiceWrapper.add(practiceButton, BorderLayout.CENTER);
        goalCard.add(practiceWrapper, BorderLayout.SOUTH);

        rightColumn.add(goalCard, BorderLayout.NORTH);

        center.add(rightColumn, BorderLayout.EAST);

        // Stack the row and push extra space below it
        content.add(Box.createVerticalStrut(8));
        center.setAlignmentX(LEFT_ALIGNMENT);
        content.add(center);
        content.add(Box.createVerticalGlue());

        add(content, BorderLayout.CENTER);
    }

    private JPanel createSuggestionCard(
        Color accentColor,
        String iconText,
        String title,
        String body
    ) {
        // Small reusable card used in the constructive suggestions section.
        JPanel card = new GlassCardPanel(18, new Color(20, 20, 20, 200));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel iconWrapper = new JPanel(new BorderLayout());
        iconWrapper.setOpaque(false);
        JLabel iconLabel = new JLabel(iconText, SwingConstants.CENTER);
        iconLabel.setForeground(accentColor);
        iconLabel.setFont(iconLabel.getFont().deriveFont(18f));
        iconWrapper.setBorder(new EmptyBorder(0, 0, 0, 12));
        iconWrapper.add(iconLabel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(textPanel.getFont().deriveFont(Font.BOLD, 14f));
        JLabel bodyLabel = new JLabel("<html>" + body + "</html>");
        bodyLabel.setForeground(new Color(200, 200, 200));
        bodyLabel.setFont(bodyLabel.getFont().deriveFont(12f));
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(bodyLabel, BorderLayout.CENTER);

        card.add(iconWrapper, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // Suggestion card configuration structure
    private static class SuggestionCardConfig {

        final Color color;
        final String icon;
        final String title;
        final String message;
        final java.util.function.Predicate<FeedbackContext> rule;

        SuggestionCardConfig(
            Color color,
            String icon,
            String title,
            String message,
            java.util.function.Predicate<FeedbackContext> rule
        ) {
            this.color = color;
            this.icon = icon;
            this.title = title;
            this.message = message;
            this.rule = rule;
        }
    }

    // List of suggestion card configs
    private static final java.util.List<SuggestionCardConfig> SUGGESTION_CARDS =
        java.util.Arrays.asList(
            new SuggestionCardConfig(
                new Color(0, 140, 100, 200),
                "⚡",
                "Speed Strategy",
                "Try to look ahead at the next word while finishing the current one to maintain momentum.",
                ctx ->
                    ctx.speedTier == FeedbackContext.SpeedTier.VERY_SLOW ||
                    ctx.speedTier == FeedbackContext.SpeedTier.SLOW ||
                    ctx.speedTier == FeedbackContext.SpeedTier.MEDIUM
            ),
            new SuggestionCardConfig(
                new Color(180, 60, 60, 200),
                "●",
                "Error Reduction",
                "Slow down by 5–10% on complex words to ensure 100% accuracy before speeding up again.",
                ctx ->
                    ctx.accuracyTier == FeedbackContext.AccuracyTier.LOW ||
                    ctx.accuracyTier == FeedbackContext.AccuracyTier.OK ||
                    ctx.controlTier == FeedbackContext.ControlTier.NEEDS_WORK
            ),
            new SuggestionCardConfig(
                new Color(40, 90, 180, 200),
                "⟳",
                "Progress Encouragement",
                "Your speed is consistent. Focus on longer passages to build endurance.",
                ctx ->
                    (ctx.speedTier == FeedbackContext.SpeedTier.FAST ||
                        ctx.speedTier == FeedbackContext.SpeedTier.VERY_FAST) &&
                    (ctx.accuracyTier == FeedbackContext.AccuracyTier.GOOD ||
                        ctx.accuracyTier ==
                        FeedbackContext.AccuracyTier.GREAT ||
                        ctx.accuracyTier == FeedbackContext.AccuracyTier.ELITE)
            ),
            new SuggestionCardConfig(
                new Color(255, 215, 0, 200),
                "★",
                "Precision Focus",
                "Your accuracy is exceptional. Now, try to push your speed limits in short, controlled bursts.",
                ctx ->
                    (ctx.speedTier == FeedbackContext.SpeedTier.VERY_SLOW ||
                        ctx.speedTier == FeedbackContext.SpeedTier.SLOW) &&
                    ctx.accuracyTier == FeedbackContext.AccuracyTier.ELITE
            ),
            new SuggestionCardConfig(
                new Color(150, 100, 255, 200),
                "≋",
                "Flow Optimization",
                "Your rhythm is stable. Minimize pauses between words to achieve a more fluid typing experience.",
                ctx ->
                    ctx.controlTier == FeedbackContext.ControlTier.STRONG &&
                    ctx.speedTier != FeedbackContext.SpeedTier.VERY_FAST
            ),
            new SuggestionCardConfig(
                new Color(0, 180, 180, 200),
                "⚖",
                "Stability Training",
                "You have a solid mid-range pace. Focus on refining your technique to eliminate the final few errors.",
                ctx ->
                    ctx.speedTier == FeedbackContext.SpeedTier.MEDIUM &&
                    (ctx.accuracyTier == FeedbackContext.AccuracyTier.GOOD ||
                        ctx.accuracyTier == FeedbackContext.AccuracyTier.GREAT)
            )
            // Dominant error category handled separately below
        );

    private JPanel buildSuggestionsPanel(SessionStats stats) {
        FeedbackContext context = FeedbackContext.from(stats);
        JPanel suggestionsPanel = new JPanel(new GridLayout(0, 1, 0, 12));
        suggestionsPanel.setOpaque(false);
        suggestionsPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Add cards from config
        for (SuggestionCardConfig config : SUGGESTION_CARDS) {
            if (config.rule.test(context)) {
                suggestionsPanel.add(
                    createSuggestionCard(
                        config.color,
                        config.icon,
                        config.title,
                        config.message
                    )
                );
            }
        }

        // Dominant Error Category (still dynamic)
        if (context.dominantCategoryKey != null) {
            String focus = null;
            switch (context.dominantCategoryKey) {
                case "punctuation":
                    focus =
                        "Pay extra attention to punctuation—most of your errors are here.";
                    break;
                case "spacing":
                    focus =
                        "Watch your spacing—this is where most of your errors occur.";
                    break;
                case "numbers":
                    focus =
                        "Numbers seem tricky—practice typing them accurately.";
                    break;
            }
            if (focus != null) {
                suggestionsPanel.add(
                    createSuggestionCard(
                        new Color(200, 160, 40, 200),
                        "✎",
                        "Focus Area",
                        focus
                    )
                );
            }
        }
        return suggestionsPanel;
    }

    public void showFeedback(SessionStats stats, FeedbackResult feedback) {
        if (stats == null || feedback == null) {
            return;
        }

        applyFeedbackToUi(stats, feedback);
    }

    private void applyFeedbackToUi(SessionStats stats, FeedbackResult feedback) {
        // Update suggestions panel dynamically
        JPanel suggestionsPanel = buildSuggestionsPanel(stats);
        suggestionsWrapper.removeAll();
        JLabel suggestionsTitle = new JLabel(
            STRINGS.getString("feedback.suggestions.title"),
            SwingConstants.LEFT
        );
        suggestionsTitle.setForeground(Color.WHITE);
        suggestionsTitle.setFont(suggestionsTitle.getFont().deriveFont(16f));
        suggestionsWrapper.add(suggestionsTitle, BorderLayout.NORTH);
        suggestionsWrapper.add(suggestionsPanel, BorderLayout.CENTER);
        suggestionsWrapper.revalidate();
        suggestionsWrapper.repaint();

        String difficultyText =
            stats.getDifficulty() != null
                ? stats.getDifficulty().name()
                : "your";
        analysisIntroLabel.setText(
            "<html>Based on your session at " +
                difficultyText +
                " difficulty, our Rule-Based Analysis Engine has generated the following assessment:</html>"
        );

        feedbackMessageLabel.setText(
            "<html>\"" + feedback.getMainMessage() + "\"</html>"
        );

        // Configurable increments for next goals
        final int SPEED_INCREMENT = 5;
        final int ACCURACY_INCREMENT = 2;
        int nextWpm = stats.getWpm() + SPEED_INCREMENT;
        int nextAccuracy = Math.min(
            100,
            stats.getAccuracy() + ACCURACY_INCREMENT
        );
        nextSpeedValueLabel.setText(String.valueOf(nextWpm));
        nextAccuracyValueLabel.setText(nextAccuracy + "%");
        nextSpeedDeltaLabel.setText("+" + (nextWpm - stats.getWpm()) + " WPM");
        nextAccuracyDeltaLabel.setText(
            "+" + (nextAccuracy - stats.getAccuracy()) + "%"
        );
    }
}
