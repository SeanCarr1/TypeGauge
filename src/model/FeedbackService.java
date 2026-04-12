package model;

public final class FeedbackService {

	private FeedbackService() {
	}

	public static FeedbackResult getFeedback(int wpm, int accuracy) {
		if (wpm > 80 && accuracy > 98) {
			return new FeedbackResult(
				"Master Typist",
				"You type with exceptional speed and near-perfect accuracy. Keep maintaining this level!");
		}
		if (wpm > 60 && accuracy > 95) {
			return new FeedbackResult(
				"Professional Speed",
				"Your typing speed and accuracy are at a professional level. A bit more practice can push you even further.");
		}
		if (wpm > 40 && accuracy > 90) {
			return new FeedbackResult(
				"Steady Progress",
				"You have a solid foundation. Keep practicing to steadily increase both speed and accuracy.");
		}
		return new FeedbackResult(
			"Building Foundation",
			"Focus on staying relaxed and hitting each key accurately. Speed will naturally increase over time.");
	}
}
