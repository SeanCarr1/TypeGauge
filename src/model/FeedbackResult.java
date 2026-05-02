package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedbackResult {
	private final String headline;
	private final String mainMessage;
	private final TargetGoals goals;
	private final String primaryFocus;
	private final String secondaryFocus;
	private final List<FeedbackCard> cards;
	private final List<String> debugTags;

	public FeedbackResult(String headline, String mainMessage, TargetGoals goals, String primaryFocus, String secondaryFocus, List<FeedbackCard> cards, List<String> debugTags) {
		this.headline = headline;
		this.mainMessage = mainMessage;
		this.goals = goals;
		this.primaryFocus = primaryFocus;
		this.secondaryFocus = secondaryFocus;
		this.cards = cards == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(cards));
		this.debugTags = debugTags == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(debugTags));
	}

	// Backward compatibility constructor (legacy)
	public FeedbackResult(String title, String message) {
		this(title, message, null, null, null, null, null);
	}

	public String getHeadline() {
		return headline;
	}

	public String getMainMessage() {
		return mainMessage;
	}

	public TargetGoals getGoals() {
		return goals;
	}

	public String getPrimaryFocus() {
		return primaryFocus;
	}

	public String getSecondaryFocus() {
		return secondaryFocus;
	}

	public List<FeedbackCard> getCards() {
		return cards;
	}

	public List<String> getDebugTags() {
		return debugTags;
	}
}
