CREATE TABLE account_owner (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               name VARCHAR(255) NOT NULL
);

CREATE TABLE account (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         owner_id BIGINT NOT NULL,
                         account_name VARCHAR(255),
                         FOREIGN KEY (owner_id) REFERENCES account_owner (id)
);

CREATE TABLE transaction (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             account_id BIGINT NOT NULL,
                             amount DECIMAL(15, 2),
                             timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (account_id) REFERENCES account (id)
);

-- Insert sample data
INSERT INTO account_owner (name) VALUES ('John Doe'), ('Jane Smith');
INSERT INTO account (owner_id, account_name) VALUES (1, 'John''s Savings'), (2, 'Jane''s Checking');
INSERT INTO transaction (account_id, amount) VALUES (1, 1000.50), (2, 250.75);