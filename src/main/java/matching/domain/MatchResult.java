package matching.domain;

import java.util.Objects;

public final class MatchResult {

    private final String providerId;
    private final boolean exact;
    private final String reason;

    private MatchResult(String providerId, boolean exact, String reason) {
        this.providerId = Objects.requireNonNull(providerId);
        this.exact = exact;
        this.reason = reason;
    }

    public static MatchResult exact(String providerId) {
        return new MatchResult(providerId, true, null);
    }

    public static MatchResult close(String providerId, String reason) {
        return new MatchResult(providerId, false, Objects.requireNonNull(reason));
    }

    public String providerId() { return providerId; }
    public boolean isExact() { return exact; }
    public String reason() { return reason; }
}
