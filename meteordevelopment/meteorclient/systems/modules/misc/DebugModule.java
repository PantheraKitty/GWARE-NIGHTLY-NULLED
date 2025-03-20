package meteordevelopment.meteorclient.systems.modules.misc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_3965;
import org.apache.commons.compress.utils.Sets;

public class DebugModule extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Set<Class<? extends class_2596<?>>>> c2sPackets;
   private final Setting<Set<Class<? extends class_2596<?>>>> s2cPackets;
   private static final Set<Class<?>> PRIMITIVE_TYPES = Sets.newHashSet(new Class[]{Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, String.class});

   public DebugModule() {
      super(Categories.Misc, "debug-module", "A module for debugging. Don't touch this unless you know what you're doing.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.c2sPackets = this.sgGeneral.add(((PacketListSetting.Builder)((PacketListSetting.Builder)(new PacketListSetting.Builder()).name("C2S-packets")).description("Client-to-server packets to log.")).filter((aClass) -> {
         return PacketUtils.getC2SPackets().contains(aClass);
      }).build());
      this.s2cPackets = this.sgGeneral.add(((PacketListSetting.Builder)((PacketListSetting.Builder)(new PacketListSetting.Builder()).name("S2C-packets")).description("Server-to-client packets to log.")).filter((aClass) -> {
         return PacketUtils.getS2CPackets().contains(aClass);
      }).build());
      this.runInMainMenu = true;
   }

   @EventHandler(
      priority = -200
   )
   private void onReceivePacket(PacketEvent.Receive event) {
      if (((Set)this.s2cPackets.get()).contains(event.packet.getClass())) {
         this.info(this.packetToString(event.packet), new Object[0]);
      }

   }

   @EventHandler(
      priority = -200
   )
   private void onSendPacket(PacketEvent.Send event) {
      if (((Set)this.c2sPackets.get()).contains(event.packet.getClass())) {
         this.info(this.packetToString(event.packet), new Object[0]);
      }

   }

   private String packetToString(class_2596<?> packet) {
      try {
         return reflectiveToString(packet);
      } catch (Exception var3) {
         return var3.getMessage();
      }
   }

   private static String reflectiveToString(Object object) throws Exception {
      return reflectiveToString(object, 0);
   }

   private static String reflectiveToString(Object object, int indentLevel) throws Exception {
      if (object == null) {
         return "null";
      } else if (PRIMITIVE_TYPES.contains(object.getClass())) {
         return object instanceof String ? "\"" + String.valueOf(object) + "\"" : object.toString();
      } else if (object.getClass().isEnum()) {
         return object.toString();
      } else if (object instanceof class_3965) {
         class_3965 bhr = (class_3965)object;
         String var10000 = bhr.method_17780().toString();
         return "{ dir: " + var10000 + ", bpos: " + bhr.method_17777().method_23854() + ", pos: " + bhr.method_17784().toString() + ", inside: " + Boolean.toString(bhr.method_17781()) + "}";
      } else if (!(object instanceof class_2596)) {
         return object.toString();
      } else {
         StringBuilder sb = new StringBuilder();
         String indent = "    ".repeat(indentLevel);
         sb.append(indent).append(object.getClass().getSimpleName()).append(" {\n");
         Method[] var4 = object.getClass().getMethods();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Method method = var4[var6];
            String methodName = method.getName();
            if (method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE && !Modifier.isStatic(method.getModifiers())) {
               Object value = method.invoke(object);
               sb.append(indent).append("    ").append(method.getReturnType().getSimpleName()).append(" ").append(methodName).append(": ").append(reflectiveToString(value, indentLevel + 1)).append("\n");
            }
         }

         sb.append(indent).append("}");
         return sb.toString();
      }
   }
}
