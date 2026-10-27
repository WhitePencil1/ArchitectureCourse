
-- Гильдии
INSERT INTO guilds (name, description)
VALUES
    ('Knights of Dawn', 'Guild of brave warriors and knights'),
    ('Arcane Order', 'Guild of mages and scholars'),
    ('Shadow Brotherhood', 'Guild of assassins and scouts');


-- Герои
INSERT INTO heroes (name, level, health, experience, gold, guild_id)
VALUES
    ('Arthas', 10, 150, 2500, 500, 1),
    ('Geralt', 15, 200, 4500, 1200, 1),
    ('Rudeus', 20, 90, 8000, 2000, 2),
    ('Dumbledore', 18, 60, 3200, 750, 2),
    ('Ezio', 14, 130, 4000, 950, 3),
    ('Legolas', 11, 120, 2800, 600, 3),
    ('Arthur', 5, 100, 500, 100, NULL);
