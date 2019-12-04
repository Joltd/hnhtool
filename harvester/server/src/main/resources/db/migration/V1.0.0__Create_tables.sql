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
	character_name varchar(255)
);

create table resource_groups (
	id numeric(19,0) identity(1,1) primary key
)

create table resources (
	id numeric(19,0) identity(1,1) primary key,
	group_id numeric(19,0) foreign key references resource_groups(id),
	name varchar(255),
	unknown tinyint,
	player tinyint,
	prop tinyint,
	container tinyint,
	item tinyint
);

create table spaces (
	id numeric(19,0) identity(1,1) primary key,
	name varchar(255),
	type varchar(255)
);

create table known_objects (
	id numeric(19,0) identity(1,1) primary key,
	space_id numeric(19,0) foreign key references spaces(id),
	parent_id numeric(19,0),
	place varchar(255),
	resource_id numeric(19,0) foreign key references resources(id),
	actual datetime,
	lost tinyint,
	x int,
	y int
);

alter table known_objects add foreign key (parent_id) references known_objects(id)