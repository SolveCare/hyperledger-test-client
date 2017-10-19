CREATE TABLE clinic.doctors (
  doctor_id  VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  email      VARCHAR(255),
  phone      VARCHAR(255),
  password   VARCHAR(255) NOT NULL,
  PRIMARY KEY (doctor_id),
  UNIQUE (phone),
  UNIQUE (email)
);
