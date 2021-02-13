create table agents (
	id integer unsigned auto_increment primary key,
	username varchar(255),
	token varchar(64),
	`character` varchar(255),
	status varchar(255),
	enabled bit(1),
	accident bit(1)
);

create table spaces (
	id integer unsigned auto_increment primary key,
	name varchar(255),
	type varchar(255)
);

create table preferences (
	id integer unsigned auto_increment primary key,
	space_id integer unsigned,
	offsetX integer,
	offsetY integer,
	zoom integer,
	foreign key (space_id) references spaces(id)
);

create table areas (
	id integer unsigned auto_increment primary key,
	space_id integer unsigned,
	fromX integer,
	fromY integer,
	toX integer,
	toY integer,
	foreign key (space_id) references spaces(id)
);

create table resource_groups (
	id integer unsigned auto_increment primary key
);

create table resource_contents (
	id integer unsigned auto_increment primary key,
	data blob
);

create table resources (
	id integer unsigned auto_increment primary key,
	group_id integer unsigned,
	content_id integer unsigned,
	name varchar(255),
	visual varchar(255),
	unknown bit(1),
	player bit(1),
	prop bit(1),
	box bit(1),
	heap bit(1),
	item bit(1),
	x integer,
	y integer,
	foreign key (group_id) references resource_groups(id),
	foreign key (content_id) references resource_contents(id)
);

create table known_objects (
	id integer unsigned auto_increment primary key,
	space_id integer unsigned,
	parent_id integer unsigned,
	place varchar(255),
	resource_id integer unsigned,
	actual datetime,
	lost bit(1),
	x integer,
	y integer,
	invalid bit(1),
	foreign key (space_id) references spaces(id),
	foreign key (parent_id) references known_objects(id),
	foreign key (resource_id) references resources(id)
);

create table tasks (
	id integer unsigned auto_increment primary key,
	agent_id integer unsigned,
	actual datetime,
	status varchar(255),
	script varchar(255),
	fail_reason varchar(255),
	log varchar(255),
	foreign key (agent_id) references agents(id)
);

create table users (
	id integer unsigned auto_increment primary key,
	username varchar(255),
	password varchar(255)
);

create table warehouses (
	id integer unsigned auto_increment primary key,
	space_id integer unsigned,
	foreign key (space_id) references spaces(id)
);

create table warehouse_cells (
	id integer unsigned auto_increment primary key,
	warehouse_id integer unsigned,
	container_id integer unsigned,
	x integer,
	y integer,
	foreign key (warehouse_id) references warehouses(id)
);
