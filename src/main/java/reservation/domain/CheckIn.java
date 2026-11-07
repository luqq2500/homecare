package reservation.domain;

import reservation.domain.exception.CheckInNotAllowedException;

import java.time.Instant;
import java.util.Objects;

/**
 * Records a provider check-in for a reservation.
 * Location must be within the check-in radius; time must be within the check-in period.
 */
public final class CheckIn {

    public static final int CHECK_IN_PERIOD_MINUTES = 15;
    public static final int CHECK_IN_RADIUS_METRES = 300;

    private final Instant checkedInAt;
    private final GeoLocation providerLocation;
    private final GeoLocation customerLocation;

    private CheckIn(Instant checkedInAt, GeoLocation providerLocation, GeoLocation customerLocation) {
        this.checkedInAt = Objects.requireNonNull(checkedInAt);
        this.providerLocation = Objects.requireNonNull(providerLocation);
        this.customerLocation = Objects.requireNonNull(customerLocation);
    }

    public static CheckIn record(Instant now,
                                 Instant bookedStart,
                                 GeoLocation providerLocation,
                                 GeoLocation customerLocation) {
        if (now.isAfter(bookedStart.plusSeconds(CHECK_IN_PERIOD_MINUTES * 60L))) {
            throw new CheckInNotAllowedException("Check-in period has expired");
        }
        double distance = providerLocation.distanceMetresTo(customerLocation);
        if (distance > CHECK_IN_RADIUS_METRES) {
            throw new CheckInNotAllowedException("Provider is outside the check-in radius");
        }
        return new CheckIn(now, providerLocation, customerLocation);
    }

    public Instant checkedInAt() { return checkedInAt; }
    public GeoLocation providerLocation() { return providerLocation; }
    public GeoLocation customerLocation() { return customerLocation; }
}
