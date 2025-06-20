package net.haru.tavern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.haru.SAOTavern;
import net.haru.rooms.ARoom;
import net.haru.tavern.save.LocationAdapter;
import net.haru.tavern.save.RoomAdapter;
import org.bukkit.Location;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TavernManager {

    private final List<Tavern> tavernList;
    private final File tavernDir;

    public TavernManager()
    {
        // Create config dirs
        if (!SAOTavern.INSTANCE.getDataFolder().exists())
            SAOTavern.INSTANCE.getDataFolder().mkdirs();
        this.tavernDir = new File(SAOTavern.INSTANCE.getDataFolder(), "tavern");
        if (!this.tavernDir.exists())
            this.tavernDir.mkdir();


        // Load from configs
        this.tavernList = new ArrayList<>();
        this.loadTaverns();

        // Register events
        SAOTavern.INSTANCE.getServer().getPluginManager().registerEvents(new TavernListener(this), SAOTavern.INSTANCE);
    }


    // Save all taverns
    public void disable()
    {
        this.tavernList.forEach(this::saveTavernData);
    }




    // Adder/Remover
    public void addTavern(Tavern tavern)
    {
        this.tavernList.add(tavern);
        this.saveTavernData(tavern);
    }

    public boolean removeTavern(String name)
    {
        for (Tavern tavern : this.tavernList)
        {
            if (Objects.equals(tavern.getName(), name))
            {
                this.tavernList.remove(tavern);
                this.delTavernData(tavern);
                return true;
            }
        }
        return false;
    }

    public boolean tavernExist(String name)
    {
        for (Tavern tavern : this.tavernList)
            if (Objects.equals(tavern.getName(), name))
                return true;
        return false;
    }

    public Tavern getTavern(String name)
    {
        for (Tavern tavern : this.tavernList)
            if (Objects.equals(tavern.getName(), name))
                return tavern;
        return null;
    }

    public Tavern getTavern(int index)
    {
        if (index >= this.tavernList.size())
            return null;
        return this.tavernList.get(index);
    }

    public List<Tavern> getTaverns() {
        return tavernList;
    }



    // Save handling
    public void saveTavernData(Tavern tavern) {
        File tavernFile = new File(this.tavernDir, tavern.getName() + ".json");

        // Register adapter for location to avoid errors
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .registerTypeAdapter(ARoom.class, new RoomAdapter())
                .setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(tavernFile)) {
            gson.toJson(tavern, writer);
            System.out.println("Saved tavern " + tavern.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delTavernData(Tavern tavern) {
        File tavernFile = new File(this.tavernDir, tavern.getName() + ".json");
        if (tavernFile.exists()) {
            tavernFile.delete();
            System.out.println("Deleted tavern " + tavern.getName());
        }
    }

    public void loadTaverns() {
        // Register adapter for location to avoid errors
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .registerTypeAdapter(ARoom.class, new RoomAdapter())
                .setPrettyPrinting().create();

        // Check directory files
        File[] files = tavernDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null)
            return;

        // Load all files
        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                Tavern tavern = gson.fromJson(reader, Tavern.class);
                if (tavern != null) {
                    tavernList.add(tavern);
                    System.out.println("Loaded tavern " + tavern.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
