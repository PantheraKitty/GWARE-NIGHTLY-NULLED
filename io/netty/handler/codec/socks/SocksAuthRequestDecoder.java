package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksAuthRequestDecoder extends ReplayingDecoder<SocksAuthRequestDecoder.State> {
   private String username;

   public SocksAuthRequestDecoder() {
      super(SocksAuthRequestDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
      byte fieldLength;
      switch((SocksAuthRequestDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (byteBuf.readByte() != SocksSubnegotiationVersion.AUTH_PASSWORD.byteValue()) {
            out.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
            break;
         } else {
            this.checkpoint(SocksAuthRequestDecoder.State.READ_USERNAME);
         }
      case READ_USERNAME:
         fieldLength = byteBuf.readByte();
         this.username = SocksCommonUtils.readUsAscii(byteBuf, fieldLength);
         this.checkpoint(SocksAuthRequestDecoder.State.READ_PASSWORD);
      case READ_PASSWORD:
         fieldLength = byteBuf.readByte();
         String password = SocksCommonUtils.readUsAscii(byteBuf, fieldLength);
         out.add(new SocksAuthRequest(this.username, password));
         break;
      default:
         throw new Error();
      }

      ctx.pipeline().remove(this);
   }

   public static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_USERNAME,
      READ_PASSWORD;
   }
}
