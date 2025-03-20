package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksAuthResponseDecoder extends ReplayingDecoder<SocksAuthResponseDecoder.State> {
   public SocksAuthResponseDecoder() {
      super(SocksAuthResponseDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
      switch((SocksAuthResponseDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (byteBuf.readByte() != SocksSubnegotiationVersion.AUTH_PASSWORD.byteValue()) {
            out.add(SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE);
            break;
         } else {
            this.checkpoint(SocksAuthResponseDecoder.State.READ_AUTH_RESPONSE);
         }
      case READ_AUTH_RESPONSE:
         SocksAuthStatus authStatus = SocksAuthStatus.valueOf(byteBuf.readByte());
         out.add(new SocksAuthResponse(authStatus));
         break;
      default:
         throw new Error();
      }

      channelHandlerContext.pipeline().remove(this);
   }

   public static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_AUTH_RESPONSE;
   }
}
