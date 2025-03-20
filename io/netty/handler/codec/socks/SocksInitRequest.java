package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class SocksInitRequest extends SocksRequest {
   private final List<SocksAuthScheme> authSchemes;

   public SocksInitRequest(List<SocksAuthScheme> authSchemes) {
      super(SocksRequestType.INIT);
      this.authSchemes = (List)ObjectUtil.checkNotNull(authSchemes, "authSchemes");
   }

   public List<SocksAuthScheme> authSchemes() {
      return Collections.unmodifiableList(this.authSchemes);
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
      byteBuf.writeByte(this.protocolVersion().byteValue());
      byteBuf.writeByte(this.authSchemes.size());
      Iterator var2 = this.authSchemes.iterator();

      while(var2.hasNext()) {
         SocksAuthScheme authScheme = (SocksAuthScheme)var2.next();
         byteBuf.writeByte(authScheme.byteValue());
      }

   }
}
