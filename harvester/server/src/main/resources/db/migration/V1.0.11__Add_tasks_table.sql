drop table agent_logs;

drop table accounts;

create table agents (
	id numeric(19,0) identity(1,1) primary key,
	username varchar(255),
	token varbinary(64),
	character varchar(255),
	status varchar(255),
	enabled tinyint,
	accident tinyint
);

create table tasks (
	id numeric(19,0) identity(1,1) primary key,
	agent_id numeric(19,0),
	actual datetime,
	status varchar(255),
	script varchar(255),
	log varchar(4000)
);

alter table tasks add foreign key (agent_id) references agents(id);