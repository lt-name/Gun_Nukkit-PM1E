package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunM3 extends ItemGunBase {

    public ItemGunM3(Integer meta, int count) {
        super(getGunData(ItemGunM3.class).getGunId(), meta, count, getGunData(ItemGunM3.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunM3(Integer meta) {
        this(meta, 1);
    }

    public ItemGunM3() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 9;
    }

    @Override
    public float getDropItemScale() {
        return 0.1f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagM3();
    }

    public static class ItemMagM3 extends ItemMagBase {

        public ItemMagM3(Integer meta, int count) {
            super(getGunData(ItemGunM3.class).getMagId(), meta, count, getGunData(ItemGunM3.class).getMagName());
            this.setCustomName(getGunData(ItemGunM3.class).getMagName());
        }

        public ItemMagM3(Integer meta) {
            this(meta, 1);
        }

        public ItemMagM3() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 10;
        }

        @Override
        public float getDropItemScale() {
            return 0.1f;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
}
