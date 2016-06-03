CREATE SCHEMA IF NOT EXISTS meetup;

CREATE TABLE meetup.bank_accounts (
    account_num INT PRIMARY KEY,
    balance INT NOT NULL,
    active BOOLEAN NOT NULL
);
