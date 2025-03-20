package meteordevelopment.meteorclient.gui.screens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.regex.Matcher;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;
import meteordevelopment.meteorclient.systems.proxies.ProxyType;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_437;

public class ProxiesImportScreen extends WindowScreen {
   private final File file;

   public ProxiesImportScreen(GuiTheme theme, File file) {
      super(theme, "Import Proxies");
      this.file = file;
      this.onClosed(() -> {
         class_437 patt0$temp = this.parent;
         if (patt0$temp instanceof ProxiesScreen) {
            ProxiesScreen screen = (ProxiesScreen)patt0$temp;
            screen.reload();
         }

      });
   }

   public void initWidgets() {
      if (this.file.exists() && this.file.isFile()) {
         this.add(this.theme.label("Importing proxies from " + this.file.getName() + "...").color(Color.GREEN));
         WVerticalList list = (WVerticalList)((WSection)this.add(this.theme.section("Log", false)).widget()).add(this.theme.verticalList()).expandX().widget();
         Proxies proxies = Proxies.get();

         try {
            int pog = 0;
            int bruh = 0;
            Iterator var5 = Files.readAllLines(this.file.toPath()).iterator();

            while(var5.hasNext()) {
               String line = (String)var5.next();
               Matcher matcher = Proxies.PROXY_PATTERN.matcher(line);
               if (matcher.matches()) {
                  String address = matcher.group(2).replaceAll("\\b0+\\B", "");
                  int port = Integer.parseInt(matcher.group(3));
                  Proxy proxy = (new Proxy.Builder()).address(address).port(port).name(matcher.group(1) != null ? matcher.group(1) : address + ":" + port).type(matcher.group(4) != null ? ProxyType.parse(matcher.group(4)) : ProxyType.Socks4).build();
                  if (proxies.add(proxy)) {
                     list.add(this.theme.label("Imported proxy: " + (String)proxy.name.get()).color(Color.GREEN));
                     ++pog;
                  } else {
                     list.add(this.theme.label("Proxy already exists: " + (String)proxy.name.get()).color(Color.ORANGE));
                     ++bruh;
                  }
               } else {
                  list.add(this.theme.label("Invalid proxy: " + line).color(Color.RED));
                  ++bruh;
               }
            }

            this.add(this.theme.label("Successfully imported " + pog + "/" + (bruh + pog) + " proxies.").color(Utils.lerp(Color.RED, Color.GREEN, (float)pog / (float)(pog + bruh))));
         } catch (IOException var11) {
            var11.printStackTrace();
         }
      } else {
         this.add(this.theme.label("Invalid File!"));
      }

      this.add(this.theme.horizontalSeparator()).expandX();
      WButton btnBack = (WButton)this.add(this.theme.button("Back")).expandX().widget();
      btnBack.action = this::method_25419;
   }
}
