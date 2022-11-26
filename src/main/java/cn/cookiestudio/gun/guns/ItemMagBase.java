package cn.cookiestudio.gun.guns;

import cn.lanink.customitemapi.item.ItemCustom;

public abstract class ItemMagBase extends ItemCustom {
    public ItemMagBase(int id) {
        super(id);
    }

    public ItemMagBase(int id, Integer meta) {
        super(id, meta);
    }

    public ItemMagBase(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    public ItemMagBase(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public abstract int getSkinId();

    public abstract float getDropItemScale();

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public boolean allowOffHand() {
        return true;
    }

    public String getCreativeGroup() {
        return "itemGroup.name.ammo";
    }

}
