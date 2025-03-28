package org.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.util.ClasspathHelper;

public abstract class Vfs {
   private static List<Vfs.UrlType> defaultUrlTypes = new ArrayList(Arrays.asList(Vfs.DefaultUrlTypes.values()));

   public static List<Vfs.UrlType> getDefaultUrlTypes() {
      return defaultUrlTypes;
   }

   public static void setDefaultURLTypes(List<Vfs.UrlType> urlTypes) {
      defaultUrlTypes = urlTypes;
   }

   public static void addDefaultURLTypes(Vfs.UrlType urlType) {
      defaultUrlTypes.add(0, urlType);
   }

   public static Vfs.Dir fromURL(URL url) {
      return fromURL(url, defaultUrlTypes);
   }

   public static Vfs.Dir fromURL(URL url, List<Vfs.UrlType> urlTypes) {
      Iterator var2 = urlTypes.iterator();

      while(var2.hasNext()) {
         Vfs.UrlType type = (Vfs.UrlType)var2.next();

         try {
            if (type.matches(url)) {
               Vfs.Dir dir = type.createDir(url);
               if (dir != null) {
                  return dir;
               }
            }
         } catch (Throwable var5) {
            if (Reflections.log != null) {
               Reflections.log.warn("could not create Dir using " + type + " from url " + url.toExternalForm() + ". skipping.", var5);
            }
         }
      }

      throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url.toExternalForm() + "]\neither use fromURL(final URL url, final List<UrlType> urlTypes) or use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) with your specialized UrlType.");
   }

   public static Vfs.Dir fromURL(URL url, Vfs.UrlType... urlTypes) {
      return fromURL(url, Arrays.asList(urlTypes));
   }

   public static Iterable<Vfs.File> findFiles(Collection<URL> inUrls, String packagePrefix, Predicate<String> nameFilter) {
      Predicate<Vfs.File> fileNamePredicate = (file) -> {
         String path = file.getRelativePath();
         if (!path.startsWith(packagePrefix)) {
            return false;
         } else {
            String filename = path.substring(path.indexOf(packagePrefix) + packagePrefix.length());
            return !filename.isEmpty() && nameFilter.test(filename.substring(1));
         }
      };
      return findFiles(inUrls, fileNamePredicate);
   }

   public static Iterable<Vfs.File> findFiles(Collection<URL> urls, Predicate<Vfs.File> filePredicate) {
      return () -> {
         return urls.stream().flatMap((url) -> {
            try {
               return StreamSupport.stream(fromURL(url).getFiles().spliterator(), false);
            } catch (Throwable var2) {
               if (Reflections.log != null) {
                  Reflections.log.error("could not findFiles for url. continuing. [" + url + "]", var2);
               }

               return Stream.of();
            }
         }).filter(filePredicate).iterator();
      };
   }

   public static java.io.File getFile(URL url) {
      java.io.File file;
      String path;
      try {
         path = url.toURI().getSchemeSpecificPart();
         if ((file = new java.io.File(path)).exists()) {
            return file;
         }
      } catch (URISyntaxException var6) {
      }

      try {
         path = URLDecoder.decode(url.getPath(), "UTF-8");
         if (path.contains(".jar!")) {
            path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
         }

         if ((file = new java.io.File(path)).exists()) {
            return file;
         }
      } catch (UnsupportedEncodingException var5) {
      }

      try {
         path = url.toExternalForm();
         if (path.startsWith("jar:")) {
            path = path.substring("jar:".length());
         }

         if (path.startsWith("wsjar:")) {
            path = path.substring("wsjar:".length());
         }

         if (path.startsWith("file:")) {
            path = path.substring("file:".length());
         }

         if (path.contains(".jar!")) {
            path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
         }

         if (path.contains(".war!")) {
            path = path.substring(0, path.indexOf(".war!") + ".war".length());
         }

         if ((file = new java.io.File(path)).exists()) {
            return file;
         }

         path = path.replace("%20", " ");
         if ((file = new java.io.File(path)).exists()) {
            return file;
         }
      } catch (Exception var4) {
      }

      return null;
   }

   private static boolean hasJarFileInPath(URL url) {
      return url.toExternalForm().matches(".*\\.jar(!.*|$)");
   }

   private static boolean hasInnerJarFileInPath(URL url) {
      return url.toExternalForm().matches(".+\\.jar!/.+");
   }

   public static enum DefaultUrlTypes implements Vfs.UrlType {
      jarFile {
         public boolean matches(URL url) {
            return url.getProtocol().equals("file") && Vfs.hasJarFileInPath(url);
         }

         public Vfs.Dir createDir(URL url) throws Exception {
            return new ZipDir(new JarFile(Vfs.getFile(url)));
         }
      },
      jarUrl {
         public boolean matches(URL url) {
            return ("jar".equals(url.getProtocol()) || "zip".equals(url.getProtocol()) || "wsjar".equals(url.getProtocol())) && !Vfs.hasInnerJarFileInPath(url);
         }

         public Vfs.Dir createDir(URL url) throws Exception {
            try {
               URLConnection urlConnection = url.openConnection();
               if (urlConnection instanceof JarURLConnection) {
                  urlConnection.setUseCaches(false);
                  return new ZipDir(((JarURLConnection)urlConnection).getJarFile());
               }
            } catch (Throwable var3) {
            }

            java.io.File file = Vfs.getFile(url);
            return file != null ? new ZipDir(new JarFile(file)) : null;
         }
      },
      directory {
         public boolean matches(URL url) {
            if (url.getProtocol().equals("file") && !Vfs.hasJarFileInPath(url)) {
               java.io.File file = Vfs.getFile(url);
               return file != null && file.isDirectory();
            } else {
               return false;
            }
         }

         public Vfs.Dir createDir(URL url) throws Exception {
            return new SystemDir(Vfs.getFile(url));
         }
      },
      jboss_vfs {
         public boolean matches(URL url) {
            return url.getProtocol().equals("vfs");
         }

         public Vfs.Dir createDir(URL url) throws Exception {
            return JbossDir.createDir(url);
         }
      },
      jboss_vfsfile {
         public boolean matches(URL url) throws Exception {
            return "vfszip".equals(url.getProtocol()) || "vfsfile".equals(url.getProtocol());
         }

         public Vfs.Dir createDir(URL url) throws Exception {
            return (new UrlTypeVFS()).createDir(url);
         }
      },
      bundle {
         public boolean matches(URL url) throws Exception {
            return url.getProtocol().startsWith("bundle");
         }

         public Vfs.Dir createDir(URL url) throws Exception {
            return Vfs.fromURL((URL)ClasspathHelper.contextClassLoader().loadClass("org.eclipse.core.runtime.FileLocator").getMethod("resolve", URL.class).invoke((Object)null, url));
         }
      },
      jarInputStream {
         public boolean matches(URL url) throws Exception {
            return url.toExternalForm().contains(".jar");
         }

         public Vfs.Dir createDir(URL url) throws Exception {
            return new JarInputDir(url);
         }
      };

      private DefaultUrlTypes() {
      }

      // $FF: synthetic method
      DefaultUrlTypes(Object x2) {
         this();
      }
   }

   public interface UrlType {
      boolean matches(URL var1) throws Exception;

      Vfs.Dir createDir(URL var1) throws Exception;
   }

   public interface File {
      String getName();

      String getRelativePath();

      InputStream openInputStream() throws IOException;
   }

   public interface Dir {
      String getPath();

      Iterable<Vfs.File> getFiles();

      default void close() {
      }
   }
}
