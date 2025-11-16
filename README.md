# Booking System

RESTful API for managing property bookings and availability blocks.

## Quick start

Building the project:

```bash
# Build and run tests
mvn clean package

# Start application
mvn spring-boot:run

# Access Swagger UI
http://localhost:8080/swagger-ui.html
```

## Requirements

- Java 21+
- Maven 3.8+

## Project structure

```
src/main/java/
├── api/           # REST controllers, DTOs, exception handling
├── domain/        # Entities, repositories, models
├── service/       # Business logic
└── exception/     # Custom exceptions

src/test/groovy/   # Spock tests (unit + integration)
```

## Key design decisions

### Database

**Decision**: use H2 (in-memory DB) instead of PostgreSQL/MySQL

**Rationale**: 
- Simplifies setup and testing (no external database required)
- Easy to reset state between test runs
- All SQL features needed (JSON type, transactions, locking) are supported

**Production**: would use PostgreSQL with proper connection pooling.

### Index strategy

**Decision**: current implementation uses simple indexes.

**Production**: in PostgreSQL, would use **partial indexes** for better performance, for example:
```sql
CREATE INDEX idx_bookings_active ON bookings(property_id, start_at, end_at) 
WHERE status = 'ACTIVE';
```

### Concurrency control

Uses **pessimistic locking** on property rows to prevent double-bookings in concurrent scenarios.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
PropertyEntity findByIdWithLock(UUID id);
```

This is the current workflow to create a booking:
1. Lock the property row (`SELECT ... FOR UPDATE`)
2. Check for date overlaps
3. Save booking if date available
4. Lock released on transaction commit/rollback

### Overlap detection

Uses standard range overlap formula: `start1 < end2 AND start2 < end1`

Handles all cases: partial overlaps, complete containment, etc.

### Guest data as JSON

**Decision**: store guests as JSON column instead of separate `guests` table.

**Rationale**: 
- Simplifies schema for the current requirements
- Guests are always accessed in context of a booking (no independent queries)
- Easy to add/update the entire guest list atomically

**Production**: need to check how it would be used to decide if worth it to create a `guests` table. 

### Date dields without time

**Decision**: use `DATE` fields (`start_at`, `end_at`) without time component.

**Trade-off**: simplifies overlap logic and validation, but probably **doesn't reflect real application use case** where check-in/check-out times should matter.

**Production**:
- `checkin_time` and `checkout_time` on properties (e.g., 3 PM / 11 AM)
- Snapshot these times in bookings (in case property times change later)
- Handle same-day turnover: checkout 11 AM, cleaning, new check-in 3 PM

**Current approach**: assumes whole-day bookings with implicit boundaries. Acceptable for this test scope but would require refactoring for production.

## Production Improvements

### Critical
- Add Spring Security + JWT authentication
- Migrate to PostgreSQL
- Add pagination and filtering to GET endpoints
- Implement proper audit logging

### Performance
- Caching for most used and critical queries
- Monitor lock wait times

## Trade-offs

| Decision                    | Trade-off | Rationale                                                                          |
|-----------------------------|-----------|------------------------------------------------------------------------------------|
| Pessimistic locking         | Lower throughput per property | Easy to implement; distributed lock guarantee                                      |
| JSON for guests             | Can't query guest data independently | Simpler schema; decision depends on whether guest history/search is needed         |
| H2 in-memory                | Data not persistent | Easier testing, matches test requirements                                          |
| DATE without time           | No check-in/check-out times | Simplifies logic; production would need time fields and same-day turnover handling |
| Incomplete `properties` API | Can't add properties dynamically | Focus on booking/block logic                                                       |
