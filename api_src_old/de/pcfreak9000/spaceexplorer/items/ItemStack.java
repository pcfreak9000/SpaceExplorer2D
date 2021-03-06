package de.pcfreak9000.spaceexplorer.items;

import de.pcfreak9000.space.core.registry.GameRegistry;

/**
 * a Stack of {@link Item}s
 *
 * @author pcfreak9000
 *
 */
public class ItemStack {

    public static final int MAX_STACKSIZE = 128;

    private final Item item;
    private final int count;

    public ItemStack(final Item item, final int count) {
        GameRegistry.getItemRegistry().checkRegistered(item);
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return this.item;
    }

    public int getCount() {
        return this.count;
    }

}
