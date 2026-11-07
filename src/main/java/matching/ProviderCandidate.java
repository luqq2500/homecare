package matching;

import java.util.Objects;

public final class ProviderCandidate {

    private final String providerId;
    private final boolean live;
    private final boolean available;
    private final boolean matchesJobs;
    private final boolean matchesHelpers;

    private ProviderCandidate(String providerId, boolean live, boolean available,
                              boolean matchesJobs, boolean matchesHelpers) {
        this.providerId = Objects.requireNonNull(providerId);
        this.live = live;
        this.available = available;
        this.matchesJobs = matchesJobs;
        this.matchesHelpers = matchesHelpers;
    }

    public static ProviderCandidate of(String providerId, boolean live, boolean available,
                                       boolean matchesJobs, boolean matchesHelpers) {
        return new ProviderCandidate(providerId, live, available, matchesJobs, matchesHelpers);
    }

    public String providerId() { return providerId; }
    public boolean isLive() { return live; }
    public boolean isAvailable() { return available; }
    public boolean matchesJobs() { return matchesJobs; }
    public boolean matchesHelpers() { return matchesHelpers; }

    public boolean isExactMatch() {
        return live && available && matchesJobs && matchesHelpers;
    }

    public boolean isCloseMatch() {
        return live && available && matchesJobs && !matchesHelpers;
    }
}
