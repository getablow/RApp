CREATE VIEW community_fav_stat AS
SELECT rid, title, writer, favorite_count
FROM recipe
WHERE reveal = 1