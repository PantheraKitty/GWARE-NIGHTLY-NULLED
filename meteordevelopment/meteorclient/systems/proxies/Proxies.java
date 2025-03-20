package meteordevelopment.meteorclient.systems.proxies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Proxies extends System<Proxies> implements Iterable<Proxy> {
   public static final Pattern PROXY_PATTERN = Pattern.compile("^(?:([\\w\\s]+)=)?((?:0*(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(?:\\.(?!:)|)){4}):(?!0)(\\d{1,4}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])(?i:@(socks[45]))?$", 8);
   private List<Proxy> proxies = new ArrayList();

   public Proxies() {
      super("proxies");
   }

   public static Proxies get() {
      return (Proxies)Systems.get(Proxies.class);
   }

   public boolean add(Proxy proxy) {
      Iterator var2 = this.proxies.iterator();

      Proxy p;
      do {
         if (!var2.hasNext()) {
            if (this.proxies.isEmpty()) {
               proxy.enabled.set(true);
            }

            this.proxies.add(proxy);
            this.save();
            return true;
         }

         p = (Proxy)var2.next();
      } while(!((ProxyType)p.type.get()).equals(proxy.type.get()) || !((String)p.address.get()).equals(proxy.address.get()) || p.port.get() != proxy.port.get());

      return false;
   }

   public void remove(Proxy proxy) {
      if (this.proxies.remove(proxy)) {
         this.save();
      }

   }

   public Proxy getEnabled() {
      Iterator var1 = this.proxies.iterator();

      Proxy proxy;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         proxy = (Proxy)var1.next();
      } while(!(Boolean)proxy.enabled.get());

      return proxy;
   }

   public void setEnabled(Proxy proxy, boolean enabled) {
      Iterator var3 = this.proxies.iterator();

      while(var3.hasNext()) {
         Proxy p = (Proxy)var3.next();
         p.enabled.set(false);
      }

      proxy.enabled.set(enabled);
      this.save();
   }

   public boolean isEmpty() {
      return this.proxies.isEmpty();
   }

   @NotNull
   public Iterator<Proxy> iterator() {
      return this.proxies.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("proxies", NbtUtils.listToTag(this.proxies));
      return tag;
   }

   public Proxies fromTag(class_2487 tag) {
      this.proxies = NbtUtils.listFromTag(tag.method_10554("proxies", 10), Proxy::new);
      return this;
   }
}
