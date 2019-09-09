insert into usuario (nome, email, senha) values ('Richard Marques', 'richard@seberino.com.br', '$2a$10$juryLMTLpx7FBDINCR1u2uhpZTGsa2PWSX1PQh2wIo2LZXjWaawW.');
insert into usuario (nome, email, senha) values ('aluno 2', 'alunbo2@seberino.com.br', '$2a$10$juryLMTLpx7FBDINCR1u2uhpZTGsa2PWSX1PQh2wIo2LZXjWaawW.');

insert into curso (nome, categoria) values ('HTML Básico', 'Desenvolvimento');
insert into curso (nome, categoria) values ('Java avançado', 'Desenvolvimento');

insert into topico(titulo, mensagem, data_criacao, status, autor_id, curso_id) values ('Dúvida 1', 'Estou com dúvidas para criar uma pagina simples', '2019-07-15','NAO_RESPONDIDO', 1, 1);
insert into topico(titulo, mensagem, data_criacao, status, autor_id, curso_id) values ('Aula 2 problema', 'Classe nao compila', '2019-07-18','NAO_RESPONDIDO', 2, 2);
insert into topico(titulo, mensagem, data_criacao, status, autor_id, curso_id) values ('JPA nao funciona', 'As dependencias nao resolvem', '2019-07-12','NAO_RESPONDIDO', 1, 2);
