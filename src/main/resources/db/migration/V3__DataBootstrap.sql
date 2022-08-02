INSERT INTO application_permission (title) VALUES
   ('USER_ALL_R'),
   ('USER_ALL_U'),
   ('USER_ALL_D'),
   ('USER_ALL_C'),
   ('USER_ALL_PROF_IMG_CRUD'),
   ('USER_ALL_SHARED_R'),
   ('USER_*'),
   ('**');

INSERT INTO application_role (title) VALUES
     ('ROLE_ADMIN'),
     ('ROLE_MODERATOR'),
     ('ROLE_USER');

INSERT INTO application_role_permission (role_id, permission_id) VALUES
     ('ROLE_ADMIN', '**'),
     ('ROLE_MODERATOR', 'USER_ALL_R'),
     ('ROLE_MODERATOR', 'USER_ALL_SHARED_R'),
     ('ROLE_MODERATOR', 'USER_ALL_PROF_IMG_CRUD'),
     ('ROLE_USER', 'USER_ALL_SHARED_R');