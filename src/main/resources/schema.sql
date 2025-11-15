--
-- PROPERTIES
CREATE TABLE properties (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE INDEX properties_status_idx ON properties(status);

--
-- BLOCKS
CREATE TABLE blocks (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    property_id UUID NOT NULL,
    start_at DATE NOT NULL,
    end_at DATE NOT NULL,
    reason VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT block_dates_chk CHECK (start_at < end_at),
    FOREIGN KEY (property_id) REFERENCES properties(id)
);

CREATE INDEX blocks_property_dates_idx ON blocks(property_id, start_at, end_at);
CREATE INDEX blocks_property_idx ON blocks(property_id);

--
-- BOOKINGS
CREATE TABLE bookings (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    property_id UUID NOT NULL,
    start_at DATE NOT NULL,
    end_at DATE NOT NULL,
    guests JSON NOT NULL,
    status VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    rebooked_at TIMESTAMP,
    CONSTRAINT booking_dates_chk CHECK (start_at < end_at),
    FOREIGN KEY (property_id) REFERENCES properties(id)
);

CREATE INDEX bookings_property_dates_idx ON bookings(property_id, start_at, end_at);
CREATE INDEX bookings_property_idx ON bookings(property_id);