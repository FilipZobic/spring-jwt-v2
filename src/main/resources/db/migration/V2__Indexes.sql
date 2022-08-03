CREATE UNIQUE INDEX application_user_unique_index_username ON application_user (username);
CREATE UNIQUE INDEX application_user_unique_index_email ON application_user (email);
CREATE UNIQUE INDEX application_user_unique_index_username_and_email_and_countryTag ON application_user (username, email, country_tag);