package io.netty.handler.codec.socks;

public enum SocksProtocolVersion {
   SOCKS4a((byte)4),
   SOCKS5((byte)5),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksProtocolVersion(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksProtocolVersion fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksProtocolVersion valueOf(byte b) {
      SocksProtocolVersion[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SocksProtocolVersion code = var1[var3];
         if (code.b == b) {
            return code;
         }
      }

      return UNKNOWN;
   }

   public byte byteValue() {
      return this.b;
   }
}
