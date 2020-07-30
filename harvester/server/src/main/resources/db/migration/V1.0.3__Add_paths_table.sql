create table paths (
	id numeric(19,0),
	space_id numeric(19,0),
	fromX int,
	fromY int,
	toX int,
	toY int
);

alter table paths add foreign key (space_id) references spaces(id);
