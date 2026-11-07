# Homa Bounded Context Assessment

Assessment of the current bounded contexts against DDD principles, the product description, and the business-logic review. Recommendations for tighter boundaries and clearer responsibilities.

---

## 1. Current contexts

| Context | Current responsibility | Coupling |
|---------|------------------------|----------|
| **Reservation** | Lifecycle, check-in, completion, dispute, cancellation, no-show, settlements | Knows prices, tier, customer, provider, jobs, location |
| **Payment** | Authorise → Held → Released / Refunded / Voided; settle | Receives amounts and settlement splits from Reservation |
| **Provider** | Identity, offerings, helpers, KYC, fines | Fines are a ledger of negative adjustments |
| **Availability** | Bookable slots, weekly pattern, block on Reserved | Blocks time when Reservation is accepted |
| **Discovery** | Search criteria, exact and close matches | Queries Provider and Availability behind a port |
| **Chat** | Customer-initiated conversations | Independent |

---

## 2. Problems with the current boundaries

### 2.1 Reservation knows too much
Reservation currently holds:
- Customer and provider identity
- Booked jobs (the scope)
- Service location
- Pricing snapshot (rate, helpers, hours, travel mode, tier)
- Settlement calculations (toProvider, toCustomer, toHoma)

Settlement is a financial decision. It belongs with Payment, not Reservation. Reservation should emit *what happened* (cancelled by customer, 5 h before start, Standard tier); Payment should compute *who gets what*.

### 2.2 Provider and Availability are split on the wrong axis
A provider's offering (what they do, at what rate, with how many helpers) and their availability (when they can do it) are both attributes of the same provider. Splitting them forces Discovery to join two contexts to answer "who can do this job at this time?"

### 2.3 Discovery is a query, not a context
Discovery has no aggregate and no lifecycle. It is a read model over Provider and Availability. Treating it as a bounded context adds a package but no domain.

### 2.4 Fines live in Provider but are triggered by Reservation
A provider fine is a consequence of a reservation event (late cancel, no-show). The fine amount is a percentage of the hours fee, which Reservation owns. Provider should not know the fee formula; it should receive a fine amount and record it.

### 2.5 Chat is correctly isolated
Chat has no dependency on Reservation status. A conversation outlives any single booking. This boundary is sound.

---

## 3. Proposed contexts

| Context | Responsibility | Aggregates |
|---------|----------------|------------|
| **Booking** | Reservation lifecycle, check-in, completion, dispute, cancellation, no-show. Emits events with facts (who, when, tier, hours fee, travel fee). Does not compute settlements. | Reservation |
| **Billing** | Authorise, hold, release, refund, void. Receives settlement instructions from Booking events and computes the split. Owns the payment state machine. | Payment, Settlement |
| **Provider** | Identity, KYC, offerings (rate, helpers, travel mode, tier, jobs, tools), availability (weekly pattern, slots, blocks), fines ledger. One aggregate for the provider's commercial terms and schedule. | Provider, Offering, Availability, Fine |
| **Matching** | Read model. Answers "which providers match this criteria?" by projecting Provider events. No aggregate. | — |
| **Conversation** | Customer-initiated chat. Independent of booking status. | Conversation |
| **Customer** | Identity, payment method, service addresses. Referenced by Booking. | Customer |

### What moved
- Settlement calculation: Reservation → Billing
- Availability: separate context → inside Provider
- Discovery: context → Matching read model
- Fines: still in Provider, but amount arrives via event, not computed locally
- Customer: new context (was implicit)

---

## 4. Integration events (proposed)

| Event | Publisher | Payload | Consumers |
|-------|-----------|---------|-----------|
| ReservationRequested | Booking | reservationId, customerId, providerId, amount | Billing (authorise) |
| ReservationAccepted | Booking | reservationId, providerId, amount | Billing (hold), Provider (block slot) |
| ReservationDeclined | Booking | reservationId | Billing (void) |
| ReservationExpired | Booking | reservationId | Billing (void) |
| ReservationCancelled | Booking | reservationId, by, reason, hoursFee, travelFee, tier, hoursBeforeStart | Billing (settle), Provider (fine if applicable) |
| JobCompletionSubmitted | Booking | reservationId, evidenceRef, submittedAt | Billing (start window) |
| DisputeFiled | Booking | reservationId, type, reason, filedBy | Billing (halt release) |
| DisputeResolved | Booking | reservationId, resolution, refundAmount | Billing (settle) |
| ProviderOfferingUpdated | Provider | providerId, offering | Matching (reindex) |
| AvailabilityUpdated | Provider | providerId, slots | Matching (reindex) |
| ProviderFineApplied | Provider | providerId, amount, reservationId | Billing (deduct from next payout) |

---

## 5. Dependency rules (proposed)

```
Booking  → (publishes) → Billing, Provider
Provider → (publishes) → Matching, Billing
Matching → (reads)     → Provider projections
Conversation → (none)
Customer → (referenced by) Booking
```

No context imports another's domain. All cross-context communication is by integration event.

---

## 6. Migration path

1. **Extract Settlement from Reservation.** Move `settleCustomerCancel`, `settleProviderNoShow`, `settleCustomerNoShow` into Billing. Reservation emits facts; Billing computes splits.
2. **Merge Availability into Provider.** Provider aggregate owns slots, weekly pattern, and blocks. Delete the Availability context.
3. **Demote Discovery to a read model.** Matching becomes a projection of Provider events. No domain package.
4. **Add Customer context.** Identity, payment method, service addresses. Booking references customerId.
5. **Wire fine amount via event.** ReservationCancelled carries hoursFee; Provider applies the percentage and records the Fine.

---

## 7. What stays the same

- Chat / Conversation remains independent.
- Payment state machine (Authorised → Held → Released / Refunded / Voided) stays in Billing.
- Reservation state machine (Requested → Reserved → Completed / Cancelled / Disputed) stays in Booking.
- Integration events remain the only cross-context channel.
- Domain-first development discipline remains.

---

## 8. Open questions

1. Should Matching be a separate deployable, or a module inside the Provider service?
2. Does Customer own the service address, or does Booking snapshot it at request time?
3. Should the fine percentage live in Billing (as a settlement rule) or in Provider (as a penalty rule)?
4. Is a weekly availability pattern sufficient, or do providers need date-specific overrides beyond blocks?
