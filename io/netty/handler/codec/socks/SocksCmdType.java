package io.netty.handler.codec.socks;

public enum SocksCmdType {
   CONNECT((byte)1),
   BIND((byte)2),
   UDP((byte)3),
   UNKNOWN((byte)-1);

   private final byte b;

   private SocksCmdType(byte b) {
      this.b = b;
   }

   /** @deprecated */
   @Deprecated
   public static SocksCmdType fromByte(byte b) {
      return valueOf(b);
   }

   public static SocksCmdType valueOf(byte b) {
      SocksCmdType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SocksCmdType code = var1[var3];
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
