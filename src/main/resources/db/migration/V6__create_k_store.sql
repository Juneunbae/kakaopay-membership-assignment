create table k_store
(
    id                bigint auto_increment                 primary key,
    store_category_id bigint                                not null,
    name              varchar(50)                           not null,
    created_at        timestamp   default current_timestamp not null,
    created_by        bigint                                null,
    updated_at        timestamp                             null,
    updated_by        bigint                                null
);