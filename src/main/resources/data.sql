INSERT INTO properties
    (id, name, address, status)
  VALUES
    ('bdad3baf-f65d-41eb-a190-677679dec204', 'Fake House', 'Fake Street, 123', 'ACTIVE');

INSERT INTO blocks
    (id, property_id, start_at, end_at, reason)
  VALUES
    ('11a52249-2193-4ebd-981a-bea0f4c5ca26', 'bdad3baf-f65d-41eb-a190-677679dec204', '2025-12-24', '2026-01-05', 'OWNER_USE');

INSERT INTO bookings
    (id, property_id, start_at, end_at, guests, status)
  VALUES
    ('f170594d-7476-4b0a-b272-0132f1277f1c', 'bdad3baf-f65d-41eb-a190-677679dec204', '2026-01-10', '2026-01-30', '[{}]', 'ACTIVE');