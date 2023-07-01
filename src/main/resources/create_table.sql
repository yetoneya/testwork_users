create table if not exists vk_user (
id serial primary key,
user_id integer unique not null,
user_f_name varchar(200),
user_l_name varchar(200),
user_b_date date,
user_city varchar(50),
user_contacts varchar(100)
);