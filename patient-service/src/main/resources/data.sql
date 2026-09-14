INSERT INTO patient (first_name, last_name, birth, gender, address, phone)
SELECT 'Test', 'TestNone', '1966-12-31', 'F', '1 Brookside St', '100-222-3333'
WHERE NOT EXISTS (
    SELECT 1 FROM patient
    WHERE first_name = 'Test'
      AND last_name = 'TestNone'
      AND birth = '1966-12-31'
);

INSERT INTO patient (first_name, last_name, birth, gender, address, phone)
SELECT 'Test', 'TestBorderline', '1945-06-24', 'M', '2 High St', '200-333-4444'
WHERE NOT EXISTS (
    SELECT 1 FROM patient
    WHERE first_name = 'Test'
      AND last_name = 'TestBorderline'
      AND birth = '1945-06-24'
);

INSERT INTO patient (first_name, last_name, birth, gender, address, phone)
SELECT 'Test', 'TestInDanger', '2004-06-18', 'M', '3 Club Road', '300-444-5555'
WHERE NOT EXISTS (
    SELECT 1 FROM patient
    WHERE first_name = 'Test'
      AND last_name = 'TestInDanger'
      AND birth = '2004-06-18'
);

INSERT INTO patient (first_name, last_name, birth, gender, address, phone)
SELECT 'Test', 'TestEarlyOnset', '2002-06-28', 'F', '4 Valley Dr', '400-555-6666'
WHERE NOT EXISTS (
    SELECT 1 FROM patient
    WHERE first_name = 'Test'
      AND last_name = 'TestEarlyOnset'
      AND birth = '2002-06-28'
);