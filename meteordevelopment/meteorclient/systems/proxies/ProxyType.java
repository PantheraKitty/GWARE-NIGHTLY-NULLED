package meteordevelopment.meteorclient.systems.proxies;

import org.jetbrains.annotations.Nullable;

public enum ProxyType {
   Socks4,
   Socks5;

   @Nullable
   public static ProxyType parse(String group) {
      ProxyType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ProxyType type = var1[var3];
         if (type.name().equalsIgnoreCase(group)) {
            return type;
         }
      }

      return null;
   }

   // $FF: synthetic method
   private static ProxyType[] $values() {
      return new ProxyType[]{Socks4, Socks5};
   }
}
