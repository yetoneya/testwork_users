## vk user data loader

### Подготовка

Установить PostgreSQL

Создать пользователя <user> c паролем <password>:

    postgres=# create user <user> with password '<password>';

Создать базу данных <dbname>:

    postgres=# create database <dbname>;

Дать права пользователю <user> на базу данных <dbname>:

    postgres=# grant all privileges on database <dbname> to <user>;

Создать в базе данных <dbname> таблицу vk_user:

    postgres=# \c testdb

    create table if not exists vk_user (
    id serial primary key,
    user_id integer unique not null,
    user_f_name varchar(200),
    user_l_name varchar(200),
    user_b_date varchar(20),
    user_city varchar(50),
    user_contacts varchar(100)
    );

Дать права пользователю <user> на таблицу vk_user:

    testdb=# grant all privileges on table vk_user to "<user>";

Заданные значения прописать в файле application.properties:

    db.user = <user>
    db.password = <password>
    db.name = <dbname>
    pool.size = <количество потоков для подключения к базе данных>

В application.properties определить свойства:

server.name

exel.directory - директория, в которую будет помещен конечный результат

Заполнить в таблице колонку user_id, например:

    insert into vk_user (user_id)
    values
    (76836116),
    (76938319),
    (76749736)
    ON CONFLICT (user_id) DO NOTHING;


Создать приложение vk, в файле application.properties установить свойства:

    access.token = <сервисный ключ из приложения>
    api.id = <id приложения>

чтобы запустить: установить java, собрать jar файл, в директории с файлом vk-users.jar выполнить команду:

    java -jar vk-users.jar
