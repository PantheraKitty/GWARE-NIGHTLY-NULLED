package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class SocksAuthResponse extends SocksResponse {
   private static final SocksSubnegotiationVersion SUBNEGOTIATION_VERSION;
   private final SocksAuthStatus authStatus;

   public SocksAuthResponse(SocksAuthStatus authStatus) {
      super(SocksResponseType.AUTH);
      this.authStatus = (SocksAuthStatus)ObjectUtil.checkNotNull(authStatus, "authStatus");
   }

   public SocksAuthStatus authStatus() {
      return this.authStatus;
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
      byteBuf.writeByte(SUBNEGOTIATION_VERSION.byteValue());
      byteBuf.writeByte(this.authStatus.byteValue());
   }

   static {
      SUBNEGOTIATION_VERSION = SocksSubnegotiationVersion.AUTH_PASSWORD;
   }
}
