package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2879;
import net.minecraft.class_9362;
import net.minecraft.class_2824.class_5907;
import net.minecraft.class_2828.class_2829;

public class Criticals extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgMace;
   private final Setting<Criticals.Mode> mode;
   private final Setting<Boolean> ka;
   private final Setting<Boolean> mace;
   private final Setting<Double> extraHeight;
   private class_2824 attackPacket;
   private class_2879 swingPacket;
   private boolean sendPackets;
   private int sendTimer;

   public Criticals() {
      super(Categories.Combat, "criticals", "Performs critical attacks when you hit your target.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgMace = this.settings.createGroup("Mace");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode on how Criticals will function.")).defaultValue(Criticals.Mode.Packet)).build());
      this.ka = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-killaura")).description("Only performs crits when using killaura.")).defaultValue(false)).visible(() -> {
         return this.mode.get() != Criticals.Mode.None;
      })).build());
      this.mace = this.sgMace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smash-attack")).description("Will always perform smash attacks when using a mace.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgMace;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("additional-height")).description("The amount of additional height to spoof. More height means more damage.")).defaultValue(0.0D).min(0.0D).sliderRange(0.0D, 100.0D);
      Setting var10003 = this.mace;
      Objects.requireNonNull(var10003);
      this.extraHeight = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public void onActivate() {
      this.attackPacket = null;
      this.swingPacket = null;
      this.sendPackets = false;
      this.sendTimer = 0;
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof IPlayerInteractEntityC2SPacket) {
         IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)var3;
         if (packet.getType() == class_5907.field_29172) {
            if ((Boolean)this.mace.get() && this.mc.field_1724.method_6047().method_7909() instanceof class_9362) {
               if (this.mc.field_1724.method_6128()) {
                  return;
               }

               this.sendPacket(0.0D);
               this.sendPacket(1.501D + (Double)this.extraHeight.get());
               this.sendPacket(0.0D);
               return;
            }

            if (this.skipCrit()) {
               return;
            }

            class_1297 entity = packet.getEntity();
            if (!(entity instanceof class_1309) || entity != ((KillAura)Modules.get().get(KillAura.class)).getTarget() && (Boolean)this.ka.get()) {
               return;
            }

            switch(((Criticals.Mode)this.mode.get()).ordinal()) {
            case 1:
               this.sendPacket(0.0625D);
               this.sendPacket(0.0D);
               return;
            case 2:
               this.sendPacket(0.11D);
               this.sendPacket(0.1100013579D);
               this.sendPacket(1.3579E-6D);
               return;
            case 3:
            case 4:
               if (!this.sendPackets) {
                  this.sendPackets = true;
                  this.sendTimer = this.mode.get() == Criticals.Mode.Jump ? 6 : 4;
                  this.attackPacket = (class_2824)event.packet;
                  if (this.mode.get() == Criticals.Mode.Jump) {
                     this.mc.field_1724.method_6043();
                  } else {
                     ((IVec3d)this.mc.field_1724.method_18798()).setY(0.25D);
                  }

                  event.cancel();
               }

               return;
            default:
               return;
            }
         }
      }

      if (event.packet instanceof class_2879 && this.mode.get() != Criticals.Mode.Packet) {
         if (this.skipCrit()) {
            return;
         }

         if (this.sendPackets && this.swingPacket == null) {
            this.swingPacket = (class_2879)event.packet;
            event.cancel();
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.sendPackets) {
         if (this.sendTimer <= 0) {
            this.sendPackets = false;
            if (this.attackPacket == null || this.swingPacket == null) {
               return;
            }

            this.mc.method_1562().method_52787(this.attackPacket);
            this.mc.method_1562().method_52787(this.swingPacket);
            this.attackPacket = null;
            this.swingPacket = null;
         } else {
            --this.sendTimer;
         }
      }

   }

   private void sendPacket(double height) {
      double x = this.mc.field_1724.method_23317();
      double y = this.mc.field_1724.method_23318();
      double z = this.mc.field_1724.method_23321();
      class_2828 packet = new class_2829(x, y + height, z, false);
      ((IPlayerMoveC2SPacket)packet).setTag(1337);
      this.mc.field_1724.field_3944.method_52787(packet);
   }

   private boolean skipCrit() {
      return !this.mc.field_1724.method_24828() || this.mc.field_1724.method_5869() || this.mc.field_1724.method_5771() || this.mc.field_1724.method_6101();
   }

   public String getInfoString() {
      return ((Criticals.Mode)this.mode.get()).name();
   }

   public static enum Mode {
      None,
      Packet,
      Bypass,
      Jump,
      MiniJump;

      // $FF: synthetic method
      private static Criticals.Mode[] $values() {
         return new Criticals.Mode[]{None, Packet, Bypass, Jump, MiniJump};
      }
   }
}
