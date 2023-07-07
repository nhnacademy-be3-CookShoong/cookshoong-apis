INSERT INTO authorities (authority_code, description) VALUES ('ADMIN', '시스템관리자');
INSERT INTO authorities (authority_code, description) VALUES ('BUSINESS', '사업자회원');
INSERT INTO authorities (authority_code, description) VALUES ('CUSTOMER', '일반회원');

INSERT INTO accounts_status (status_code, description) VALUES ('ACTIVE', '활성');
INSERT INTO accounts_status (status_code, description) VALUES ('DORMANCY', '휴면');
INSERT INTO accounts_status (status_code, description) VALUES ('WITHDRAWAL', '탈퇴');

INSERT INTO ranks (rank_code, name) VALUES ('LEVEL_1', '프랜드');
INSERT INTO ranks (rank_code, name) VALUES ('LEVEL_2', '패밀리');
INSERT INTO ranks (rank_code, name) VALUES ('LEVEL_3', '마스터');
INSERT INTO ranks (rank_code, name) VALUES ('LEVEL_4', 'VIP마스터');
