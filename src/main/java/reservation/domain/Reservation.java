package reservation.domain;

import reservation.domain.exception.InvalidStatusTransitionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root for a Homa reservation.
 * Enforces the status lifecycle and business invariants from the product description.
 */
public class Reservation {

    private final ReservationId id;
    private final Instant requestedAt;
    private final BookedWindow bookedWindow;
    private final HoursFee hoursFee;
    private final TravelFee travelFee;
    private final CancellationTier cancellationTier;
    private ReservationStatus status;
    private Instant acceptedAt;
    private Instant completedAt;
    private CheckIn checkIn;

    private Reservation(ReservationId id,
                        Instant requestedAt,
                        BookedWindow bookedWindow,
                        HoursFee hoursFee,
                        TravelFee travelFee,
                        CancellationTier cancellationTier) {
        this.id = Objects.requireNonNull(id);
        this.requestedAt = Objects.requireNonNull(requestedAt);
        this.bookedWindow = Objects.requireNonNull(bookedWindow);
        this.hoursFee = Objects.requireNonNull(hoursFee);
        this.travelFee = Objects.requireNonNull(travelFee);
        this.cancellationTier = Objects.requireNonNull(cancellationTier);
        this.status = ReservationStatus.REQUESTED;
    }

    public static Reservation request(ReservationId id,
                                      Instant requestedAt,
                                      BookedWindow bookedWindow,
                                      HoursFee hoursFee,
                                      TravelFee travelFee,
                                      CancellationTier cancellationTier) {
        return new Reservation(id, requestedAt, bookedWindow, hoursFee, travelFee, cancellationTier);
    }

    public void accept(Instant now) {
        if (status != ReservationStatus.REQUESTED) {
            throw new InvalidStatusTransitionException("Can only accept from REQUESTED");
        }
        if (isExpired(now)) {
            throw new InvalidStatusTransitionException("Cannot accept an expired request");
        }
        this.status = ReservationStatus.RESERVED;
        this.acceptedAt = now;
    }

    public void decline() {
        if (status != ReservationStatus.REQUESTED) {
            throw new InvalidStatusTransitionException("Can only decline from REQUESTED");
        }
        this.status = ReservationStatus.DECLINED;
    }

    public void expire(Instant now) {
        if (status != ReservationStatus.REQUESTED) {
            throw new InvalidStatusTransitionException("Can only expire from REQUESTED");
        }
        if (!isExpired(now)) {
            throw new InvalidStatusTransitionException("Request has not yet expired");
        }
        this.status = ReservationStatus.EXPIRED;
    }

    public BigDecimal providerCancellationFine(Instant now) {
        Instant feeWindowStart = bookedWindow.start().minusSeconds(24 * 3600);
        BigDecimal base = hoursFee.amount();
        if (now.isBefore(feeWindowStart)) {
            return BigDecimal.ZERO;
        }
        return base.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal providerNoShowFine() {
        return hoursFee.amount().multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    public void cancelByCustomer(Instant now) {
        if (status != ReservationStatus.REQUESTED && status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Cannot cancel from current status");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public void cancelByProvider(Instant now) {
        if (status != ReservationStatus.REQUESTED && status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Cannot cancel from current status");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public void markDisputed() {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Dispute only allowed while RESERVED");
        }
        this.status = ReservationStatus.DISPUTED;
    }

    public void markJobCompletedWithEvidence(Instant now) {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Can only mark job completed while RESERVED");
        }
        this.completedAt = now;
    }

    public void moveToCompletedAfterDisputeWindowOrResolution(Instant now) {
        if (status != ReservationStatus.RESERVED && status != ReservationStatus.DISPUTED) {
            throw new InvalidStatusTransitionException("Can only complete from RESERVED or DISPUTED");
        }
        this.status = ReservationStatus.COMPLETED;
        if (this.completedAt == null) {
            this.completedAt = now;
        }
    }

    public BigDecimal customerNoShowCompensation() {
        BigDecimal hoursPortion = hoursFee.amount().multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
        return travelFee.amount().add(hoursPortion);
    }

    public BigDecimal customerCancellationFee(Instant now, boolean travelIncludedInPrice) {
        if (status != ReservationStatus.REQUESTED && status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Cancellation fee only applicable while cancellable");
        }
        Instant feeWindowStart = bookedWindow.start().minusSeconds(24 * 3600);
        if (now.isBefore(feeWindowStart)) {
            return BigDecimal.ZERO;
        }
        int percent = cancellationTier.feePercent();
        BigDecimal base = travelIncludedInPrice
                ? hoursFee.amount().add(travelFee.amount())
                : hoursFee.amount();
        BigDecimal fee = base.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return fee.min(customerNoShowCompensation());
    }

    public BigDecimal customerCancellationFee(Instant now) {
        return customerCancellationFee(now, false);
    }

    public void recordCheckIn(Instant now, GeoLocation providerLocation, GeoLocation customerLocation) {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Check-in only allowed while RESERVED");
        }
        this.checkIn = CheckIn.record(now, bookedWindow.start(), providerLocation, customerLocation);
    }

    public boolean hasCheckedIn() {
        return checkIn != null;
    }

    public CheckIn checkIn() {
        return checkIn;
    }

    public boolean isExpired(Instant now) {
        Instant expiry = requestedAt.plusSeconds(24 * 3600);
        Instant effectiveExpiry = expiry.isBefore(bookedWindow.start()) ? expiry : bookedWindow.start();
        return !now.isBefore(effectiveExpiry);
    }

    public ReservationId id() { return id; }
    public ReservationStatus status() { return status; }
    public HoursFee hoursFee() { return hoursFee; }
    public TravelFee travelFee() { return travelFee; }
    public CancellationTier cancellationTier() { return cancellationTier; }
    public BookedWindow bookedWindow() { return bookedWindow; }
}
