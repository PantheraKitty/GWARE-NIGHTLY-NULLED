package io.netty.handler.codec.socks;

public enum SocksAuthStatus {
   SUCCESS((byte)0),
   FAILURE((byte)-1);

   private final byte b;

   private SocksAuthStatus(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksAuthStatus fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksAuthStatus valueOf(byte b) {
      SocksAuthStatus[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SocksAuthStatus code = var1[var3];
         if (code.b == b) {
            return code;
         }
      }

      return FAILURE;
   }

   public byte byteValue() {
      return this.b;
   }
}
