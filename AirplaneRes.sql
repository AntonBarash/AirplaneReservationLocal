USE AirplaneRes;
SET @@auto_increment_increment=1;
ALTER TABLE Customer AUTO_INCREMENT = 1;
SET @@auto_increment_increment=1;
ALTER TABLE Flight AUTO_INCREMENT = 1;
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
FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE CASCADE,
FOREIGN KEY (flight_id) REFERENCES Flight(flight_id) ON DELETE CASCADE,
PRIMARY KEY(customer_id, flight_id, seatn));
INSERT INTO Customer(fname, lname, contactn, creditcardn, ccv, exp, email, pass, account_type) values('admin','admin',1111111111,1,111,1111,'admin@admin','cs411',1);

INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('01:01', 'Boston', 'Chicago', '2022-01-01', 5, 1);
INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('01:01', 'Boston', 'Philadelphia', '2022-01-01', 4, 2);
INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('01:01', 'Boston', 'Toronto', '2022-01-01', 3, 3);

INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('01:01', 'Boston', 'D', '2022-01-01', 5, 1);

INSERT INTO Customer(fname, lname, contactn, creditcardn, ccv, exp, email, pass, account_type) values('a','a',1111111111,1,1,1,'a@a','a',0);



INSERT INTO Books(customer_id, flight_id, seatn) values(1,45,1);

SELECT * FROM Flight;

SELECT * FROM Customer;

SELECT Auto_increment FROM information_schema.tables WHERE table_name='Customer';

SELECT * FROM Books;