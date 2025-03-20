package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerEntityAccessor;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2848;
import net.minecraft.class_2824.class_5907;
import net.minecraft.class_2848.class_2849;

public class Sprint extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Sprint.Mode> mode;
   public final Setting<Boolean> jumpFix;
   private final Setting<Boolean> keepSprint;
   private final Setting<Boolean> unsprintOnHit;
   private final Setting<Boolean> unsprintInWater;

   public Sprint() {
      super(Categories.Movement, "sprint", "Automatically sprints.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("speed-mode")).description("What mode of sprinting.")).defaultValue(Sprint.Mode.Strict)).build());
      this.jumpFix = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jump-fix")).description("Whether to correct jumping directions.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == Sprint.Mode.Rage;
      })).build());
      this.keepSprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("keep-sprint")).description("Whether to keep sprinting after attacking an entity.")).defaultValue(false)).build());
      this.unsprintOnHit = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("unsprint-on-hit")).description("Whether to stop sprinting when attacking, to ensure you get crits and sweep attacks.")).defaultValue(false)).build());
      this.unsprintInWater = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("unsprint-in-water")).description("Whether to stop sprinting when in water.")).defaultValue(true)).build());
   }

   public void onDeactivate() {
      this.mc.field_1724.method_5728(false);
   }

   @EventHandler
   private void onTickMovement(TickEvent.Post event) {
      if (this.shouldSprint()) {
         this.mc.field_1724.method_5728(true);
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onPacketSend(PacketEvent.Send event) {
      if ((Boolean)this.unsprintOnHit.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof IPlayerInteractEntityC2SPacket) {
            IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)var3;
            if (packet.getType() == class_5907.field_29172) {
               this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12985));
               this.mc.field_1724.method_5728(false);
               return;
            }
         }
      }

   }

   @EventHandler
   private void onPacketSent(PacketEvent.Sent event) {
      if ((Boolean)this.unsprintOnHit.get() && (Boolean)this.keepSprint.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof IPlayerInteractEntityC2SPacket) {
            IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)var3;
            if (packet.getType() == class_5907.field_29172) {
               if (this.shouldSprint() && !this.mc.field_1724.method_5624()) {
                  this.mc.method_1562().method_52787(new class_2848(this.mc.field_1724, class_2849.field_12981));
                  this.mc.field_1724.method_5728(true);
               }

               return;
            }
         }

      }
   }

   public boolean shouldSprint() {
      if (!(Boolean)this.unsprintInWater.get() || !this.mc.field_1724.method_5799() && !this.mc.field_1724.method_5869()) {
         boolean strictSprint = this.mc.field_1724.field_6250 > 1.0E-5F && ((ClientPlayerEntityAccessor)this.mc.field_1724).invokeCanSprint() && (!this.mc.field_1724.field_5976 || this.mc.field_1724.field_34927) && (!this.mc.field_1724.method_5799() || this.mc.field_1724.method_5869());
         return this.isActive() && (this.mode.get() == Sprint.Mode.Rage || strictSprint) && (this.mc.field_1755 == null || (Boolean)((GUIMove)Modules.get().get(GUIMove.class)).sprint.get());
      } else {
         return false;
      }
   }

   public boolean rageSprint() {
      return this.isActive() && this.mode.get() == Sprint.Mode.Rage;
   }

   public boolean stopSprinting() {
      return !this.isActive() || !(Boolean)this.keepSprint.get();
   }

   public static enum Mode {
      Strict,
      Rage;

      // $FF: synthetic method
      private static Sprint.Mode[] $values() {
         return new Sprint.Mode[]{Strict, Rage};
      }
   }
}
