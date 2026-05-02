package model;

public final class FeedbackService {

    private FeedbackService() {}

    // Data-driven feedback rule structure
    private static class FeedbackRule {

        final int minWpm;
        final int minAccuracy;
        final String headline;
        final String message;

        FeedbackRule(
            int minWpm,
            int minAccuracy,
            String headline,
            String message
        ) {
            this.minWpm = minWpm;
            this.minAccuracy = minAccuracy;
            this.headline = headline;
            this.message = message;
        }

        boolean matches(int wpm, int accuracy) {
            return wpm >= minWpm && accuracy >= minAccuracy;
        }
    }

    // Ordered from highest to lowest priority
    private static final FeedbackRule[] RULES = new FeedbackRule[] {
        new FeedbackRule(
            81,
            100,
            "Flawless Master",
            "You type with exceptional speed and 100% accuracy. Your performance is absolutely flawless!"
        ),
        new FeedbackRule(
            81,
            99,
            "Master Typist",
            "You type with exceptional speed and near-perfect accuracy. Keep maintaining this level!"
        ),
        new FeedbackRule(
            61,
            100,
            "Elite Professional",
            "Your typing speed is professional and your accuracy is perfect. Excellent work!"
        ),
        new FeedbackRule(
            61,
            96,
            "Professional Speed",
            "Your typing speed and accuracy are at a professional level. A bit more practice can push you even further."
        ),
        new FeedbackRule(
            41,
            100,
            "Precise Progress",
            "A solid, perfect performance! With this foundation of accuracy, you're ready to increase your speed."
        ),
        new FeedbackRule(
            41,
            91,
            "Steady Progress",
            "You have a solid foundation. Keep practicing to steadily increase both speed and accuracy."
        ),
        new FeedbackRule(
            0,
            100,
            "Accurate Foundation",
            "Perfect accuracy! Keep this up and your speed will naturally increase over time."
        ),
        new FeedbackRule(
            0,
            0,
            "Building Foundation",
            "Focus on staying relaxed and hitting each key accurately. Speed will naturally increase over time."
        ),
    };

    public static FeedbackResult getFeedback(int wpm, int accuracy) {
        for (FeedbackRule rule : RULES) {
            if (rule.matches(wpm, accuracy)) {
                return new FeedbackResult(rule.headline, rule.message);
            }
        }
        // Fallback (should never hit)
        return new FeedbackResult("Feedback", "No feedback rule matched.");
    }
}
