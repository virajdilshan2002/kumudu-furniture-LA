
create database KUMUDU_FURNITURE;

use KUMUDU_FURNITURE;


create table customer(
	cusId varchar(10) primary key,
	cusName varchar(20) not null,
	cusAddress varchar(50) not null,
	cusEmail varchar(50),
	cusContact varchar(15) not null
);

create table furniture(
	furnId varchar(10) primary key,
	imageFile mediumblob,
	furnDescription varchar(20) not null,
	furnWoodType varchar(20) not null,
	furnColor varchar(20) not null,
	furnPrice decimal(10) not null,
	furnQty int(5)
);

create table orders(
	orderId varchar(10) primary key,
	cusId varchar(10) not null,
	orderDate date not null,
	paymentType varchar(20) not null,
	advancePayment decimal(10),
	totalPayment decimal(10) not null,
	foreign key(cusId) references customer(cusId) on update cascade on delete cascade
);

create table orderDetail(
	orderId varchar(10),
	furnId varchar(10),
	qty int(5),
	unitPrice decimal(10) not null,
	foreign key(orderId) references orders(orderId) on update cascade on delete cascade,
	foreign key(furnId) references furniture(furnId) on update cascade on delete cascade
);

create table supplier(
	supId varchar(10) primary key,
	supName varchar(20) not null,
	supDescription varchar(50) not null,
	supAddress varchar(50) not null,
	supContact varchar(15)
);

create table newArrivalDetail(
	supId varchar(10),
	furnId varchar(10),
	qty int(5),
	unitPrice decimal(10) not null,
	foreign key(supId) references supplier(supId) on update cascade on delete cascade,
	foreign key(furnId) references furniture(furnId) on update cascade on delete cascade
);

create table customOrders(
	cusOrderId varchar(10) primary key,
	cusId varchar(10) not null,
	orderDate date not null,
	paymentType varchar(20) not null,
	advancePayment decimal(10),
	TotalPayment decimal(10) not null,
	foreign key(cusId) references customer(cusId) on update cascade on delete cascade
);

create table customFurniture(
	cusFurnId varchar(10) primary key,
    cusFmageFile mediumblob,
    cusFurnDescription varchar(20) not null,
    cusFurnWoodType varchar(20) not null,
    cusFurnColor varchar(20) not null,
    cusFurnPrice decimal(10) not null,
    cusFurnQty int(5)
);

create table customOrderDetail(
	cusOrderId varchar(10),
	cusFurnId varchar(10),
	qty int(5),
	unitPrice decimal(10),
	foreign key(cusOrderId) references customOrders(cusOrderId) on update cascade on delete cascade,
	foreign key(cusFurnId) references customFurniture(cusFurnId) on update cascade on delete cascade
);

create table credential(
	userName varchar(10) primary key,
	fullName varchar(50) not null,
	email varchar(50) not null,
	password varchar(10) not null
);
