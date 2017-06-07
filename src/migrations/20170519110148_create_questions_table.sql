DROP TABLE IF EXISTS `questions`;

CREATE TABLE questions (
  id  Integer PRIMARY KEY NOT NULL auto_increment,
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
 (creador, pregunta, respuestaCorrecta, wrong1, wrong2, wrong3, active) VALUES
 ('sajoma', 'A cuantos grados hierve el agua?', '100°C', '80°C', '120°C', '150°C', 1),
 ('sajoma', 'un elefante recien nacido, pesa mas o menos de 500 kilos?', 'Menos de 500Kg', 'Mas de 500Kg', NULL, NULL, 1),
 ('sajoma', 'Que se mide con un nigrómetro?', 'La oscuridad del ambiente', 'La cantidad de pigmentos en la piel', 'La densidad de la tinta', NULL, 1),
 ('sajoma', 'Como se llaman las fotografias obtenidas con la luz de un rayo laser?', 'Holografias', 'Fotolitos', 'Laserografias', NULL, 1),
 ('sajoma', 'Los crateres de la luna, ¿son volcanicos?', 'No', 'Si', NULL, NULL, 1),
 ('sajoma', 'Cuantas vueltas alrededor del sol da la luna en un año?', 'Una', 'Dos y media', 'Dos', 'Media', 1),
 ('sajoma', 'Que se combate tomando piramidón?', 'La fiebre', 'La sarna', 'La hepatitis', NULL, 1),
 ('sajoma', 'Los aminoácidos contienen proteínas o las proteínas contienen aminoácidos?', 'Las proteínas contienen aminoácidos', 'Los aminoácidos contienen proteínas', NULL, NULL, 1),
 ('sajoma', 'Quien dijo: "Es mas fácil lograr una gran obra a partir de un tema pequeño"', 'Leonardo Da Vinci', 'Monet', 'Durero', NULL, 1),
 ('sajoma', 'Cual fue el primer grupo de Rock Nacional en grabar un CD (Compact-Disk)', 'Soda Stereo', 'Los pericos', 'Pabellón Psiquiátrico', NULL, 1),
 ('sajoma', 'Como se llama ahora, la antigüa nota "ut"', 'Do', 'Mi', 'Fa', 'Si', 1),
 ('sajoma', 'En un dado, que número está en el lado opuesto al 1?', '6', '3', '2', '4', 1),
 ('sajoma', 'En Boxeo, que peso hay entre el pesado y el semi-pesado?', 'Crucero', 'Mediano', 'Tres cuartos-pesado', 'ninguno', 1),
 ('sajoma', 'Cuanto vale un envido no querido en un partido de truco?', 'Un punto', 'Nada', 'Dos Puntos', 'Tres Puntos', 1),
 ('sajoma', 'Cuantos jugadores integran un equipo de Basquet?', 'Cinco', 'Siete', 'Ocho', 'Diez', 1),
 ('sajoma', 'De que color es la vestimenta tradicional de un gimnasta?', 'Blanca', 'Negra', 'Celeste o Azul', 'Negra y blanca', 1),
 ('sajoma', 'Si en Cuba lo invitan a ver un partido de pelota, que deporte lo llevan a ver?', 'Baseball', 'Football', 'Basquet', 'Tenis de Mesa', 1),
 ('sajoma', 'Cuantos cambios de jugadores pueden hacerse durante un partido de Basquet?', 'Los que se desee', 'Hasta 5', 'Hasta 2', 'Hasta 3', 1),
 ('sajoma', 'Como se llama el Yo-Yo en Estados Unidos?', 'Yo-Yo', 'Me-Me', 'I-I', 'SwingBack', 1),
 ('sajoma', 'Que mide un barómetro', 'La presión del aire', 'La densidad de un fluido', 'Distancias Milimétricas', NULL, 1),
 ('sajoma', 'Históricamente, el paraguas deriva de la sombrilla o la sombrilla deriva del paraguas?', 'El paraguas deriva de la sombrilla', 'La sombrilla deriva del paraguas', NULL, NULL, 1),
 ('sajoma', 'El primer "bebé de probeta", fue nena o varón?', 'Nena', 'Varón', NULL, NULL, 1),
 ('sajoma', 'Cuantos comensales cabían en la mesa redonda del Rey Arturo?', 'Ciento Cincuenta', 'Doce', 'Nueve', 'Tres', 1),
 ('sajoma', 'Si un señor es "provecto", entonces es: ', 'Viejo', 'Avaro', 'Pecador', NULL, 1),
 ('sajoma', 'Que célebre personaje de ficción dijo que "donde la imaginación está ausente, no hay horror posible"?', 'Sherlock Holmes', 'Drácula', 'Hamlet', NULL, 1);
