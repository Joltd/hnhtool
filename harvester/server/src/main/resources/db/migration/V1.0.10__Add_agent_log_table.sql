create table agent_logs (
	id numeric(19,0) identity(1,1) primary key,
	date datetime,
	account_id  numeric(19,0) foreign key references accounts(id),
	log varchar(max)
);