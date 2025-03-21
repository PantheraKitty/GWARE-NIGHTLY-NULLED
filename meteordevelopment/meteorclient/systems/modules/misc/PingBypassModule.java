package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.class_642;

public class PingBypassModule extends Module {
   private final Setting<String> proxyAddress;
   private final Setting<Integer> proxyPort;
   private class_642 originalServerInfo;

   public PingBypassModule() {
      super(Categories.Misc, "ping-bypass", "Routes your connection through a PingBypass proxy.");
      this.proxyAddress = this.settings.getDefaultGroup().add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("proxy-address")).description("The address of the PingBypass proxy server.")).defaultValue("localhost")).build());
      this.proxyPort = this.settings.getDefaultGroup().add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("proxy-port")).description("The port of the PingBypass proxy server.")).defaultValue(25565)).min(1).max(65535).build());
   }

   public void setOriginalServerInfo(class_642 serverInfo) {
      this.originalServerInfo = serverInfo;
   }

   public class_642 getOriginalServerInfo() {
      return this.originalServerInfo;
   }

   public String getProxyAddress() {
      return (String)this.proxyAddress.get();
   }

   public int getProxyPort() {
      return (Integer)this.proxyPort.get();
   }
}
