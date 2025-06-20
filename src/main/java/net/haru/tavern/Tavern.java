package net.haru.tavern;

import net.haru.SAOTavern;
import net.haru.rooms.ARoom;
import org.bukkit.Location;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tavern {

    private final String name;
    private List<ARoom> rooms;
    private Location location, c1, c2;
    private int price;

    private UUID owner;
    private String ownerName;

    private long rentTime, endOfRent;
    private int income;

    private List<Location> placedDecoration;

    public Tavern(String name)
    {
        this.name = name;
        this.rooms = new ArrayList<>();
        this.price = 1000;
        this.placedDecoration = new ArrayList<>();
    }


    public String getName()
    {
        return this.name;
    }


    public void setC1(Location c1) {
        this.c1 = c1;
    }
    public Location getC1() {
        return c1;
    }

    public void setC2(Location c2) {
        this.c2 = c2;
    }
    public Location getC2() {
        return c2;
    }

    public boolean hasCorners()
    {
        return this.c1 != null && this.c2 != null;
    }

    public Vector3i getSize()
    {
        if (this.c1 == null || this.c2 == null)
            return new Vector3i(0,0,0);

        int x = Math.abs(c1.getBlockX() - c2.getBlockX()) + 1;
        int y = Math.abs(c1.getBlockY() - c2.getBlockY()) + 1;
        int z = Math.abs(c1.getBlockZ() - c2.getBlockZ()) + 1;

        return new Vector3i(x, y, z);
    }


    public UUID getOwner()
    {
        return this.owner;
    }
    public String getOwnerName()
    {
        return this.ownerName;
    }
    public void setOwner(UUID owner) {
        this.owner = owner;
        this.income = 0;
        if (this.owner == null)
            this.ownerName = null;
        else
        {
            this.ownerName = SAOTavern.INSTANCE.getServer().getPlayer(this.owner).getDisplayName();
            this.rentTime = System.currentTimeMillis();
            this.endOfRent = this.rentTime + (SAOTavern.INSTANCE.getConfig().getInt("location-time-minutes") * 1000L * 60L);
        }
    }
    public boolean isInside(Location loc) {
        if (this.location == null || this.c1 == null || this.c2 == null) return false;
        // Ensure both locations are in the same world
        if (!loc.getWorld().equals(c1.getWorld()) || !c1.getWorld().equals(c2.getWorld())) {
            return false;
        }

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        double minX = Math.min(c1.getX(), c2.getX());
        double maxX = Math.max(c1.getX(), c2.getX());

        double minY = Math.min(c1.getY(), c2.getY());
        double maxY = Math.max(c1.getY(), c2.getY());

        double minZ = Math.min(c1.getZ(), c2.getZ());
        double maxZ = Math.max(c1.getZ(), c2.getZ());

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = Math.max(0, price);
    }




    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }


    public List<ARoom> getRooms() {
        if (this.rooms == null)
            this.rooms = new ArrayList<>();
        return rooms;
    }
    public void addRoom(ARoom room)
    {
        this.rooms.add(room);
    }
    public void removeRoom(ARoom room)
    {
        this.rooms.remove(room);
    }
    public ARoom getRoom(int index)
    {
        if (this.rooms == null)
            this.rooms = new ArrayList<>();
        if (index >= this.rooms.size())
            return null;
        return this.rooms.get(index);
    }


    public void addIncome(int income)
    {
        this.income += income;
    }
    public int getIncome() {
        return income;
    }

    public void addRent()
    {
        this.endOfRent += SAOTavern.INSTANCE.getConfig().getInt("location-time-minutes") * 1000L * 60L;
    }
    public long getMinutesRemaining() {
        long remaining = this.endOfRent - System.currentTimeMillis();
        return Math.max(remaining / 60000, 0);
    }


    public void addDecoration(Location loc)
    {
        if (this.placedDecoration == null)
            this.placedDecoration = new ArrayList<>();
        this.placedDecoration.add(loc);
    }
    public void removeDecoration(Location loc)
    {
        this.placedDecoration.remove(loc);
    }
}
