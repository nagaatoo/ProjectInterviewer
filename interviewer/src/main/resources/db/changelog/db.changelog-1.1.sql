-- changeset e.khodosov:2

create table if not exists templates
(
    id      uuid primary key default gen_random_uuid(),
    version integer not null default 1,
    name    varchar(32),
    owner   varchar(24)
);

create table if not exists questionnaires
(
    id          uuid primary key default gen_random_uuid(),
    version     integer not null default 1,
    author      varchar(24),
    name        varchar(128),
    description text
);

create table if not exists rooms
(
    id      uuid primary key default gen_random_uuid(),
    version integer not null default 1
);

create table if not exists interviews
(
    id                uuid primary key      default gen_random_uuid(),
    version           integer      not null default 1,
    started           boolean      not null default false,
    finished_date     timestamp,
    name              varchar(128) not null,
    date              timestamp    not null,
    created_date      timestamp    not null,
    template_id       uuid references templates (id),
    questionnaire_id  uuid references questionnaires (id),
    solution          text,
    interviewer_login varchar(24),
    hr_login          varchar(24),
    room_id           uuid references rooms (id),
    owner             varchar(24)
);

create type element_type as enum ('QUESTION', 'TEXT', 'CODE');

create table if not exists builder_items
(
    id                  uuid primary key default gen_random_uuid(),
    element_type        element_type not null,
    element_description text,
    element_value       text, -- значение зависит от типа и обрабатывается кодом
    questionnaire_id    uuid references questionnaires (id),
    template_id         uuid references templates (id),
    created             timestamp    not null
);

create table if not exists history_builder_items
(
    id                  uuid primary key default gen_random_uuid(),
    element_type        element_type not null,
    element_description text,
    element_value       text, -- значение зависит от типа и обрабатывается кодом
    element_order       integer      not null,
    interview_id        uuid references interviews (id),
    questionnaire_id    uuid references questionnaires (id)
);

create table if not exists edit_lock
(
    id               uuid primary key default gen_random_uuid(),
    questionnaire_id uuid references questionnaires (id),
    template_id      uuid references templates (id),
    start_edit       timestamp not null,
    unlock_edit      timestamp not null,
    unique (questionnaire_id),
    unique (template_id)
);