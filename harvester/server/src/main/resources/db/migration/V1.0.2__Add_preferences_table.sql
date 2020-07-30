create table preferences (
	id numeric(19,0) identity(1,1) primary key,
	space_id numeric(19,0),
	offsetX int,
	offsetY int,
	zoom int
)