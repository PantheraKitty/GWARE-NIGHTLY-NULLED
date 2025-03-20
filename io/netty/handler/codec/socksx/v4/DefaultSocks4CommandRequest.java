package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

public class DefaultSocks4CommandRequest extends AbstractSocks4Message implements Socks4CommandRequest {
   private final Socks4CommandType type;
   private final String dstAddr;
   private final int dstPort;
   private final String userId;

   public DefaultSocks4CommandRequest(Socks4CommandType type, String dstAddr, int dstPort) {
      this(type, dstAddr, dstPort, "");
   }

   public DefaultSocks4CommandRequest(Socks4CommandType type, String dstAddr, int dstPort, String userId) {
      if (dstPort > 0 && dstPort < 65536) {
         this.type = (Socks4CommandType)ObjectUtil.checkNotNull(type, "type");
         this.dstAddr = IDN.toASCII((String)ObjectUtil.checkNotNull(dstAddr, "dstAddr"));
         this.userId = (String)ObjectUtil.checkNotNull(userId, "userId");
         this.dstPort = dstPort;
      } else {
         throw new IllegalArgumentException("dstPort: " + dstPort + " (expected: 1~65535)");
      }
   }

   public Socks4CommandType type() {
      return this.type;
   }

   public String dstAddr() {
      return this.dstAddr;
   }

   public int dstPort() {
      return this.dstPort;
   }

   public String userId() {
      return this.userId;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append(StringUtil.simpleClassName(this));
      DecoderResult decoderResult = this.decoderResult();
      if (!decoderResult.isSuccess()) {
         buf.append("(decoderResult: ");
         buf.append(decoderResult);
         buf.append(", type: ");
      } else {
         buf.append("(type: ");
      }

      buf.append(this.type());
      buf.append(", dstAddr: ");
      buf.append(this.dstAddr());
      buf.append(", dstPort: ");
      buf.append(this.dstPort());
      buf.append(", userId: ");
      buf.append(this.userId());
      buf.append(')');
      return buf.toString();
   }
}
