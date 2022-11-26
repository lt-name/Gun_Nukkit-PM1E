package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import lombok.Getter;

@Getter
public class ItemGunMk18 extends ItemGunBase {

    public ItemGunMk18(Integer meta, int count) {
        super(getGunData(ItemGunMk18.class).getGunId(), meta, count, getGunData(ItemGunMk18.class).getGunName());
        gunData = getGunData(this.getClass());
        this.setTextureName(gunData.getGunName());
        this.setCustomName(gunData.getGunName());
        if (!this.getNamedTag().contains("ammoCount")) {
            this.setAmmoCount(this.getGunData().getMagSize());
        } else {
            this.setAmmoCount(this.getAmmoCount());
        }
    }

    public ItemGunMk18(Integer meta) {
        this(meta, 1);
    }

    public ItemGunMk18() {
        this(0);
    }

    public void doInit() {
    }

    @Override
    public int getSkinId() {
        return 16;
    }

    @Override
    public float getDropItemScale() {
        return 0.06f;
    }

    @Override
    public ItemMagBase getItemMagObject() {
        return new ItemMagMk18();
    }

    public static class ItemMagMk18 extends ItemMagBase {

        public ItemMagMk18(Integer meta, int count) {
            super(getGunData(ItemGunMk18.class).getMagId(), meta, count, getGunData(ItemGunMk18.class).getMagName());
            this.setCustomName(getGunData(ItemGunMk18.class).getMagName());
        }

        public ItemMagMk18(Integer meta) {
            this(meta, 1);
        }

        public ItemMagMk18() {
            this(0);
        }

        @Override
        public int getSkinId() {
            return 17;
        }

        @Override
        public float getDropItemScale() {
            return 0.05f;
        }
    }
}
