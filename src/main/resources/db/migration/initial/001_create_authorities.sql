-- changelog Adis: 001 create authority table
create table authorities(
    id bigserial primary key,
    name varchar(55) not null unique
);