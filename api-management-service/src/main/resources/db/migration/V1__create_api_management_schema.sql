CREATE TABLE organizations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    status ENUM('INACTIVE', 'ACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'INACTIVE',
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6),

    CONSTRAINT pk_organizations
        PRIMARY KEY (id),

    CONSTRAINT uk_organizations_name
        UNIQUE (name),

    CONSTRAINT uk_organizations_slug
        UNIQUE (slug)
);

CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,

    CONSTRAINT pk_roles
        PRIMARY KEY (id),

    CONSTRAINT uk_roles_name
        UNIQUE (name)
);

CREATE TABLE management_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    organization_id BIGINT,
    role_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_management_users
        PRIMARY KEY (id),

    CONSTRAINT uk_management_users_username
        UNIQUE (username),

    CONSTRAINT fk_management_users_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations(id),

    CONSTRAINT fk_management_users_role
        FOREIGN KEY (role_id)
            REFERENCES roles(id),

    INDEX idx_management_users_organization
    (organization_id)
);

CREATE TABLE api_routes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    route_id VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    path_pattern VARCHAR(500) NOT NULL,
    target_base_url VARCHAR(1000) NOT NULL,
    path_prefix VARCHAR(500) NOT NULL,
    request_per_minute INTEGER,
    response_timeout_ms INTEGER NOT NULL,
    idempotency_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    configuration_version BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_api_routes
        PRIMARY KEY (id),

    CONSTRAINT uk_api_routes_tenant_route
        UNIQUE (organization_id, route_id),

    CONSTRAINT fk_api_routes_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations(id)
);

CREATE TABLE route_allowed_methods (
   route_pk BIGINT NOT NULL,
   http_method VARCHAR(10) NOT NULL,

   CONSTRAINT pk_route_allowed_methods
       PRIMARY KEY (route_pk, http_method),

   CONSTRAINT fk_route_allowed_methods_route
       FOREIGN KEY (route_pk)
           REFERENCES api_routes (id)
           ON DELETE CASCADE
);

CREATE TABLE route_header_rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    route_pk BIGINT NOT NULL,
    direction ENUM('REQUEST', 'RESPONSE') NOT NULL,
    action ENUM('ADD', 'SET', 'UNSET', 'REMOVE') NOT NULL,
    header_name VARCHAR(255) NOT NULL,
    header_value VARCHAR(2000),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_route_header_rules
        PRIMARY KEY (id),

    CONSTRAINT fk_route_header_rules_route
        FOREIGN KEY (route_pk)
            REFERENCES api_routes (id)
            ON DELETE CASCADE
);

CREATE TABLE route_configuration_versions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    route_id VARCHAR(100) NOT NULL,
    version_number BIGINT NOT NULL,
    change_type ENUM('CREATED', 'UPDATED', 'ROLLBACK', 'ACTIVATED', 'DEACTIVATED') NOT NULL,
    configuration_snapshot LONGTEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_route_configuration_versions
        PRIMARY KEY (id),

    CONSTRAINT uk_route_versions_tenant_version
        UNIQUE (organization_id, route_id, version_number),

    CONSTRAINT fk_route_versions_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations (id)
);

CREATE TABLE control_plane_audit_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT,
    actor VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(100) NOT NULL,
    correlation_id VARCHAR(100),
    duration_ms BIGINT NOT NULL,
    result VARCHAR(20) NOT NULL,
    details VARCHAR(2000),
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_control_plane_audit_records
        PRIMARY KEY (id),

    CONSTRAINT fk_audit_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations (id)
);

CREATE TABLE management_user_setup_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    management_user_id BIGINT NOT NULL,
    token_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    used_at DATETIME(6) NULL,
    revoked_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_management_user_setup_tokens
        PRIMARY KEY (id),

    CONSTRAINT uk_management_user_setup_tokens_hash
        UNIQUE (token_hash),

    CONSTRAINT fk_management_user_setup_tokens_user
        FOREIGN KEY (management_user_id)
            REFERENCES management_users (id)
            ON DELETE CASCADE,

    INDEX idx_management_user_setup_tokens_user
        (management_user_id),

    INDEX idx_management_user_setup_tokens_expiry
        (expires_at)
);