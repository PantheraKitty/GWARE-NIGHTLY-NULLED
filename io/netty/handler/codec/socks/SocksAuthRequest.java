package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.CharsetEncoder;

public final class SocksAuthRequest extends SocksRequest {
   private static final SocksSubnegotiationVersion SUBNEGOTIATION_VERSION;
   private final String username;
   private final String password;

   public SocksAuthRequest(String username, String password) {
      super(SocksRequestType.AUTH);
      ObjectUtil.checkNotNull(username, "username");
      ObjectUtil.checkNotNull(password, "password");
      CharsetEncoder asciiEncoder = CharsetUtil.encoder(CharsetUtil.US_ASCII);
      if (asciiEncoder.canEncode(username) && asciiEncoder.canEncode(password)) {
         if (username.length() > 255) {
            throw new IllegalArgumentException("username: " + username + " exceeds 255 char limit");
         } else if (password.length() > 255) {
            throw new IllegalArgumentException("password: **** exceeds 255 char limit");
         } else {
            this.username = username;
            this.password = password;
         }
      } else {
         throw new IllegalArgumentException("username: " + username + " or password: **** values should be in pure ascii");
      }
   }

   public String username() {
      return this.username;
   }

   public String password() {
      return this.password;
   }

   public void encodeAsByteBuf(ByteBuf byteBuf) {
      byteBuf.writeByte(SUBNEGOTIATION_VERSION.byteValue());
      byteBuf.writeByte(this.username.length());
      byteBuf.writeCharSequence(this.username, CharsetUtil.US_ASCII);
      byteBuf.writeByte(this.password.length());
      byteBuf.writeCharSequence(this.password, CharsetUtil.US_ASCII);
   }

   static {
      SUBNEGOTIATION_VERSION = SocksSubnegotiationVersion.AUTH_PASSWORD;
   }
}
