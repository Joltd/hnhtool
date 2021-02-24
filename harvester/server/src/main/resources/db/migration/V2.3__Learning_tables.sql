create table jobs (
	id integer unsigned auto_increment primary key,
	name varchar(255),
	enabled bit(1),
	type varchar(255)
);

alter table tasks add job_id integer unsigned;
alter table tasks add foreign key (job_id) references jobs(id);

create table learnings (
	id integer unsigned primary key,
	agent_id integer unsigned,
	area_id integer unsigned,
	foreign key (id) references jobs(id),
	foreign key (agent_id) references agents(id),
	foreign key (area_id) references areas(id)
);

create table learning_stats (
	id integer unsigned auto_increment primary key,
	date datetime,
	learning_id integer unsigned,
	task_id integer unsigned,
	learning_points integer,
	experience_points integer,
	mental_weights integer,
	foreign key (learning_id) references learnings(id),
	foreign key (task_id) references tasks(id)
);