package net.haru.tasks;

import net.haru.SAOTavern;
import net.haru.rooms.ARoom;
import net.haru.tavern.Tavern;
import net.haru.tavern.TavernManager;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class UpdateTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final TavernManager tavernManager;
    private BukkitTask task;

    public UpdateTask(SAOTavern tavern) {
        this.plugin = tavern;
        this.tavernManager = tavern.getTavernManager();
    }

    @Override
    public void run() {
        for (Tavern tavern : this.tavernManager.getTaverns()) {

            // Tavern signs
            if (tavern.getLocation() != null && tavern.getLocation().getBlock().getState() instanceof Sign sign) {
                sign.setLine(0, "Taverne: " + tavern.getName());
                if (tavern.getOwner() != null)
                {
                    sign.setLine(1, "Propriétaire: " + tavern.getOwnerName());
                    sign.setLine(2, "Temps: " + tavern.getMinutesRemaining() + "m");
                    sign.setLine(3, "");
                }
                else
                {
                    sign.setLine(1, "");
                    sign.setLine(2, "Prix: " + tavern.getPrice() + "g");
                    sign.setLine(3, "À louée");
                }
                sign.update();
            }

            // Tavern rent
            if (tavern.getMinutesRemaining() == 0)
                tavern.setOwner(null);
            else if (tavern.getMinutesRemaining() == 60)
            {
                Player player = SAOTavern.INSTANCE.getServer().getPlayer(tavern.getOwner());
                if (player != null)
                    player.sendMessage("Il vous reste 1 heure de location de votre taverne " + tavern.getName());
            }



            // Room signs
            for (ARoom room : tavern.getRooms()) {
                if (room.getLocation() != null && room.getLocation().getBlock().getState() instanceof Sign sign) {
                    sign.setLine(0, "Chambre");
                    if (room.getOwner() != null)
                    {
                        sign.setLine(1, "Locataire: " + room.getOwnerName());
                        sign.setLine(2, "Temps: " + room.getMinutesRemaining() + "m");
                        sign.setLine(3, "");
                    }
                    else
                    {
                        sign.setLine(1, "Temps: " + room.getLocMinutes() + "m");
                        sign.setLine(2, "Prix: " + room.getPrice() + "g");
                        sign.setLine(3, "À louée");
                    }
                    sign.update();
                }


                // Room rent
                if (room.getMinutesRemaining() == 0)
                    room.setOwner(null);
                else if (room.getMinutesRemaining() == 60)
                {
                    Player player = SAOTavern.INSTANCE.getServer().getPlayer(room.getOwner());
                    if (player != null)
                        player.sendMessage("Il vous reste 1 heure de location de votre chambre");
                }
            }
        }
    }

    public void enable() {
        this.task = this.runTaskTimer(plugin, 0L, 60L * 5L); // every 5 seconds
    }

    public void disable()
    {
        this.task.cancel();
    }
}