package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.Iterator;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1671;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_2708;
import net.minecraft.class_2709;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;

public class ElytraFakeFly extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgBoost;
   private final Setting<ElytraFakeFly.Mode> mode;
   public final Setting<Double> fireworkDelay;
   public final Setting<Double> horizontalSpeed;
   public final Setting<Double> verticalSpeed;
   public final Setting<Double> accelTime;
   public final Setting<Boolean> sprintToBoost;
   public final Setting<Double> sprintToBoostMaxSpeed;
   public final Setting<Double> boostAccelTime;
   private int fireworkTicksLeft;
   private boolean needsFirework;
   private class_243 lastMovement;
   private class_243 currentVelocity;
   private long timeOfLastRubberband;
   private class_243 lastRubberband;

   public ElytraFakeFly() {
      super(Categories.Movement, "elytra-fakefly", "Gives you more control over your elytra but funnier.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgBoost = this.settings.createGroup("Boost");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Determines how to fake fly")).defaultValue(ElytraFakeFly.Mode.Chestplate)).build());
      this.fireworkDelay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("firework-delay")).description("Length of a firework.")).defaultValue(2.1D).min(0.0D).max(5.0D).build());
      this.horizontalSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("horizontal-speed")).description("Controls how fast will you go horizontally.")).defaultValue(50.0D).min(0.0D).max(100.0D).build());
      this.verticalSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vertical-speed")).description("Controls how fast will you go veritcally.")).defaultValue(30.0D).min(0.0D).max(100.0D).build());
      this.accelTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("accel-time")).description("Controls how fast will you accelerate and decelerate in second")).defaultValue(0.25D).min(0.01D).max(2.0D).build());
      this.sprintToBoost = this.sgBoost.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sprint-to-boost")).description("Allows you to hold sprint to go extra fast")).defaultValue(true)).build());
      this.sprintToBoostMaxSpeed = this.sgBoost.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("boost-max-speed")).description("Controls how fast will can go at maximum boost speed")).defaultValue(100.0D).min(50.0D).sliderMax(300.0D).build());
      this.boostAccelTime = this.sgBoost.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("boost-accel-time")).description("Conbtrols how fast you will accelerate and decelerate when boosting")).defaultValue(0.5D).min(0.01D).sliderMax(2.0D).build());
      this.fireworkTicksLeft = 0;
      this.needsFirework = false;
      this.lastMovement = class_243.field_1353;
      this.currentVelocity = class_243.field_1353;
      this.timeOfLastRubberband = 0L;
      this.lastRubberband = class_243.field_1353;
   }

   public void onActivate() {
      this.needsFirework = this.getIsUsingFirework();
      this.currentVelocity = this.mc.field_1724.method_18798();
      this.mc.field_1724.method_6043();
      this.mc.field_1724.method_24830(false);
   }

   public void onDeactivate() {
      PlayerUtils.silentSwapEquipChestplate();
      this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12984));
      this.mc.field_1724.method_5660(false);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      boolean isUsingFirework = this.getIsUsingFirework();
      if (isUsingFirework || InvUtils.find(class_1802.field_8639).found()) {
         class_243 desiredVelocity = new class_243(0.0D, 0.0D, 0.0D);
         double yaw = Math.toRadians((double)this.mc.field_1724.method_36454());
         double pitch = Math.toRadians((double)this.mc.field_1724.method_36455());
         class_243 direction = (new class_243(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch))).method_1029();
         if (this.mc.field_1690.field_1894.method_1434()) {
            desiredVelocity = desiredVelocity.method_1019(direction.method_18805(this.getHorizontalSpeed() / 20.0D, 0.0D, this.getHorizontalSpeed() / 20.0D));
         }

         if (this.mc.field_1690.field_1881.method_1434()) {
            desiredVelocity = desiredVelocity.method_1019(direction.method_18805(-this.getHorizontalSpeed() / 20.0D, 0.0D, -this.getHorizontalSpeed() / 20.0D));
         }

         if (this.mc.field_1690.field_1913.method_1434()) {
            desiredVelocity = desiredVelocity.method_1019(direction.method_18805(this.getHorizontalSpeed() / 20.0D, 0.0D, this.getHorizontalSpeed() / 20.0D).method_1024(1.5707964F));
         }

         if (this.mc.field_1690.field_1849.method_1434()) {
            desiredVelocity = desiredVelocity.method_1019(direction.method_18805(this.getHorizontalSpeed() / 20.0D, 0.0D, this.getHorizontalSpeed() / 20.0D).method_1024(-1.5707964F));
         }

         if (this.mc.field_1690.field_1903.method_1434()) {
            desiredVelocity = desiredVelocity.method_1031(0.0D, (Double)this.verticalSpeed.get() / 20.0D, 0.0D);
         }

         if (this.mc.field_1690.field_1832.method_1434()) {
            desiredVelocity = desiredVelocity.method_1031(0.0D, -(Double)this.verticalSpeed.get() / 20.0D, 0.0D);
         }

         this.currentVelocity = new class_243(this.mc.field_1724.method_18798().field_1352, this.currentVelocity.field_1351, this.mc.field_1724.method_18798().field_1350);
         class_243 velocityDifference = desiredVelocity.method_1020(this.currentVelocity);
         double maxDelta = this.getHorizontalSpeed() / 20.0D / (this.getHorizontalAccelTime() * 20.0D);
         if (velocityDifference.method_1027() > maxDelta * maxDelta) {
            velocityDifference = velocityDifference.method_1029().method_1021(maxDelta);
         }

         this.currentVelocity = this.currentVelocity.method_1019(velocityDifference);
         class_238 boundingBox = this.mc.field_1724.method_5829();
         double playerFeetY = boundingBox.field_1322;
         class_238 groundBox = new class_238(boundingBox.field_1323, playerFeetY - 0.1D, boundingBox.field_1321, boundingBox.field_1320, playerFeetY, boundingBox.field_1324);
         Iterator var16 = class_2338.method_10094((int)Math.floor(groundBox.field_1323), (int)Math.floor(groundBox.field_1322), (int)Math.floor(groundBox.field_1321), (int)Math.floor(groundBox.field_1320), (int)Math.floor(groundBox.field_1325), (int)Math.floor(groundBox.field_1324)).iterator();

         while(var16.hasNext()) {
            class_2338 pos = (class_2338)var16.next();
            class_2680 blockState = this.mc.field_1687.method_8320(pos);
            if (blockState.method_26212(this.mc.field_1687, pos)) {
               double blockTopY = (double)pos.method_10264() + 1.0D;
               double distanceToBlock = playerFeetY - blockTopY;
               if (distanceToBlock >= 0.0D && distanceToBlock < 0.1D && this.currentVelocity.field_1351 < 0.0D) {
                  this.currentVelocity = new class_243(this.currentVelocity.field_1352, 0.1D, this.currentVelocity.field_1350);
               }
            }
         }

         if (this.fireworkTicksLeft < (int)((Double)this.fireworkDelay.get() * 20.0D) - 3 && this.fireworkTicksLeft > 3 && !isUsingFirework) {
            this.fireworkTicksLeft = 0;
         }

         PlayerUtils.silentSwapEquipElytra();
         this.mc.field_1724.field_3944.method_52787(new class_2848(this.mc.field_1724, class_2849.field_12982));
         if (this.fireworkTicksLeft <= 0) {
            this.needsFirework = true;
         }

         if (this.needsFirework && this.currentVelocity.method_1033() > 1.0E-7D) {
            this.useFirework();
            this.needsFirework = false;
         }

         if (this.fireworkTicksLeft >= 0) {
            --this.fireworkTicksLeft;
         }

         if (this.mode.get() == ElytraFakeFly.Mode.Chestplate) {
            PlayerUtils.silentSwapEquipChestplate();
         }

      }
   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      if (this.isActive()) {
         if (this.getIsUsingFirework() || InvUtils.find(class_1802.field_8639).found()) {
            if (this.lastMovement == null) {
               this.lastMovement = event.movement;
            }

            class_243 newMovement = this.currentVelocity;
            this.mc.field_1724.method_18799(newMovement);
            ((IVec3d)event.movement).set(newMovement.field_1352, newMovement.field_1351, newMovement.field_1350);
            this.lastMovement = newMovement;
         }
      }
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
   }

   @EventHandler
   public void onPacketReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2708) {
         class_2708 packet = (class_2708)var3;
         if (packet.method_11733().contains(class_2709.field_12400)) {
            this.currentVelocity = new class_243(packet.method_11734(), this.currentVelocity.field_1351, this.currentVelocity.field_1350);
         }

         if (packet.method_11733().contains(class_2709.field_12398)) {
            this.currentVelocity = new class_243(this.currentVelocity.field_1352, packet.method_11735(), this.currentVelocity.field_1350);
         }

         if (packet.method_11733().contains(class_2709.field_12403)) {
            this.currentVelocity = new class_243(this.currentVelocity.field_1352, this.currentVelocity.field_1351, packet.method_11738());
         }

         if (!packet.method_11733().contains(class_2709.field_12400) && !packet.method_11733().contains(class_2709.field_12398) && !packet.method_11733().contains(class_2709.field_12403)) {
            if (System.currentTimeMillis() - this.timeOfLastRubberband < 100L) {
               this.currentVelocity = (new class_243(packet.method_11734(), packet.method_11735(), packet.method_11738())).method_1020(this.lastRubberband);
            }

            this.timeOfLastRubberband = System.currentTimeMillis();
            this.lastRubberband = new class_243(packet.method_11734(), packet.method_11735(), packet.method_11738());
         }
      }

   }

   private boolean getIsUsingFirework() {
      boolean usingFirework = false;
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1671) {
            class_1671 firework = (class_1671)entity;
            if (firework.method_24921() != null && firework.method_24921().equals(this.mc.field_1724)) {
               usingFirework = true;
            }
         }
      }

      return usingFirework;
   }

   public boolean isFlying() {
      return this.isActive();
   }

   private void useFirework() {
      this.fireworkTicksLeft = (int)((Double)this.fireworkDelay.get() * 20.0D);
      int hotbarSilentSwapSlot = -1;
      int inventorySilentSwapSlot = -1;
      FindItemResult itemResult = InvUtils.findInHotbar(class_1802.field_8639);
      if (!itemResult.found()) {
         FindItemResult invResult = InvUtils.find(class_1802.field_8639);
         if (!invResult.found()) {
            return;
         }

         FindItemResult hotbarSlotToSwapToResult = InvUtils.findInHotbar((x) -> {
            return x.method_7909() != class_1802.field_8288;
         });
         inventorySilentSwapSlot = invResult.slot();
         hotbarSilentSwapSlot = hotbarSlotToSwapToResult.found() ? hotbarSlotToSwapToResult.slot() : 0;
         this.mc.field_1761.method_2906(this.mc.field_1724.field_7498.field_7763, inventorySilentSwapSlot, hotbarSilentSwapSlot, class_1713.field_7791, this.mc.field_1724);
         itemResult = InvUtils.findInHotbar(class_1802.field_8639);
      }

      if (itemResult.found()) {
         if (itemResult.isOffhand()) {
            this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5810);
            this.mc.field_1724.method_6104(class_1268.field_5810);
         } else {
            InvUtils.swap(itemResult.slot(), true);
            this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
            this.mc.field_1724.method_6104(class_1268.field_5808);
            InvUtils.swapBack();
         }

         if (inventorySilentSwapSlot != -1 && hotbarSilentSwapSlot != -1) {
            this.mc.field_1761.method_2906(this.mc.field_1724.field_7498.field_7763, inventorySilentSwapSlot, hotbarSilentSwapSlot, class_1713.field_7791, this.mc.field_1724);
         }

      }
   }

   private double getHorizontalSpeed() {
      if (this.mc.field_1690.field_1867.method_1434()) {
         double horizontalVelocity = this.currentVelocity.method_37267();
         return Math.clamp(horizontalVelocity * 1.3D * 20.0D, (Double)this.horizontalSpeed.get(), (Double)this.sprintToBoostMaxSpeed.get());
      } else {
         return (Double)this.horizontalSpeed.get();
      }
   }

   private double getHorizontalAccelTime() {
      return this.currentVelocity.method_37267() > (Double)this.horizontalSpeed.get() ? (Double)this.boostAccelTime.get() : (Double)this.accelTime.get();
   }

   public static enum Mode {
      Chestplate,
      Elytra;

      // $FF: synthetic method
      private static ElytraFakeFly.Mode[] $values() {
         return new ElytraFakeFly.Mode[]{Chestplate, Elytra};
      }
   }
}
