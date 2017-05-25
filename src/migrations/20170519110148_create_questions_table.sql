DROP TABLE IF EXISTS `questions`;

CREATE TABLE questions (
  id  int(11) auto_increment PRIMARY KEY,
  leido  BIT,
  creador  VARCHAR(128),
  pregunta  VARCHAR(128),
  respuestaCorrecta  VARCHAR(128),
  wrong1  VARCHAR(128),
  wrong2  VARCHAR(128),
  wrong3  VARCHAR(128),
  active  BIT,
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;

INSERT INTO questions 
 (id, leido, creador, pregunta, respuestaCorrecta, wrong1, wrong2, wrong3, active) VALUES
 (1, 0, 'sajoma', '¿A cuantos grados hierve el agua?', '100°C', '80°C', '120°C', '150°C', 1),
 (2, 0, 'sajoma', '¿un elefante recien nacido, pesa mas o menos de 500 kilos?', 'Menos de 500Kg', 'Mas de 500Kg', NULL, NULL, 1),
 (3, 0, 'sajoma', '¿Que se mide con un nigrómetro?', 'La oscuridad del ambiente', 'La cantidad de pigmentos en la piel', 'La densidad de la tinta', NULL, 1),
 (4, 0, 'sajoma', '¿Como se llaman las fotografias obtenidas con la luz de un rayo laser?', 'Holografias', 'Fotolitos', 'Laserografias', NULL, 1),
 (5, 0, 'sajoma', 'Los crateres de la luna, ¿son volcanicos?', 'No', 'Si', NULL, NULL, 1),
 (6, 0, 'sajoma', '¿Cuantas vueltas alrededor del sol da la luna en un año?', 'Una', 'Dos y media', 'Dos', 'Media', 1);
