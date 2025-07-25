alter table k_point_history
    add store_name varchar(50) null after store_id;

alter table k_point_history
    modify category varchar(20) not null after store_name;