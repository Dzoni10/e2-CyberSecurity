INSERT INTO app_user (name,surname,email,password,role,organization,is_verified) VALUES
('admin', 'adminSurname','admin@gmail.com','$2a$10$GM4u5y13dnV.7YzspEXldOsUezNj8Zc3T.YTNjY2y03sAhSNVSaXu', 'ADMIN','administration',true);

INSERT INTO app_user (name,surname,email,password,role,organization,is_verified) VALUES
    ('user','userSurname', 'basic@gmail.com','$2a$10$NaNFujI1ZCzCnUU99VvhUemFjNvJItP5LHWp7v166Q7qfaJmAtQ2K', 'BASIC','organization',true);

INSERT INTO app_user (name,surname,email,password,role,organization,is_verified) VALUES
    ('CA','CASurname', 'ca@gmail.com','$2a$10$/DgY49QwW9v9hbqDr/.sHeZhy1Ncaa7BJo9TtJ4nKwuXygMgn.AL6', 'CA','organization',true);
