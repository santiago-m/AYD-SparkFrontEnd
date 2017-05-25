DROP TABLE IF EXISTS `admins`;

CREATE TABLE admins (
  id  int(11) auto_increment PRIMARY KEY,
  username  VARCHAR(128),
  password  VARCHAR(128),
  puntaje int(11)
)ENGINE=InnoDB;

INSERT INTO admins (id, username, password, puntaje) VALUES
 (1, 'sajoma', '13794682', 0),
 (2, 'matesuiso', 'asd', 0);
