create sequence gen.hibernate_sequence start 1 increment 1;

create table if not exists gen.comment (id int8 not null,
                                        placed_at timestamp not null,
                                        text varchar(1000) not null,
                                        parent_comment_id int8,
                                        topic_id int8 not null,
                                        user_id int8,
                                        primary key (id));

create table gen.like (id int8 not null,
                       comment_id int8 not null,
                       user_id int8 not null,
                       primary key (id));

create table if not exists gen.role (id int8 not null,
                                     color int4 not null check (color>=0 AND color<=16777215),
                                     name varchar(20) not null,
                                     primary key (id));

create table if not exists gen.section (id int8 not null,
                                        name varchar(100) not null,
                                        placed_at timestamp not null,
                                        primary key (id));

create table if not exists gen.tag (id int8 not null,
                                    name varchar(20) not null,
                                    primary key (id));

create table if not exists gen.topic (id int8 not null,
                                      name varchar(100) not null,
                                      placed_at timestamp not null,
                                      views int8 not null,
                                      section_id int8 not null,
                                      user_id int8,
                                      primary key (id));

create table if not exists gen.topic_tag (tag_id int8 not null,
                                          topic_id int8 not null,
                                          primary key (tag_id, topic_id));

create table if not exists gen.user_info (id int8 not null,
                                          avatar bytea,
                                          email varchar(255) not null,
                                          information varchar(1000),
                                          non_locked boolean not null,
                                          password varchar(255) not null,
                                          registration_date date not null,
                                          username varchar(20) not null,
                                          role_id int8,
                                          primary key (id));

alter table if exists gen.role
    add constraint UK_8sewwnpamngi6b1dwaa88askk unique (name);

alter table if exists gen.tag
    add constraint UK_1wdpsed5kna2y38hnbgrnhi5b unique (name);

alter table if exists gen.user_info
    add constraint UK_f2ksd6h8hsjtd57ipfq9myr64 unique (username);

alter table if exists gen.like
    add constraint UQ_UserId_CommentID unique(user_id, comment_id);

alter table if exists gen.comment
    add constraint FKhvh0e2ybgg16bpu229a5teje7
        foreign key (parent_comment_id) references gen.comment
            ON UPDATE CASCADE
            ON DELETE CASCADE;

alter table if exists gen.comment
    add constraint FKo3bvevu9ua4w6f8qu2b177f16
        foreign key (topic_id) references gen.topic
            ON UPDATE CASCADE
            ON DELETE CASCADE;

alter table if exists gen.comment
    add constraint FKcs3mffo2nicys344xkiymy9u1
        foreign key (user_id) references gen.user_info
            ON UPDATE CASCADE
            ON DELETE SET NULL;

alter table if exists gen.like
    add constraint FKk85todbpuuo0emfdaftw1rnu3
        foreign key (comment_id) references gen.comment
            ON UPDATE CASCADE
            ON DELETE CASCADE;

alter table if exists gen.like
    add constraint FKtm9m6en8fet445r24aookwsab
        foreign key (user_id) references gen.user_info
            ON UPDATE CASCADE
            ON DELETE CASCADE;

alter table if exists gen.topic
    add constraint FKah1j9hn2hy1kb0i7ehpcs2ahk
        foreign key (section_id) references gen.section
            ON UPDATE CASCADE
            ON DELETE CASCADE;

alter table if exists gen.topic
    add constraint FKkx9emv4if8hmxv8ncoefegf9q
        foreign key (user_id) references gen.user_info
            ON UPDATE CASCADE
            ON DELETE SET NULL;

alter table if exists gen.topic_tag
    add constraint FKqvqmd2eomy749a9uei56n71rd
        foreign key (topic_id) references gen.topic
            ON UPDATE CASCADE
            ON DELETE CASCADE;

alter table if exists gen.topic_tag
    add constraint FKghqd3bga4hj3pklcwmgw36f9l
        foreign key (tag_id) references gen.tag
            ON UPDATE CASCADE
            ON DELETE CASCADE;

alter table if exists gen.user_info
    add constraint FKgatgpi3b28ljsw6bo16jrvykb
        foreign key (role_id) references gen.role
            ON UPDATE CASCADE
            ON DELETE SET NULL;