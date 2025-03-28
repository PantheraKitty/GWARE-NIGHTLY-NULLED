package io.netty.handler.codec.socksx.v5;

import io.netty.util.internal.ObjectUtil;

public class Socks5PasswordAuthStatus implements Comparable<Socks5PasswordAuthStatus> {
   public static final Socks5PasswordAuthStatus SUCCESS = new Socks5PasswordAuthStatus(0, "SUCCESS");
   public static final Socks5PasswordAuthStatus FAILURE = new Socks5PasswordAuthStatus(255, "FAILURE");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks5PasswordAuthStatus valueOf(byte b) {
      switch(b) {
      case -1:
         return FAILURE;
      case 0:
         return SUCCESS;
      default:
         return new Socks5PasswordAuthStatus(b);
      }
   }

   public Socks5PasswordAuthStatus(int byteValue) {
      this(byteValue, "UNKNOWN");
   }

   public Socks5PasswordAuthStatus(int byteValue, String name) {
      this.name = (String)ObjectUtil.checkNotNull(name, "name");
      this.byteValue = (byte)byteValue;
   }

   public byte byteValue() {
      return this.byteValue;
   }

   public boolean isSuccess() {
      return this.byteValue == 0;
   }

   public int hashCode() {
      return this.byteValue;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Socks5PasswordAuthStatus)) {
         return false;
      } else {
         return this.byteValue == ((Socks5PasswordAuthStatus)obj).byteValue;
      }
   }

   public int compareTo(Socks5PasswordAuthStatus o) {
      return this.byteValue - o.byteValue;
   }

   public String toString() {
      String text = this.text;
      if (text == null) {
         this.text = text = this.name + '(' + (this.byteValue & 255) + ')';
      }

      return text;
   }
}
