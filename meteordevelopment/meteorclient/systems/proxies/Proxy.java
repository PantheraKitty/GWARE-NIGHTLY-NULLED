package meteordevelopment.meteorclient.systems.proxies;

import java.net.InetSocketAddress;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;
import net.minecraft.class_2520;

public class Proxy implements ISerializable<Proxy> {
   public final Settings settings = new Settings();
   private final SettingGroup sgGeneral;
   private final SettingGroup sgOptional;
   public Setting<String> name;
   public Setting<ProxyType> type;
   public Setting<String> address;
   public Setting<Integer> port;
   public Setting<Boolean> enabled;
   public Setting<String> username;
   public Setting<String> password;

   private Proxy() {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgOptional = this.settings.createGroup("Optional");
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the proxy.")).build());
      this.type = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("type")).description("The type of proxy.")).defaultValue(ProxyType.Socks5)).build());
      this.address = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("address")).description("The ip address of the proxy.")).filter(Utils::ipFilter).build());
      this.port = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("port")).description("The port of the proxy.")).defaultValue(0)).range(0, 65535).sliderMax(65535).noSlider().build());
      this.enabled = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Whether the proxy is enabled.")).defaultValue(true)).build());
      this.username = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("username")).description("The username of the proxy.")).build());
      this.password = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("password")).description("The password of the proxy.")).visible(() -> {
         return ((ProxyType)this.type.get()).equals(ProxyType.Socks5);
      })).build());
   }

   public Proxy(class_2520 tag) {
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgOptional = this.settings.createGroup("Optional");
      this.name = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the proxy.")).build());
      this.type = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("type")).description("The type of proxy.")).defaultValue(ProxyType.Socks5)).build());
      this.address = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("address")).description("The ip address of the proxy.")).filter(Utils::ipFilter).build());
      this.port = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("port")).description("The port of the proxy.")).defaultValue(0)).range(0, 65535).sliderMax(65535).noSlider().build());
      this.enabled = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Whether the proxy is enabled.")).defaultValue(true)).build());
      this.username = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("username")).description("The username of the proxy.")).build());
      this.password = this.sgOptional.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("password")).description("The password of the proxy.")).visible(() -> {
         return ((ProxyType)this.type.get()).equals(ProxyType.Socks5);
      })).build());
      this.fromTag((class_2487)tag);
   }

   public boolean resolveAddress() {
      int port = (Integer)this.port.get();
      String address = (String)this.address.get();
      if (port > 0 && port <= 65535 && address != null && !address.isBlank()) {
         InetSocketAddress socketAddress = new InetSocketAddress(address, port);
         return !socketAddress.isUnresolved();
      } else {
         return false;
      }
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public Proxy fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_10562("settings"));
      }

      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Proxy proxy = (Proxy)o;
         return Objects.equals(proxy.address.get(), this.address.get()) && Objects.equals(proxy.port.get(), this.port.get());
      } else {
         return false;
      }
   }

   public static class Builder {
      protected ProxyType type;
      protected String address;
      protected int port;
      protected String name;
      protected String username;
      protected boolean enabled;

      public Builder() {
         this.type = ProxyType.Socks5;
         this.address = "";
         this.port = 0;
         this.name = "";
         this.username = "";
         this.enabled = false;
      }

      public Proxy.Builder type(ProxyType type) {
         this.type = type;
         return this;
      }

      public Proxy.Builder address(String address) {
         this.address = address;
         return this;
      }

      public Proxy.Builder port(int port) {
         this.port = port;
         return this;
      }

      public Proxy.Builder name(String name) {
         this.name = name;
         return this;
      }

      public Proxy.Builder username(String username) {
         this.username = username;
         return this;
      }

      public Proxy.Builder enabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      public Proxy build() {
         Proxy proxy = new Proxy();
         if (!this.type.equals(proxy.type.getDefaultValue())) {
            proxy.type.set(this.type);
         }

         if (!this.address.equals(proxy.address.getDefaultValue())) {
            proxy.address.set(this.address);
         }

         if (this.port != (Integer)proxy.port.getDefaultValue()) {
            proxy.port.set(this.port);
         }

         if (!this.name.equals(proxy.name.getDefaultValue())) {
            proxy.name.set(this.name);
         }

         if (!this.username.equals(proxy.username.getDefaultValue())) {
            proxy.username.set(this.username);
         }

         if (this.enabled != (Boolean)proxy.enabled.getDefaultValue()) {
            proxy.enabled.set(this.enabled);
         }

         return proxy;
      }
   }
}
