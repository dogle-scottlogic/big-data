DROP table client;

CREATE TABLE CLIENT(
    id VARCHAR(40) NOT NULL UNIQUE, 
    name VARCHAR(30) NOT NULL,
    address VARCHAR(120) NOT NULL,
    email VARCHAR(60),
    PRIMARY KEY (id)
);

