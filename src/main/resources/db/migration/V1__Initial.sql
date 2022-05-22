CREATE TABLE application_user (
  id UUID,
  username VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  password VARCHAR NOT NULL,
  country_tag CHAR(2) NOT NULL,
  enabled BOOLEAN DEFAULT TRUE NOT NULL,
  date_of_birth DATE NOT NULL,
  created_at TIMESTAMP without TIME ZONE DEFAULT (now() at time zone 'utc') NOT NULL,
  updated_at TIMESTAMP without TIME ZONE DEFAULT (now() at time zone 'utc') NOT NULL,
  CONSTRAINT pk_application_user_id PRIMARY KEY (id),
  UNIQUE (username, email)
);

CREATE TABLE  application_role (
    title VARCHAR(64),
    CONSTRAINT pk_application_role_id PRIMARY KEY (title)
);

CREATE TABLE application_permission (
    title VARCHAR(64),
    CONSTRAINT pk_application_permission_id PRIMARY KEY (title)
);

CREATE TABLE application_role_user (
    role_id VARCHAR(64),
    user_id UUID,
    created_at TIMESTAMP without TIME ZONE DEFAULT (now() at time zone 'utc') NOT NULL,

    CONSTRAINT pk_application_role_user_id PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_application_role_user_role_id FOREIGN KEY (role_id) REFERENCES application_role(title),
    CONSTRAINT fk_application_role_user_user_id FOREIGN KEY (user_id) REFERENCES application_user(id)
);

CREATE TABLE application_role_permission (
    role_id VARCHAR(64),
    permission_id VARCHAR(64),

    CONSTRAINT pk_application_role_permission_id PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_application_role_permission_role_id FOREIGN KEY (role_id) REFERENCES application_role(title),
    CONSTRAINT fk_application_role_permission_permission_id FOREIGN KEY (permission_id) REFERENCES application_permission(title)
);

INSERT INTO application_permission (title) VALUES
    ('USER_SELF_R'),
    ('USER_SELF_U'),
    ('USER_ALL_R'),
    ('USER_ALL_U'),
    ('USER_ALL_D'),
    ('USER_ALL_C'),
    ('USER_ALL_T'),
    ('USER_*'),
    ('**');

INSERT INTO application_role (title) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MODERATOR'),
    ('ROLE_USER');

INSERT INTO application_role_permission (role_id, permission_id) VALUES
     ('ROLE_ADMIN', '**'),
     ('ROLE_MODERATOR', 'USER_ALL_R'),
     ('ROLE_MODERATOR', 'USER_ALL_C'),
     ('ROLE_MODERATOR', 'USER_ALL_T'),
     ('ROLE_MODERATOR', 'USER_SELF_R'),
     ('ROLE_MODERATOR', 'USER_SELF_U'),
     ('ROLE_USER', 'USER_SELF_R'),
     ('ROLE_USER', 'USER_SELF_U');
