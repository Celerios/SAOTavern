package net.haru.gui;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.ess3.api.MaxMoneyException;
import net.haru.SAOTavern;
import net.haru.rooms.ARoom;
import net.haru.tavern.Tavern;
import net.haru.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

public class RoomGUI implements Listener {

    private final Inventory gui;
    private final ARoom room;
    private final Tavern tavern;

    public RoomGUI(Tavern tavern, ARoom room)
    {
        gui = Bukkit.createInventory(null, 27, "Chambre: " + room.getUuid());
        this.tavern = tavern;
        this.room = room;
        SAOTavern.INSTANCE.getServer().getPluginManager().registerEvents(this, SAOTavern.INSTANCE);
    }

    public void open(Player player) {
        this.gui.clear();

        if (this.room.getOwner() == null)
        {
            ItemBuilder item = new ItemBuilder(Material.GOLD_INGOT);
            item.setName("Louer la chambre");
            item.setLore(List.of("Prix: " + this.room.getPrice(), "Temps: " + this.room.getMinutesRemaining() + " minutes"));
            gui.setItem(13, item.build());
        }

        else
        {
            if (this.room.getOwner().compareTo(player.getUniqueId()) == 0)
            {
                ItemBuilder item = new ItemBuilder(Material.PAPER);
                item.setName("Prolonger la location");
                item.setLore(List.of("Temps restant: " + this.room.getMinutesRemaining() + " minutes", "Prix: " + this.room.getPrice()));
                gui.setItem(13, item.build());
            }

            else
            {
                ItemBuilder item = new ItemBuilder(Material.PAPER);
                item.setName("Chambre déjà louée!");
                item.setLore(List.of("Propriétaire: " + this.room.getOwnerName(), "Temps restant: " + this.room.getMinutesRemaining() + " minutes"));
                gui.setItem(13, item.build());
            }
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Chambre: " + room.getUuid())) return;
        event.setCancelled(true);


        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Page/Back
        if (event.getSlot() == 13) {
            if (this.room.getOwner() == null || this.room.getOwner().compareTo(player.getUniqueId()) == 0)
            {
                try {
                    BigDecimal price = BigDecimal.valueOf(this.room.getPrice());

                    if (Economy.hasEnough(player.getUniqueId(), price)) {
                        Economy.subtract(player.getUniqueId(), price);
                        this.tavern.addIncome(this.room.getPrice());

                        // new rent/add rent
                        if (this.room.getOwner() == null) {
                            this.room.setOwner(player.getUniqueId());
                            player.sendMessage("Chambre louée!");
                        }
                        else {
                            this.room.addRent();
                            player.sendMessage("Location prolongée!");
                        }

                        open(player);

                        // Pay tavern owner
                        Economy.add(this.tavern.getOwner(), price);
                        Player owner = SAOTavern.INSTANCE.getServer().getPlayer(this.tavern.getOwner());
                        if (owner != null)
                            owner.sendMessage("Quelqu'un à louer votre chambre pour " + price + "g");
                    }
                } catch (UserDoesNotExistException | MaxMoneyException | NoLoanPermittedException ignored) {}
            }

            // Already rented out
            else
            {
                player.sendMessage("Chambre déjà louée!");
            }
        }
    }

}
