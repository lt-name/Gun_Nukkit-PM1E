package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunBarrett extends ItemGunBase {

    public ItemGunBarrett(Integer meta, int count) {
        super(getGunData(ItemGunBarrett.class).getGunId(), meta, count, getGunData(ItemGunBarrett.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunBarrett(Integer meta) {
        this(meta, 1);
    }

    public ItemGunBarrett() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 7;
    }

    @Override
    public float getDropItemScale() {
        return 0.08f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagBarrett();
    }

    public static class ItemMagBarrett extends ItemMagBase {
        public ItemMagBarrett(Integer meta, int count) {
            super(getGunData(ItemGunBarrett.class).getMagId(), meta, count, getGunData(ItemGunBarrett.class).getMagName());
            this.setCustomName(getGunData(ItemGunBarrett.class).getMagName());
        }

        public ItemMagBarrett(Integer meta) {
            this(meta, 1);
        }

        public ItemMagBarrett() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 8;
        }

        @Override
        public float getDropItemScale() {
            return 0.15f;
        }
    }
}
