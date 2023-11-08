/*
 SQL SCHEMA CREATION SCRIPT FOR My-Application-Board Database
 Version : 1.0
 Author : Patrice ALLARY
 */

-- DROP CONSTRAINTS
ALTER TABLE IF EXISTS job DROP CONSTRAINT IF EXISTS fk_job_status;
ALTER TABLE IF EXISTS job DROP CONSTRAINT IF EXISTS fk_job_enterprise;

ALTER TABLE IF EXISTS contacts DROP CONSTRAINT IF EXISTS fk_contact_address;
ALTER TABLE IF EXISTS contacts DROP CONSTRAINT IF EXISTS fk_contact_role;
ALTER TABLE IF EXISTS contacts DROP CONSTRAINT IF EXISTS fk_contact_title;
ALTER TABLE IF EXISTS contacts DROP CONSTRAINT IF EXISTS fk_contact_enterprise;

ALTER TABLE IF EXISTS enterprise DROP CONSTRAINT IF EXISTS fk_enterprise_address;
ALTER TABLE IF EXISTS enterprise DROP CONSTRAINT IF EXISTS fk_enterprise_type;
ALTER TABLE IF EXISTS enterprise DROP CONSTRAINT IF EXISTS fk_enterprise_activity;

ALTER TABLE IF EXISTS contacts_list DROP CONSTRAINT IF EXISTS pk_contacts_list;

/*
 TABLES
 */

-- Civilite (Mr, Ms)
DROP TABLE IF EXISTS contact_title;
CREATE TABLE contact_title
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Result of a job job (accepted / rejected / delayed...)
DROP TABLE IF EXISTS job_results;
CREATE TABLE job_results
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
-- Contact role in enterprise (HR Manager / CTO ...)
DROP TABLE IF EXISTS contact_roles;
CREATE TABLE contact_roles
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
-- Enterprise type (TINY / SMALL / BIG)
DROP TABLE IF EXISTS enterprise_type;
CREATE TABLE enterprise_type
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Enterprise activity (Services / Banking / Insurrance...)
DROP TABLE IF EXISTS enterprise_activity;
CREATE TABLE enterprise_activity
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Postal Address (contacts & enterprise)
DROP TABLE IF EXISTS address;
CREATE TABLE address
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address1 VARCHAR(250) NOT NULL,
    address2 VARCHAR(250),
    zip_code VARCHAR(50),
    city    VARCHAR(250) NOT NULL
);

-- enterprise
DROP TABLE IF EXISTS enterprise;
CREATE TABLE enterprise
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    phone VARCHAR(50),
    notes CLOB,
    address_id BIGINT NOT NULL,
    type_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL
);

-- job
DROP TABLE IF EXISTS job;
CREATE TABLE job
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(250) NOT NULL,
    date_created    TIMESTAMP NOT NULL,
    date_send    TIMESTAMP,
    date_raised    TIMESTAMP,
    date_interview    TIMESTAMP,
    url VARCHAR(250),
    status_id  BIGINT NOT NULL,
    enterprise_id BIGINT NOT NULL
);

-- enterprise contact
DROP TABLE IF EXISTS contacts;
CREATE TABLE contacts
(
    id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(50),
    linkedin_profile VARCHAR(250),
    address_id BIGINT,
    role_id BIGINT NOT NULL,
    title_id BIGINT,
    enterprise_id BIGINT NOT NULL
);

/*
 CONSTAINTS
 */
ALTER TABLE job ADD CONSTRAINT fk_job_status FOREIGN KEY (status_id) REFERENCES job_results(id);
ALTER TABLE job ADD CONSTRAINT fk_job_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise(id);

ALTER TABLE contacts ADD CONSTRAINT fk_contact_address FOREIGN KEY (address_id) REFERENCES address(id);
ALTER TABLE contacts ADD CONSTRAINT fk_contact_role FOREIGN KEY (role_id) REFERENCES contact_roles(id);
ALTER TABLE contacts ADD CONSTRAINT fk_contact_title FOREIGN KEY (title_id) REFERENCES contact_title(id);
ALTER TABLE contacts ADD CONSTRAINT fk_contact_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise(id);

ALTER TABLE enterprise ADD CONSTRAINT fk_enterprise_address FOREIGN KEY (address_id) REFERENCES address(id);
ALTER TABLE enterprise ADD CONSTRAINT fk_enterprise_type FOREIGN KEY (type_id) REFERENCES enterprise_type(id);
ALTER TABLE enterprise ADD CONSTRAINT fk_enterprise_activity FOREIGN KEY (activity_id) REFERENCES enterprise_activity(id);

