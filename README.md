# Homa

On-demand home care platform selling professional helper time.

## Architecture

Follows Clean Architecture, SOLID, and Domain-Driven Design.

Bounded contexts are self-contained modules with domain / usecase / infra layers.
Dependencies flow strictly inward. Cross-context communication is exclusively via the shared `ie/` integration event module.

## Current Status

- `reservation` domain initiated (status machine, pricing snapshot, temporal constraints).
- Old booking code has been superseded; this repository is the clean starting point for Homa.

See the product specification and backend module discipline for full rules.
