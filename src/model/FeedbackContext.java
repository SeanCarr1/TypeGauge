package model;

/**
 * FeedbackContext computes derived metrics and tiers from SessionStats for feedback rules.
 * No Swing/UI dependencies. Deterministic calculations only.
 */
public class FeedbackContext {
    public enum SpeedTier { VERY_SLOW, SLOW, MEDIUM, FAST, VERY_FAST }
    public enum AccuracyTier { LOW, OK, GOOD, GREAT, ELITE }
    public enum ControlTier { NEEDS_WORK, OK, STRONG }

    public final int wpm;
    public final int accuracy;
    public final int errors;
    public final int timeSeconds;
    public final int totalChars;
    public final String difficulty;
    public final String performanceGrade;

    public final double errorsPerMinute;
    public final double errorRate;
    public final String dominantCategoryKey;
    public final SpeedTier speedTier;
    public final AccuracyTier accuracyTier;
    public final ControlTier controlTier;

    private FeedbackContext(
        int wpm, int accuracy, int errors, int timeSeconds, int totalChars, String difficulty, String performanceGrade,
        double errorsPerMinute, double errorRate, String dominantCategoryKey,
        SpeedTier speedTier, AccuracyTier accuracyTier, ControlTier controlTier
    ) {
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.errors = errors;
        this.timeSeconds = timeSeconds;
        this.totalChars = totalChars;
        this.difficulty = difficulty;
        this.performanceGrade = performanceGrade;
        this.errorsPerMinute = errorsPerMinute;
        this.errorRate = errorRate;
        this.dominantCategoryKey = dominantCategoryKey;
        this.speedTier = speedTier;
        this.accuracyTier = accuracyTier;
        this.controlTier = controlTier;
    }

    public static FeedbackContext from(SessionStats stats) {
        if (stats == null) throw new IllegalArgumentException("stats cannot be null");
        int wpm = stats.getWpm();
        int accuracy = stats.getAccuracy();
        int errors = stats.getErrors();
        int timeSeconds = Math.max(1, stats.getTimeElapsedSeconds());
        int totalChars = Math.max(1, stats.getTotalChars());
        String difficulty = stats.getDifficulty() != null ? stats.getDifficulty().name() : null;
        String performanceGrade = stats.getPerformanceGrade();

        double errorsPerMinute = errors / (timeSeconds / 60.0);
        double errorRate = errors / (double) totalChars;
        String dominantCategoryKey = getDominantCategoryKey(stats);

        SpeedTier speedTier = classifySpeed(wpm);
        AccuracyTier accuracyTier = classifyAccuracy(accuracy);
        ControlTier controlTier = classifyControl(errorsPerMinute, errorRate);

        return new FeedbackContext(
            wpm, accuracy, errors, timeSeconds, totalChars, difficulty, performanceGrade,
            errorsPerMinute, errorRate, dominantCategoryKey,
            speedTier, accuracyTier, controlTier
        );
    }

    private static SpeedTier classifySpeed(int wpm) {
        if (wpm < 15) return SpeedTier.VERY_SLOW;
        if (wpm < 30) return SpeedTier.SLOW;
        if (wpm < 45) return SpeedTier.MEDIUM;
        if (wpm < 70) return SpeedTier.FAST;
        return SpeedTier.VERY_FAST;
    }

    private static AccuracyTier classifyAccuracy(int accuracy) {
        if (accuracy < 80) return AccuracyTier.LOW;
        if (accuracy < 90) return AccuracyTier.OK;
        if (accuracy < 95) return AccuracyTier.GOOD;
        if (accuracy < 98) return AccuracyTier.GREAT;
        return AccuracyTier.ELITE;
    }

    private static ControlTier classifyControl(double errorsPerMinute, double errorRate) {
        if (errorsPerMinute > 6 || errorRate > 0.10) return ControlTier.NEEDS_WORK;
        if (errorsPerMinute > 2.5 || errorRate > 0.04) return ControlTier.OK;
        return ControlTier.STRONG;
    }

    private static String getDominantCategoryKey(SessionStats stats) {
        if (stats.getErrors() == 0) {
            return null;
        }
        int letter = stats.getLetterErrors();
        int number = stats.getNumberErrors();
        int punctuation = stats.getPunctuationErrors();
        int spacing = stats.getSpacingErrors();
        if (spacing >= letter && spacing >= number && spacing >= punctuation) return "spacing";
        if (punctuation >= letter && punctuation >= number) return "punctuation";
        if (number >= letter) return "numbers";
        return "letters";
    }
}
