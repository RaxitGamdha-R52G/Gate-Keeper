UPDATE management_users
SET email = '${super-admin-email}'
WHERE username = 'platform-admin'
  AND role_id = 1;

UPDATE management_users
SET email = '${gateway-service-email}'
WHERE username = 'gateway-service'
  AND role_id = 3;

-- every existing management user has an email.
-- The migration must fail if any email is still missing.
DELIMITER //
CREATE PROCEDURE validate_management_user_emails()
BEGIN
    IF EXISTS (
        SELECT 1
        FROM management_users
        WHERE email IS NULL OR TRIM(email) = ''
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'Every management user must have an email before NOT NULL is enforced';
    END IF;
END//
DELIMITER ;

CALL validate_management_user_emails();

DROP PROCEDURE validate_management_user_emails;

ALTER TABLE management_users
    MODIFY COLUMN email VARCHAR (254) NOT NULL;

ALTER TABLE management_users
    ADD CONSTRAINT uk_management_users_email UNIQUE (email);