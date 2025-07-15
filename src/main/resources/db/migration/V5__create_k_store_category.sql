create table k_store_category
(
    id         bigint auto_increment               primary key,
    name       varchar(50)                         not null,
    created_at timestamp default current_timestamp not null,
    created_by bigint                              null,
    updated_at timestamp                           null,
    updated_by bigint                              null
);