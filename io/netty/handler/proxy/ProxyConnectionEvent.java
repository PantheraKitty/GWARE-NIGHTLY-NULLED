package io.netty.handler.proxy;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public final class ProxyConnectionEvent {
   private final String protocol;
   private final String authScheme;
   private final SocketAddress proxyAddress;
   private final SocketAddress destinationAddress;
   private String strVal;

   public ProxyConnectionEvent(String protocol, String authScheme, SocketAddress proxyAddress, SocketAddress destinationAddress) {
      this.protocol = (String)ObjectUtil.checkNotNull(protocol, "protocol");
      this.authScheme = (String)ObjectUtil.checkNotNull(authScheme, "authScheme");
      this.proxyAddress = (SocketAddress)ObjectUtil.checkNotNull(proxyAddress, "proxyAddress");
      this.destinationAddress = (SocketAddress)ObjectUtil.checkNotNull(destinationAddress, "destinationAddress");
   }

   public String protocol() {
      return this.protocol;
   }

   public String authScheme() {
      return this.authScheme;
   }

   public <T extends SocketAddress> T proxyAddress() {
      return this.proxyAddress;
   }

   public <T extends SocketAddress> T destinationAddress() {
      return this.destinationAddress;
   }

   public String toString() {
      if (this.strVal != null) {
         return this.strVal;
      } else {
         StringBuilder buf = (new StringBuilder(128)).append(StringUtil.simpleClassName(this)).append('(').append(this.protocol).append(", ").append(this.authScheme).append(", ").append(this.proxyAddress).append(" => ").append(this.destinationAddress).append(')');
         return this.strVal = buf.toString();
      }
   }
}
