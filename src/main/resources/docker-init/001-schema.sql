-- spring.sql.init.separator=;;

-- 1. TABLES
-- Timestamps are managed exclusively by Spring Data JPA Auditing.

CREATE TABLE IF NOT EXISTS public.addresses
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    street              VARCHAR(50),
    street_number       VARCHAR(6),
    home_number         VARCHAR(6),
    post_code           VARCHAR(6),
    city                VARCHAR(50),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by          VARCHAR(64),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_by    VARCHAR(64)
);;

COMMENT ON TABLE public.addresses
    IS 'Physical addresses; each client owns its own address record';;

CREATE TABLE IF NOT EXISTS public.roles
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(30) NOT NULL UNIQUE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by          VARCHAR(64),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_by    VARCHAR(64)
);;

COMMENT ON TABLE public.roles
    IS 'System access levels dictionary';;

CREATE TABLE IF NOT EXISTS public.clients
(
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name        VARCHAR(50)  NOT NULL,
    last_name         VARCHAR(50)  NOT NULL,
    email             VARCHAR(64)  NOT NULL UNIQUE,
    phone             VARCHAR(9)   NOT NULL,
    registration_date DATE         NOT NULL DEFAULT CURRENT_DATE,
    expiration_date   DATE,
    address_id        BIGINT,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by        VARCHAR(64),
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_by  VARCHAR(64),

    CONSTRAINT fk_clients_address
        FOREIGN KEY (address_id)
        REFERENCES public.addresses (id)
            ON DELETE SET NULL
);;

COMMENT ON TABLE public.clients
    IS 'Gym members; expiration_date NULL means no active membership';;

CREATE TABLE IF NOT EXISTS public.users
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email               VARCHAR(64)  NOT NULL UNIQUE,
    first_name          VARCHAR(50)  NOT NULL,
    last_name           VARCHAR(50)  NOT NULL,
    password            VARCHAR(64)  NOT NULL,
    role_id             BIGINT       NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by          VARCHAR(64),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_by    VARCHAR(64),

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES public.roles (id)
            ON DELETE RESTRICT
);;

COMMENT ON TABLE public.users
    IS 'Internal staff accounts';;

-- 2. INDEXES

CREATE INDEX IF NOT EXISTS idx_clients_address
    ON public.clients(address_id);;

CREATE INDEX IF NOT EXISTS idx_users_role
    ON public.users(role_id);;