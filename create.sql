DROP DATABASE IF EXISTS PeDri;
CREATE DATABASE PeDri;
USE PeDri;

CREATE TABLE  user(
	username varchar(30),
	password varchar(30),
    age  int(2),
    name varchar(40),
    contact_number varchar(20),
    address varchar(30),
    identity varchar(8),
    verifProf varchar(15),
	PRIMARY KEY(username)
);
CREATE TABLE  owner(
	username_owner varchar(30),
	password_owner varchar(30),
    age  int(2),
    name varchar(40),
    contact_number varchar(20),
    address varchar(30),
    identity varchar(8),
	verifProf varchar(15),
    business varchar(20),
	PRIMARY KEY(username_owner)
);
/*
CREATE TABLE  Services(
	username_owner varchar(20),
    services_name varchar(20),
    current_price double
);*/

/*CREATE TABLE  CarPoolPassenger(
	starting_point varchar(20),
	final_destination varchar(20)
);*/
CREATE TABLE  StartingEndPoint(
	starting_point varchar(20),
	final_destination varchar(20)
);

CREATE TABLE  CarPoolDriver(
    username_driver varchar(30),
    years_driving int,
	starting_point varchar(20),
	final_destination varchar(20),
    car_name varchar(30),
    date_route int,
    month_route varchar(10),
    year_route int,
    cost_route double,
    comments text,
    max_number_passenger int,
    route_again varchar(20),
    start_time varchar(10)
);

CREATE TABLE  CarPoolPassRequest(
	name_passenger varchar(30),
    car_name varchar(20),
    number_passenger int,
    number_luggage int
);
/*CREATE TABLE  CarPoolDriverRequest(
	address_departure_point varchar(40),
    endpoint_address varchar(40),
    date varchar(20),
    start_time varchar(10),
    max_number_passenger int,
    cost_route double,
    route_again varchar(10),
    car_name_dr varchar(20)
);*/

/*CREATE TABLE  RoutesPosts(
	address_departure_point varchar(40),
    endpoint_address varchar(40),
    date varchar(20),
    start_time varchar(10),
    max_number_passenger int,
    cost_route double,
    route_again varchar(10),
    car_name_dr varchar(20)
);*/


CREATE TABLE  cars(
	name_car varchar(20),
    location_car varchar(30),
    cars_rental_company varchar(20),
    phone_number varchar(20),
    date_car int,
    month_car varchar(10),
    year_car int,
    car_cost_per_day int,
    fuel varchar(10),
    consumption_per_100km varchar(10),
    emissions varchar(20),
    max_speed int,
    number_of_seats int
);

CREATE TABLE  PickUpVehicle(
	pickUp_loc varchar(30),
    pickUp_time varchar(10)
);
CREATE TABLE  carRentalRequest(
	name_driver varchar(30),
    age_driver int,
    duration_rent int,
    name_car varchar(30),
    license_duration int
);

CREATE TABLE Parking (
    name_parking VARCHAR(20),
    location_parking VARCHAR(30),
    phone_number varchar(10),
    address varchar(30),
    max_number_seat_parking int,
    remaining_parking_spaces int,
    parking_cost_per_hour varchar(10),
    available_times VARCHAR(255) DEFAULT '08:00-09:00,09:00-10:00,10:00-11:00,11:00-12:00,12:00-13:00,13:00-14:00,14:00-15:00,15:00-16:00,16:00-17:00,17:00-18:00,18:00-19:00,19:00-20:00,20:00-21:00,21:00-22:00,22:00-23:00,23:00-00:00' NOT NULL,
	username_owner varchar(30)
);

CREATE TABLE  CarWash(
	name_carWash varchar(20),
    location_carWash varchar(30),
	phone_number varchar(10),
	address varchar(30),
    max_number_seat_carWash int,
    remaining_carWash_spaces int,
	carWash_cost_per_hour varchar(10),
    available_times VARCHAR(255) DEFAULT '08:00-09:00,09:00-10:00,10:00-11:00,11:00-12:00,12:00-13:00,13:00-14:00,14:00-15:00,15:00-16:00,16:00-17:00,17:00-18:00,18:00-19:00,19:00-20:00,20:00-21:00,21:00-22:00,22:00-23:00,23:00-00:00' NOT NULL,
	username_owner varchar(30)
);
CREATE TABLE ParkingReservations (
    name_parking VARCHAR(20),
    location_parking VARCHAR(30),
    reservation_date DATE,
    reservation_time VARCHAR(20),
    username VARCHAR(50),
    cost double,
    username_owner varchar(30)
);
CREATE TABLE CarWashReservations (
    name_carWash VARCHAR(20),
    location_carWash VARCHAR(30),
    reservation_date DATE,
    reservation_time VARCHAR(20),
    username VARCHAR(50),
    cost double,
    username_owner varchar(30)
);


CREATE TABLE  WalletProfileUser(
	username_wallet varchar(20),
    money_account double,
    primary key(username_wallet)
);
CREATE TABLE  PaysafeCard(
	code BIGINT,
    money double
);
CREATE TABLE  HistoryTransaction(
	username_wallet varchar(20),
    amount double,
    transaction_date timestamp,
    description varchar(100),
    name_business varchar(20)
);


CREATE TABLE  Paypal(
	username_paypal varchar(20),
    email_paypal varchar(40),
    code_paypal varchar(20),
    money_account_paypal double
);

CREATE TABLE service_price(
username_owner varchar(20),
service_name varchar(20),
current_price varchar(20),
area varchar(20)
);

CREATE TABLE Offers(
username_owner varchar(20),
service_name varchar(20),
current_price varchar(20)
);