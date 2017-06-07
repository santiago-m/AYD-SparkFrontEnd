DROP TABLE IF EXISTS respondidas;

CREATE TABLE respondidas (
  usuario Integer,
  pregunta Integer,
  PRIMARY KEY (usuario, pregunta)
)ENGINE=InnoDB;
