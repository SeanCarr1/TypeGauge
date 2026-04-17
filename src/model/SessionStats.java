package model;

import java.util.BitSet;

public class SessionStats {

	private int wpm;
	private int accuracy;
	private int errors;
	private int timeElapsedSeconds;
	private int totalChars;
	private int letterErrors;
	private int numberErrors;
	private int punctuationErrors;
	private int spacingErrors;
	private BitSet mistakenCharacterIndexes = new BitSet();
	private Difficulty difficulty;
	private String targetText;
	private String userInput;
	private String performanceGrade;

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

	public int getLetterErrors() {
		return letterErrors;
	}

	public void setLetterErrors(int letterErrors) {
		this.letterErrors = letterErrors;
	}

	public int getNumberErrors() {
		return numberErrors;
	}

	public void setNumberErrors(int numberErrors) {
		this.numberErrors = numberErrors;
	}

	public int getPunctuationErrors() {
		return punctuationErrors;
	}

	public void setPunctuationErrors(int punctuationErrors) {
		this.punctuationErrors = punctuationErrors;
	}

	public int getSpacingErrors() {
		return spacingErrors;
	}

	public void setSpacingErrors(int spacingErrors) {
		this.spacingErrors = spacingErrors;
	}

	public BitSet getMistakenCharacterIndexes() {
		return (BitSet) mistakenCharacterIndexes.clone();
	}

	public void setMistakenCharacterIndexes(BitSet mistakenCharacterIndexes) {
		this.mistakenCharacterIndexes = mistakenCharacterIndexes != null ? (BitSet) mistakenCharacterIndexes.clone() : new BitSet();
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

	public String getPerformanceGrade() {
		return performanceGrade;
	}

	public void setPerformanceGrade(String performanceGrade) {
		this.performanceGrade = performanceGrade;
	}
}
