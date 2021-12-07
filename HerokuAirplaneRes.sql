drop database AirplaneRes;
create database AirplaneRes;
use heroku_50d2532af7614cd;
DROP TABLE Books;
DROP TABLE Flight;
DROP TABLE Customer;
CREATE TABLE Flight(
destination_loc VARCHAR(255),
departing_loc VARCHAR(255),
date VARCHAR(255),
flight_id INTEGER AUTO_INCREMENT,
total_seats INTEGER,
price INTEGER,
PRIMARY KEY(flight_id));

CREATE TABLE Customer(
fname VARCHAR(255),
lname VARCHAR(255),
contactn INTEGER,
creditcardn INTEGER,
ccv INTEGER,
email VARCHAR(255),
pass VARCHAR(255),
account_type INTEGER,
customer_id INTEGER AUTO_INCREMENT,
PRIMARY KEY(customer_id));

CREATE TABLE Books(
customer_id INTEGER,
flight_id INTEGER,
seatn INTEGER,
FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
FOREIGN KEY (flight_id) REFERENCES Flight(flight_id),
PRIMARY KEY(customer_id, flight_id));

INSERT INTO Flight(destination_loc,departing_loc,date,total_seats,price) values('Boston', 'Chicago', 01012001,5, 1);
INSERT INTO Flight(destination_loc,departing_loc,date,total_seats,price) values('Boston', 'Philadelphia', 01012001, 4, 2);
INSERT INTO Flight(destination_loc,departing_loc,date,total_seats,price) values('Boston', 'Toronto', 01012001, 3, 3);

INSERT INTO Customer(fname,lname,contactn,creditcardn,ccv,email,pass,account_type) values('a','a',1,1,1,'a@a','a',0);
INSERT INTO Customer(fname,lname,contactn,creditcardn,ccv,email,pass,account_type) values('b','b',1,1,1,'b@b','b',1);
INSERT INTO Books(customer_id, flight_id, seatn) values(1, 1, 1);

SELECT * FROM Customer;