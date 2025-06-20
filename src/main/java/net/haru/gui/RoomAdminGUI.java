package net.haru.gui;

import net.haru.SAOTavern;
import net.haru.commands.TavernCommand;
import net.haru.rooms.ARoom;
import net.haru.rooms.impl.BaseRoom;
import net.haru.tavern.Tavern;
import net.haru.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RoomAdminGUI implements Listener {

    private final Tavern tavern;
    private Inventory gui;
    private int page;
    private ARoom room;

    private Player player;
    private boolean isModifyingCorners, isModifyingSign;

    public RoomAdminGUI(Tavern tavern)
    {
        this.tavern = tavern;
        this.gui = Bukkit.createInventory(null, 45, "Gestion des chambre de: " + tavern.getName());
        this.page = 0;
        this.room = null;
        this.isModifyingCorners = false;
        this.isModifyingSign = false;
        SAOTavern.INSTANCE.getServer().getPluginManager().registerEvents(this, SAOTavern.INSTANCE);
    }

    public void open(Player player)
    {
        this.player = player;
        this.gui.clear();


        // Room listing/creation
        if (this.room == null)
        {
            ARoom room;
            int start = this.page * 27;
            for (int i = 0; i < 27; i++)
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

            ItemBuilder item = new ItemBuilder(Material.ARROW);
            item.setName("Revenir en arrière");
            gui.setItem(36, item.build());

            // Page
            if (page > 0)
            {
                item = new ItemBuilder(Material.ARROW);
                item.setName("Précédent");
                gui.setItem(38, item.build());
            }

            item = new ItemBuilder(Material.BLUE_BED);
            item.setName("Nouvelle chambre");
            gui.setItem(40, item.build());

            if (start == this.page + 27 && this.tavern.getRoom(start) != null)
            {
                item = new ItemBuilder(Material.ARROW);
                item.setName("Suivant");
                gui.setItem(42, item.build());
            }
        }


        // Room management
        else {
            ItemBuilder item = new ItemBuilder(Material.OAK_FENCE);
            item.setName("Modifier la zone de la chambre");
            item.setLore(List.of("Taille: " + this.room.getSize().x + "x" + this.room.getSize().y  + "x" + this.room.getSize().z));
            gui.setItem(20, item.build());

            item = new ItemBuilder(Material.OAK_SIGN);
            item.setName("Modifier la position du panneau");
            if (this.room.getLocation() != null)
                item.setLore(List.of("Position: " + this.room.getLocation().getX() + "x" + this.room.getLocation().getY()  + "x" + this.room.getLocation().getZ()));
            gui.setItem(22, item.build());

            item = new ItemBuilder(Material.BARRIER);
            item.setName("Supprimer la chambre");
            gui.setItem(24, item.build());

            item = new ItemBuilder(Material.ARROW);
            item.setName("Revenir au menu");
            gui.setItem(40, item.build());
        }

        player.openInventory(gui);
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Gestion des chambre de: " + tavern.getName())) return;
        event.setCancelled(true);


        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Page/Back
        if (clicked.getType() == Material.ARROW) {
            if (event.getSlot() == 36)
            {
                player.closeInventory();
                TavernCommand.tAdminGuis.get(this.tavern.getName()).open(player);
            }
            else if (event.getSlot() == 38)
            {
                this.page--;
                this.open((Player) event.getWhoClicked());
            }
            else if (event.getSlot() == 42)
            {
                this.page++;
                this.open((Player) event.getWhoClicked());
            }

            // Back from room menu
            else if (event.getSlot() == 40)
            {
                this.room = null;
                this.open((Player) event.getWhoClicked());
            }
        }

        // Room creation
        if (clicked.getType() == Material.BLUE_BED)
        {
            if (event.getSlot() == 40)
            {
                // Can add multiple room types here
                ARoom room1 = new BaseRoom();
                this.tavern.addRoom(room1);

                // Set room and refresh
                this.room = room1;
                open(player);
            }
        }


        // Room selection
        else if (clicked.getType() == Material.RED_BED)
        {
            ARoom room1 = this.tavern.getRoom(Integer.parseInt(clicked.getItemMeta().getDisplayName().replace("Chambre n°", "")));
            if (room1 != null)
                this.room = room1;
            open(player);
        }


        // Room management
        if (this.room != null)
        {
            if (event.getSlot() == 20)
            {
                this.isModifyingCorners = true;
                this.room.setC1(null);
                this.room.setC2(null);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage("Faite un clic gauche et un clic droit pour définir les coins de la chambre");
            }
            else if (event.getSlot() == 22)
            {
                this.isModifyingSign = true;
                this.room.setLocation(null);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage("Faite un clic droit pour définir le panneau de la chambre");
            }
            else if (event.getSlot() == 24)
            {
                this.tavern.removeRoom(this.room);
                this.room = null;
                open(player);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.getPlayer() == this.player)
        {

            // Get loc
            if (event.getClickedBlock() == null)
                return;
            Location loc = event.getClickedBlock().getLocation();


            // Set corners
            if (this.isModifyingCorners)
            {
                event.setCancelled(true);
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.room.getC1() == null)
                {
                    this.room.setC1(loc);
                    event.getPlayer().sendMessage("Position 1 mise à " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                }
                else if (event.getAction() == Action.LEFT_CLICK_BLOCK && this.room.getC2() == null)
                {
                    this.room.setC2(loc);
                    event.getPlayer().sendMessage("Position 2 mise à " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                }

                // Check if done and reopen gui
                if (this.room.hasCorners())
                {
                    this.isModifyingCorners = false;
                    open(event.getPlayer());
                }
            }

            // Set sign of room
            if (this.isModifyingSign)
            {
                event.setCancelled(true);
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.room.getLocation() == null)
                {
                    if (event.getClickedBlock().getType().toString().contains("SIGN"))
                    {
                        this.isModifyingSign = false;
                        this.room.setLocation(loc);
                        event.getPlayer().sendMessage("Position du panneau de la chambre mise à " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                        open(event.getPlayer());
                    }
                    else
                        event.getPlayer().sendMessage("Veuillez cliquer sur un panneau");
                }
            }
        }
    }
}
