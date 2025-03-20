package meteordevelopment.meteorclient.systems.modules.movement;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.LivingEntityAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1294;
import net.minecraft.class_1299;
import net.minecraft.class_1934;
import net.minecraft.class_2246;
import net.minecraft.class_238;
import net.minecraft.class_259;
import net.minecraft.class_2596;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2828;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import net.minecraft.class_3612;
import net.minecraft.class_5134;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;

public class Jesus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgWater;
   private final SettingGroup sgLava;
   private final Setting<Boolean> powderSnow;
   private final Setting<Jesus.Mode> waterMode;
   private final Setting<Boolean> dipIfBurning;
   private final Setting<Boolean> dipOnSneakWater;
   private final Setting<Boolean> dipOnFallWater;
   private final Setting<Integer> dipFallHeightWater;
   private final Setting<Jesus.Mode> lavaMode;
   private final Setting<Boolean> dipIfFireResistant;
   private final Setting<Boolean> dipOnSneakLava;
   private final Setting<Boolean> dipOnFallLava;
   private final Setting<Integer> dipFallHeightLava;
   private final class_2339 blockPos;
   private int tickTimer;
   private int packetTimer;
   private boolean prePathManagerWalkOnWater;
   private boolean prePathManagerWalkOnLava;
   public boolean isInBubbleColumn;

   public Jesus() {
      super(Categories.Movement, "jesus", "Walk on liquids and powder snow like Jesus.");
      this.sgGeneral = this.settings.createGroup("General");
      this.sgWater = this.settings.createGroup("Water");
      this.sgLava = this.settings.createGroup("Lava");
      this.powderSnow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("powder-snow")).description("Walk on powder snow.")).defaultValue(true)).build());
      this.waterMode = this.sgWater.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to treat the water.")).defaultValue(Jesus.Mode.Solid)).build());
      this.dipIfBurning = this.sgWater.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-if-burning")).description("Lets you go into the water when you are burning.")).defaultValue(true)).visible(() -> {
         return this.waterMode.get() == Jesus.Mode.Solid;
      })).build());
      this.dipOnSneakWater = this.sgWater.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-sneak")).description("Lets you go into the water when your sneak key is held.")).defaultValue(true)).visible(() -> {
         return this.waterMode.get() == Jesus.Mode.Solid;
      })).build());
      this.dipOnFallWater = this.sgWater.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-fall")).description("Lets you go into the water when you fall over a certain height.")).defaultValue(true)).visible(() -> {
         return this.waterMode.get() == Jesus.Mode.Solid;
      })).build());
      this.dipFallHeightWater = this.sgWater.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("dip-fall-height")).description("The fall height at which you will go into the water.")).defaultValue(4)).range(1, 255).sliderRange(3, 20).visible(() -> {
         return this.waterMode.get() == Jesus.Mode.Solid && (Boolean)this.dipOnFallWater.get();
      })).build());
      this.lavaMode = this.sgLava.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to treat the lava.")).defaultValue(Jesus.Mode.Solid)).build());
      this.dipIfFireResistant = this.sgLava.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-if-resistant")).description("Lets you go into the lava if you have Fire Resistance effect.")).defaultValue(true)).visible(() -> {
         return this.lavaMode.get() == Jesus.Mode.Solid;
      })).build());
      this.dipOnSneakLava = this.sgLava.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-sneak")).description("Lets you go into the lava when your sneak key is held.")).defaultValue(true)).visible(() -> {
         return this.lavaMode.get() == Jesus.Mode.Solid;
      })).build());
      this.dipOnFallLava = this.sgLava.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dip-on-fall")).description("Lets you go into the lava when you fall over a certain height.")).defaultValue(true)).visible(() -> {
         return this.lavaMode.get() == Jesus.Mode.Solid;
      })).build());
      this.dipFallHeightLava = this.sgLava.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("dip-fall-height")).description("The fall height at which you will go into the lava.")).defaultValue(4)).range(1, 255).sliderRange(3, 20).visible(() -> {
         return this.lavaMode.get() == Jesus.Mode.Solid && (Boolean)this.dipOnFallLava.get();
      })).build());
      this.blockPos = new class_2339();
      this.tickTimer = 10;
      this.packetTimer = 0;
      this.isInBubbleColumn = false;
   }

   public void onActivate() {
      this.prePathManagerWalkOnWater = (Boolean)PathManagers.get().getSettings().getWalkOnWater().get();
      this.prePathManagerWalkOnLava = (Boolean)PathManagers.get().getSettings().getWalkOnLava().get();
      PathManagers.get().getSettings().getWalkOnWater().set(this.waterMode.get() == Jesus.Mode.Solid);
      PathManagers.get().getSettings().getWalkOnLava().set(this.lavaMode.get() == Jesus.Mode.Solid);
   }

   public void onDeactivate() {
      PathManagers.get().getSettings().getWalkOnWater().set(this.prePathManagerWalkOnWater);
      PathManagers.get().getSettings().getWalkOnLava().set(this.prePathManagerWalkOnLava);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      boolean bubbleColumn = this.isInBubbleColumn;
      this.isInBubbleColumn = false;
      if (this.waterMode.get() == Jesus.Mode.Bob && this.mc.field_1724.method_5799() || this.lavaMode.get() == Jesus.Mode.Bob && this.mc.field_1724.method_5771()) {
         double fluidHeight;
         if (this.mc.field_1724.method_5771()) {
            fluidHeight = this.mc.field_1724.method_5861(class_3486.field_15518);
         } else {
            fluidHeight = this.mc.field_1724.method_5861(class_3486.field_15517);
         }

         double swimHeight = this.mc.field_1724.method_29241();
         if (this.mc.field_1724.method_5799() && fluidHeight > swimHeight) {
            ((LivingEntityAccessor)this.mc.field_1724).swimUpwards(class_3486.field_15517);
         } else if (this.mc.field_1724.method_24828() && fluidHeight <= swimHeight && ((LivingEntityAccessor)this.mc.field_1724).getJumpCooldown() == 0) {
            this.mc.field_1724.method_6043();
            ((LivingEntityAccessor)this.mc.field_1724).setJumpCooldown(10);
         } else {
            ((LivingEntityAccessor)this.mc.field_1724).swimUpwards(class_3486.field_15518);
         }
      }

      if (!this.mc.field_1724.method_5799() || this.waterShouldBeSolid()) {
         if (!this.mc.field_1724.method_20232()) {
            if (!this.mc.field_1724.method_5771() || this.lavaShouldBeSolid()) {
               if (bubbleColumn) {
                  if (this.mc.field_1690.field_1903.method_1434() && this.mc.field_1724.method_18798().method_10214() < 0.11D) {
                     ((IVec3d)this.mc.field_1724.method_18798()).setY(0.11D);
                  }

               } else if (!this.mc.field_1724.method_5799() && !this.mc.field_1724.method_5771()) {
                  class_2680 blockBelowState = this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10074());
                  boolean waterLogger = false;

                  try {
                     waterLogger = (Boolean)blockBelowState.method_11654(class_2741.field_12508);
                  } catch (Exception var7) {
                  }

                  if (this.tickTimer == 0) {
                     ((IVec3d)this.mc.field_1724.method_18798()).setY(0.3D);
                  } else if (this.tickTimer == 1 && (blockBelowState == class_2246.field_10382.method_9564() || blockBelowState == class_2246.field_10164.method_9564() || waterLogger)) {
                     ((IVec3d)this.mc.field_1724.method_18798()).setY(0.0D);
                  }

                  ++this.tickTimer;
               } else {
                  ((IVec3d)this.mc.field_1724.method_18798()).setY(0.11D);
                  this.tickTimer = 0;
               }
            }
         }
      }
   }

   @EventHandler
   private void onCanWalkOnFluid(CanWalkOnFluidEvent event) {
      if (this.mc.field_1724 == null || !this.mc.field_1724.method_20232()) {
         if ((event.fluidState.method_15772() == class_3612.field_15910 || event.fluidState.method_15772() == class_3612.field_15909) && this.waterShouldBeSolid()) {
            event.walkOnFluid = true;
         } else if ((event.fluidState.method_15772() == class_3612.field_15908 || event.fluidState.method_15772() == class_3612.field_15907) && this.lavaShouldBeSolid()) {
            event.walkOnFluid = true;
         }

      }
   }

   @EventHandler
   private void onFluidCollisionShape(CollisionShapeEvent event) {
      if (!event.state.method_26227().method_15769()) {
         if (event.state.method_26204() == class_2246.field_10382 | event.state.method_26227().method_15772() == class_3612.field_15910 && !this.mc.field_1724.method_5799() && this.waterShouldBeSolid() && (double)event.pos.method_10264() <= this.mc.field_1724.method_23318() - 1.0D) {
            event.shape = class_259.method_1077();
         } else if (event.state.method_26204() == class_2246.field_10164 && !this.mc.field_1724.method_5771() && this.lavaShouldBeSolid() && (!this.lavaIsSafe() || (double)event.pos.method_10264() <= this.mc.field_1724.method_23318() - 1.0D)) {
            event.shape = class_259.method_1077();
         }

      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828) {
         class_2828 packet = (class_2828)var3;
         if (!this.mc.field_1724.method_5799() || this.waterShouldBeSolid()) {
            if (!this.mc.field_1724.method_5771() || this.lavaShouldBeSolid()) {
               if (packet instanceof class_2829 || packet instanceof class_2830) {
                  if (!this.mc.field_1724.method_5799() && !this.mc.field_1724.method_5771() && !(this.mc.field_1724.field_6017 > 3.0F) && this.isOverLiquid()) {
                     if (this.mc.field_1724.field_3913.field_3905 == 0.0F && this.mc.field_1724.field_3913.field_3907 == 0.0F) {
                        event.cancel();
                     } else if (this.packetTimer++ >= 4) {
                        this.packetTimer = 0;
                        event.cancel();
                        double x = packet.method_12269(0.0D);
                        double y = packet.method_12268(0.0D) + 0.05D;
                        double z = packet.method_12274(0.0D);
                        Object newPacket;
                        if (packet instanceof class_2829) {
                           newPacket = new class_2829(x, y, z, true);
                        } else {
                           newPacket = new class_2830(x, y, z, packet.method_12271(0.0F), packet.method_12270(0.0F), true);
                        }

                        this.mc.method_1562().method_48296().method_10743((class_2596)newPacket);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean waterShouldBeSolid() {
      if (EntityUtils.getGameMode(this.mc.field_1724) != class_1934.field_9219 && !this.mc.field_1724.method_31549().field_7479) {
         if (this.mc.field_1724.method_5854() != null) {
            class_1299<?> vehicle = this.mc.field_1724.method_5854().method_5864();
            if (vehicle == class_1299.field_6121 || vehicle == class_1299.field_38096) {
               return false;
            }
         }

         if (((Flight)Modules.get().get(Flight.class)).isActive()) {
            return false;
         } else if ((Boolean)this.dipIfBurning.get() && this.mc.field_1724.method_5809()) {
            return false;
         } else if ((Boolean)this.dipOnSneakWater.get() && this.mc.field_1690.field_1832.method_1434()) {
            return false;
         } else if ((Boolean)this.dipOnFallWater.get() && this.mc.field_1724.field_6017 > (float)(Integer)this.dipFallHeightWater.get()) {
            return false;
         } else {
            return this.waterMode.get() == Jesus.Mode.Solid;
         }
      } else {
         return false;
      }
   }

   private boolean lavaShouldBeSolid() {
      if (EntityUtils.getGameMode(this.mc.field_1724) != class_1934.field_9219 && !this.mc.field_1724.method_31549().field_7479) {
         if (!this.lavaIsSafe() && this.lavaMode.get() == Jesus.Mode.Solid) {
            return true;
         } else if ((Boolean)this.dipOnSneakLava.get() && this.mc.field_1690.field_1832.method_1434()) {
            return false;
         } else if ((Boolean)this.dipOnFallLava.get() && this.mc.field_1724.field_6017 > (float)(Integer)this.dipFallHeightLava.get()) {
            return false;
         } else {
            return this.lavaMode.get() == Jesus.Mode.Solid;
         }
      } else {
         return false;
      }
   }

   private boolean lavaIsSafe() {
      if (!(Boolean)this.dipIfFireResistant.get()) {
         return false;
      } else {
         return this.mc.field_1724.method_6059(class_1294.field_5918) && (double)this.mc.field_1724.method_6112(class_1294.field_5918).method_5584() > 300.0D * this.mc.field_1724.method_45325(class_5134.field_51579);
      }
   }

   private boolean isOverLiquid() {
      boolean foundLiquid = false;
      boolean foundSolid = false;
      List<class_238> blockCollisions = (List)Streams.stream(this.mc.field_1687.method_20812(this.mc.field_1724, this.mc.field_1724.method_5829().method_989(0.0D, -0.5D, 0.0D))).map(class_265::method_1107).collect(Collectors.toCollection(ArrayList::new));
      Iterator var4 = blockCollisions.iterator();

      while(var4.hasNext()) {
         class_238 bb = (class_238)var4.next();
         this.blockPos.method_10102(class_3532.method_16436(0.5D, bb.field_1323, bb.field_1320), class_3532.method_16436(0.5D, bb.field_1322, bb.field_1325), class_3532.method_16436(0.5D, bb.field_1321, bb.field_1324));
         class_2680 blockState = this.mc.field_1687.method_8320(this.blockPos);
         if (!(blockState.method_26204() == class_2246.field_10382 | blockState.method_26227().method_15772() == class_3612.field_15910) && blockState.method_26204() != class_2246.field_10164) {
            if (!blockState.method_26215()) {
               foundSolid = true;
            }
         } else {
            foundLiquid = true;
         }
      }

      return foundLiquid && !foundSolid;
   }

   public boolean canWalkOnPowderSnow() {
      return this.isActive() && (Boolean)this.powderSnow.get();
   }

   public static enum Mode {
      Solid,
      Bob,
      Ignore;

      // $FF: synthetic method
      private static Jesus.Mode[] $values() {
         return new Jesus.Mode[]{Solid, Bob, Ignore};
      }
   }
}
