-- changeset e.khodosov:1
create type role as enum ('INTERVIEWER', 'HR', 'MANAGER');
create table users(
    login varchar(24) primary key not null,
    pass varchar(64) not null,
    fio varchar(64) not null,
    role role not null
);
insert into users (login, pass, fio, role) values ('admin', '$2a$10$8uVBgBnJxOXXZhTXIKyioOkrNXltUUzOP7zS/gHhtvDfDkUMTY1T.', 'Админов Админ Админович', 'INTERVIEWER');