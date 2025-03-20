package meteordevelopment.meteorclient.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.events.entity.EntityDestroyEvent;
import meteordevelopment.meteorclient.events.entity.player.PickItemsEvent;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.packets.ContainerSlotUpdateEvent;
import meteordevelopment.meteorclient.events.packets.InventoryEvent;
import meteordevelopment.meteorclient.events.packets.PlaySoundPacketEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.mixininterface.IExplosionS2CPacket;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_2535;
import net.minecraft.class_2604;
import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_2664;
import net.minecraft.class_2672;
import net.minecraft.class_2678;
import net.minecraft.class_2716;
import net.minecraft.class_2767;
import net.minecraft.class_2775;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_8588;
import net.minecraft.class_8673;
import net.minecraft.class_8675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public abstract class ClientPlayNetworkHandlerMixin extends class_8673 {
   @Shadow
   private class_638 field_3699;
   @Unique
   private boolean ignoreChatMessage;
   @Unique
   private boolean worldNotNull;

   @Shadow
   public abstract void method_45729(String var1);

   protected ClientPlayNetworkHandlerMixin(class_310 client, class_2535 connection, class_8675 connectionState) {
      super(client, connection, connectionState);
   }

   @Inject(
      method = {"onEntitySpawn"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onEntitySpawn(class_2604 packet, CallbackInfo info) {
      if (packet != null && packet.method_11169() != null && ((NoRender)Modules.get().get(NoRender.class)).noEntity(packet.method_11169()) && ((NoRender)Modules.get().get(NoRender.class)).getDropSpawnPacket()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"onGameJoin"},
      at = {@At("HEAD")}
   )
   private void onGameJoinHead(class_2678 packet, CallbackInfo info) {
      this.worldNotNull = this.field_3699 != null;
   }

   @Inject(
      method = {"onGameJoin"},
      at = {@At("TAIL")}
   )
   private void onGameJoinTail(class_2678 packet, CallbackInfo info) {
      if (this.worldNotNull) {
         MeteorClient.EVENT_BUS.post((Object)GameLeftEvent.get());
      }

      MeteorClient.EVENT_BUS.post((Object)GameJoinedEvent.get());
   }

   @Inject(
      method = {"onEnterReconfiguration"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
   shift = Shift.AFTER
)}
   )
   private void onEnterReconfiguration(class_8588 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post((Object)GameLeftEvent.get());
   }

   @Inject(
      method = {"onPlaySound"},
      at = {@At("HEAD")}
   )
   private void onPlaySound(class_2767 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post((Object)PlaySoundPacketEvent.get(packet));
   }

   @Inject(
      method = {"onChunkData"},
      at = {@At("TAIL")}
   )
   private void onChunkData(class_2672 packet, CallbackInfo info) {
      class_2818 chunk = this.field_45588.field_1687.method_8497(packet.method_11523(), packet.method_11524());
      MeteorClient.EVENT_BUS.post((Object)(new ChunkDataEvent(chunk)));
   }

   @Inject(
      method = {"onScreenHandlerSlotUpdate"},
      at = {@At("TAIL")}
   )
   private void onContainerSlotUpdate(class_2653 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post((Object)ContainerSlotUpdateEvent.get(packet));
   }

   @Inject(
      method = {"onInventory"},
      at = {@At("TAIL")}
   )
   private void onInventory(class_2649 packet, CallbackInfo info) {
      MeteorClient.EVENT_BUS.post((Object)InventoryEvent.get(packet));
   }

   @Inject(
      method = {"onEntitiesDestroy"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/network/packet/s2c/play/EntitiesDestroyS2CPacket;getEntityIds()Lit/unimi/dsi/fastutil/ints/IntList;"
)}
   )
   private void onEntitiesDestroy(class_2716 packet, CallbackInfo ci) {
      IntListIterator var3 = packet.method_36548().iterator();

      while(var3.hasNext()) {
         int id = (Integer)var3.next();
         MeteorClient.EVENT_BUS.post((Object)EntityDestroyEvent.get(this.field_45588.field_1687.method_8469(id)));
      }

   }

   @Inject(
      method = {"onExplosion"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
   shift = Shift.AFTER
)}
   )
   private void onExplosionVelocity(class_2664 packet, CallbackInfo ci) {
      Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
      if ((Boolean)velocity.explosions.get()) {
         ((IExplosionS2CPacket)packet).setVelocityX((float)((double)packet.method_11472() * velocity.getHorizontal(velocity.explosionsHorizontal)));
         ((IExplosionS2CPacket)packet).setVelocityY((float)((double)packet.method_11473() * velocity.getVertical(velocity.explosionsVertical)));
         ((IExplosionS2CPacket)packet).setVelocityZ((float)((double)packet.method_11474() * velocity.getHorizontal(velocity.explosionsHorizontal)));
      }
   }

   @Inject(
      method = {"onItemPickupAnimation"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/world/ClientWorld;getEntityById(I)Lnet/minecraft/entity/Entity;",
   ordinal = 0
)}
   )
   private void onItemPickupAnimation(class_2775 packet, CallbackInfo info) {
      class_1297 itemEntity = this.field_45588.field_1687.method_8469(packet.method_11915());
      class_1297 entity = this.field_45588.field_1687.method_8469(packet.method_11912());
      if (itemEntity instanceof class_1542 && entity == this.field_45588.field_1724) {
         MeteorClient.EVENT_BUS.post((Object)PickItemsEvent.get(((class_1542)itemEntity).method_6983(), packet.method_11913()));
      }

   }

   @Inject(
      method = {"sendChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendChatMessage(String message, CallbackInfo ci) {
      if (!this.ignoreChatMessage) {
         if (message.startsWith((String)Config.get().prefix.get()) || BaritoneUtils.IS_AVAILABLE && message.startsWith(BaritoneUtils.getPrefix())) {
            if (message.startsWith((String)Config.get().prefix.get())) {
               try {
                  Commands.dispatch(message.substring(((String)Config.get().prefix.get()).length()));
               } catch (CommandSyntaxException var4) {
                  ChatUtils.error(var4.getMessage());
               }

               this.field_45588.field_1705.method_1743().method_1803(message);
               ci.cancel();
            }

         } else {
            SendMessageEvent event = (SendMessageEvent)MeteorClient.EVENT_BUS.post((ICancellable)SendMessageEvent.get(message));
            if (!event.isCancelled()) {
               this.ignoreChatMessage = true;
               this.method_45729(event.message);
               this.ignoreChatMessage = false;
            }

            ci.cancel();
         }
      }
   }
}
