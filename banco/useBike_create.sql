-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2017-09-04 12:46:21.139

-- tables
-- Table: bks_bike
CREATE TABLE bks_bike (
    bks_id integer NOT NULL CONSTRAINT bks_bike_pk PRIMARY KEY,
    bks_identificacao varchar(20),
    usr_id integer NOT NULL,
    bks_latitude varchar(30),
    bks_lagitude varchar(30),
    bks_disponivel boolean,
    CONSTRAINT bks_bike_usr_usuario FOREIGN KEY (usr_id)
    REFERENCES usr_usuario (usr_id)
);

-- Table: usr_usuario
CREATE TABLE usr_usuario (
    usr_id integer NOT NULL CONSTRAINT usr_usuario_pk PRIMARY KEY,
    usr_username varchar(50),
    usr_email varchar(100),
    usr_password varchar(20),
    usr_dt_nascimento datetime
);

-- End of file.

