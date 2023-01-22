package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunAwp extends ItemGunBase {

    public ItemGunAwp(Integer meta, int count) {
        super(getGunData(ItemGunAwp.class).getGunId(), meta, count, getGunData(ItemGunAwp.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunAwp(Integer meta) {
        this(meta, 1);
    }

    public ItemGunAwp() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 5;
    }

    @Override
    public float getDropItemScale() {
        return 0.1f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagAwp();
    }

    public static class ItemMagAwp extends ItemMagBase {

        public ItemMagAwp(Integer meta, int count) {
            super(getGunData(ItemGunAwp.class).getMagId(), meta, count, getGunData(ItemGunAwp.class).getMagName());
            this.setCustomName(getGunData(ItemGunAwp.class).getMagName());
        }

        public ItemMagAwp(Integer meta) {
            this(meta, 1);
        }

        public ItemMagAwp() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 6;
        }

        @Override
        public float getDropItemScale() {
            return 0.15f;
        }
    }
}
