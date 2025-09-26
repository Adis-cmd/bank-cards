-- changelog Adis: 002 create user table
create table users(
    id                 bigserial primary key,
    name               varchar(100),
    surname            varchar(100),
    email              varchar(255) unique not null,
    password           varchar(255) not null,
    enabled            boolean not null default true,
    authorities_id     integer,

    constraint fk_user_authorities
            foreign key (authorities_id)
                references authorities (id)
                on delete restrict
                on update cascade
)