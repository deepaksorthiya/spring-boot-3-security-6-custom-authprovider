INSERT INTO users VALUES ('user','{bcrypt}$2a$10$ZAlOpaGFYTlMvy3xRcYAyeRA7Pez9V859pCengRG4Popio7S3NoDW','userFN','userLN',1);

INSERT INTO users VALUES ('admin', '{bcrypt}$2a$10$BoN85AFbOsRzp0x/Ls8Yzem3ALG0EvLnwJaJIOJdTqVvG/XD4lcQS', 'Admin', 'Admin', '1');

INSERT INTO authorities VALUES ('user','USER');

INSERT INTO authorities VALUES ('admin', 'USER');

INSERT INTO groups VALUES (1, 'ADMIN_GROUP');

INSERT INTO group_authorities VALUES (1, 'ADMIN');

INSERT INTO group_members VALUES (1, 'admin', 1);