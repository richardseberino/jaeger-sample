use contas;
create table conta_corrente (id_conta int not null auto_increment primary key, agencia int not null, conta int not null, bloqueada boolean, saldo decimal (15,2) not null, titular varchar(150));
create table senha (id int not null primary key, senha varchar(100));
insert into conta_corrente (agencia,conta,bloqueada, saldo, titular) values ( 10,10,false, 100000, 'Richard Marques');
insert into conta_corrente (agencia,conta,bloqueada, saldo, titular) values ( 10,11,false, 200000, 'Rafael Camilo');
insert into conta_corrente (agencia,conta,bloqueada, saldo, titular) values ( 10,12,false, 500000, 'Jean Anami');
insert into conta_corrente (agencia,conta,bloqueada, saldo, titular) values ( 10,13,false, 100000, 'Marcelo Dias');

insert into senha  select id_conta, '123456' from conta_corrente;
