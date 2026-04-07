-- Выполните вручную в PostgreSQL (один раз), если при INSERT в users_activity падает:
-- ERROR: violates check constraint "users_activity_action_type_check"
--
-- Старое ограничение не включало ADD_RAM и ADD_STORAGE из enum ActionType в приложении.

ALTER TABLE users_activity DROP CONSTRAINT IF EXISTS users_activity_action_type_check;

ALTER TABLE users_activity
    ADD CONSTRAINT users_activity_action_type_check
        CHECK (action_type IN (
                               'BUILD_CREATE',
                               'BUILD_UPDATE',
                               'BUILD_DELETE',
                               'BUILD_SAVE',
                               'ADD_RAM',
                               'ADD_STORAGE'
            ));
