package net.haru.commands;

import net.haru.SAOTavern;
import net.haru.gui.AdminManagementGUI;
import net.haru.gui.TavernAdminGUI;
import net.haru.tavern.Tavern;
import net.haru.tavern.TavernManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TavernCommand implements CommandExecutor {

    private final TavernManager tavernManager;

    public static Map<String, TavernAdminGUI> tAdminGuis;
    private static Map<UUID, AdminManagementGUI> adminGuis;

    public TavernCommand()
    {
        this.tavernManager = SAOTavern.INSTANCE.getTavernManager();
        tAdminGuis = new HashMap<>();
        adminGuis = new HashMap<>();
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String cmd, String[] args) {

        if (commandSender instanceof Player player && args.length > 0 && isAdmin(player))
        {
            switch (args[0])
            {
                // Tavern creation
                case "create":
                {
                    if (args.length > 1)
                    {
                        String name = args[1];
                        if (!this.tavernManager.tavernExist(name))
                        {
                            // Can do: multiple tavern type
                            Tavern tavern = new Tavern(name);
                            this.tavernManager.addTavern(tavern);
                            player.sendMessage("La taverne " + name + " a été créée!");
                        }
                        else
                            player.sendMessage("La taverne " + name + " existe déjà!");
                    }
                    else
                        player.sendMessage("Veuillez spécifier le nom de la taverne!");
                    break;
                }

                // Tavern deletion
                case "delete":
                {
                    if (args.length > 1)
                    {
                        String name = args[1];
                        if (!this.tavernManager.tavernExist(name))
                            player.sendMessage("La taverne " + name + " n'existe pas!");
                        else
                        {
                            this.tavernManager.removeTavern(name);
                            player.sendMessage("La taverne " + name + " a été supprimée!");
                        }
                    }
                    else
                        player.sendMessage("Veuillez spécifier le nom de la taverne!");
                    break;
                }



                // Tavern management
                case "manage":
                {
                    // With name precised open wanted tavern gui
                    if (args.length > 1) {
                        String name = args[1];
                        if (!this.tavernManager.tavernExist(name))
                            player.sendMessage("La taverne " + name + " n'existe pas!");
                        else
                        {
                            Tavern tavern = this.tavernManager.getTavern(name);
                            if (!tAdminGuis.containsKey(tavern.getName()))
                                tAdminGuis.put(tavern.getName(), new TavernAdminGUI(tavern));
                            tAdminGuis.get(tavern.getName()).open(player);
                        }
                    }

                    // General management gui
                    else
                    {
                        if (!adminGuis.containsKey(player.getUniqueId()))
                            adminGuis.put(player.getUniqueId(), new AdminManagementGUI());
                        adminGuis.get(player.getUniqueId()).open(player);
                    }
                    break;
                }
            }

            return true;
        }
        return false;
    }




    private boolean isAdmin(Player player)
    {

        if (!player.hasPermission("tavern.admin"))
        {
            player.sendMessage("Tu n'as pas la permission de faire cette commande!");
            return false;
        }

        return true;
    }
}
