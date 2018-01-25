insert into userz (id, username, password, enabled, LASTPASSWORDRESETDATE, created_date, modified_date, created_by) VALUES
(1, 'admin','$2a$10$jXpPejCd3B38GtdKBDTabuWmIIcfeEuwnOkgyJTRur1Bz7JLegKuK', TRUE , '2018-01-01 00:00:00', 1516904239560,1516904239560, 'default');
insert into AUTHORITY (id, name) VALUES
(1, 'ROLE_ADMIN');
insert into USER_AUTHORITY (USER_ID, AUTHORITY_ID) VALUES
(1, 1);