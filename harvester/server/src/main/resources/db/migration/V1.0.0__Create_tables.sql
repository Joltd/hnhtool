create table users (
    id numeric(19,0) identity(1,1) primary key,
    username varchar(255),
    password varchar(255)
);

insert into users (username, password) values ('root', '$2a$10$xFtCg53fRadQvBiLZ962TORMZzf/AFFgEVj3BweUJ5Q9LTPmLw9my');

create table accounts (
	id numeric(19,0) identity(1,1) primary key,
	username varchar(255),
	token varbinary(64),
	defaultCharacter varchar(255)
);

create table spaces (
	id numeric(19,0) identity(1,1) primary key
	name varchar(255),
	type varchar(255)
);

create table known_objects (
	id numeric(19,0) identity(1,1) primary key,
	owner_id numeric(19,0) foreign key references spaces(id),
	x int,
	y int,
	doorway tinyint,
	container tinyint,
	space_from_id numeric(19,0) foreign key references spaces(id),
	space_to_id numeric(19,0) foreign key references spaces(id)
);

create table known_items (
	id numeric(19,0) identity(1,1) primary key,
	owner_id numeric(19,0) foreign key references known_objects(id),
	name varchar(255),
	quality float,
	food tinyint,
	weapon tinyint,
	curiosity tinyint
);