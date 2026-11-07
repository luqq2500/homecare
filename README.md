# Homa

On-demand home care platform. Customers book provider time; providers accept, check in, complete, and get paid. Business rules live in the domain.

## Bounded contexts

| Context | Responsibility |
|---------|----------------|
| **Reservation** | Lifecycle (Requested → Reserved → Completed / Cancelled / Disputed), check-in, completion evidence, dispute window, cancellation and no-show settlements |
| **Payment** | Authorise → Held → Released / Refunded / Voided; settle in one operation |
| **Provider** | Identity, offerings, helpers, KYC, fines ledger |
| **Availability** | Bookable slots, weekly pattern, block on Reserved |
| **Discovery** | Search criteria, exact and close matches |
| **Chat** | Customer-initiated conversations |

## Architecture

Clean Architecture / DDD. Inward dependencies only.

```
domain ← usecase ← infra
         ↑
        ie/   (integration events)
        shared/ (Money, GeoLocation, ...)
```

Cross-context communication is by integration events only. No direct imports between bounded contexts.

## Development discipline

1. Finish domain logic
2. Write domain tests (intent tests from Gherkin + targeted mutation tests)
3. Pass domain quality gates (tests green, mutation ≥ 80 %)
4. Finish usecase logic
5. Write usecase tests
6. Pass usecase quality gates
7. Only then move to the next bounded context

Reflect against the product description before starting each new context.

## Quality gates

- All tests green
- Mutation score ≥ 80 % per domain package (PIT)
- Dependency rules (no reverse dependencies)
- Intent tests named in DDD camelCase (`whenShould...` style)

## Current status

| Context | Domain | Usecase | Mutation |
|---------|--------|---------|----------|
| Reservation | Done | Done | 78 % |
| Payment | Done | Done | 81 % |
| Provider | Done | Done | 79 % |
| Availability | Done | Done | 93 % |
| Discovery | Done | Done | 78 % |
| Chat | Done | Done | 100 % |

## Build

```bash
mvn test
mvn org.pitest:pitest-maven:mutationCoverage \
  -DtargetClasses=<context>.domain.** \
  -DtargetTests=<context>.domain.**
```

## Product

See `homa-product-description.md` and `homa-bounded-contexts.md` for the source of truth.
