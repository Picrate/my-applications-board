INSERT INTO JOB_RESULTS(NAME) VALUES
                                  ( 'Acceptée' ),
                                  ('Refusée par l employeur'),
                                  ('Refusée')
;

INSERT INTO CONTACT_TITLE(NAME) VALUES
                                   ( 'Mr' ),
                                   ( 'Mme' )
;

INSERT INTO CONTACT_ROLES(NAME) VALUES
                                    ( 'Chargé de recrutement IT' ),
                                    ( 'Responsable des Ressources Humaines' ),
                                    ( 'Dirigeant' )
;

INSERT INTO ENTERPRISE_TYPE(NAME) VALUES
                                      ( 'PME' ),
                                      ( 'Grand Groupe' ),
                                      ( 'ETI' )
;

INSERT INTO ENTERPRISE_ACTIVITY(NAME) VALUES
                                          ( 'Assurance' ),
                                          ( 'Banque' ),
                                          ( 'ESN' )
;

insert into ADDRESS (ADDRESS1, ADDRESS2, ZIP_CODE, CITY) values ('0452 Summer Ridge Street', 'Apt 268', '92119 CEDEX', 'Clichy');
insert into ADDRESS (ADDRESS1, ADDRESS2, ZIP_CODE, CITY) values ('24 Morrow Circle', 'Apt 1601', null, 'Weetobula');
insert into ADDRESS (ADDRESS1, ADDRESS2, ZIP_CODE, CITY) values ('64374 Farwell Court', 'Suite 20', null, 'Daojiang');

insert into ENTERPRISE (NAME, PHONE, NOTES, ADDRESS_ID, TYPE_ID, ACTIVITY_ID) values ('Yadel', '6672553534', 'quis orci eget orci vehicula', 3, 1, 1);
insert into ENTERPRISE (NAME, PHONE, NOTES, ADDRESS_ID, TYPE_ID, ACTIVITY_ID) values ('Flashdog', '2525243945', 'odio condimentum id luctus nec molestie', 1, 3, 2);
insert into ENTERPRISE (NAME, PHONE, NOTES, ADDRESS_ID, TYPE_ID, ACTIVITY_ID) values ('Feedfire', '3001738561', 'ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia', 2, 2, 3);

INSERT INTO CONTACTS(FIRSTNAME, LASTNAME, EMAIL, ADDRESS_ID, ROLE_ID, TITLE_ID, ENTERPRISE_ID) VALUES
                                                                                    ('Nanine', 'Pea', 'npea0@ifeng.com', 1,1,2,1 ),
                                                                                    ('Sandie', 'Thorbon', 'sthorbon1@wufoo.com', 2,2,2,2),
                                                                                    ('Aleta', 'Bradbeer', 'abradbeer2@springer.com', 3,3,1,3);

insert into JOB (NAME, DATE_CREATED, STATUS_ID, ENTERPRISE_ID) values ('My-First-Apply', '2023-10-12 00:00:00.000', 2, 3);
insert into JOB (NAME, DATE_CREATED, STATUS_ID, ENTERPRISE_ID) values ('My-Second-Apply', '2023-10-13 23:59:00.000', 1, 3);
insert into JOB (NAME, DATE_CREATED, STATUS_ID, ENTERPRISE_ID) values ('My-Third-Apply', '2023-10-14 12:01:00.000', 1, 2);
