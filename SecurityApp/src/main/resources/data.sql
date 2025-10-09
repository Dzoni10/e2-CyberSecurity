INSERT INTO app_user (name,surname,email,password,role,organization,is_verified,must_change_password,first_login) VALUES
('admin', 'adminSurname','admin@gmail.com','$2a$10$GM4u5y13dnV.7YzspEXldOsUezNj8Zc3T.YTNjY2y03sAhSNVSaXu', 'ADMIN','administration',true,false,false);

INSERT INTO app_user (name,surname,email,password,role,organization,is_verified,must_change_password,first_login) VALUES
    ('user','userSurname', 'basic@gmail.com','$2a$10$NaNFujI1ZCzCnUU99VvhUemFjNvJItP5LHWp7v166Q7qfaJmAtQ2K', 'BASIC','organization',true,false,false);

INSERT INTO app_user (name,surname,email,password,role,organization,is_verified,must_change_password,first_login) VALUES
    ('CA','CASurname', 'ca@gmail.com','$2a$10$MNWQfSFCzvszQ30eXo0aX.OxvgDWPuTYvPoGKIl7BQP3xjzDZdIOS', 'CA','organization',true,false,false);
