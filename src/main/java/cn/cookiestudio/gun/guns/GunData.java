package cn.cookiestudio.gun.guns;

import cn.cookiestudio.gun.GunPlugin;
import cn.cookiestudio.gun.guns.achieve.ItemGunM3;
import cn.cookiestudio.gun.network.AnimateEntityPacket;
import cn.cookiestudio.gun.network.CameraShakePacket;
import cn.cookiestudio.gun.utils.BVector3;
import cn.cookiestudio.gun.utils.SoundUtil;
import cn.cookiestudio.gun.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

@Getter
@Setter
public class GunData {

    private static Random random = new Random(System.currentTimeMillis());
    private static String emptyGunSound = "empty_gun";

    private int magSize;
    private double fireCoolDown;
    private double reloadTime;
    private int slownessLevel;
    private int slownessLevelAim;
    private double hitDamage;
    private double range;
    private double recoil;
    private String particle;
    private FireParticle fireParticle;
    private String gunName;
    private String magName;
    private String fireSound;
    private String magInSound;
    private String magOutSound;
    private String reloadAnimationTP;
    private String reloadAnimationFP;
    private String animationControllerTP;
    private String animationControllerFP;
    private int gunId;
    private int magId;
    private double fireSwingIntensity;
    private double fireSwingDuration;

    @Builder
    public GunData(int gunId, int magId, String gunName, String magName, int magSize, double fireCoolDown, double reloadTime, int slownessLevel, int slownessLevelAim, double hitDamage, double range, double recoil, String particle, double fireSwingIntensity, double fireSwingDuration) {
        this.gunId = gunId;
        this.magId = magId;

        //storage
        this.gunName = gunName;
        this.magSize = magSize;
        this.fireCoolDown = fireCoolDown;
        this.reloadTime = reloadTime;
        this.slownessLevel = slownessLevel;
        this.slownessLevelAim = slownessLevelAim;
        this.fireSwingIntensity = fireSwingIntensity;
        this.fireSwingDuration = fireSwingDuration;
        this.hitDamage = hitDamage;
        this.range = range;
        this.particle = particle;
        this.magName = magName;
        this.recoil = recoil;

        //dynamic
        this.fireParticle = new FireParticle();
        this.fireSound = gunName + "_fire";
        this.magInSound = gunName + "_magin";
        this.magOutSound = gunName + "_magout";
        this.reloadAnimationFP = "animation." + this.gunName + ".first_person.reload";
        this.reloadAnimationTP = "animation." + this.gunName + ".third_person.reload";
        this.animationControllerFP = "controller.animation." + this.gunName + ".first_person";
        this.animationControllerTP = "controller.animation." + this.gunName + ".third_person";
    }

    public void fire(Player player, ItemGunBase gunType) {
        SoundUtil.playSound(player, this.getFireSound(), 1.0F, 1.0F);
        Utils.shakeCamera(player, (float) fireSwingIntensity, 0.1F, CameraShakePacket.CameraShakeType.ROTATIONAL, CameraShakePacket.CameraShakeAction.ADD);
        if (player.isSprinting()) {
            player.setSprinting(false);
            player.sendMovementSpeed(player.getMovementSpeed());
        }
        Player[] showParticlePlayers = Server
                .getInstance()
                .getOnlinePlayers()
                .values()
                .stream()
                .filter(p -> GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(p.getName()).isOpenTrajectoryParticle())
                .toArray(Player[]::new);
        if (gunType instanceof ItemGunM3) {
            Location location = player.clone();
            for (int i = 1; i <= 10; i++) {
                player.yaw += random.nextInt(11) - 5;
                player.pitch += random.nextInt(11) - 5;
                fireParticle.accept(player, showParticlePlayers);
                player.setRotation(location.getYaw(), location.getPitch());
            }
        } else {
            fireParticle.accept(player, showParticlePlayers);
        }
        if (recoil != 0) {
            Vector3 vector3 = getRecoilPos(player, recoil);
            player.setMotion(vector3);
        }
    }

    public void fire(EntityHuman entityHuman, ItemGunBase gunType) {
        SoundUtil.playSound(entityHuman, this.getFireSound(), 1.0F, 1.0F);
        if (entityHuman.isSprinting()) {
            entityHuman.setSprinting(false);
        }
        Player[] showParticlePlayers = Server
                .getInstance()
                .getOnlinePlayers()
                .values()
                .stream()
                .filter(p -> GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(p.getName()).isOpenTrajectoryParticle())
                .toArray(Player[]::new);
        if (gunType instanceof ItemGunM3) {
            Location location = entityHuman.clone();
            for (int i = 1; i <= 10; i++) {
                entityHuman.yaw += random.nextInt(11) - 5;
                entityHuman.pitch += random.nextInt(11) - 5;
                fireParticle.accept(entityHuman, showParticlePlayers);
                entityHuman.setRotation(location.getYaw(), location.getPitch());
            }
        } else {
            fireParticle.accept(entityHuman, showParticlePlayers);
        }
        if (recoil != 0) {
            Vector3 vector3 = getRecoilPos(entityHuman, recoil);
            entityHuman.setMotion(vector3);
        }
    }

    public void startReload(EntityHuman entityHuman) {
//        playReloadAnimation(entityHuman);
        SoundUtil.playSound(entityHuman, magOutSound, 1.0F, 1.0F);
    }

    public void reloadFinish(EntityHuman entityHuman) {
        SoundUtil.playSound(entityHuman, magInSound, 1.0F, 1.0F);
    }

