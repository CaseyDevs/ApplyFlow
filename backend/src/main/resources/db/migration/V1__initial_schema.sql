CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE company (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    name VARCHAR(255) NOT NULL,
    rating DOUBLE PRECISION
);

CREATE TABLE contact (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    company_id BIGINT,
    CONSTRAINT fk_contact_company FOREIGN KEY (company_id) REFERENCES company (id)
);

CREATE TABLE application (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    company_id BIGINT,
    user_id BIGINT NOT NULL,
    version BIGINT,
    CONSTRAINT fk_application_company FOREIGN KEY (company_id) REFERENCES company (id),
    CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE interview (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    date TIMESTAMP(6),
    type VARCHAR(255) NOT NULL,
    contact_id BIGINT,
    application_id BIGINT NOT NULL,
    CONSTRAINT fk_interview_contact FOREIGN KEY (contact_id) REFERENCES contact (id),
    CONSTRAINT fk_interview_application FOREIGN KEY (application_id) REFERENCES application (id)
);

CREATE TABLE note (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    interview_id BIGINT NOT NULL,
    CONSTRAINT fk_note_interview FOREIGN KEY (interview_id) REFERENCES interview (id)
);

CREATE TABLE job_board (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL,
    title VARCHAR(255),
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_job_board_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE job_board_member (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    job_board_id BIGINT,
    role VARCHAR(255),
    CONSTRAINT fk_job_board_member_user FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT fk_job_board_member_job_board FOREIGN KEY (job_board_id) REFERENCES job_board (id)
);

CREATE TABLE job_board_application (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    job_board_member_id BIGINT,
    added_at TIMESTAMP(6) NOT NULL,
    application_id BIGINT,
    job_board_id BIGINT,
    CONSTRAINT uk_job_board_application_application_job_board UNIQUE (application_id, job_board_id),
    CONSTRAINT fk_job_board_application_member FOREIGN KEY (job_board_member_id) REFERENCES job_board_member (id),
    CONSTRAINT fk_job_board_application_application FOREIGN KEY (application_id) REFERENCES application (id),
    CONSTRAINT fk_job_board_application_job_board FOREIGN KEY (job_board_id) REFERENCES job_board (id)
);

CREATE TABLE job_board_application_status (
    id BIGSERIAL PRIMARY KEY,
    job_board_application_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    CONSTRAINT uk_job_board_application_status_application_user UNIQUE (job_board_application_id, user_id),
    CONSTRAINT fk_job_board_application_status_application FOREIGN KEY (job_board_application_id) REFERENCES job_board_application (id),
    CONSTRAINT fk_job_board_application_status_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE email_verification_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255),
    user_id BIGINT UNIQUE,
    expiry_date TIMESTAMP(6),
    CONSTRAINT fk_email_verification_token_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE verification_token (
    id BIGINT PRIMARY KEY,
    token VARCHAR(255),
    user_id BIGINT UNIQUE,
    expiry_date TIMESTAMP(6),
    CONSTRAINT fk_verification_token_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE SEQUENCE verification_token_seq START WITH 1 INCREMENT BY 50;
