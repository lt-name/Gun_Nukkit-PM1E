package cn.cookiestudio.gun.guns;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.EntityDefinition;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.types.EntityLink;
import lombok.Getter;
import lombok.Setter;

import static cn.nukkit.network.protocol.SetEntityLinkPacket.TYPE_PASSENGER;

@Getter
@Setter
public class EntityCustomItem extends EntityLiving implements CustomEntity {

    public final static int NETWORK_ID = 0;
    public final static EntityDefinition DEFINITION = EntityDefinition.builder()
            .identifier("pixelpoly:firearm_item")
            .spawnEgg(false)
            .implementation(EntityCustomItem.class)
            .build();

    public boolean hadCollision = false;

    @Getter
    private Item item;

    public EntityCustomItem(FullChunk chunk, CompoundTag nbt, int skinId, float scale, Item item) {
        super(chunk, nbt.putInt("skinId", skinId));
        this.setScale(scale);
        this.item = item;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        return DEFINITION;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setDataProperty(new IntEntityData(DATA_SKIN_ID, namedTag.getInt("skinId")));
    }

    @Override
    public DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        addEntity.id = this.getIdentifier();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.TYPE_RIDER : TYPE_PASSENGER, false, false);
        }

        return addEntity;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            MovingObjectPosition movingObjectPosition = null;

            if (!this.isCollided) {
                this.motionY -= this.getGravity();
            }

            Vector3 moveVector = new Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);


            double nearDistance = Integer.MAX_VALUE;

            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.isCollided && !this.hadCollision) {
                this.hadCollision = true;

                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;

                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
                this.yaw = Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
                this.pitch = Math.atan2(this.motionY, f) * 180 / Math.PI;
                hasUpdate = true;
            }

            this.updateMovement();

        }

        return hasUpdate;
    }
}
