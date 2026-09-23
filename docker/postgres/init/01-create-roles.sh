#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --set=db_name="$POSTGRES_DB" \
  --set=db_user="$DB_USER" \
  --set=db_password="$DB_PASSWORD" \
  --set=flyway_user="$FLYWAY_USER" \
  --set=flyway_password="$FLYWAY_PASSWORD" <<-'EOSQL'

CREATE ROLE :"db_user"
    LOGIN
    PASSWORD :'db_password';

CREATE ROLE :"flyway_user"
    LOGIN
    PASSWORD :'flyway_password';

GRANT CONNECT ON DATABASE :"db_name" TO :"db_user";
GRANT CONNECT ON DATABASE :"db_name" TO :"flyway_user";

GRANT USAGE ON SCHEMA public TO :"db_user";
GRANT USAGE, CREATE ON SCHEMA public TO :"flyway_user";

-- Existing tables/sequences (none yet at init time, but harmless to include)
GRANT SELECT, INSERT, UPDATE, DELETE
    ON ALL TABLES IN SCHEMA public
    TO :"db_user";

GRANT ALL PRIVILEGES
    ON ALL TABLES IN SCHEMA public
    TO :"flyway_user";

GRANT USAGE, SELECT
    ON ALL SEQUENCES IN SCHEMA public
    TO :"db_user";

GRANT ALL PRIVILEGES
    ON ALL SEQUENCES IN SCHEMA public
    TO :"flyway_user";

-- Future tables/sequences created by Flyway migrations
ALTER DEFAULT PRIVILEGES FOR ROLE :"flyway_user"
IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE
ON TABLES TO :"db_user";

ALTER DEFAULT PRIVILEGES FOR ROLE :"flyway_user"
IN SCHEMA public
GRANT USAGE, SELECT
ON SEQUENCES TO :"db_user";

EOSQL