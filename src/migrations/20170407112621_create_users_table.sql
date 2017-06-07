DROP TABLE IF EXISTS `users`;

CREATE TABLE users (
  id  Integer auto_increment PRIMARY KEY,
  username  VARCHAR(128),
  password  VARCHAR(128),
  puntaje int(11),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;
