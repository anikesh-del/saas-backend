CREATE OR REPLACE FUNCTION prevent_audit_log_modification()
RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'audit_log is immutable: % not allowed', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER audit_log_no_update_delete
BEFORE UPDATE OR DELETE ON audit_log
FOR EACH ROW EXECUTE FUNCTION prevent_audit_log_modification();

CREATE TRIGGER audit_log_no_truncate
BEFORE TRUNCATE ON audit_log
FOR EACH STATEMENT EXECUTE FUNCTION prevent_audit_log_modification();