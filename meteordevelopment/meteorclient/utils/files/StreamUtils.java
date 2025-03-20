package meteordevelopment.meteorclient.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import meteordevelopment.meteorclient.MeteorClient;
import org.apache.commons.io.IOUtils;

public class StreamUtils {
   private StreamUtils() {
   }

   public static void copy(File from, File to) {
      try {
         FileInputStream in = new FileInputStream(from);

         try {
            FileOutputStream out = new FileOutputStream(to);

            try {
               in.transferTo(out);
            } catch (Throwable var8) {
               try {
                  out.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            out.close();
         } catch (Throwable var9) {
            try {
               in.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         in.close();
      } catch (IOException var10) {
         MeteorClient.LOG.error("Error copying from file '%s' to file '%s'.".formatted(new Object[]{from.getName(), to.getName()}), var10);
      }

   }

   public static void copy(InputStream in, File to) {
      try {
         FileOutputStream out = new FileOutputStream(to);

         try {
            in.transferTo(out);
         } catch (Throwable var11) {
            try {
               out.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }

            throw var11;
         }

         out.close();
      } catch (IOException var12) {
         MeteorClient.LOG.error("Error writing to file '%s'.".formatted(new Object[]{to.getName()}));
      } finally {
         IOUtils.closeQuietly(in);
      }

   }
}
