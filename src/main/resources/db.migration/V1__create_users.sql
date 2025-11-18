-- ===========================
-- USERS TABLE (app_user)
-- ===========================
CREATE TABLE IF NOT EXISTS app_user (
                                        id SERIAL PRIMARY KEY,
                                        username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
    );

-- ===========================
-- ROLE TABLE
-- ===========================
CREATE TABLE IF NOT EXISTS role (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(100) UNIQUE NOT NULL
    );

-- ===========================
-- USER_ROLES JOIN TABLE
-- Many-to-Many: app_user <-> role
-- ===========================
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id)
    );

-- ===========================
-- STORE TABLE
-- ===========================
CREATE TABLE IF NOT EXISTS store (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    owner_id BIGINT,
    CONSTRAINT fk_store_owner FOREIGN KEY (owner_id) REFERENCES app_user(id)
    );

-- ===========================
-- RATING TABLE
-- ===========================
CREATE TABLE IF NOT EXISTS rating (
                                      id SERIAL PRIMARY KEY,
                                      store_id BIGINT NOT NULL,
                                      score INT NOT NULL,
                                      comment VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_rating_store FOREIGN KEY (store_id) REFERENCES store(id)
    );

-- ===========================
-- INSERT DEFAULT ROLES
-- ===========================
INSERT INTO role (name) VALUES ('SYSTEM_ADMIN')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO role (name) VALUES ('STORE_OWNER')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO role (name) VALUES ('USER')
    ON CONFLICT (name) DO NOTHING;

ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE;

-- Add address column (nullable)
ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS address VARCHAR(500);

-- Optional: Add index on email for faster lookups
CREATE INDEX IF NOT EXISTS idx_app_user_email ON app_user(email);