    public void emptyGun(EntityHuman entityHuman) {
        SoundUtil.playSound(entityHuman, emptyGunSound, 1.0F, 1.0F);
    }

    public Vector3 getRecoilPos(EntityHuman entityHuman, double length) {
        Vector3 pos = BVector3.fromLocation(entityHuman, length).rotate(180, 0).addToPos();
        pos.y = entityHuman.y;
        return pos;
    }

    public void playReloadAnimation(Player player) {
        AnimateEntityPacket packetTP = new AnimateEntityPacket();
        packetTP.setAnimation(reloadAnimationTP);
        packetTP.setNextState("");
        packetTP.setStopExpression("");
        packetTP.setController(animationControllerTP);
        packetTP.setBlendOutTime(0);
        packetTP.getEntityRuntimeIds().add(player.getId());
        player.dataPacket(packetTP);
        AnimateEntityPacket packetFP = new AnimateEntityPacket();
        packetFP.setAnimation(reloadAnimationFP);
        packetFP.setNextState("");
        packetFP.setStopExpression("");
        packetFP.setController(animationControllerFP);
        packetFP.setBlendOutTime(0);
        packetFP.getEntityRuntimeIds().add(player.getId());
        player.dataPacket(packetFP);
    }

    public void addWalkingSlownessEffect(Player player) {
        Effect effect = Effect.getEffect(Effect.SLOWNESS);
        effect.setAmplifier(this.slownessLevel - 1);
        effect.setVisible(false);
        effect.setDuration(99999);
        player.removeEffect(Effect.SLOWNESS);
        player.addEffect(effect);
    }

    public void addAimingSlownessEffect(Player player) {
        Effect effect = Effect.getEffect(Effect.SLOWNESS);
        effect.setAmplifier(this.slownessLevelAim - 1);
        effect.setVisible(false);
        effect.setDuration(99999);
        player.removeEffect(Effect.SLOWNESS);
        player.addEffect(effect);
    }

    private class FireParticle implements BiConsumer<Position, Player[]> {

        @Override
        public void accept(Position pos, Player[] showPlayers) {
            if (!(pos instanceof EntityHuman)) {
                return;
            }
            EntityHuman entityHuman = (EntityHuman) pos;
            Location pos1;
            if (entityHuman.isSneaking()) {
                pos1 = entityHuman.getLocation().add(0, -0.15, 0);
            } else {
                pos1 = entityHuman;
            }
            Map<String, List<Position>> map = new ConcurrentHashMap<>();
            Map<Integer, Position> ammoMap = new ConcurrentHashMap<>();
            Map<Entity, Integer> hitMap = new ConcurrentHashMap<>();
            List<Position> ammoParticleList = new CopyOnWriteArrayList<>();
            List<Position> hitParticleList = new CopyOnWriteArrayList<>();
            BVector3 face = BVector3.fromLocation(pos1, 0.8);
            Block blocked = null;
            Position blockedPos = null;
            for (int i = 0; i <= range * 20; i++) {
                Position lastAmmoPos = Position.fromObject(face.addToPos(pos1).add(0, 1.62, 0), pos1.level);
                Position ammoPos = Position.fromObject(face.extend(0.05).addToPos(pos1).add(0, 1.62, 0), pos1.level);
                if (!ammoPos.getLevelBlock().canPassThrough()) {
                    blocked = ammoPos.getLevelBlock();
                    blockedPos = lastAmmoPos.clone();
                    break;
                }
                ammoMap.put(i, ammoPos);
                if (i % 4 == 0) ammoParticleList.add(ammoPos);
            }
            ammoMap.entrySet().stream().forEach(entry -> {
                FullChunk chunk = entry.getValue().getChunk();
                if (chunk == null)
                    return;
                chunk.getEntities().values().stream().forEach(entity -> {
                    if (entity.getBoundingBox().isVectorInside(entry.getValue()) && !entity.equals(entityHuman)) {
                        if (hitMap.containsKey(entity)) {
                            if (hitMap.get(entity) > entry.getKey()) {
                                hitMap.put(entity, entry.getKey());
                            }
                        } else {
                            hitMap.put(entity, entry.getKey());
                        }
                    }
                });
            });
            hitMap.keySet().stream().forEach(entity -> {
                EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entityHuman, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, (float) hitDamage, 0F);
                event.setAttackCooldown(0);
                entity.attack(event);
                hitParticleList.add(ammoMap.get(hitMap.get(entity)));
            });
            for (Position hitPos : hitParticleList) {
                hitPos.getLevel().addParticle(new DestroyBlockParticle(hitPos, Block.get(152)));
            }
            if (blocked != null)
                blocked.getLevel().addParticle(new DestroyBlockParticle(blockedPos, blocked));
            map.put(particle, ammoParticleList);
            Position fireSmokePos = Position.fromObject(BVector3.fromLocation(pos1, 0.8).addToPos(pos1).add(0, 1.62, 0), pos1.level);
            if (entityHuman instanceof Player &&
                    GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(entityHuman.getName()).isOpenMuzzleParticle())
                Utils.sendParticle("minecraft:eyeofender_death_explode_particle", fireSmokePos, Server.getInstance().getOnlinePlayers().values().toArray(new Player[0]));
            for (Map.Entry<String, List<Position>> entry : map.entrySet()) {
                String particleName = entry.getKey();
                Position[] particlePositions = entry.getValue().toArray(new Position[0]);
                for (Position particlePosition : particlePositions) {
                    Utils.sendParticle(particleName, particlePosition, showPlayers);
                }
            }
        }
    }
}
