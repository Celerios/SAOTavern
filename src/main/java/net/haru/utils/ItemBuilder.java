package net.haru.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemBuilder {

    private String name;
    private int amount = 1;
    private List<String> lore = new ArrayList<>();
    private Material type;
    private int damage;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> flags = new HashSet<>();
    private ItemMeta meta;
    private boolean unbreakable = false;

    public ItemBuilder(final Material type) {
        this.type = type;
        this.meta = null;
    }

    public ItemBuilder(final ItemStack itemStack) {
        this.type = itemStack.getType();
        this.amount = itemStack.getAmount();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta instanceof Damageable damageable) {
                this.damage = damageable.getDamage();
            }

            this.name = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null;
            this.lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
            this.flags = itemMeta.getItemFlags();
            this.unbreakable = itemMeta.isUnbreakable();
            this.meta = itemMeta;
        }

        this.enchantments = itemStack.getEnchantments();
    }

    public Material getType() {
        return type;
    }

    public ItemBuilder setType(final Material type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    public ItemMeta getMeta() {
        return this.meta;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemBuilder setLore(final List<String> lore) {
        this.lore = lore;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public ItemBuilder setAmount(final int amount) {
        this.amount = amount;
        return this;
    }

    public int getDamage() {
        return damage;
    }

    public ItemBuilder setDamage(final int damage) {
        this.damage = damage;
        return this;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ItemBuilder addEnchantments(final Enchantment ench, final int power) {
        this.enchantments.put(ench, power);
        return this;
    }

    public Set<ItemFlag> getFlags() {
        return flags;
    }

    public ItemBuilder addFlag(final ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(type);
        itemStack.setAmount(amount);

        ItemMeta itemMeta = this.meta == null ? itemStack.getItemMeta() : meta;

        if (itemMeta == null) {
            throw new IllegalStateException("ItemMeta could not be obtained for material: " + type);
        }

        if (name != null) itemMeta.setDisplayName(name);
        if (lore != null) itemMeta.setLore(lore);
        itemMeta.setUnbreakable(unbreakable);

        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        }

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        for (ItemFlag flag : flags) {
            itemMeta.addItemFlags(flag);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
