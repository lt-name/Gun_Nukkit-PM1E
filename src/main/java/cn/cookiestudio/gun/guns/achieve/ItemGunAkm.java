package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunAkm extends ItemGunBase {

    public ItemGunAkm(Integer meta, int count) {
        super(getGunData(ItemGunAkm.class).getGunId(), meta, count, getGunData(ItemGunAkm.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunAkm(Integer meta) {
        this(meta, 1);
    }

    public ItemGunAkm() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 12;
    }

    @Override
    public float getDropItemScale() {
        return 0.05f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagAkm();
    }

    public static class ItemMagAkm extends ItemMagBase {

        public ItemMagAkm(Integer meta, int count) {
            super(getGunData(ItemGunAkm.class).getMagId(), meta, count, getGunData(ItemGunAkm.class).getMagName());
            this.setCustomName(getGunData(ItemGunAkm.class).getMagName());
        }

        public ItemMagAkm(Integer meta) {
            this(meta, 1);
        }

        public ItemMagAkm() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 13;
        }

        @Override
        public float getDropItemScale() {
            return 0.05f;
        }
    }
}
