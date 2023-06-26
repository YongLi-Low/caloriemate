create database blobdb;

use blobdb;

create table users(
	id varchar(32),
    username varchar(256) not null,
    email varchar(256) not null,
	password varchar(256) not null,
    add bmi float,
    add calories int,
    add image longblob,
    constraint users_id primary key (id)
);

create table caloriestracker(
	cal_id int not null auto_increment,
    username varchar(256) not null,
    calories double not null,
    entryDate date not null,
    foodName varchar(64) not null,
    quantity double not null,
    user_id varchar(32),
    constraint pk_cal_id primary key (cal_id),
    constraint fk_users_id foreign key (user_id) references users(id)
);