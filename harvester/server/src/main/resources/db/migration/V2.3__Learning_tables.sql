create table jobs (
	id integer unsigned auto_increment primary key,
	name varchar(255),
	enabled bit(1),
	type varchar(255)
);

alter table tasks add job_id integer unsigned;
alter table tasks add foreign key (job_id) references jobs(id);

create table learnings (
	id integer unsigned auto_increment primary key,
	job_id integer unsigned,
	agent_id integer unsigned,
	area_id integer unsigned,
	foreign key (job_id) references jobs(id),
	foreign key (agent_id) references agents(id),
	foreign key (area_id) references areas(id)
);

create table learning_stats (
	id integer unsigned auto_increment primary key,
	date datetime,
	agent varchar(255),
	task_id integer unsigned,
	learningPoints integer,
	experiencePoints integer,
	mentalWeights integer,
	foreign key (task_id) references tasks(id)
);