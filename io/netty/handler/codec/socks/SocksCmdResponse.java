package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import java.net.IDN;

public final class SocksCmdResponse extends SocksResponse {
   private final SocksCmdStatus cmdStatus;
   private final SocksAddressType addressType;
   private final String host;
   private final int port;
   private static final byte[] DOMAIN_ZEROED = new byte[]{0};
   private static final byte[] IPv4_HOSTNAME_ZEROED = new byte[]{0, 0, 0, 0};
   private static final byte[] IPv6_HOSTNAME_ZEROED = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   public SocksCmdResponse(SocksCmdStatus cmdStatus, SocksAddressType addressType) {
      this(cmdStatus, addressType, (String)null, 0);
   }

   public SocksCmdResponse(SocksCmdStatus cmdStatus, SocksAddressType addressType, String host, int port) {
      super(SocksResponseType.CMD);
      ObjectUtil.checkNotNull(cmdStatus, "cmdStatus");
      ObjectUtil.checkNotNull(addressType, "addressType");
      if (host != null) {
         switch(addressType) {
         case IPv4:
            if (!NetUtil.isValidIpV4Address(host)) {
               throw new IllegalArgumentException(host + " is not a valid IPv4 address");
            }
            break;
         case DOMAIN:
            String asciiHost = IDN.toASCII(host);
            if (asciiHost.length() > 255) {
               throw new IllegalArgumentException(host + " IDN: " + asciiHost + " exceeds 255 char limit");
            }

            host = asciiHost;
            break;
         case IPv6:
            if (!NetUtil.isValidIpV6Address(host)) {
               throw new IllegalArgumentException(host + " is not a valid IPv6 address");
            }
         case UNKNOWN:
         }
      }

      if (port >= 0 && port <= 65535) {
         this.cmdStatus = cmdStatus;
         this.addressType = addressType;
         this.host = host;
         this.port = port;
      } else {
         throw new IllegalArgumentException(port + " is not in bounds 0 <= x <= 65535");
      }
   }

   public SocksCmdStatus cmdStatus() {
      return this.cmdStatus;
   }

   public SocksAddressType addressType() {
      return this.addressType;
   }

   public String host() {
      return this.host != null && this.addressType == SocksAddressType.DOMAIN ? IDN.toUnicode(this.host) : this.host;
   }

   public int port() {
      return this.port;
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
      byteBuf.writeByte(this.protocolVersion().byteValue());
      byteBuf.writeByte(this.cmdStatus.byteValue());
      byteBuf.writeByte(0);
      byteBuf.writeByte(this.addressType.byteValue());
      byte[] hostContent;
      switch(this.addressType) {
      case IPv4:
         hostContent = this.host == null ? IPv4_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(this.host);
         byteBuf.writeBytes(hostContent);
         byteBuf.writeShort(this.port);
         break;
      case DOMAIN:
         if (this.host != null) {
            byteBuf.writeByte(this.host.length());
            byteBuf.writeCharSequence(this.host, CharsetUtil.US_ASCII);
         } else {
            byteBuf.writeByte(DOMAIN_ZEROED.length);
            byteBuf.writeBytes(DOMAIN_ZEROED);
         }

         byteBuf.writeShort(this.port);
         break;
      case IPv6:
         hostContent = this.host == null ? IPv6_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(this.host);
         byteBuf.writeBytes(hostContent);
         byteBuf.writeShort(this.port);
      }

   }
}
