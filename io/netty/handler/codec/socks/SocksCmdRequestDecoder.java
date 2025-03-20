package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.NetUtil;
import java.util.List;

public class SocksCmdRequestDecoder extends ReplayingDecoder<SocksCmdRequestDecoder.State> {
   private SocksCmdType cmdType;
   private SocksAddressType addressType;

   public SocksCmdRequestDecoder() {
      super(SocksCmdRequestDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
      label22:
      switch((SocksCmdRequestDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (byteBuf.readByte() != SocksProtocolVersion.SOCKS5.byteValue()) {
            out.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
            break;
         } else {
            this.checkpoint(SocksCmdRequestDecoder.State.READ_CMD_HEADER);
         }
      case READ_CMD_HEADER:
         this.cmdType = SocksCmdType.valueOf(byteBuf.readByte());
         byteBuf.skipBytes(1);
         this.addressType = SocksAddressType.valueOf(byteBuf.readByte());
         this.checkpoint(SocksCmdRequestDecoder.State.READ_CMD_ADDRESS);
      case READ_CMD_ADDRESS:
         String host;
         int port;
         switch(this.addressType) {
         case IPv4:
            String host = NetUtil.intToIpAddress(byteBuf.readInt());
            int port = byteBuf.readUnsignedShort();
            out.add(new SocksCmdRequest(this.cmdType, this.addressType, host, port));
            break label22;
         case DOMAIN:
            int fieldLength = byteBuf.readByte();
            host = SocksCommonUtils.readUsAscii(byteBuf, fieldLength);
            port = byteBuf.readUnsignedShort();
            out.add(new SocksCmdRequest(this.cmdType, this.addressType, host, port));
            break label22;
         case IPv6:
            byte[] bytes = new byte[16];
            byteBuf.readBytes(bytes);
            host = SocksCommonUtils.ipv6toStr(bytes);
            port = byteBuf.readUnsignedShort();
            out.add(new SocksCmdRequest(this.cmdType, this.addressType, host, port));
            break label22;
         case UNKNOWN:
            out.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
            break label22;
         default:
            throw new Error();
         }
      default:
         throw new Error();
      }

      ctx.pipeline().remove(this);
   }

   public static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_CMD_HEADER,
      READ_CMD_ADDRESS;
   }
}
