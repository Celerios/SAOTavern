package net.haru.gui;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.ess3.api.MaxMoneyException;
import net.haru.SAOTavern;
import net.haru.commands.TavernCommand;
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
import java.util.ArrayList;
import java.util.List;

public class TavernGUI implements Listener {

    private final Inventory gui;
    private final Tavern tavern;
    private ARoom room;
    private int type, page;

    public TavernGUI(Tavern tavern)
    {
        this.tavern = tavern;
        this.gui = Bukkit.createInventory(null, 27, "Taverne: " + this.tavern.getName());
        this.room = null;
        this.type = 0;
        this.page = 0;
        SAOTavern.INSTANCE.getServer().getPluginManager().registerEvents(this, SAOTavern.INSTANCE);
    }


    public void open(Player player) {
        this.gui.clear();

        if (this.tavern.getOwner() == null)
        {
            ItemBuilder item = new ItemBuilder(Material.GOLD_INGOT);
            item.setName("Louer la taverne");
            item.setLore(List.of("Prix: " + this.tavern.getPrice(), "Temps: " + this.tavern.getMinutesRemaining() + " minutes"));
            gui.setItem(13, item.build());
        }

        else
        {

            if (this.tavern.getOwner().compareTo(player.getUniqueId()) == 0)
            {
                // Main menu
                if (type == 0)
                {
                    ItemBuilder item = new ItemBuilder(Material.GOLD_INGOT);
                    item.setName("Revenus");
                    item.setLore(List.of("Chambre: " + this.tavern.getIncome()));
                    gui.setItem(11, item.build());

                    item = new ItemBuilder(Material.PAPER);
                    item.setName("Prolonger la location!");
                    item.setLore(List.of("Temps restant: " + this.tavern.getMinutesRemaining() + " minutes", "Prix: " + this.tavern.getPrice()));
                    gui.setItem(13, item.build());

                    item = new ItemBuilder(Material.RED_BED);
                    item.setName("Gérer les chambres");
                    gui.setItem(15, item.build());
                }


                // Rooms menu
                else if (type == 1)
                {

                    // Rooms listing
                    if (this.room == null)
                    {

                        ARoom room;
                        int start = this.page * 18;
                        for (int i = 0; i < 18; i++)
                        {
                            room = this.tavern.getRoom(start++);
                            if (room == null)
                                break;

                            // Set room items
                            ItemBuilder item = new ItemBuilder(Material.RED_BED);
                            item.setName("Chambre n°" + (start - 1));


                            List<String> lore = new ArrayList<>();
                            if (room.getOwner() != null)
                                lore.add("Propriétaire: " + room.getOwnerName());
                            else
                                lore.add("Propriétaire: Chambre à louée");
                            lore.add("Prix de location: " + room.getPrice());
                            item.setLore(lore);

                            gui.setItem(i, item.build());
                        }

                        // Page
                        if (page > 0)
                        {
                            ItemBuilder item = new ItemBuilder(Material.ARROW);
                            item.setName("Précédent");
                            gui.setItem(21, item.build());
                        }

                        if (start == this.page + 28 && this.tavern.getRoom(start) != null)
                        {
                            ItemBuilder item = new ItemBuilder(Material.ARROW);
                            item.setName("Suivant");
                            gui.setItem(23, item.build());
                        }
                    }

                    // Room price
                    else
                    {
                        // Location time
                        ItemBuilder item = new ItemBuilder(Material.IRON_BLOCK);
                        item.setName("-60m");
                        gui.setItem(0, item.build());

                        item = new ItemBuilder(Material.IRON_INGOT);
                        item.setName("-15m");
                        gui.setItem(1, item.build());

                        item = new ItemBuilder(Material.IRON_NUGGET);
                        item.setName("-1m");
                        gui.setItem(2, item.build());

                        item = new ItemBuilder(Material.CLOCK);
                        item.setName("Temps de location: " + this.room.getLocMinutes());
                        gui.setItem(4, item.build());

                        item = new ItemBuilder(Material.IRON_NUGGET);
                        item.setName("+1m");
                        gui.setItem(6, item.build());

                        item = new ItemBuilder(Material.IRON_INGOT);
                        item.setName("+15m");
                        gui.setItem(7, item.build());

                        item = new ItemBuilder(Material.IRON_BLOCK);
                        item.setName("+60m");
                        gui.setItem(8, item.build());



                        // Price
                        item = new ItemBuilder(Material.GOLD_BLOCK);
                        item.setName("-1000");
                        gui.setItem(9, item.build());

                        item = new ItemBuilder(Material.GOLD_INGOT);
                        item.setName("-100");
                        gui.setItem(10, item.build());

                        item = new ItemBuilder(Material.GOLD_NUGGET);
                        item.setName("-10");
                        gui.setItem(11, item.build());

                        item = new ItemBuilder(Material.OAK_SIGN);
                        item.setName("Prix: " + this.room.getPrice());
                        gui.setItem(13, item.build());

                        item = new ItemBuilder(Material.GOLD_NUGGET);
                        item.setName("+10");
                        gui.setItem(15, item.build());

                        item = new ItemBuilder(Material.GOLD_INGOT);
                        item.setName("+100");
                        gui.setItem(16, item.build());

                        item = new ItemBuilder(Material.GOLD_BLOCK);
                        item.setName("+1000");
                        gui.setItem(17, item.build());
                    }

                    ItemBuilder item = new ItemBuilder(Material.ARROW);
                    item.setName("Revenir en arrière");
                    gui.setItem(18, item.build());
                }
            }

            else
            {
                ItemBuilder item = new ItemBuilder(Material.PAPER);
                item.setName("Taverne déjà louée!");
                item.setLore(List.of("Propriétaire: " + this.tavern.getOwnerName(), "Temps restant: " + this.tavern.getMinutesRemaining() + " minutes"));
                gui.setItem(13, item.build());
            }
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Taverne: " + this.tavern.getName())) return;
        event.setCancelled(true);


        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Page/Back
        if (clicked.getType() == Material.ARROW) {
            if (event.getSlot() == 21)
            {
                this.page--;
                this.open((Player) event.getWhoClicked());
            }
            else if (event.getSlot() == 23)
            {
                this.page++;
                this.open((Player) event.getWhoClicked());
            }

            // Back from room menu
            else if (event.getSlot() == 18)
            {
                this.type = 0;
                this.room = null;
                this.open((Player) event.getWhoClicked());
            }
        }

        // Rent
        if (event.getSlot() == 13) {
            if (this.tavern.getOwner() == null || this.tavern.getOwner().compareTo(player.getUniqueId()) == 0)
            {
                try {
                    if (Economy.hasEnough(player.getUniqueId(), BigDecimal.valueOf(this.tavern.getPrice()))) {
                        Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(this.tavern.getPrice()));

                        // new rent/add rent
                        if (this.tavern.getOwner() == null) {
                            this.tavern.setOwner(player.getUniqueId());
                            player.sendMessage("Taverne louée!");
                        }
                        else {
                            this.tavern.addRent();
                            player.sendMessage("Location prolongée!");
                        }
                        open(player);
                    }
                } catch (UserDoesNotExistException | MaxMoneyException | NoLoanPermittedException ignored) {}
            }


            // Already rented out
            else
            {
                player.sendMessage("Taverne déjà louée!");
            }
        }

        // Management
        if (this.tavern.getOwner().compareTo(player.getUniqueId()) == 0) {

            // Go to rooms management
            if (this.type == 0 && event.getSlot() == 15)
            {
                this.type = 1;
                this.page = 0;
                open(player);
            }


            // Rooms management
            else if (this.type == 1)
            {

                // Room selection
                if (clicked.getType() == Material.RED_BED)
                {
                    ARoom room1 = this.tavern.getRoom(Integer.parseInt(clicked.getItemMeta().getDisplayName().replace("Chambre n°", "")));
                    if (room1 != null)
                        this.room = room1;
                    open(player);
                }


                else if (this.room != null)
                {
                    switch (event.getSlot())
                    {
                        // Location time
                        case 0 -> this.room.setLocMinutes(this.room.getLocMinutes() - 60);
                        case 1 -> this.room.setLocMinutes(this.room.getLocMinutes() - 15);
                        case 2 -> this.room.setLocMinutes(this.room.getLocMinutes() - 1);

                        case 6 -> this.room.setLocMinutes(this.room.getLocMinutes() + 1);
                        case 7 -> this.room.setLocMinutes(this.room.getLocMinutes() + 15);
                        case 8 -> this.room.setLocMinutes(this.room.getLocMinutes() + 60);

                        // Price
                        case 9 -> this.room.setPrice(this.room.getPrice() - 1000);
                        case 10 -> this.room.setPrice(this.room.getPrice() - 100);
                        case 11 -> this.room.setPrice(this.room.getPrice() - 10);

                        case 15 -> this.room.setPrice(this.room.getPrice() + 10);
                        case 16 -> this.room.setPrice(this.room.getPrice() + 100);
                        case 17 -> this.room.setPrice(this.room.getPrice() + 1000);
                    }
                    open((Player) event.getWhoClicked());
                }
            }
        }
    }

}
