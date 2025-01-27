package cn.cookiestudio.gun.guns;

import cn.cookiestudio.gun.CoolDownTimer;
import cn.cookiestudio.gun.GunPlugin;
import cn.cookiestudio.gun.playersetting.PlayerSettingMap;
import cn.lanink.customitemapi.item.ItemCustomEdible;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.potion.Effect;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public abstract class ItemGunBase extends ItemCustomEdible {


    static {
        Server.getInstance().getPluginManager().registerEvents(new Listener(), GunPlugin.getInstance());
        Server.getInstance().getScheduler().scheduleRepeatingTask(GunPlugin.getInstance(), () -> {
            Server.getInstance().getOnlinePlayers().values().forEach(player -> {
                if (player.getInventory().getItemInHand() instanceof ItemGunBase) {
                    ItemGunBase itemGun = (ItemGunBase) player.getInventory().getItemInHand();
                    if (player.isSneaking()) {
                        itemGun.getGunData().addAimingSlownessEffect(player);
                    } else {
                        itemGun.getGunData().addWalkingSlownessEffect(player);
                    }
                    if (!GunPlugin.getInstance().getCoolDownTimer().isCooling(player) || GunPlugin.getInstance().getCoolDownTimer().getCoolDownMap().get(player).getType() != CoolDownTimer.Type.RELOAD) {
                        if (GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(player.getName()).getFireMode() == PlayerSettingMap.FireMode.AUTO) {
                            if (!GunPlugin.getInstance().getFireTask().firing(player)) {
                                player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">\n§dAUTO MODE: §cOFF");
                            } else {
                                player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">\n§dAUTO MODE: §aON");
                            }
                        } else {
                            player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">");
                        }
                        return;
                    }
                    CoolDownTimer.CoolDown coolDown = GunPlugin.getInstance().getCoolDownTimer().getCoolDownMap().get(player);
                    if (coolDown.getType() == CoolDownTimer.Type.RELOAD) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("RELOAD: §a");
                        int bound = (int) (30.0 * ((double) coolDown.coolDownTick / (itemGun.getGunData().getReloadTime() * 20)));
                        for (int i = 30; i >= 1; i--) {
                            if (i < bound) stringBuilder.append("|");
                            if (i == bound) stringBuilder.append("|§c");
                            if (i > bound) stringBuilder.append("|");
                        }
                        player.sendActionBar(stringBuilder.toString(), 0, 1, 0);
                    }
                }
            });
        }, 1);
    }

    protected GunData gunData;

    public ItemGunBase(int id) {
        super(id);
    }

    public ItemGunBase(int id, Integer meta) {
        super(id, meta);
    }

    public ItemGunBase(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    public ItemGunBase(int id, Integer meta, int count, String textureName) {
        super(id, meta, count);
        this.setTextureName(textureName);
    }

    public static GunData getGunData(Class<? extends ItemGunBase> clazz) {
        return GunPlugin.getInstance().getGunDataMap().get(clazz);
    }

    public abstract int getSkinId();

    public abstract float getDropItemScale();

    @Override
    public boolean canAlwaysEat() {
        return true;
    }

    @Override
    public int getEatTick() {
        return (int) (this.getGunData().getFireCoolDown() * 20);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return false;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean allowOffHand() {
        return true;
    }

    public String getCreativeGroup() {
        return "itemGroup.name.gun";
    }

    public GunInteractAction interact(Player player) {
        if (GunPlugin.getInstance().getCoolDownTimer().isCooling(player)) {
            return GunInteractAction.COOLING;
        }
        ItemGunBase itemGun = (ItemGunBase) player.getInventory().getItemInHand();
        if (itemGun.getAmmoCount() > 0) {
            itemGun.getGunData().fire(player, itemGun);
            itemGun.setAmmoCount(itemGun.getAmmoCount() - 1);
            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), itemGun);
            GunPlugin.getInstance().getCoolDownTimer().addCoolDown(player, (int) (itemGun.getGunData().getFireCoolDown() * 20), () -> {
            }, () -> CoolDownTimer.Operator.NO_ACTION, CoolDownTimer.Type.FIRECOOLDOWN);
            return GunInteractAction.FIRE;
        }
        if (itemGun.getAmmoCount() == 0) {//todo:debug
            if (itemGun.reload(player))
                return GunInteractAction.RELOAD;
            else
                return GunInteractAction.EMPTY_GUN;
        }
        return null;
    }

    public GunInteractAction interact(EntityHuman entityHuman) {
        if (GunPlugin.getInstance().getCoolDownTimer().isCooling(entityHuman)) {
            return GunInteractAction.COOLING;
        }
        ItemGunBase itemGun = (ItemGunBase) entityHuman.getInventory().getItemInHand();
        if (itemGun.getAmmoCount() > 0) {
            itemGun.getGunData().fire(entityHuman, itemGun);
            itemGun.setAmmoCount(itemGun.getAmmoCount() - 1);
            entityHuman.getInventory().setItem(entityHuman.getInventory().getHeldItemIndex(), itemGun);
            GunPlugin.getInstance().getCoolDownTimer().addCoolDown(entityHuman, (int) (itemGun.getGunData().getFireCoolDown() * 20), () -> {
            }, () -> CoolDownTimer.Operator.NO_ACTION, CoolDownTimer.Type.FIRECOOLDOWN);
            return GunInteractAction.FIRE;
        }
        if (itemGun.getAmmoCount() == 0) {//todo:debug
            if (itemGun.reload(entityHuman))
                return GunInteractAction.RELOAD;
            else
                return GunInteractAction.EMPTY_GUN;
        }
        return null;
    }

    public boolean reload(Player player) {
        CoolDownTimer coolDownTimer = GunPlugin.getInstance().getCoolDownTimer();
        if (coolDownTimer.isCooling(player)) {
            CoolDownTimer.CoolDown coolDown = coolDownTimer.getCoolDownMap().get(player);
            if (coolDown.getType() == CoolDownTimer.Type.RELOAD)
                coolDownTimer.interrupt(player);
            return false;
        }
        if (player.getGamemode() != Player.CREATIVE &&!player.getInventory().contains(Item.get(this.getGunData().getMagId()))) {
            this.getGunData().emptyGun(player);
            return false;
        }
        this.getGunData().startReload(player);
        GunPlugin.getInstance().getCoolDownTimer().addCoolDown(player, (int) (this.getGunData().getReloadTime() * 20), () -> {
            this.getGunData().reloadFinish(player);
            this.setAmmoCount(this.getGunData().getMagSize());
            player.getInventory().setItem(player.getInventory().getHeldItemIndex(), this);
            if (player.getGamemode() != Player.CREATIVE) {
                for (Map.Entry<Integer, Item> entry : player.getInventory().getContents().entrySet()) {
                    Item item = entry.getValue();
                    int slot = entry.getKey();
                    if (item.equals(Item.get(this.getGunData().getMagId()))) {//todo:debug
                        item.setCount(item.count - 1);
                        player.getInventory().setItem(slot, item);
                        break;
                    }
                }
            }
        }, () -> {
            player.sendMessage("§creload interrupt!");
            return CoolDownTimer.Operator.INTERRUPT;
        }, CoolDownTimer.Type.RELOAD);
        return true;
    }

    public boolean reload(EntityHuman entityHuman) {
        CoolDownTimer coolDownTimer = GunPlugin.getInstance().getCoolDownTimer();
        if (coolDownTimer.isCooling(entityHuman)) {
            CoolDownTimer.CoolDown coolDown = coolDownTimer.getCoolDownMap().get(entityHuman);
            if (coolDown.getType() == CoolDownTimer.Type.RELOAD)
                coolDownTimer.interrupt(entityHuman);
            return false;
        }
        this.getGunData().startReload(entityHuman);
        GunPlugin.getInstance().getCoolDownTimer().addCoolDown(entityHuman, (int) (this.getGunData().getReloadTime() * 20), () -> {
            this.getGunData().reloadFinish(entityHuman);
            this.setAmmoCount(this.getGunData().getMagSize());
            entityHuman.getInventory().setItem(entityHuman.getInventory().getHeldItemIndex(), this);
        }, () -> CoolDownTimer.Operator.INTERRUPT, CoolDownTimer.Type.RELOAD);
        return true;
    }

    public int getAmmoCount() {
        if (this.getNamedTag() != null) {
            return this.getNamedTag().getInt("ammoCount");
        }
        return 0;
    }

    public void setAmmoCount(int count) {
        if (this.getNamedTag() != null) {
            this.setNamedTag(this.getNamedTag().putInt("ammoCount", count));
        } else {
            this.setNamedTag(new CompoundTag().putInt("ammoCount", count));
        }
    }

    public abstract ItemMagBase getItemMagObject();

    public enum GunInteractAction {
        FIRE,
        RELOAD,
        COOLING,
        EMPTY_GUN
    }

    private static class Listener implements cn.nukkit.event.Listener {
        @EventHandler
        public void onPlayerAnimation(PlayerAnimationEvent event) {
            if (event.getAnimationType() == AnimatePacket.Action.SWING_ARM && event.getPlayer().getInventory().getItemInHand() instanceof ItemGunBase) {
                if (GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(event.getPlayer().getName()).getFireMode() == PlayerSettingMap.FireMode.AUTO) {
                    GunPlugin.getInstance().getFireTask().changeState(event.getPlayer());
                } else {
                    ((ItemGunBase) event.getPlayer().getInventory().getItemInHand()).reload(event.getPlayer());
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onItemSpawn(ItemSpawnEvent e) {
            Item item = e.getEntity().getItem();
            EntityItem drop = e.getEntity();

            if (item instanceof ItemGunBase) {
                ItemGunBase itemGun = (ItemGunBase) item;
                EntityCustomItem customDrop = new EntityCustomItem(drop.getChunk(), drop.namedTag, itemGun.getSkinId(), itemGun.getDropItemScale(), item);
                customDrop.setMotion(drop.getMotion());
                drop.kill();
                customDrop.spawnToAll();
            } else if (item instanceof ItemMagBase) {
                ItemMagBase itemMag = (ItemMagBase) item;
                EntityCustomItem customDrop = new EntityCustomItem(drop.getChunk(), drop.namedTag, itemMag.getSkinId(), itemMag.getDropItemScale(), item);
                customDrop.setMotion(drop.getMotion());
                drop.kill();
                customDrop.spawnToAll();
            }
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getPlayer().getInventory().getItemInHand() instanceof ItemGunBase && (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
                ItemGunBase itemGun = (ItemGunBase) event.getPlayer().getInventory().getItemInHand();
                itemGun.interact(event.getPlayer());
            }
        }

        @EventHandler
        public void onEntityInteract(EntityInteractEvent event) {
            if (event.getEntity() instanceof EntityHuman) {
                EntityHuman human = (EntityHuman) event.getEntity();
                if (human.getInventory().getItemInHand() instanceof ItemGunBase) {
                    ((ItemGunBase) human.getInventory().getItemInHand()).interact(human);
                }
            }
        }

        @EventHandler
        public void onPlayerHeldItem(PlayerItemHeldEvent event) {
            if (!(event.getItem() instanceof ItemGunBase))
                event.getPlayer().removeEffect(Effect.SLOWNESS);
        }

        @EventHandler
        public void onPlayerInteractEntityGunOrMag(EntityDamageByEntityEvent event) {
            if (event.getEntity() instanceof EntityCustomItem) {
                EntityCustomItem entity = (EntityCustomItem) event.getEntity();
                int empty = ((Player) event.getDamager()).getInventory().firstEmpty(null);
                if (empty != -1){
                    ((Player) event.getDamager()).getInventory().setItem(empty, entity.getItem());
                }
                entity.close();
            }
        }

        @EventHandler
        public void onEntityGunOrMagHurt(EntityDamageEvent event){
            if (event.getEntity() instanceof EntityCustomItem){
                if (!(event instanceof EntityDamageByEntityEvent)) {
                    event.setCancelled();
                }
            }
        }

    }

}