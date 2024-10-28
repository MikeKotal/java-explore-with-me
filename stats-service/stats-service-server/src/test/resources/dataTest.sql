MERGE INTO stats(app, uri, ip, timestamp)
KEY(timestamp)
VALUES('test', '/event/test', '1.1.1.1', '2024-01-01T00:00:00'),
('test1', '/event/test1', '2.2.2.2', '2024-01-02T00:00:00'),
('test2', '/event/test2', '3.3.3.3', '2024-01-03T00:00:00'),
('test2', '/event/test2', '3.3.3.3', '2024-01-04T00:00:00');