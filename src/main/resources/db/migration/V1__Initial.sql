CREATE TABLE application_user (
  id BYTEA,
  username VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  password VARCHAR NOT NULL,
  country_tag CHAR(2) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  date_of_birth DATE NOT NULL,
  created_at TIMESTAMP without TIME ZONE NOT NULL
      DEFAULT (now() at time zone 'utc'),
  updated_at TIMESTAMP without TIME ZONE NOT NULL
      DEFAULT (now() at time zone 'utc'),
  CONSTRAINT pk_user_id PRIMARY KEY (id),
  UNIQUE (username, email)
);