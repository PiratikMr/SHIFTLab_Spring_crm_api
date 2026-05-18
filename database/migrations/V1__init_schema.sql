CREATE TABLE IF NOT EXISTS sellers
(
    id                BIGSERIAL    PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    contact_info      VARCHAR(255),
    registration_date TIMESTAMP    NOT NULL,
    is_deleted        BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS transactions
(
    id               BIGSERIAL      PRIMARY KEY,
    seller_id        BIGINT         NOT NULL,
    amount           NUMERIC(38, 2) NOT NULL,
    payment_type     VARCHAR(255)   NOT NULL,
    transaction_date TIMESTAMP      NOT NULL,
    CONSTRAINT fk_transaction_seller
        FOREIGN KEY (seller_id) REFERENCES sellers (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_transactions_seller_id
    ON transactions (seller_id);

CREATE INDEX IF NOT EXISTS idx_transactions_date
    ON transactions (transaction_date);
