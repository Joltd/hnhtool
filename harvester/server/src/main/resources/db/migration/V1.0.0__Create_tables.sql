create table users (
    id numeric(19,0) identity(1,1) primary key,
    username varchar(255),
    password varchar(255)
);

insert into users (username, password) values ('root', '$2a$10$xFtCg53fRadQvBiLZ962TORMZzf/AFFgEVj3BweUJ5Q9LTPmLw9my');

create table spaces (
	id numeric(19,0) identity(1,1) primary key,
	name varchar(255),
	type varchar(255)
);

create table known_objects (
	id numeric(19,0) identity(1,1) primary key,
	owner_id numeric(19,0) foreign key references spaces(id),
	resource varchar(255),
	x int,
	y int,
	actual datetime,
	researched tinyint,
	player tinyint,
	doorway tinyint,
	container tinyint,
	count int,
	max int
);

create table accounts (
	id numeric(19,0) identity(1,1) primary key,
	username varchar(255),
	token varbinary(64),
	default_character varchar(255),
	character_object_id numeric(19,0) foreign key references known_objects(id)
);

create table paths (
	id numeric(19,0) identity(1,1) primary key,
	from_id numeric(19,0) foreign key references known_objects(id),
	to_id numeric(19,0) foreign key references known_objects(id),
	distance float
);

create table known_items (
	id numeric(19,0) identity(1,1) primary key,
	owner_id numeric(19,0) foreign key references known_objects(id),
	parent_id numeric(19,0) foreign key references known_items(id),
	resource varchar(255),
	x int,
	y int,
	actual datetime,
	name varchar(255),
	quality float,
	food tinyint,
	weapon tinyint,
	curiosity tinyint
);