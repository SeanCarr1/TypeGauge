package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Difficulty {
	BEGINNER,
	INTERMEDIATE,
	ADVANCED;

	private static final Random RANDOM = new Random();

	public List<String> getSampleTexts() {
		switch (this) {
		case BEGINNER:
			// TODO: Replace placeholder texts with actual samples from React app (Appendix A).
			return Arrays.asList(
				"The quick brown fox jumps over the lazy dog.",
				"Typing is a useful skill for everyone.");
		case INTERMEDIATE:
			return Arrays.asList(
				"Practice every day to steadily improve your typing speed.",
				"Accuracy matters more than raw speed when learning to type.");
		case ADVANCED:
			return Arrays.asList(
				"Advanced typists balance high speed with consistent accuracy under pressure.",
				"Sustained focus helps maintain both speed and precision during long typing sessions.");
		default:
			return Collections.emptyList();
		}
	}

	public String getRandomSampleText() {
		List<String> samples = getSampleTexts();
		if (samples.isEmpty()) {
			return "";
		}
		return samples.get(RANDOM.nextInt(samples.size()));
	}
}
