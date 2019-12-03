create table users (
    id numeric(19,0) identity(1,1) primary key,
    username varchar(255),
    password varchar(255)
);

insert into users (username, password) values ('root', '$2a$10$xFtCg53fRadQvBiLZ962TORMZzf/AFFgEVj3BweUJ5Q9LTPmLw9my');

create table resources (
	id numeric(19,0) identity(1,1) primary key,
	name varchar(255),
	unknown tinyint,
	player tinyint,
    object tinyint,
    doorway tinyint,
    container tinyint,
    heap tinyint,
    item tinyint
);

create table accounts (
	id numeric(19,0) identity(1,1) primary key,
	username varchar(255),
	token varbinary(64),
	character_name varchar(255)
);

create table spaces (
	id numeric(19,0) identity(1,1) primary key,
	name varchar(255),
	type varchar(255)
);

create table known_objects (
	id numeric(19,0) identity(1,1) primary key,
	owner_id numeric(19,0) foreign key references spaces(id),
	place varchar(255),
	resource_id numeric(19,0) foreign key references resources(id),
	x int,
	y int,
	actual datetime,
	lost tinyint
);

create table known_items (
	id numeric(19,0) identity(1,1) primary key,
	owner_id numeric(19,0) foreign key references known_objects(id),
	resource_id numeric(19,0) foreign key references resources(id),
	x int,
	y int,
	actual datetime,
	lost tinyint,
	name varchar(255),
	quality float
);