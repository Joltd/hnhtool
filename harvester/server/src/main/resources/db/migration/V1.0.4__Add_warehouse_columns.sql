alter table warehouses add space_id numeric(19,0)
go

alter table warehouses add foreign key (space_id) references spaces(id)
go