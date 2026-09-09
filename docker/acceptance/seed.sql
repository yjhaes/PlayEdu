-- Deterministic data for the gated acceptance probes. It is not application seed data.
INSERT INTO users (
    id, email, name, avatar, password, salt, id_card, credit1, create_ip, create_city,
    is_active, is_lock, is_verify, is_set_password, from_scene, deleted, created_at, updated_at
) VALUES
    (7001, 'acceptance-7001@example.test', 'Acceptance Learner 7001', 0, '', '', '', 0, '', '', 1, 0, 0, 0, 0, 0, NOW(), NOW()),
    (7002, 'acceptance-7002@example.test', 'Acceptance Learner 7002', 0, '', '', '', 0, '', '', 1, 0, 0, 0, 0, 0, NOW(), NOW()),
    (7003, 'acceptance-7003@example.test', 'Acceptance Learner 7003', 0, '', '', '', 0, '', '', 1, 0, 0, 0, 0, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    is_active = VALUES(is_active),
    is_lock = VALUES(is_lock),
    deleted = VALUES(deleted),
    updated_at = NOW();

INSERT INTO courses (
    id, title, thumb, charge, short_desc, class_hour, is_show, is_required, admin_id,
    created_at, updated_at, deleted_at
) VALUES
    (8001, 'Redis acceptance course', 0, 0, 'Only used by the multi-instance acceptance plan', 2, 1, 0, 0, NOW(), NOW(), NULL)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    class_hour = VALUES(class_hour),
    is_show = VALUES(is_show),
    deleted_at = NULL,
    updated_at = NOW();

INSERT INTO course_chapters (id, course_id, name, sort, created_at, updated_at)
VALUES (8101, 8001, 'Acceptance chapter', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), updated_at = NOW();

INSERT INTO course_hour (
    id, course_id, chapter_id, sort, title, type, rid, duration, created_at, deleted
) VALUES
    (9001, 8001, 8101, 0, 'Acceptance hour 1', 'video', 0, 3600, NOW(), 0),
    (9002, 8001, 8101, 1, 'Acceptance hour 2', 'video', 0, 3600, NOW(), 0)
ON DUPLICATE KEY UPDATE
    course_id = VALUES(course_id),
    chapter_id = VALUES(chapter_id),
    duration = VALUES(duration),
    deleted = 0;

INSERT INTO user_learn_duration_stats (user_id, duration, created_date)
VALUES
    (7001, 180, CURRENT_DATE),
    (7002, 120, CURRENT_DATE),
    (7003, 60, CURRENT_DATE),
    (7001, 300, CURRENT_DATE - INTERVAL 1 DAY),
    (7002, 240, CURRENT_DATE - INTERVAL 1 DAY)
ON DUPLICATE KEY UPDATE duration = VALUES(duration);
