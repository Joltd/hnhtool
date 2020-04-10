alter table accounts add enabled tinyint
go

-- noinspection SqlWithoutWhere

update accounts set enabled = 1
go
