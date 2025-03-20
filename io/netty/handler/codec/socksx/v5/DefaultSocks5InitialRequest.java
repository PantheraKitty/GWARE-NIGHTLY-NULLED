package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DefaultSocks5InitialRequest extends AbstractSocks5Message implements Socks5InitialRequest {
   private final List<Socks5AuthMethod> authMethods;

   public DefaultSocks5InitialRequest(Socks5AuthMethod... authMethods) {
      ObjectUtil.checkNotNull(authMethods, "authMethods");
      List<Socks5AuthMethod> list = new ArrayList(authMethods.length);
      Socks5AuthMethod[] var3 = authMethods;
      int var4 = authMethods.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Socks5AuthMethod m = var3[var5];
         if (m == null) {
            break;
         }

         list.add(m);
      }

      this.authMethods = Collections.unmodifiableList((List)ObjectUtil.checkNonEmpty(list, "list"));
   }

   public DefaultSocks5InitialRequest(Iterable<Socks5AuthMethod> authMethods) {
      ObjectUtil.checkNotNull(authMethods, "authSchemes");
      List<Socks5AuthMethod> list = new ArrayList();
      Iterator var3 = authMethods.iterator();

      while(var3.hasNext()) {
         Socks5AuthMethod m = (Socks5AuthMethod)var3.next();
         if (m == null) {
            break;
         }

         list.add(m);
      }

      this.authMethods = Collections.unmodifiableList((List)ObjectUtil.checkNonEmpty(list, "list"));
   }

   public List<Socks5AuthMethod> authMethods() {
      return this.authMethods;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder(StringUtil.simpleClassName(this));
      DecoderResult decoderResult = this.decoderResult();
      if (!decoderResult.isSuccess()) {
         buf.append("(decoderResult: ");
         buf.append(decoderResult);
         buf.append(", authMethods: ");
      } else {
         buf.append("(authMethods: ");
      }

      buf.append(this.authMethods());
      buf.append(')');
      return buf.toString();
   }
}
