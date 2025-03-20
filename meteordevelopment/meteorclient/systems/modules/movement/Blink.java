package meteordevelopment.meteorclient.systems.modules.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_634;
import org.joml.Vector3d;

public class Blink extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> renderOriginal;
   private final Setting<Keybind> cancelBlink;
   private final List<class_2828> packets;
   private FakePlayerEntity model;
   private final Vector3d start;
   private boolean cancelled;
   private int timer;

   public Blink() {
      super(Categories.Movement, "blink", "Allows you to essentially teleport while suspending motion updates.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.renderOriginal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-original")).description("Renders your player model at the original position.")).defaultValue(true)).build());
      this.cancelBlink = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("cancel-blink")).description("Cancels sending packets and sends you back to your original position.")).defaultValue(Keybind.none())).action(() -> {
         this.cancelled = true;
         if (this.isActive()) {
            this.toggle();
         }

      }).build());
      this.packets = new ArrayList();
      this.start = new Vector3d();
      this.cancelled = false;
      this.timer = 0;
   }

   public void onActivate() {
      if ((Boolean)this.renderOriginal.get()) {
         this.model = new FakePlayerEntity(this.mc.field_1724, this.mc.field_1724.method_7334().getName(), 20.0F, true);
         this.model.doNotPush = true;
         this.model.hideWhenInsideCamera = true;
         this.model.spawn();
      }

      Utils.set(this.start, this.mc.field_1724.method_19538());
   }

   public void onDeactivate() {
      this.dumpPackets(!this.cancelled);
      if (this.cancelled) {
         this.mc.field_1724.method_23327(this.start.x, this.start.y, this.start.z);
      }

      this.cancelled = false;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      ++this.timer;
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828) {
         class_2828 p = (class_2828)var3;
         event.cancel();
         class_2828 prev = this.packets.isEmpty() ? null : (class_2828)this.packets.getLast();
         if (prev == null || p.method_12273() != prev.method_12273() || p.method_12271(-1.0F) != prev.method_12271(-1.0F) || p.method_12270(-1.0F) != prev.method_12270(-1.0F) || p.method_12269(-1.0D) != prev.method_12269(-1.0D) || p.method_12268(-1.0D) != prev.method_12268(-1.0D) || p.method_12274(-1.0D) != prev.method_12274(-1.0D)) {
            synchronized(this.packets) {
               this.packets.add(p);
            }
         }
      }
   }

   public String getInfoString() {
      return String.format("%.1f", (float)this.timer / 20.0F);
   }

   private void dumpPackets(boolean send) {
      synchronized(this.packets) {
         if (send) {
            List var10000 = this.packets;
            class_634 var10001 = this.mc.field_1724.field_3944;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::method_52787);
         }

         this.packets.clear();
      }

      if (this.model != null) {
         this.model.despawn();
         this.model = null;
      }

      this.timer = 0;
   }
}
