package model;

public class TargetGoals {
    private final int targetWpm;
    private final int targetAccuracy;
    private final int deltaWpm;
    private final int deltaAccuracy;

    public TargetGoals(int targetWpm, int targetAccuracy, int deltaWpm, int deltaAccuracy) {
        this.targetWpm = targetWpm;
        this.targetAccuracy = targetAccuracy;
        this.deltaWpm = deltaWpm;
        this.deltaAccuracy = deltaAccuracy;
    }

    public int getTargetWpm() {
        return targetWpm;
    }

    public int getTargetAccuracy() {
        return targetAccuracy;
    }

    public int getDeltaWpm() {
        return deltaWpm;
    }

    public int getDeltaAccuracy() {
        return deltaAccuracy;
    }
}
