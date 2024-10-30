create definer = magou277@`%` view ennemidropview as
select `e`.`nom` AS `ennemi`, json_arrayagg(`d`.`nom`) AS `drops`
from ((`rpg_game`.`ennemi_drops` `ed` join `rpg_game`.`ennemis` `e`
       on ((`ed`.`ennemi_id` = `e`.`id`))) join `rpg_game`.divers `d` on ((`ed`.`drop_id` = `d`.`id`)))
group by `e`.`nom`;

