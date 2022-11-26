package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunTaurus extends ItemGunBase {

    public ItemGunTaurus(Integer meta, int count) {
        super(getGunData(ItemGunTaurus.class).getGunId(), meta, count, getGunData(ItemGunTaurus.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunTaurus(Integer meta) {
        this(meta, 1);
    }

    public ItemGunTaurus() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 1;
    }

    @Override
    public float getDropItemScale() {
        return 0.2f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagTaurus();
    }

    public static class ItemMagTaurus extends ItemMagBase {

        public ItemMagTaurus(Integer meta, int count) {
            super(getGunData(ItemGunTaurus.class).getMagId(), meta, count, getGunData(ItemGunTaurus.class).getMagName());
            this.setCustomName(getGunData(ItemGunTaurus.class).getMagName());
        }

        public ItemMagTaurus(Integer meta) {
            this(meta, 1);
        }

        public ItemMagTaurus() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 2;
        }

        @Override
        public float getDropItemScale() {
            return 0.2f;
        }
    }
}
