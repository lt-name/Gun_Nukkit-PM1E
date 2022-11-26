package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunP90 extends ItemGunBase {

    public ItemGunP90(Integer meta, int count) {
        super(getGunData(ItemGunP90.class).getGunId(), meta, count, getGunData(ItemGunP90.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunP90(Integer meta) {
        this(meta, 1);
    }

    public ItemGunP90() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 3;
    }

    @Override
    public float getDropItemScale() {
        return 0.1f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagP90();
    }

    public static class ItemMagP90 extends ItemMagBase {

        public ItemMagP90(Integer meta, int count) {
            super(getGunData(ItemGunP90.class).getMagId(), meta, count, getGunData(ItemGunP90.class).getMagName());
            this.setCustomName(getGunData(ItemGunP90.class).getMagName());
        }

        public ItemMagP90(Integer meta) {
            this(meta, 1);
        }

        public ItemMagP90() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 4;
        }

        @Override
        public float getDropItemScale() {
            return 0.1f;
        }
    }
}
