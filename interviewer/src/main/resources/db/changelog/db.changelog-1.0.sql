-- changeset e.khodosov:1
create type role as enum ('INTERVIEWER', 'HR', 'MANAGER');
create table users(
    login varchar(24) primary key not null,
    pass text not null,
    role role not null
);
insert into users (login, pass, role) values ('admin', '$2a$10$8uVBgBnJxOXXZhTXIKyioOkrNXltUUzOP7zS/gHhtvDfDkUMTY1T.', 'INTERVIEWER');