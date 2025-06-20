package net.haru.gui;

import net.haru.SAOTavern;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TavernAdminGUI implements Listener {

    private final RoomAdminGUI roomAdminGUI;
    private final Tavern tavern;
    private Inventory gui;
    private Page page;

    private Player player;
    private boolean isModifyingCorners, isModifyingSign;

    private enum Page
    {
        MAIN,
        PRICE
    }

    public TavernAdminGUI(Tavern tavern)
    {
        this.tavern = tavern;
        this.roomAdminGUI = new RoomAdminGUI(tavern);
        this.gui = Bukkit.createInventory(null, 36, "Gestion de la Taverne: " + tavern.getName());
        this.page = Page.MAIN;
        this.isModifyingCorners = false;
        this.isModifyingSign = false;
        SAOTavern.INSTANCE.getServer().getPluginManager().registerEvents(this, SAOTavern.INSTANCE);
    }

    public void open(Player player)
    {
        this.player = player;
        this.gui.clear();

        switch (this.page)
        {
            case MAIN ->
            {
                ItemBuilder item = new ItemBuilder(Material.GOLD_INGOT);
                item.setName("Changer le prix");
                item.setLore(List.of("Prix: " + tavern.getPrice()));
                gui.setItem(10, item.build());

                item = new ItemBuilder(Material.OAK_FENCE);
                item.setName("Modifier la zone de la taverne");
                item.setLore(List.of("Taille: " + tavern.getSize().x + "x" + tavern.getSize().y  + "x" + tavern.getSize().z));
                gui.setItem(12, item.build());

                item = new ItemBuilder(Material.OAK_SIGN);
                item.setName("Modifier la position du panneau");
                if (tavern.getLocation() != null)
                    item.setLore(List.of("Position: " + tavern.getLocation().getX() + "x" + tavern.getLocation().getY()  + "x" + tavern.getLocation().getZ()));
                gui.setItem(14, item.build());

                item = new ItemBuilder(Material.RED_BED);
                item.setName("Modifier les chambres");
                gui.setItem(16, item.build());
            }




            case PRICE -> {

                ItemBuilder item = new ItemBuilder(Material.GOLD_BLOCK);
                item.setName("-1000");
                gui.setItem(9, item.build());

                item = new ItemBuilder(Material.GOLD_INGOT);
                item.setName("-100");
                gui.setItem(10, item.build());

                item = new ItemBuilder(Material.GOLD_NUGGET);
                item.setName("-10");
                gui.setItem(11, item.build());

                item = new ItemBuilder(Material.OAK_SIGN);
                item.setName("Prix: " + tavern.getPrice());
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

                item = new ItemBuilder(Material.ARROW);
                item.setName("Revenir au menu");
                gui.setItem(31, item.build());
            }
        }

        player.openInventory(gui);
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Gestion de la Taverne: " + tavern.getName())) return;
        event.setCancelled(true);


        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        switch (page)
        {
            case MAIN -> {
                if (event.getSlot() == 10)
                {
                    this.page = Page.PRICE;
                    this.open((Player) event.getWhoClicked());
                }
                else if (event.getSlot() == 12)
                {
                    this.isModifyingCorners = true;
                    this.tavern.setC1(null);
                    this.tavern.setC2(null);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage("Faite un clic gauche et un clic droit pour définir les coins de la taverne");
                }
                else if (event.getSlot() == 14)
                {
                    this.isModifyingSign = true;
                    this.tavern.setLocation(null);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage("Faite un clic droit pour définir le panneau de la taverne");
                }
                else if (event.getSlot() == 16)
                {
                    player.closeInventory();
                    this.roomAdminGUI.open(player);
                }
            }





            case PRICE -> {
                switch (event.getSlot())
                {
                    case 9 -> tavern.setPrice(tavern.getPrice() - 1000);
                    case 10 -> tavern.setPrice(tavern.getPrice() - 100);
                    case 11 -> tavern.setPrice(tavern.getPrice() - 10);

                    case 15 -> tavern.setPrice(tavern.getPrice() + 10);
                    case 16 -> tavern.setPrice(tavern.getPrice() + 100);
                    case 17 -> tavern.setPrice(tavern.getPrice() + 1000);

                    case 31 -> this.page = Page.MAIN;
                }
                open((Player) event.getWhoClicked());
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
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.tavern.getC1() == null)
                {
                    this.tavern.setC1(loc);
                    event.getPlayer().sendMessage("Position 1 mise à " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                }
                else if (event.getAction() == Action.LEFT_CLICK_BLOCK && this.tavern.getC2() == null)
                {
                    this.tavern.setC2(loc);
                    event.getPlayer().sendMessage("Position 2 mise à " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                }

                // Check if done and reopen gui
                if (this.tavern.hasCorners())
                {
                    this.isModifyingCorners = false;
                    open(event.getPlayer());
                }
            }

            // Set sign of tavern
            if (this.isModifyingSign)
            {
                event.setCancelled(true);
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.tavern.getLocation() == null)
                {
                    if (event.getClickedBlock().getType().toString().contains("SIGN"))
                    {
                        this.isModifyingSign = false;
                        this.tavern.setLocation(loc);
                        event.getPlayer().sendMessage("Position du panneau de la taverne mise à " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                        open(event.getPlayer());
                    }
                    else
                        event.getPlayer().sendMessage("Veuillez cliquer sur un panneau");
                }
            }
        }
    }
}
