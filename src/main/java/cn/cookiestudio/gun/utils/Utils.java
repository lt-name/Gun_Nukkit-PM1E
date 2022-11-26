package cn.cookiestudio.gun.utils;

import cn.cookiestudio.gun.network.CameraShakePacket;
import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author LT_Name
 */
public class Utils {

    private Utils() {

    }

    public static void shakeCamera(Player player, float intensity, float duration, CameraShakePacket.CameraShakeType shakeType, CameraShakePacket.CameraShakeAction shakeAction) {
        CameraShakePacket packet = new CameraShakePacket();
        packet.setIntensity(intensity);
        packet.setDuration(duration);
        packet.setShakeType(shakeType);
        packet.setShakeAction(shakeAction);
        player.dataPacket(packet);
    }

    public static void sendParticle(String identifier, Position pos, Player[] showPlayers) {
        Arrays.stream(showPlayers).forEach(player -> {
            if (!player.isOnline())
                return;
            SpawnParticleEffectPacket packet = new SpawnParticleEffectPacket();
            packet.identifier = identifier;
            packet.dimensionId = pos.getLevel().getDimension();
            packet.position = pos.asVector3f();
            try {
                player.dataPacket(packet);
            } catch (Throwable t) {
            }
        });
    }

    public static double rand(double min, double max) {
        if (min == max) {
            return max;
        }
        return min + Math.random() * (max-min);
    }

    public static int toInt(Object object) {
        return new BigDecimal(object.toString()).intValue();
    }

}
