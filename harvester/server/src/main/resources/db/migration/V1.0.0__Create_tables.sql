create table users (
    id numeric(19,0) identity(1,1) primary key,
    username varchar(255),
    password varchar(255)
);

insert into users (username, password) values ('root', '$2a$10$xFtCg53fRadQvBiLZ962TORMZzf/AFFgEVj3BweUJ5Q9LTPmLw9my');

create table resources (
	id numeric(19,0) primary key,
	name varchar(255)
);

create table spaces (
	id numeric(19,0) identity(1,1) primary key,
	name varchar(255),
	type varchar(255)
);

create table known_objects (
	id numeric(19,0) identity(1,1) primary key,
	owner_id numeric(19,0) foreign key references spaces(id),
	resource_id numeric(19,0),
	x int,
	y int,
	actual datetime,
	player tinyint,
	doorway tinyint,
	container tinyint
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
	name varchar(255),
	quality float,
	food tinyint,
	weapon tinyint,
	curiosity tinyint
);

create table tasks (
	id numeric(19,0) identity(1,1) primary key,
	module varchar(255),
	step varchar(255),
	status varchar(255),
	fail_reason varchar(255)
);