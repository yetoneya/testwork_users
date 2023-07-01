postgres=# create user testuser with password 'testpassword';
postgres=# create database testdb;
postgres=# grant all privileges on database testdb to testuser;
postgres=# \c testdb
testdb=# grant all privileges on table vk_user to "testuser";

create table if not exists vk_user (
id serial primary key,
user_id integer unique not null,
user_f_name varchar(200),
user_l_name varchar(200),
user_b_date date,
user_city varchar(50),
user_contacts varchar(100)
);

insert into vk_user (user_id)
values
(76436116),
(76436117),
(76436119),
(764477761);
