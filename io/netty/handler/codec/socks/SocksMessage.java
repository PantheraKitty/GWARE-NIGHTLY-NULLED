package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public abstract class SocksMessage {
   private final SocksMessageType type;
   private final SocksProtocolVersion protocolVersion;

   protected SocksMessage(SocksMessageType type) {
      this.protocolVersion = SocksProtocolVersion.SOCKS5;
      this.type = (SocksMessageType)ObjectUtil.checkNotNull(type, "type");
   }

   public SocksMessageType type() {
      return this.type;
   }

   public SocksProtocolVersion protocolVersion() {
      return this.protocolVersion;
   }

   /** @deprecated */
   @Deprecated
   public abstract void encodeAsByteBuf(ByteBuf var1);
}
