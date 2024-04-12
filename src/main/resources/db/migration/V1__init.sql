CREATE TABLE medicine_inventory
(
    id                       BIGINT       NOT NULL,
    medicine_name            VARCHAR(255) NULL,
    quantity_per_unit        INT          NULL,
    consumption_per_day      DOUBLE       NULL,
    consumption_per_month    DOUBLE       NULL,
    quantity_available_today DOUBLE       NULL,
    days_available_for       DOUBLE       NULL,
    date_last_updated        datetime     NULL,
    ending_by_date           datetime     NULL,
    CONSTRAINT pk_medicineinventory PRIMARY KEY (id)
);