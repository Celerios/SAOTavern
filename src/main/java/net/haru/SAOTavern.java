package net.haru;

import net.haru.commands.TavernCommand;
import net.haru.tasks.UpdateTask;
import net.haru.tavern.TavernManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SAOTavern extends JavaPlugin {

    public static SAOTavern INSTANCE;
    private PluginCommand tavernCmd;
    private TavernManager tavernManager;
    private UpdateTask updateTask;

    @Override
    public void onEnable() {
        INSTANCE = this;

        saveDefaultConfig();

        // Register manager
        this.tavernManager = new TavernManager();

        // Register commands
        this.tavernCmd = getCommand("tavern");
        if (this.tavernCmd != null)
            this.tavernCmd.setExecutor(new TavernCommand());

        // Refresh task for signs
        this.updateTask = new UpdateTask(this);
        this.updateTask.enable();

        getLogger().info("SAOTavern enabled!");
    }


    @Override
    public void onDisable() {
        getLogger().info("SAOTavern disabled!");

        // Stop task
        this.updateTask.disable();

        // Remove managers
        this.tavernManager.disable();

        // Remove command executors
        this.tavernCmd.setExecutor(null);
    }



    public TavernManager getTavernManager()
    {
        return this.tavernManager;
    }
}