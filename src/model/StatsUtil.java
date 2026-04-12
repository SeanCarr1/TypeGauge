package model;

public final class StatsUtil {

	private StatsUtil() {
	}

	public static int calculateWpm(int charactersTyped, double elapsedSeconds) {
		if (elapsedSeconds <= 0) {
			return 0;
		}
		double words = charactersTyped / 5.0;
		double minutes = elapsedSeconds / 60.0;
		return (int) Math.round(words / minutes);
	}

	public static int calculateAccuracy(int correctChars, int totalChars) {
		if (totalChars <= 0) {
			return 0;
		}
		double percent = (correctChars * 100.0) / totalChars;
		return (int) Math.max(0, Math.round(percent));
	}

	public static int countErrors(String targetText, String finalInput) {
		if (targetText == null || finalInput == null) {
			return 0;
		}
		int length = Math.min(targetText.length(), finalInput.length());
		int errors = 0;
		for (int i = 0; i < length; i++) {
			if (finalInput.charAt(i) != targetText.charAt(i)) {
				errors++;
			}
		}
		// Count remaining characters in target as errors if input is shorter
		if (finalInput.length() < targetText.length()) {
			errors += targetText.length() - finalInput.length();
		}
		return errors;
	}
}
