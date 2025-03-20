package io.netty.handler.codec.socks;

public enum SocksSubnegotiationVersion {
   AUTH_PASSWORD((byte)1),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksSubnegotiationVersion(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksSubnegotiationVersion fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksSubnegotiationVersion valueOf(byte b) {
      SocksSubnegotiationVersion[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SocksSubnegotiationVersion code = var1[var3];
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
