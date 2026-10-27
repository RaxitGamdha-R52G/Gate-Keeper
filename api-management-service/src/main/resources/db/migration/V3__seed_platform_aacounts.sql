INSERT INTO management_users
    (organization_id, role_id, username, password_hash, enabled, created_at, updated_at)
SELECT NULL,
       roles.id,
       'platform-admin',
       '${super-admin-password-hash}',
       TRUE,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM roles
WHERE roles.name = 'SUPER_ADMIN';

INSERT INTO management_users
    (organization_id, role_id, username, password_hash, enabled, created_at, updated_at)
SELECT NULL,
       roles.id,
       'gateway-service',
       '${gateway-service-password-hash}',
       TRUE,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM roles
WHERE roles.name = 'GATEWAY_SERVICE';