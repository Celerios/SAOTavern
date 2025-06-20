package net.haru.gui;

import net.haru.SAOTavern;
import net.haru.commands.TavernCommand;
import net.haru.tavern.Tavern;
import net.haru.tavern.TavernManager;
import net.haru.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AdminManagementGUI implements Listener {

    private TavernManager tavernManager;
    private Inventory gui;
    private int page;

    public AdminManagementGUI()
    {
        this.tavernManager = SAOTavern.INSTANCE.getTavernManager();
        this.gui = Bukkit.createInventory(null, 45, "Tavernes");
        this.page = 0;
        SAOTavern.INSTANCE.getServer().getPluginManager().registerEvents(this, SAOTavern.INSTANCE);
    }

    public void open(Player player)
    {
        // Clear for page switch
        this.gui.clear();

        Tavern tavern;
        int start = this.page * 36;
        for (int i = 0; i < 36; i++)
        {
            tavern = this.tavernManager.getTavern(start++);
            if (tavern == null)
                break;

            // Set tavern items
            ItemBuilder item = new ItemBuilder(Material.OAK_SIGN);
            item.setName(tavern.getName());


            List<String> lore = new ArrayList<>();
            if (tavern.getOwner() != null)
                lore.add("Propriétaire: " + tavern.getOwnerName());
            else
                lore.add("Propriétaire: Taverne à louée");
            lore.add("Prix de location: " + tavern.getPrice());
            item.setLore(lore);


            gui.setItem(i, item.build());
        }

        // Page
        if (page > 0)
        {
            ItemBuilder item = new ItemBuilder(Material.ARROW);
            item.setName("Précédent");
            gui.setItem(39, item.build());
        }

        if (start == this.page + 36 && this.tavernManager.getTavern(start) != null)
        {
            ItemBuilder item = new ItemBuilder(Material.ARROW);
            item.setName("Suivant");
            gui.setItem(41, item.build());
        }

        // Open
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Tavernes")) return;
        event.setCancelled(true);


        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;


        // Page
        if (clicked.getType() == Material.ARROW) {
            if (event.getSlot() == 39)
            {
                this.page--;
                this.open((Player) event.getWhoClicked());
            }
            else if (event.getSlot() == 41)
            {
                this.page++;
                this.open((Player) event.getWhoClicked());
            }
        }


        // Taverns
        else if (clicked.getType() == Material.OAK_SIGN)
        {

            Tavern tavern = this.tavernManager.getTavern(clicked.getItemMeta().getDisplayName());

            if (!TavernCommand.tAdminGuis.containsKey(tavern.getName()))
                TavernCommand.tAdminGuis.put(tavern.getName(), new TavernAdminGUI(tavern));
            TavernCommand.tAdminGuis.get(tavern.getName()).open((Player) event.getWhoClicked());
        }
    }
}
