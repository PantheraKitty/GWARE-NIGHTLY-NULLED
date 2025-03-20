package javassist.util.proxy;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles.Lookup;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.bytecode.ClassFile;

public class FactoryHelper {
   public static final Class<?>[] primitiveTypes;
   public static final String[] wrapperTypes;
   public static final String[] wrapperDesc;
   public static final String[] unwarpMethods;
   public static final String[] unwrapDesc;
   public static final int[] dataSize;

   public static final int typeIndex(Class<?> type) {
      for(int i = 0; i < primitiveTypes.length; ++i) {
         if (primitiveTypes[i] == type) {
            return i;
         }
      }

      throw new RuntimeException("bad type:" + type.getName());
   }

   /** @deprecated */
   public static Class<?> toClass(ClassFile cf, ClassLoader loader) throws CannotCompileException {
      return toClass(cf, (Class)null, loader, (ProtectionDomain)null);
   }

   /** @deprecated */
   public static Class<?> toClass(ClassFile cf, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
      return toClass(cf, (Class)null, loader, domain);
   }

   public static Class<?> toClass(ClassFile cf, Class<?> neighbor, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
      try {
         byte[] b = toBytecode(cf);
         return ProxyFactory.onlyPublicMethods ? DefineClassHelper.toPublicClass(cf.getName(), b) : DefineClassHelper.toClass(cf.getName(), neighbor, loader, domain, b);
      } catch (IOException var5) {
         throw new CannotCompileException(var5);
      }
   }

   public static Class<?> toClass(ClassFile cf, Lookup lookup) throws CannotCompileException {
      try {
         byte[] b = toBytecode(cf);
         return DefineClassHelper.toClass(lookup, b);
      } catch (IOException var3) {
         throw new CannotCompileException(var3);
      }
   }

   private static byte[] toBytecode(ClassFile cf) throws IOException {
      ByteArrayOutputStream barray = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(barray);

      try {
         cf.write(out);
      } finally {
         out.close();
      }

      return barray.toByteArray();
   }

   public static void writeFile(ClassFile cf, String directoryName) throws CannotCompileException {
      try {
         writeFile0(cf, directoryName);
      } catch (IOException var3) {
         throw new CannotCompileException(var3);
      }
   }

   private static void writeFile0(ClassFile cf, String directoryName) throws CannotCompileException, IOException {
      String classname = cf.getName();
      String filename = directoryName + File.separatorChar + classname.replace('.', File.separatorChar) + ".class";
      int pos = filename.lastIndexOf(File.separatorChar);
      if (pos > 0) {
         String dir = filename.substring(0, pos);
         if (!dir.equals(".")) {
            (new File(dir)).mkdirs();
         }
      }

      DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));

      try {
         cf.write(out);
      } catch (IOException var10) {
         throw var10;
      } finally {
         out.close();
      }

   }

   static {
      primitiveTypes = new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE};
      wrapperTypes = new String[]{"java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Void"};
      wrapperDesc = new String[]{"(Z)V", "(B)V", "(C)V", "(S)V", "(I)V", "(J)V", "(F)V", "(D)V"};
      unwarpMethods = new String[]{"booleanValue", "byteValue", "charValue", "shortValue", "intValue", "longValue", "floatValue", "doubleValue"};
      unwrapDesc = new String[]{"()Z", "()B", "()C", "()S", "()I", "()J", "()F", "()D"};
      dataSize = new int[]{1, 1, 1, 1, 1, 2, 1, 2};
   }
}
