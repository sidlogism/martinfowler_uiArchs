USE mysql;

-- create new application-level user "uiuser" if it doesn't exists already in order to run the application with less privileges
-- MySQL 8.0 Reference Manual > SQL statements > Database Administration Statements > Account Management Statements
-- see https://dev.mysql.com/doc/refman/8.0/en/create-user.html
CREATE USER IF NOT EXISTS 'uiuser'@'localhost' IDENTIFIED BY 'XXXXX';
-- ALTER USER 'uiuser'@'localhost' IDENTIFIED BY 'XXXXXX'

-- print changes
-- see https://mariadb.com/kb/en/mysqluser-table/
SELECT user,host,super_priv,authentication_string FROM mysql.user;


-- grant new "uiuser" rights for new db "martinfowler_uiArchs"
GRANT SELECT ON martinfowler_uiArchs.* TO uiuser@localhost;
GRANT INSERT ON martinfowler_uiArchs.* TO uiuser@localhost;
GRANT UPDATE ON martinfowler_uiArchs.* TO uiuser@localhost;
GRANT LOCK TABLES ON martinfowler_uiArchs.* TO uiuser@localhost;
-- print changes
SHOW GRANTS FOR uiuser@localhost;
