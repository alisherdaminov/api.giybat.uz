CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

insert into profile(id,name,username,password,status,visible,created_date)
values (1,'Admin',
'alisherdaminov135@gmail.com',
"$2a$10$a0TCTofobwGVlLv1mnxeVeyORsZ535TWuIkj1JgQnQ.81IpZ40o5i","ACTIVE",true,now());

SELECT setval('profile_id_seq', max(id)) FROM profile;

insert into profile_role_entity(profile_id,roles,created_date)
values (1,"ROLE_USER",now()),
(1,"ROLE_ADMIN",now());