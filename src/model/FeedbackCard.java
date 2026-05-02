package model;

import java.util.Objects;

public class FeedbackCard {
    private final String title;
    private final String iconText;
    private final String body;
    private final CardKind kind;

    public FeedbackCard(String title, String iconText, String body, CardKind kind) {
        this.title = title;
        this.iconText = iconText;
        this.body = body;
        this.kind = kind;
    }

    public String getTitle() {
        return title;
    }

    public String getIconText() {
        return iconText;
    }

    public String getBody() {
        return body;
    }

    public CardKind getKind() {
        return kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackCard that = (FeedbackCard) o;
        return Objects.equals(title, that.title) &&
               Objects.equals(iconText, that.iconText) &&
               Objects.equals(body, that.body) &&
               kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, iconText, body, kind);
    }
}
