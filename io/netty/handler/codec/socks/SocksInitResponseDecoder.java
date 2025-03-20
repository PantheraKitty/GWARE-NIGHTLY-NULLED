package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksInitResponseDecoder extends ReplayingDecoder<SocksInitResponseDecoder.State> {
   public SocksInitResponseDecoder() {
      super(SocksInitResponseDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
      switch((SocksInitResponseDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (byteBuf.readByte() != SocksProtocolVersion.SOCKS5.byteValue()) {
            out.add(SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE);
            break;
         } else {
            this.checkpoint(SocksInitResponseDecoder.State.READ_PREFERRED_AUTH_TYPE);
         }
      case READ_PREFERRED_AUTH_TYPE:
         SocksAuthScheme authScheme = SocksAuthScheme.valueOf(byteBuf.readByte());
         out.add(new SocksInitResponse(authScheme));
         break;
      default:
         throw new Error();
      }

      ctx.pipeline().remove(this);
   }

   public static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_PREFERRED_AUTH_TYPE;
   }
}
