package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class SocksInitResponse extends SocksResponse {
   private final SocksAuthScheme authScheme;

   public SocksInitResponse(SocksAuthScheme authScheme) {
      super(SocksResponseType.INIT);
      this.authScheme = (SocksAuthScheme)ObjectUtil.checkNotNull(authScheme, "authScheme");
   }

   public SocksAuthScheme authScheme() {
      return this.authScheme;
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
      byteBuf.writeByte(this.protocolVersion().byteValue());
      byteBuf.writeByte(this.authScheme.byteValue());
   }
}
