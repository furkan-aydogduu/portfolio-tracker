drop table if exists portfolio;

create sequence portfolio_seq;

create table portfolio (
    id bigint not null default portfolio_seq.nextval PRIMARY KEY ,
    crypto_currency_name varchar(250) not null,
    amount_purchased decimal(20,5) not null,
    wallet_location varchar(250) not null,
    market_value_at_purchased_time decimal(20,5) not null,
    creation_time timestamp not null,
    updated_time timestamp not null
);