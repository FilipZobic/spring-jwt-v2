INSERT INTO application_permission (title) VALUES
   ('USER_SELF_R'),
   ('USER_SELF_U'),
   ('USER_ALL_R'),
   ('USER_ALL_U'),
   ('USER_ALL_D'),
   ('USER_ALL_C'),
   ('USER_ALL_T'),
   ('USER_ALL_P_IMG'),
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
     ('ROLE_MODERATOR', 'USER_ALL_P_IMG'),
     ('ROLE_USER', 'USER_SELF_R'),
     ('ROLE_USER', 'USER_SELF_U');