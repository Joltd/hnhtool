drop table warehouse_cells;

create table warehouse_cells (
	id numeric(19,0) identity(1,1) primary key,
	warehouse_id numeric(19,0),
	x int,
	y int,
	container_id numeric(19,0)
);

alter table warehouse_cells add foreign key (warehouse_id) references warehouses(id);
alter table warehouse_cells add foreign key (container_id) references known_objects(id);