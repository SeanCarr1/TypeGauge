package model;

public class SessionStats {

	private int wpm;
	private int accuracy;
	private int errors;
	private int timeElapsedSeconds;
	private int totalChars;
	private Difficulty difficulty;
	private String targetText;
	private String userInput;

	public int getWpm() {
		return wpm;
	}

	public void setWpm(int wpm) {
		this.wpm = wpm;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getTimeElapsedSeconds() {
		return timeElapsedSeconds;
	}

	public void setTimeElapsedSeconds(int timeElapsedSeconds) {
		this.timeElapsedSeconds = timeElapsedSeconds;
	}

	public int getTotalChars() {
		return totalChars;
	}

	public void setTotalChars(int totalChars) {
		this.totalChars = totalChars;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public String getTargetText() {
		return targetText;
	}

	public void setTargetText(String targetText) {
		this.targetText = targetText;
	}

	public String getUserInput() {
		return userInput;
	}

	public void setUserInput(String userInput) {
		this.userInput = userInput;
	}
}
