create table k_point_history
(
    id                bigint auto_increment               primary key,
    barcode           varchar(10)                         not null,
    store_category_id bigint                              not null,
    point             int                                 null,
    action            varchar(5)                          not null,
    store_id          bigint                              not null,
    created_at        timestamp default current_timestamp not null
);