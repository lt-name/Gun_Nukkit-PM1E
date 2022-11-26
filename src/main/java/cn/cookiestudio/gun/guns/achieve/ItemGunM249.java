package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunM249 extends ItemGunBase {

    public ItemGunM249(Integer meta, int count) {
        super(getGunData(ItemGunM249.class).getGunId(), meta, count, getGunData(ItemGunM249.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunM249(Integer meta) {
        this(meta, 1);
    }

    public ItemGunM249() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 14;
    }

    @Override
    public float getDropItemScale() {
        return 0.045f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagM249();
    }

    public static class ItemMagM249 extends ItemMagBase {

        public ItemMagM249(Integer meta, int count) {
            super(getGunData(ItemGunM249.class).getMagId(), meta, count, getGunData(ItemGunM249.class).getMagName());
            this.setCustomName(getGunData(ItemGunM249.class).getMagName());
        }

        public ItemMagM249(Integer meta) {
            this(meta, 1);
        }

        public ItemMagM249() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 15;
        }

        @Override
        public float getDropItemScale() {
            return 0.05f;
        }
    }
}
