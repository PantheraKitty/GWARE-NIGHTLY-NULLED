package javassist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ByteArrayClassPath implements ClassPath {
   protected String classname;
   protected byte[] classfile;

   public ByteArrayClassPath(String name, byte[] classfile) {
      this.classname = name;
      this.classfile = classfile;
   }

   public String toString() {
      return "byte[]:" + this.classname;
   }

   public InputStream openClassfile(String classname) {
      return this.classname.equals(classname) ? new ByteArrayInputStream(this.classfile) : null;
   }

   public URL find(String classname) {
      if (this.classname.equals(classname)) {
         String cname = classname.replace('.', '/') + ".class";

         try {
            return new URL((URL)null, "file:/ByteArrayClassPath/" + cname, new ByteArrayClassPath.BytecodeURLStreamHandler());
         } catch (MalformedURLException var4) {
         }
      }

      return null;
   }

   private class BytecodeURLConnection extends URLConnection {
      protected BytecodeURLConnection(URL url) {
         super(url);
      }

      public void connect() throws IOException {
      }

      public InputStream getInputStream() throws IOException {
         return new ByteArrayInputStream(ByteArrayClassPath.this.classfile);
      }

      public int getContentLength() {
         return ByteArrayClassPath.this.classfile.length;
      }
   }

   private class BytecodeURLStreamHandler extends URLStreamHandler {
      private BytecodeURLStreamHandler() {
      }

      protected URLConnection openConnection(URL u) {
         return ByteArrayClassPath.this.new BytecodeURLConnection(u);
      }

      // $FF: synthetic method
      BytecodeURLStreamHandler(Object x1) {
         this();
      }
   }
}
