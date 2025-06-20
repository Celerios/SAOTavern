package net.haru.rooms;

import net.haru.SAOTavern;
import org.bukkit.Location;
import org.joml.Vector3i;

import java.util.UUID;

public abstract class ARoom {

    private final UUID uuid;
    private Location location, c1, c2;
    private int price;
    private int locMinutes;

    private UUID owner;
    private String ownerName;
    private long rentTime, endOfRent;

    public ARoom()
    {
        this.uuid = UUID.randomUUID();
        this.price = 100;
        this.locMinutes = 60 * 24;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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


    public int getLocMinutes() {
        return locMinutes;
    }
    public void setLocMinutes(int locMinutes) {
        this.locMinutes = Math.max(0, locMinutes);
    }


    public void setOwner(UUID owner) {
        this.owner = owner;
        if (this.owner == null)
            this.ownerName = null;
        else
        {
            this.ownerName = SAOTavern.INSTANCE.getServer().getPlayer(this.owner).getDisplayName();
            this.rentTime = System.currentTimeMillis();
            this.endOfRent = this.rentTime + (this.getLocMinutes() * 1000L * 60L);
        }
    }
    public UUID getOwner() {
        return owner;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public UUID getUuid() {
        return uuid;
    }


    public void addRent()
    {
        this.endOfRent += this.getLocMinutes() * 1000L * 60L;
    }
    public long getMinutesRemaining() {
        long remaining = this.endOfRent - System.currentTimeMillis();
        return Math.max(remaining / 60000, 0);
    }
}
