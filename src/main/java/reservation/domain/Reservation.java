package reservation.domain;

import reservation.domain.exception.InvalidStatusTransitionException;

import java.math.BigDecimal;
import static reservation.domain.HomaPolicy.*;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

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
    private Instant completionSubmittedAt;
    private Instant resolvedAt;
    private CheckIn checkIn;
    private Dispute dispute;
    private CompletionEvidence completionEvidence;
    private String customerId;
    private String providerId;
    private java.util.List<String> jobs;
    private PricingSnapshot pricingSnapshot;
    private Cancellation cancellation;

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
        Instant feeWindowStart = bookedWindow.start().minusSeconds(HomaPolicy.REQUEST_EXPIRY_HOURS * 3600);
        if (now.isBefore(feeWindowStart) && !hasCheckedIn()) {
            return BigDecimal.ZERO;
        }
        BigDecimal percent = hasCheckedIn() ? new java.math.BigDecimal(HomaPolicy.PROVIDER_NO_SHOW_FINE_PERCENT).divide(new java.math.BigDecimal(100)) : new java.math.BigDecimal(HomaPolicy.PROVIDER_LATE_CANCELLATION_FINE_PERCENT).divide(new java.math.BigDecimal(100));
        return hoursFee.amount().multiply(percent).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal providerNoShowFine() {
        return hoursFee.amount().multiply(new java.math.BigDecimal(HomaPolicy.PROVIDER_NO_SHOW_FINE_PERCENT).divide(new java.math.BigDecimal(100))).setScale(2, RoundingMode.HALF_UP);
    }

    public void cancelByCustomer(Instant now) {
        if (status != ReservationStatus.REQUESTED && status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Cannot cancel from current status");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public void cancelByProvider(Instant now) {
        if (status == ReservationStatus.REQUESTED) {
            this.status = ReservationStatus.DECLINED;
            return;
        }
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Cannot cancel from current status");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public void markDisputed() {
        fileDispute(DisputeType.PROBLEM_WITH_FINISHED_WORK, "unspecified", bookedWindow.start());
    }

    public void fileDispute(DisputeType type, String reason, Instant now) {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Dispute only allowed while RESERVED");
        }
        if (this.dispute != null) {
            throw new InvalidStatusTransitionException("Only one dispute allowed per reservation");
        }
        this.dispute = Dispute.file(type, reason, now);
        this.status = ReservationStatus.DISPUTED;
    }

    public void resolveDispute(DisputeResolution resolution, Instant now) {
        if (status != ReservationStatus.DISPUTED || this.dispute == null) {
            throw new InvalidStatusTransitionException("No open dispute to resolve");
        }
        this.dispute.resolve(resolution);
        this.status = ReservationStatus.COMPLETED;
        this.resolvedAt = now;
        if (this.completedAt == null) {
            this.completedAt = now;
        }
    }

    public void markJobCompletedWithEvidence(String evidenceReference, Instant now) {
        if (status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Can only mark job completed while RESERVED");
        }
        if (now.isBefore(bookedWindow.start())) {
            throw new InvalidStatusTransitionException("Cannot complete before booked start");
        }
        this.completionEvidence = CompletionEvidence.of(evidenceReference, now);
        this.completionSubmittedAt = now;
        this.completedAt = now;
    }

    public void markJobCompletedWithEvidence(Instant now) {
        markJobCompletedWithEvidence("evidence-pending", now);
    }

    public void moveToCompletedAfterDisputeWindowOrResolution(Instant now) {
        if (status == ReservationStatus.DISPUTED) {
            throw new InvalidStatusTransitionException("Resolve dispute first");
        }
        if (status != ReservationStatus.RESERVED || completionEvidence == null) {
            throw new InvalidStatusTransitionException("Needs evidence and RESERVED");
        }
        if (now.isBefore(disputeWindowEnd())) {
            throw new InvalidStatusTransitionException("Dispute window still open");
        }
        this.status = ReservationStatus.COMPLETED;
        if (this.completedAt == null) {
            this.completedAt = now;
        }
    }

    public BigDecimal customerNoShowCompensation() {
        BigDecimal hoursPortion = hoursFee.amount().multiply(new java.math.BigDecimal(HomaPolicy.PROVIDER_NO_SHOW_FINE_PERCENT).divide(new java.math.BigDecimal(100))).setScale(2, RoundingMode.HALF_UP);
        return travelFee.amount().add(hoursPortion);
    }

    public BigDecimal customerCancellationFee(Instant now, boolean travelIncludedInPrice) {
        if (status != ReservationStatus.REQUESTED && status != ReservationStatus.RESERVED) {
            throw new InvalidStatusTransitionException("Cancellation fee only applicable while cancellable");
        }
        if (status == ReservationStatus.REQUESTED) {
            return BigDecimal.ZERO;
        }
        Instant feeWindowStart = bookedWindow.start().minusSeconds(HomaPolicy.REQUEST_EXPIRY_HOURS * 3600);
        if (!now.isAfter(feeWindowStart)) {
            return BigDecimal.ZERO;
        }
        if (!now.isBefore(bookedWindow.start())) {
            return customerNoShowCompensation();
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
        if (now.isBefore(bookedWindow.start())) {
            throw new InvalidStatusTransitionException("Check-in not yet open");
        }
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
        Instant expiry = requestedAt.plusSeconds(HomaPolicy.REQUEST_EXPIRY_HOURS * 3600);
        Instant effectiveExpiry = expiry.isBefore(bookedWindow.start()) ? expiry : bookedWindow.start();
        return !now.isBefore(effectiveExpiry);
    }

    public ReservationId id() { return id; }
    public Instant requestedAt() { return requestedAt; }
    public BookedWindow bookedWindow() { return bookedWindow; }
    public HoursFee hoursFee() { return hoursFee; }
    public TravelFee travelFee() { return travelFee; }
    public CancellationTier cancellationTier() { return cancellationTier; }
    public ReservationStatus status() { return status; }
    public Instant acceptedAt() { return acceptedAt; }
    public Instant completedAt() { return completedAt; }
    public Instant completionSubmittedAt() { return completionSubmittedAt; }
    public Instant resolvedAt() { return resolvedAt; }
    public Dispute dispute() { return dispute; }
    public CompletionEvidence completionEvidence() { return completionEvidence; }
    public String customerId() { return customerId; }
    public String providerId() { return providerId; }
    public java.util.List<String> jobs() { return jobs; }
    public PricingSnapshot pricingSnapshot() { return pricingSnapshot; }
    public Cancellation cancellation() { return cancellation; }

    public Instant disputeWindowEnd() {
        Instant bookedEnd = bookedWindow.start().plusSeconds(bookedWindow.hours() * 3600L);
        Instant completionPlusOne = (completedAt != null) ? completedAt.plusSeconds(3600) : bookedEnd;
        return bookedEnd.isAfter(completionPlusOne) ? bookedEnd.plusSeconds(3600) : completionPlusOne;
    }

    public void closeDisputeWindow(Instant now) {
        if (status != ReservationStatus.RESERVED || completionEvidence == null) {
            throw new InvalidStatusTransitionException("Needs evidence and RESERVED");
        }
        if (now.isBefore(disputeWindowEnd())) {
            throw new InvalidStatusTransitionException("Window still open");
        }
        this.status = ReservationStatus.COMPLETED;
    }

    public void fileDisputeWithWindow(DisputeType type, String reason, Instant now) {
        if (status != ReservationStatus.RESERVED && status != ReservationStatus.COMPLETED) {
            throw new InvalidStatusTransitionException("Can only dispute from RESERVED or COMPLETED");
        }
        if (now.isBefore(bookedWindow.start()) || now.isAfter(disputeWindowEnd())) {
            throw new InvalidStatusTransitionException("Outside dispute window");
        }
        this.dispute = Dispute.file(type, reason, now);
        this.status = ReservationStatus.DISPUTED;
    }

    public void reportProviderNoShow(Instant now) {
        if (status != ReservationStatus.RESERVED || hasCheckedIn()) {
            throw new InvalidStatusTransitionException("Provider no-show only when Reserved and not checked in");
        }
        this.cancellation = Cancellation.of(Cancellation.By.PROVIDER, Cancellation.Reason.NO_SHOW, now);
        this.status = ReservationStatus.CANCELLED;
    }

    public void reportCustomerNoShow(Instant now) {
        if (status != ReservationStatus.RESERVED || !hasCheckedIn()) {
            throw new InvalidStatusTransitionException("Customer no-show only after check-in");
        }
        this.cancellation = Cancellation.of(Cancellation.By.CUSTOMER, Cancellation.Reason.CUSTOMER_NO_SHOW, now);
        this.status = ReservationStatus.CANCELLED;
    }

    public static Reservation requestWithIdentity(ReservationId id, Instant requestedAt,
                                                  BookedWindow bookedWindow, HoursFee hoursFee,
                                                  TravelFee travelFee, CancellationTier cancellationTier,
                                                  String customerId, String providerId,
                                                  java.util.List<String> jobs, PricingSnapshot pricingSnapshot) {
        Reservation r = request(id, requestedAt, bookedWindow, hoursFee, travelFee, cancellationTier);
        r.customerId = Objects.requireNonNull(customerId);
        r.providerId = Objects.requireNonNull(providerId);
        r.jobs = java.util.List.copyOf(jobs);
        r.pricingSnapshot = Objects.requireNonNull(pricingSnapshot);
        return r;
    }
}
