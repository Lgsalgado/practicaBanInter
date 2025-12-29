CREATE TABLE IF NOT EXISTS campaigns (
    id BIGSERIAL PRIMARY KEY,
    campaign_code BIGINT NOT NULL,
    campaign_acronym VARCHAR(5) NOT NULL,
    company_ruc VARCHAR(255) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    campaign_description VARCHAR(255),
    campaign_date DATE NOT NULL,
    number_of_clients INTEGER NOT NULL,
    campaign_budget NUMERIC(19, 2) NOT NULL
);
