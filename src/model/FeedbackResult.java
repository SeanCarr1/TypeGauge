package model;

public class FeedbackResult {

	private final String title;
	private final String message;

	public FeedbackResult(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}
}
