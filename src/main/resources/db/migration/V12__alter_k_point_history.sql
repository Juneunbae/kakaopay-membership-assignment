alter table k_point_history
    add total_point int not null after point;

alter table k_point_history
    modify action varchar (10) not null;