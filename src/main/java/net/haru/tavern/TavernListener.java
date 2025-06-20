package net.haru.tavern;

import net.haru.gui.AdminManagementGUI;
import net.haru.gui.RoomGUI;
import net.haru.gui.TavernAdminGUI;
import net.haru.gui.TavernGUI;
import net.haru.rooms.ARoom;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TavernListener implements Listener {

    public final Map<String, TavernGUI> tavernGui;
    private final Map<String, Map<UUID, RoomGUI>> roomGui;
    private final TavernManager tavernManager;

    public TavernListener(TavernManager tavernManager)
    {
        this.tavernManager = tavernManager;
        this.tavernGui = new HashMap<>();
        this.roomGui = new HashMap<>();
    }

    @EventHandler
    public void signChange(SignChangeEvent event)
    {
        // Cancel sign change
        for (Tavern tavern : this.tavernManager.getTaverns()) {
            if (tavern.getLocation() != null && event.getBlock().getLocation().distance(tavern.getLocation()) == 0)
                event.setCancelled(true);

            for (ARoom room : tavern.getRooms()) {

                if (room.getLocation() != null && event.getBlock().getLocation().distance(room.getLocation()) == 0)
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void signInteract(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();


        if (block.getState() instanceof Sign) {

            for (Tavern tavern : this.tavernManager.getTaverns()) {
                if (tavern.getLocation() != null && block.getLocation().distance(tavern.getLocation()) == 0)
                {
                    event.setCancelled(true);

                    // Open tavern gui
                    if (!tavernGui.containsKey(tavern.getName()))
                        tavernGui.put(tavern.getName(), new TavernGUI(tavern));
                    tavernGui.get(tavern.getName()).open(event.getPlayer());
                }

                for (ARoom room : tavern.getRooms()) {

                    if (room.getLocation() != null && block.getLocation().distance(room.getLocation()) == 0)
                    {
                        event.setCancelled(true);

                        // Open room gui
                        if (!roomGui.containsKey(tavern.getName()))
                            roomGui.put(tavern.getName(), new HashMap<>());
                        if (!roomGui.get(tavern.getName()).containsKey(room.getUuid()))
                            roomGui.get(tavern.getName()).put(room.getUuid(), new RoomGUI(tavern, room));
                        roomGui.get(tavern.getName()).get(room.getUuid()).open(event.getPlayer());
                    }
                }
            }
        }
    }


    @EventHandler
    public void roomInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        for (Tavern tavern : this.tavernManager.getTaverns()) {
            for (ARoom room : tavern.getRooms()) {
                if (room.isInside(event.getClickedBlock().getLocation()))
                {
                    if (room.getOwner() == null || room.getOwner().compareTo(event.getPlayer().getUniqueId()) != 0)
                    {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("Ce n'est pas votre chambre!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDestroy(BlockBreakEvent event)
    {
        for (Tavern tavern : this.tavernManager.getTaverns()) {
            if (tavern.isInside(event.getBlock().getLocation()))
            {
                if (tavern.getOwner() == null || tavern.getOwner().compareTo(event.getPlayer().getUniqueId()) != 0)
                {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Ce n'est pas votre taverne!");
                }

                // Remove placed decoration
                else
                {
                    tavern.removeDecoration(event.getBlock().getLocation());
                    event.setCancelled(false);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        for (Tavern tavern : this.tavernManager.getTaverns()) {
            if (tavern.isInside(event.getBlock().getLocation()))
            {
                if (tavern.getOwner().compareTo(event.getPlayer().getUniqueId()) != 0)
                    event.setCancelled(true);

                // Add placed decoration to know what blocks are not natural
                else
                {
                    tavern.addDecoration(event.getBlock().getLocation());
                    event.setCancelled(false);
                }
            }
        }
    }
}
