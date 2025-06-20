package net.haru.tavern.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.haru.rooms.ARoom;
import net.haru.rooms.impl.BaseRoom;
import org.bukkit.Location;

import java.io.IOException;

public class RoomAdapter extends TypeAdapter<ARoom> {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).create();

    @Override
    public void write(JsonWriter out, ARoom value) throws IOException {
        JsonObject json = gson.toJsonTree(value).getAsJsonObject();
        json.addProperty("type", value.getClass().getSimpleName());
        gson.toJson(json, out);
    }

    @Override
    public ARoom read(JsonReader in) throws IOException {
        JsonObject json = gson.fromJson(in, JsonObject.class);
        String type = json.get("type").getAsString();

        // Add different room implementation here
        Class<? extends ARoom> clazz = switch (type) {
            case "BaseRoom" -> BaseRoom.class;
            default -> throw new IllegalArgumentException("Unknown room type: " + type);
        };

        return gson.fromJson(json, clazz);
    }
}