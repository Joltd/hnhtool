create table areas (
	id numeric(19,0) identity(1,1) primary key,
	space_id numeric(19,0)  foreign key references spaces(id),
	fromX int,
	fromY int,
	toX int,
	toY int
);