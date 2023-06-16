package cn.cookiestudio.gun;

import cn.cookiestudio.gun.command.GunCommand;
import cn.cookiestudio.gun.guns.EntityCustomItem;
import cn.cookiestudio.gun.guns.GunData;
import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;
import cn.cookiestudio.gun.guns.achieve.*;
import cn.cookiestudio.gun.network.AnimateEntityPacket;
import cn.cookiestudio.gun.network.CameraShakePacket;
import cn.cookiestudio.gun.playersetting.PlayerSettingPool;
import cn.lanink.customitemapi.CustomItemAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.custom.EntityManager;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Logger;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GunPlugin extends PluginBase {

    @Getter
    private static GunPlugin instance;
    private Config config;
    private final Map<Class<? extends ItemGunBase>, GunData> gunDataMap = new HashMap<>();
    private final Map<String, Class<? extends ItemGunBase>> stringClassMap = new HashMap<>();
    private CoolDownTimer coolDownTimer;

    private PlayerSettingPool playerSettingPool;
    private FireTask fireTask;

    private Skin crateSkin;
    private Skin ammoBoxSkin;

    {
        stringClassMap.put("akm", ItemGunAkm.class);
        stringClassMap.put("awp", ItemGunAwp.class);
        stringClassMap.put("barrett", ItemGunBarrett.class);
        stringClassMap.put("m3", ItemGunM3.class);
        stringClassMap.put("m249", ItemGunM249.class);
        stringClassMap.put("mk18", ItemGunMk18.class);
        stringClassMap.put("mp5", ItemGunMp5.class);
        stringClassMap.put("p90", ItemGunP90.class);
        stringClassMap.put("taurus", ItemGunTaurus.class);
    }

    private static byte[] getBytes(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    @Override
    public void onEnable() {
        instance = this;
        playerSettingPool = new PlayerSettingPool();
        fireTask = new FireTask(this);
        copyResource();
        config = new Config(getDataFolder() + "/config.yml");

        this.initCrateSkin();
        this.registerPacket();

        loadGunData();
        registerEntity();
        registerListener();
        registerCommand();
        coolDownTimer = new CoolDownTimer();
        //感谢所有支持者！！！
        printSponsors();
    }

    private void registerEntity() {
        EntityManager.get().registerDefinition(EntityCustomItem.DEFINITION);
    }

    private void copyResource() {
        saveDefaultConfig();
        Path p = Paths.get(Server.getInstance().getDataPath() + "resource_packs/gun.zip");
        if (!Files.exists(p)) {
            this.getLogger().warning("未在目录" + p + "下找到材质包，正在复制，请在完成后重启服务器应用更改");
            try {
                Files.copy(this.getClass().getClassLoader().getResourceAsStream("resources/gun.zip"), p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadGunData() {
        Map<String, Object> map = config.getAll();
        AtomicInteger id = new AtomicInteger(10000);
        map.entrySet().forEach(e -> {
            Map<String, Object> value = (Map<String, Object>) e.getValue();
            GunData gunData = GunData
                    .builder()
                    .gunId(id.get())
                    .magId(1000 + id.get())
                    .gunName(e.getKey())
                    .magName((String) value.get("magName"))
                    .hitDamage((Double) value.get("hitDamage"))
                    .fireCoolDown((Double) value.get("fireCoolDown"))
                    .magSize((Integer) value.get("magSize"))
                    .slownessLevel((int) value.get("slownessLevel"))
                    .slownessLevelAim((int) value.get("slownessLevelAim"))
                    .particle((String) value.get("particle"))
                    .reloadTime((Double) value.get("reloadTime"))
                    .range((Double) value.get("range"))
                    .recoil((Double) value.get("recoil"))
                    .fireSwingIntensity((Double) value.get("fireSwingIntensity"))
                    .fireSwingDuration((Double) value.get("fireSwingDuration"))
                    .build();
            gunDataMap.put(stringClassMap.get(e.getKey()), gunData);
            try {
                ItemGunBase itemGun = stringClassMap.get(e.getKey()).newInstance();
                CustomItemAPI.getInstance().registerCustomItem(itemGun.getId(), itemGun.getClass());
                this.addCreativeItem(itemGun);

                ItemMagBase itemMagObject = itemGun.getItemMagObject();
                CustomItemAPI.getInstance().registerCustomItem(itemMagObject.getId(), itemMagObject.getClass());
                this.addCreativeItem(itemMagObject);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            id.addAndGet(1);
        });
    }

    private void addCreativeItem(Item item) {
        int[] protocols = new int[] {
                ProtocolInfo.v1_18_30,
                ProtocolInfo.v1_19_0,
                ProtocolInfo.v1_19_20,
                ProtocolInfo.v1_19_50,
                ProtocolInfo.v1_19_60,
                ProtocolInfo.v1_19_70,
                ProtocolInfo.v1_19_80,
                ProtocolInfo.v1_20_0
        };
        for (int protocol : protocols) {
            Item.addCreativeItem(protocol, item);
        }
    }

    private void initCrateSkin(){
        crateSkin = new Skin();
        try {
            crateSkin.setTrusted(true);
            crateSkin.setGeometryData(new String(getBytes(GunPlugin.getInstance().getResource("resources/model/crate/skin.json"))));
            crateSkin.setGeometryName("geometry.crate");
            crateSkin.setSkinId("crate");
            crateSkin.setSkinData(ImageIO.read(GunPlugin.getInstance().getResource("resources/model/crate/skin.png")));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        ammoBoxSkin = new Skin();
        try {
            ammoBoxSkin.setTrusted(true);
            ammoBoxSkin.setGeometryData(new String(getBytes(GunPlugin.getInstance().getResource("resources/model/ammobox/skin.json"))));
            ammoBoxSkin.setGeometryName("geometry.ammobox");
            ammoBoxSkin.setSkinId("ammobox");
            ammoBoxSkin.setSkinData(ImageIO.read(GunPlugin.getInstance().getResource("resources/model/ammobox/skin.png")));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void registerListener() {
        Server.getInstance().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onDataPacketReceive(DataPacketReceiveEvent event) {
                if (event.getPacket() instanceof EntityEventPacket) {
                    EntityEventPacket packet = (EntityEventPacket) event.getPacket();
                    if (packet.event == EntityEventPacket.EATING_ITEM) {
                        Player player = event.getPlayer();
                        if (player.getInventory().getItemInHand() instanceof ItemGunBase) {
                            event.setCancelled(true); //屏蔽右键使用枪时的食用食物声音
                        }
                    }
                }
            }
        }, this);
    }

    private void registerCommand() {
        Server.getInstance().getCommandMap().register("", new GunCommand("gun"));
    }

    private void registerPacket() {
        Server.getInstance().getNetwork().registerPacket(ProtocolInfo.ANIMATE_ENTITY_PACKET, AnimateEntityPacket.class);
        Server.getInstance().getNetwork().registerPacket(ProtocolInfo.CAMERA_SHAKE_PACKET, CameraShakePacket.class);
    }

    public void saveGunData(GunData gunData){
        String gunName = gunData.getGunName();
        config.set(gunName + ".magSize", gunData.getMagSize());
        config.set(gunName + ".fireCoolDown", gunData.getFireCoolDown());
        config.set(gunName + ".reloadTime", gunData.getReloadTime());
        config.set(gunName + ".slownessLevel", gunData.getSlownessLevel());
        config.set(gunName + ".slownessLevelAim", gunData.getSlownessLevelAim());
        config.set(gunName + ".fireSwingIntensity", gunData.getFireSwingIntensity());
        config.set(gunName + ".fireSwingDuration", gunData.getFireSwingDuration());
        config.set(gunName + ".hitDamage", gunData.getHitDamage());
        config.set(gunName + ".range", gunData.getRange());
        config.set(gunName + ".particle", gunData.getParticle());
        config.set(gunName + ".magName", gunData.getMagName());
        config.set(gunName + ".recoil", gunData.getRecoil());
        config.save();
    }

    private void printSponsors() {
        Logger logger = this.getLogger();
        logger.warning("§a感谢以下付费用户的支持！(按照时间顺序)：");
        logger.info("§akaoya00");
        logger.info("§a米奇不妙屋");
        logger.info("§aAntiPlayer");
        logger.info("§aSaber");
        logger.info("§aEmoyears");
        logger.info("§a帅哥");
        logger.info("§a冷冻罗非鱼");
        logger.info("§a乱枝");
        logger.info("§aB站从不咕咕的鸽纸");
        logger.info("§aVOCALOIDty");
        logger.info("§aSUNS服务器");
        logger.info("§aHVHCloser");
        logger.info("§a一");
        logger.info("§a抢铁特有的扫把动集");
        logger.info("§acq6biuaa");
        logger.info("§aNiceXCPLS");
        logger.info("§awakefairy");
        logger.info("§aXKSHADOW");
        logger.info("§aSoHugePenguin");
        logger.info("§a关键词");
        logger.info("§a江南/.");
        logger.info("§aX·zk");
        logger.info("§a");
        logger.info("§aanzhiren");
        logger.info("§a영도..");
        logger.warning("感谢以下开发者的帮助！：");
        logger.info("§alt_name");
        logger.info("§azimzaza4");
        logger.info("§aReiyans");
        logger.info("§a若水");
        logger.info("§a泡泡");
        logger.info("§a炙寒");
    }
}
