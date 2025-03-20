package meteordevelopment.meteorclient.systems;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.files.StreamUtils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_148;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import org.apache.commons.io.FilenameUtils;

public abstract class System<T> implements ISerializable<T> {
   private final String name;
   private File file;
   protected boolean isFirstInit;
   private static final DateTimeFormatter DATE_TIME_FORMATTER;

   public System(String name) {
      this.name = name;
      if (name != null) {
         this.file = new File(MeteorClient.FOLDER, name + ".nbt");
         this.isFirstInit = !this.file.exists();
      }

   }

   public void init() {
   }

   public void save(File folder) {
      File file = this.getFile();
      if (file != null) {
         class_2487 tag = this.toTag();
         if (tag != null) {
            try {
               File tempFile = File.createTempFile("meteor-client", file.getName());
               class_2507.method_10630(tag, tempFile.toPath());
               if (folder != null) {
                  file = new File(folder, file.getName());
               }

               file.getParentFile().mkdirs();
               StreamUtils.copy(tempFile, file);
               tempFile.delete();
            } catch (IOException var5) {
               var5.printStackTrace();
            }

         }
      }
   }

   public void save() {
      this.save((File)null);
   }

   public void load(File folder) {
      File file = this.getFile();
      if (file != null) {
         try {
            if (folder != null) {
               file = new File(folder, file.getName());
            }

            if (file.exists()) {
               try {
                  this.fromTag(class_2507.method_10633(file.toPath()));
               } catch (class_148 var6) {
                  String var10000 = FilenameUtils.removeExtension(file.getName());
                  String backupName = var10000 + "-" + ZonedDateTime.now().format(DATE_TIME_FORMATTER) + ".backup.nbt";
                  File backup = new File(file.getParentFile(), backupName);
                  StreamUtils.copy(file, backup);
                  MeteorClient.LOG.error("Error loading " + this.name + ". Possibly corrupted?");
                  MeteorClient.LOG.info("Saved settings backup to '" + String.valueOf(backup) + "'.");
                  var6.printStackTrace();
               }
            }
         } catch (IOException var7) {
            var7.printStackTrace();
         }

      }
   }

   public void load() {
      this.load((File)null);
   }

   public File getFile() {
      return this.file;
   }

   public String getName() {
      return this.name;
   }

   public class_2487 toTag() {
      return null;
   }

   public T fromTag(class_2487 tag) {
      return null;
   }

   static {
      DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
   }
}
