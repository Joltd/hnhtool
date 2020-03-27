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

create table resource_contents (
	id numeric(19,0) identity(1,1) primary key,
	data varbinary(max)
);

create table resources (
	id numeric(19,0) identity(1,1) primary key,
	group_id numeric(19,0) foreign key references resource_groups(id),
	content_id numeric(19,0) foreign key references resource_contents(id),
	name varchar(255),
	visual varchar(255),
	unknown tinyint,
	player tinyint,
	prop tinyint,
	box tinyint,
	heap tinyint,
	item tinyint,
	x int,
	y int
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

alter table known_objects add foreign key (parent_id) references known_objects(id);

create table warehouses (
	id numeric(19,0) identity(1,1) primary key
);

create table warehouse_cells (
	id numeric(19,0),
	warehouse_id numeric(19,0),
	x int,
	y int,
	container_id numeric(19,0)
);

alter table warehouse_cells add foreign key (warehouse_id) references warehouses(id);
alter table warehouse_cells add foreign key (container_id) references known_objects(id);