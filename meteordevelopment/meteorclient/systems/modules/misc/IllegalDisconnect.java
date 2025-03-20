package meteordevelopment.meteorclient.systems.modules.misc;

import java.time.Instant;
import meteordevelopment.meteorclient.mixin.ClientConnectionAccessor;
import meteordevelopment.meteorclient.mixin.ClientPlayNetworkHandlerAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1268;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_2824;
import net.minecraft.class_2868;
import net.minecraft.class_2886;
import net.minecraft.class_7469;
import net.minecraft.class_7648;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_3515.class_7426;

public class IllegalDisconnect extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> disableAutoReconnect;
   private final Setting<IllegalDisconnect.IllegalDisconnectMethod> method;

   public IllegalDisconnect() {
      super(Categories.Misc, "illegal-disconnect", "Disconnects you from the server using illegal packets.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.disableAutoReconnect = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-auto-reconnect")).description("Disables auto reconnect when using this module.")).defaultValue(true)).build());
      this.method = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("method")).description("The method used to disconnect.")).defaultValue(IllegalDisconnect.IllegalDisconnectMethod.Slot)).build());
   }

   public void onActivate() {
      if (Utils.canUpdate()) {
         this.performIllegalDisconnect();
      }
   }

   public void performIllegalDisconnect() {
      if ((Boolean)this.disableAutoReconnect.get()) {
         ((AutoReconnect)Modules.get().get(AutoReconnect.class)).toggle();
      }

      class_2596<?> illegalPacket = null;
      switch(((IllegalDisconnect.IllegalDisconnectMethod)this.method.get()).ordinal()) {
      case 0:
         illegalPacket = new class_2868(-69);
         break;
      case 1:
         illegalPacket = new class_2797("§", Instant.now(), class_7426.method_43531(), (class_7469)null, ((ClientPlayNetworkHandlerAccessor)this.mc.method_1562()).getLastSeenMessagesCollector().method_46266().comp_1074());
         break;
      case 2:
         illegalPacket = class_2824.method_34207(this.mc.field_1724, false, class_1268.field_5808);
         break;
      case 3:
         illegalPacket = new class_2829(Double.NaN, 69.0D, Double.NaN, false);
         break;
      case 4:
         illegalPacket = new class_2886(class_1268.field_5808, 0, 0.0F, 0.0F);
      }

      if (illegalPacket != null) {
         ((ClientConnectionAccessor)this.mc.method_1562().method_48296()).invokeSend((class_2596)illegalPacket, (class_7648)null);
      }

   }

   public static enum IllegalDisconnectMethod {
      Slot,
      Chat,
      Interact,
      Movement,
      SequenceBreak;

      // $FF: synthetic method
      private static IllegalDisconnect.IllegalDisconnectMethod[] $values() {
         return new IllegalDisconnect.IllegalDisconnectMethod[]{Slot, Chat, Interact, Movement, SequenceBreak};
      }
   }
}
