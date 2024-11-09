MERGE INTO users(name, email)
KEY(email)
VALUES('Name', 'test@test.ru'),
('Name1', 'test1@test.ru'),
('Name2', 'test2@test.ru'),
('Name3', 'test3@test.ru'),
('Name4', 'test4@test.ru');

MERGE INTO categories(name)
KEY(name)
VALUES('ТестКатегория'), ('ТестКатегория1'), ('ТестКатегория2');

MERGE INTO event_locations(lat, lon)
KEY(lat)
VALUES(1.1, 1.2);

MERGE INTO events(annotation, category_id, description, created_on, event_date, location_id, published_on, paid,
    participant_limit, request_moderation, title, confirmed_requests, initiator_id, state, views)
KEY(annotation)
VALUES('Мегааннотация', 1, 'Мегаописание', '2024-10-01 23:59:59', '2024-10-01 23:59:59', 1, '2024-10-01 23:59:59', true,
    2, true, 'Мегатитл', 0, 1, 'CANCELED', 0),
    ('Для запросов', 2, 'Запросный', '2024-10-01 23:59:59', '2024-10-01 23:59:59', 1, '2024-10-01 23:59:59', true,
    2, true, 'Мегатитл', 0, 2, 'PUBLISHED', 0);

MERGE INTO requests(event_id, requester_id, created, status)
KEY(created)
VALUES(1, 1, '2024-10-01 23:59:59', 'PENDING');

MERGE INTO compilations(pinned, title)
KEY(title)
VALUES(true, 'ТестоваяПодборка');

MERGE INTO event_compilations(event_id, compilation_id)
KEY(compilation_id)
VALUES(2, 1);

MERGE INTO comments(event_id, commenter_id, comment, created_at)
KEY(comment)
VALUES(1, 1, 'Супер комментарий', '2024-10-01 23:59:59'), (1, 2, 'Полностью поддерживаю', '2024-10-02 23:59:59');