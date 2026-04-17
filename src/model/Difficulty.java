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
			return Arrays.asList(
				"I like to type every day.",
				"This is a simple practice sentence.",
				"Keep your fingers on the home row.",
				"Slow and steady typing builds accuracy.",
				"Take a short break, then try again.",
				"Use both hands when you type.",
				"Look at the screen, not the keys.",
				"Small steps help you improve.",
				"Press each key with gentle force.",
				"Breathe, relax, and keep going.");
		case INTERMEDIATE:
			return Arrays.asList(
				"It's a busy day, so I'll practice carefully. Don't rush, keep your eyes on the text. When you miss a key, correct it and continue.",
				"My keyboard feels familiar, yet it still surprises me. I'm learning where commas, dots, and apostrophes go. With steady practice, I won't hesitate.",
				"We typed for five minutes, then checked the results. I didn't expect so many small errors, but that's fine. Now I'll slow down, and I'll improve.",
				"You're doing well, but you shouldn't skip punctuation. If a word ends with a comma, type it exactly as shown. After each sentence, pause briefly and reset.",
				"Today's quote is longer, and it's more detailed than before. Don't panic if you stumble, just backspace and fix it. Keep going, and you'll finish strong.",
				"I can't always type fast, but I can type cleanly. If you see a comma, type it, then move on. Don't forget the period at the end.",
				"It's okay to slow down, especially at first. You're building accuracy, not chasing luck. When you're ready, increase speed little by little.",
				"I'll read the line once, then I'll type it again. If I make a mistake, I'll correct it with backspace. When I finish a sentence, I'll keep the rhythm.",
				"Don't skip contractions, they're part of the text. It's easy to miss an apostrophe, so watch closely. If you slip, fix it, then continue.",
				"You're practicing punctuation, so be precise. Type commas, periods, and apostrophes exactly as shown. When you're done, review your pace and relax.");
		case ADVANCED:
			return Arrays.asList(
				"Although the methodology appears straightforward, the implementation is deliberately intricate; it demands meticulous attention to timing, punctuation, and consistently precise keystrokes.",
				"In interdisciplinary research, miscommunication often arises from ambiguous terminology; therefore, define constraints clearly, test assumptions rigorously, and document outcomes concisely.",
				"The committee's decision, while pragmatic, wasn't universally welcomed; nevertheless, it established a resilient framework, improved traceability, and reduced operational risk.",
				"As the algorithm iterates, it recalibrates internal parameters; minor deviations accumulate, so validate inputs, monitor drift, and refactor before complexity overwhelms clarity.",
				"If you're determined to master advanced typing, prioritize accuracy over haste; cultivate rhythm, respect punctuation, and let consistency, rather than adrenaline driven speed.",
				"When systems become heterogeneous, interoperability can degrade subtly; align schemas, normalize encoding, and measure latency before declaring success.",
				"The cryptographic handshake seems trivial, yet it's notoriously fragile; rotate keys, verify certificates, and log failures with disciplined restraint.",
				"Concurrency is rarely the villain, but it magnifies carelessness; isolate shared state, constrain side effects, and prefer deterministic tests.",
				"Even a well tuned pipeline can regress without warning; monitor throughput, audit assumptions, and recalibrate thresholds when conditions shift.",
				"Precision isn't optional in technical writing; choose exact verbs, avoid vague modifiers, and let structure, not flourish, carry meaning.");
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
