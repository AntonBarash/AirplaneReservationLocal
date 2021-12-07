use heroku_50d2532af7614cd;
DROP TABLE Books;
DROP TABLE Flight;
DROP TABLE Customer;
CREATE TABLE Flight(
time VARCHAR(255),
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
exp INTEGER,
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

INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('01:01', 'Boston', 'Chicago', '2022-01-01', 5, 1);
INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('01:01', 'Boston', 'Philadelphia', '2022-01-01', 4, 2);
INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('01:01', 'Boston', 'Toronto', '2022-01-01', 3, 3);

SELECT * FROM Flight;

SELECT * FROM Customer;