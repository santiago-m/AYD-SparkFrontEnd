DROP TABLE IF EXISTS games;

CREATE TABLE games (
  id Int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  jugador1 Int,
  jugador2 Int,
  ganador Int,
  estado enum ('inactivo', 'en cola', 'activo', 'finalizado')
)ENGINE=InnoDB;
