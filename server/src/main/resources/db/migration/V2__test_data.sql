-- USERS
INSERT INTO users (id, username, email, password, role, user_type)
VALUES (1, 'admin', 'admin@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'ADMIN',
        'ADMIN'),
       (2, 'parent', 'parent@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (3, 'employee', 'employee@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (4, 'parent1', 'parent1@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (5, 'parent2', 'parent2@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (6, 'parent3', 'parent3@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (7, 'parent4', 'parent4@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (8, 'parent5', 'parent5@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (9, 'parent6', 'parent6@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (10, 'parent7', 'parent7@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (11, 'parent8', 'parent8@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (12, 'parent9', 'parent9@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju', 'PARENT',
        'PARENT'),
       (13, 'employee1', 'employee1@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (14, 'employee2', 'employee2@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (15, 'employee3', 'employee3@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (16, 'employee4', 'employee4@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (17, 'employee5', 'employee5@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (18, 'employee6', 'employee6@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (19, 'employee7', 'employee7@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (20, 'employee8', 'employee8@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE'),
       (21, 'employee9', 'employee9@example.org', '$2a$10$UUpTR/DE/7D9qOLj7Zmotudf/xzhfhPlNZBWXW3n09vXIN.fHX8ju',
        'EMPLOYEE', 'EMPLOYEE');

-- CHILDREN
INSERT INTO children (id, name, birth_date, parent_id)
VALUES (1, 'Child_parent1_1', '2015-05-01', 4),
       (2, 'Child_parent1_2', '2016-06-02', 4),
       (3, 'Child_parent2_1', '2014-07-03', 5),
       (4, 'Child_parent2_2', '2015-08-04', 5),
       (5, 'Child_parent3_1', '2013-09-05', 6),
       (6, 'Child_parent3_2', '2014-10-06', 6),
       (7, 'Child_parent4_1', '2012-11-07', 7),
       (8, 'Child_parent4_2', '2013-12-08', 7),
       (9, 'Child_parent5_1', '2011-01-09', 8),
       (10, 'Child_parent5_2', '2012-02-10', 8),
       (11, 'Child_parent6_1', '2010-03-11', 9),
       (12, 'Child_parent6_2', '2011-04-12', 9),
       (13, 'Child_parent7_1', '2009-05-13', 10),
       (14, 'Child_parent7_2', '2010-06-14', 10),
       (15, 'Child_parent8_1', '2008-07-15', 11),
       (16, 'Child_parent8_2', '2009-08-16', 11),
       (17, 'Child_parent9_1', '2007-09-17', 12),
       (18, 'Child_parent9_2', '2008-10-18', 12),
       (19, 'Child_parent_1', '2017-03-01', 2),
       (20, 'Child_parent_2', '2018-04-02', 2);

-- EMPLOYEES
INSERT INTO employees (id, full_name, position, user_id)
VALUES (1, 'Employee employee1', 'Position 1', 13),
       (2, 'Employee employee2', 'Position 2', 14),
       (3, 'Employee employee3', 'Position 3', 15),
       (4, 'Employee employee4', 'Position 4', 16),
       (5, 'Employee employee5', 'Position 5', 17),
       (6, 'Employee employee6', 'Position 1', 18),
       (7, 'Employee employee7', 'Position 2', 19),
       (8, 'Employee employee8', 'Position 3', 20),
       (9, 'Employee employee9', 'Position 4', 21),
       (10, 'Main Employee', 'Main Position', 3);

-- CAMP_SESSIONS
INSERT INTO camp_sessions (id, name, start_date, end_date, description)
VALUES (1, 'Session 1', '2024-06-01', '2024-06-14', 'Description for session 1'),
       (2, 'Session 2', '2024-06-15', '2024-06-28', 'Description for session 2'),
       (3, 'Session 3', '2024-07-01', '2024-07-14', 'Description for session 3'),
       (4, 'Session 4', '2024-07-15', '2024-07-28', 'Description for session 4'),
       (5, 'Session 5', '2024-08-01', '2024-08-14', 'Description for session 5');

-- VOUCHERS
INSERT INTO vouchers (id, child_id, session_id, status, booking_date)
VALUES (1, 1, 1, 'BOOKED', '2024-05-01'),
       (2, 2, 2, 'CONFIRMED', '2024-05-02'),
       (3, 3, 3, 'BOOKED', '2024-05-03'),
       (4, 4, 4, 'USED', '2024-05-04'),
       (5, 5, 5, 'CANCELLED', '2024-05-05'),
       (6, 6, 1, 'BOOKED', '2024-05-06'),
       (7, 7, 2, 'CONFIRMED', '2024-05-07'),
       (8, 8, 3, 'BOOKED', '2024-05-08'),
       (9, 9, 4, 'USED', '2024-05-09'),
       (10, 10, 5, 'CANCELLED', '2024-05-10'),
       (11, 11, 1, 'BOOKED', '2024-05-11'),
       (12, 12, 2, 'CONFIRMED', '2024-05-12'),
       (13, 13, 3, 'BOOKED', '2024-05-13'),
       (14, 14, 4, 'USED', '2024-05-14'),
       (15, 15, 5, 'CANCELLED', '2024-05-15'),
       (16, 16, 1, 'BOOKED', '2024-05-16'),
       (17, 17, 2, 'CONFIRMED', '2024-05-17'),
       (18, 18, 3, 'BOOKED', '2024-05-18'),
       (19, 19, 4, 'USED', '2024-05-19'),
       (20, 20, 5, 'CANCELLED', '2024-05-20');

-- DUTY_LOGS
INSERT INTO duty_logs (id, date, employee_id, session_id, report)
VALUES (1, '2024-05-01', 1, 1, 'Duty report Employee employee1 #1'),
       (2, '2024-05-02', 2, 2, 'Duty report Employee employee2 #1'),
       (3, '2024-05-03', 3, 3, 'Duty report Employee employee3 #1'),
       (4, '2024-05-04', 4, 4, 'Duty report Employee employee4 #1'),
       (5, '2024-05-05', 5, 5, 'Duty report Employee employee5 #1'),
       (6, '2024-05-06', 6, 1, 'Duty report Employee employee6 #1'),
       (7, '2024-05-07', 7, 2, 'Duty report Employee employee7 #1'),
       (8, '2024-05-08', 8, 3, 'Duty report Employee employee8 #1'),
       (9, '2024-05-09', 9, 4, 'Duty report Employee employee9 #1'),
       (10, '2024-05-10', 10, 5, 'Duty report Main Employee #1'),
       (11, '2024-05-11', 1, 1, 'Duty report Employee employee1 #2'),
       (12, '2024-05-12', 2, 2, 'Duty report Employee employee2 #2'),
       (13, '2024-05-13', 3, 3, 'Duty report Employee employee3 #2'),
       (14, '2024-05-14', 4, 4, 'Duty report Employee employee4 #2'),
       (15, '2024-05-15', 5, 5, 'Duty report Employee employee5 #2'),
       (16, '2024-05-16', 6, 1, 'Duty report Employee employee6 #2'),
       (17, '2024-05-17', 7, 2, 'Duty report Employee employee7 #2'),
       (18, '2024-05-18', 8, 3, 'Duty report Employee employee8 #2'),
       (19, '2024-05-19', 9, 4, 'Duty report Employee employee9 #2'),
       (20, '2024-05-20', 10, 5, 'Duty report Main Employee #2');

-- SCHEDULES
INSERT INTO schedules (id, date, session_id, employee_id, time, event_type, description)
VALUES (1, '2024-06-01', 1, 1, '09:00:00', 'EventType1', 'Schedule for Employee employee1 #1'),
       (2, '2024-06-02', 2, 2, '10:00:00', 'EventType2', 'Schedule for Employee employee2 #1'),
       (3, '2024-06-03', 3, 3, '11:00:00', 'EventType1', 'Schedule for Employee employee3 #1'),
       (4, '2024-06-04', 4, 4, '12:00:00', 'EventType2', 'Schedule for Employee employee4 #1'),
       (5, '2024-06-05', 5, 5, '13:00:00', 'EventType1', 'Schedule for Employee employee5 #1'),
       (6, '2024-06-06', 1, 6, '14:00:00', 'EventType2', 'Schedule for Employee employee6 #1'),
       (7, '2024-06-07', 2, 7, '15:00:00', 'EventType1', 'Schedule for Employee employee7 #1'),
       (8, '2024-06-08', 3, 8, '16:00:00', 'EventType2', 'Schedule for Employee employee8 #1'),
       (9, '2024-06-09', 4, 9, '17:00:00', 'EventType1', 'Schedule for Employee employee9 #1'),
       (10, '2024-06-10', 5, 10, '18:00:00', 'EventType2', 'Schedule for Main Employee #1'),
       (11, '2024-06-11', 1, 1, '09:00:00', 'EventType1', 'Schedule for Employee employee1 #2'),
       (12, '2024-06-12', 2, 2, '10:00:00', 'EventType2', 'Schedule for Employee employee2 #2'),
       (13, '2024-06-13', 3, 3, '11:00:00', 'EventType1', 'Schedule for Employee employee3 #2'),
       (14, '2024-06-14', 4, 4, '12:00:00', 'EventType2', 'Schedule for Employee employee4 #2'),
       (15, '2024-06-15', 5, 5, '13:00:00', 'EventType1', 'Schedule for Employee employee5 #2'),
       (16, '2024-06-16', 1, 6, '14:00:00', 'EventType2', 'Schedule for Employee employee6 #2'),
       (17, '2024-06-17', 2, 7, '15:00:00', 'EventType1', 'Schedule for Employee employee7 #2'),
       (18, '2024-06-18', 3, 8, '16:00:00', 'EventType2', 'Schedule for Employee employee8 #2'),
       (19, '2024-06-19', 4, 9, '17:00:00', 'EventType1', 'Schedule for Employee employee9 #2'),
       (20, '2024-06-20', 5, 10, '18:00:00', 'EventType2', 'Schedule for Main Employee #2');

-- MENUS
INSERT INTO menus (id, date, session_id, breakfast, lunch, dinner, notes)
VALUES (1, '2024-06-01', 1, 'Breakfast 1', 'Lunch 1', 'Dinner 1', 'Notes 1'),
       (2, '2024-06-02', 2, 'Breakfast 2', 'Lunch 2', 'Dinner 2', 'Notes 2'),
       (3, '2024-06-03', 3, 'Breakfast 3', 'Lunch 3', 'Dinner 3', 'Notes 3'),
       (4, '2024-06-04', 4, 'Breakfast 4', 'Lunch 4', 'Dinner 4', 'Notes 4'),
       (5, '2024-06-05', 5, 'Breakfast 5', 'Lunch 5', 'Dinner 5', 'Notes 5'),
       (6, '2024-06-06', 1, 'Breakfast 6', 'Lunch 6', 'Dinner 6', 'Notes 6'),
       (7, '2024-06-07', 2, 'Breakfast 7', 'Lunch 7', 'Dinner 7', 'Notes 7'),
       (8, '2024-06-08', 3, 'Breakfast 8', 'Lunch 8', 'Dinner 8', 'Notes 8'),
       (9, '2024-06-09', 4, 'Breakfast 9', 'Lunch 9', 'Dinner 9', 'Notes 9'),
       (10, '2024-06-10', 5, 'Breakfast 10', 'Lunch 10', 'Dinner 10', 'Notes 10');

-- MEDICAL_CARDS
INSERT INTO medical_cards (id, child_id, health_info, chronic_diseases, allergies, vaccinations, notes)
VALUES (1, 1, 'Health info 1', 'None', 'None', 'Vaccine1', 'Notes 1'),
       (2, 2, 'Health info 2', 'None', 'None', 'Vaccine2', 'Notes 2'),
       (3, 3, 'Health info 3', 'None', 'None', 'Vaccine3', 'Notes 3'),
       (4, 4, 'Health info 4', 'None', 'None', 'Vaccine4', 'Notes 4'),
       (5, 5, 'Health info 5', 'None', 'None', 'Vaccine5', 'Notes 5'),
       (6, 6, 'Health info 6', 'None', 'None', 'Vaccine6', 'Notes 6'),
       (7, 7, 'Health info 7', 'None', 'None', 'Vaccine7', 'Notes 7'),
       (8, 8, 'Health info 8', 'None', 'None', 'Vaccine8', 'Notes 8'),
       (9, 9, 'Health info 9', 'None', 'None', 'Vaccine9', 'Notes 9'),
       (10, 10, 'Health info 10', 'None', 'None', 'Vaccine10', 'Notes 10'),
       (11, 11, 'Health info 11', 'None', 'None', 'Vaccine11', 'Notes 11'),
       (12, 12, 'Health info 12', 'None', 'None', 'Vaccine12', 'Notes 12'),
       (13, 13, 'Health info 13', 'None', 'None', 'Vaccine13', 'Notes 13'),
       (14, 14, 'Health info 14', 'None', 'None', 'Vaccine14', 'Notes 14'),
       (15, 15, 'Health info 15', 'None', 'None', 'Vaccine15', 'Notes 15'),
       (16, 16, 'Health info 16', 'None', 'None', 'Vaccine16', 'Notes 16'),
       (17, 17, 'Health info 17', 'None', 'None', 'Vaccine17', 'Notes 17'),
       (18, 18, 'Health info 18', 'None', 'None', 'Vaccine18', 'Notes 18'),
       (19, 19, 'Health info 19', 'None', 'None', 'Vaccine19', 'Notes 19'),
       (20, 20, 'Health info 20', 'None', 'None', 'Vaccine20', 'Notes 20');

-- MEDICAL_VISITS
INSERT INTO medical_visits (id, date, child_id, doctor_id, description, recommendations, medications)
VALUES (1, '2024-05-01', 1, 1, 'Visit 1', 'Rec 1', 'Med 1'),
       (2, '2024-05-02', 2, 2, 'Visit 2', 'Rec 2', 'Med 2'),
       (3, '2024-05-03', 3, 3, 'Visit 3', 'Rec 3', 'Med 3'),
       (4, '2024-05-04', 4, 4, 'Visit 4', 'Rec 4', 'Med 4'),
       (5, '2024-05-05', 5, 5, 'Visit 5', 'Rec 5', 'Med 5'),
       (6, '2024-05-06', 6, 6, 'Visit 6', 'Rec 6', 'Med 6'),
       (7, '2024-05-07', 7, 7, 'Visit 7', 'Rec 7', 'Med 7'),
       (8, '2024-05-08', 8, 8, 'Visit 8', 'Rec 8', 'Med 8'),
       (9, '2024-05-09', 9, 9, 'Visit 9', 'Rec 9', 'Med 9'),
       (10, '2024-05-10', 10, 10, 'Visit 10', 'Rec 10', 'Med 10'),
       (11, '2024-05-11', 11, 1, 'Visit 11', 'Rec 11', 'Med 11'),
       (12, '2024-05-12', 12, 2, 'Visit 12', 'Rec 12', 'Med 12'),
       (13, '2024-05-13', 13, 3, 'Visit 13', 'Rec 13', 'Med 13'),
       (14, '2024-05-14', 14, 4, 'Visit 14', 'Rec 14', 'Med 14'),
       (15, '2024-05-15', 15, 5, 'Visit 15', 'Rec 15', 'Med 15'),
       (16, '2024-05-16', 16, 6, 'Visit 16', 'Rec 16', 'Med 16'),
       (17, '2024-05-17', 17, 7, 'Visit 17', 'Rec 17', 'Med 17'),
       (18, '2024-05-18', 18, 8, 'Visit 18', 'Rec 18', 'Med 18'),
       (19, '2024-05-19', 19, 9, 'Visit 19', 'Rec 19', 'Med 19'),
       (20, '2024-05-20', 20, 10, 'Visit 20', 'Rec 20', 'Med 20');

-- NOTIFICATIONS
INSERT INTO notifications (id, recipient_id, type, subject, message, status, created_at, sent_at)
VALUES (1, 4, 'email', 'Subject 1', 'Message 1', 'sent', '2024-05-01 10:00:00', '2024-05-01 10:01:00'),
       (2, 5, 'email', 'Subject 2', 'Message 2', 'sent', '2024-05-02 10:00:00', '2024-05-02 10:01:00'),
       (3, 6, 'email', 'Subject 3', 'Message 3', 'sent', '2024-05-03 10:00:00', '2024-05-03 10:01:00'),
       (4, 7, 'email', 'Subject 4', 'Message 4', 'sent', '2024-05-04 10:00:00', '2024-05-04 10:01:00'),
       (5, 8, 'email', 'Subject 5', 'Message 5', 'sent', '2024-05-05 10:00:00', '2024-05-05 10:01:00'),
       (6, 9, 'email', 'Subject 6', 'Message 6', 'sent', '2024-05-06 10:00:00', '2024-05-06 10:01:00'),
       (7, 10, 'email', 'Subject 7', 'Message 7', 'sent', '2024-05-07 10:00:00', '2024-05-07 10:01:00'),
       (8, 11, 'email', 'Subject 8', 'Message 8', 'sent', '2024-05-08 10:00:00', '2024-05-08 10:01:00'),
       (9, 12, 'email', 'Subject 9', 'Message 9', 'sent', '2024-05-09 10:00:00', '2024-05-09 10:01:00'),
       (10, 2, 'email', 'Subject 10', 'Message 10', 'sent', '2024-05-10 10:00:00', '2024-05-10 10:01:00');

-- PAYMENTS
INSERT INTO payments (id, parent_id, voucher_id, amount, date, status, method, comment)
VALUES (1, 4, 1, 10000.0, '2024-05-01 12:00:00', 'paid', 'card', 'Payment 1'),
       (2, 5, 2, 11000.0, '2024-05-02 12:00:00', 'paid', 'card', 'Payment 2'),
       (3, 6, 3, 12000.0, '2024-05-03 12:00:00', 'paid', 'card', 'Payment 3'),
       (4, 7, 4, 13000.0, '2024-05-04 12:00:00', 'paid', 'card', 'Payment 4'),
       (5, 8, 5, 14000.0, '2024-05-05 12:00:00', 'paid', 'card', 'Payment 5'),
       (6, 9, 6, 15000.0, '2024-05-06 12:00:00', 'paid', 'card', 'Payment 6'),
       (7, 10, 7, 16000.0, '2024-05-07 12:00:00', 'paid', 'card', 'Payment 7'),
       (8, 11, 8, 17000.0, '2024-05-08 12:00:00', 'paid', 'card', 'Payment 8'),
       (9, 12, 9, 18000.0, '2024-05-09 12:00:00', 'paid', 'card', 'Payment 9'),
       (10, 2, 10, 19000.0, '2024-05-10 12:00:00', 'paid', 'card', 'Payment 10');

-- ACTIVITY_LOGS
INSERT INTO activity_logs (id, user_id, action, timestamp, description)
VALUES (1, 4, 'Login', '2024-05-01 09:00:00', 'User parent1 logged in'),
       (2, 5, 'Login', '2024-05-02 09:00:00', 'User parent2 logged in'),
       (3, 6, 'Login', '2024-05-03 09:00:00', 'User parent3 logged in'),
       (4, 7, 'Login', '2024-05-04 09:00:00', 'User parent4 logged in'),
       (5, 8, 'Login', '2024-05-05 09:00:00', 'User parent5 logged in'),
       (6, 9, 'Login', '2024-05-06 09:00:00', 'User parent6 logged in'),
       (7, 10, 'Login', '2024-05-07 09:00:00', 'User parent7 logged in'),
       (8, 11, 'Login', '2024-05-08 09:00:00', 'User parent8 logged in'),
       (9, 12, 'Login', '2024-05-09 09:00:00', 'User parent9 logged in'),
       (10, 2, 'Login', '2024-05-10 09:00:00', 'User parent logged in');

SELECT setval('activity_logs_id_seq', (SELECT MAX(id) FROM activity_logs));
SELECT setval('camp_sessions_id_seq', (SELECT MAX(id) FROM camp_sessions));
SELECT setval('children_id_seq', (SELECT MAX(id) FROM children));
SELECT setval('duty_logs_id_seq', (SELECT MAX(id) FROM duty_logs));
SELECT setval('employees_id_seq', (SELECT MAX(id) FROM employees));
SELECT setval('medical_cards_id_seq', (SELECT MAX(id) FROM medical_cards));
SELECT setval('medical_visits_id_seq', (SELECT MAX(id) FROM medical_visits));
SELECT setval('menus_id_seq', (SELECT MAX(id) FROM menus));
SELECT setval('notifications_id_seq', (SELECT MAX(id) FROM notifications));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
SELECT setval('schedules_id_seq', (SELECT MAX(id) FROM schedules));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('vouchers_id_seq', (SELECT MAX(id) FROM vouchers));