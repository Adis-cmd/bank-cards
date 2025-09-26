-- changelog Adis: 003 create card table
create table cards(
    id                 bigserial primary key,
    balance numeric(15,2) not null default 0.00,
    owner_id           integer,
    card_number varchar(16) unique not null,
    expiration_date    timestamp default current_date,

    constraint fk_card_owner
                foreign key (owner_id)
                    references users (id)
                    on delete restrict
                    on update cascade
);