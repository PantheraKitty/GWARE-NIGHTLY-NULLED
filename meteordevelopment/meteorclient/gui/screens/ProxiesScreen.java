package meteordevelopment.meteorclient.gui.screens;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class ProxiesScreen extends WindowScreen {
   private final List<WCheckbox> checkboxes = new ArrayList();

   public ProxiesScreen(GuiTheme theme) {
      super(theme, "Proxies");
   }

   public void initWidgets() {
      WTable table = (WTable)this.add(this.theme.table()).expandX().minWidth(400.0D).widget();
      this.initTable(table);
      this.add(this.theme.horizontalSeparator()).expandX();
      WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      WButton newBtn = (WButton)l.add(this.theme.button("New")).expandX().widget();
      newBtn.action = () -> {
         MeteorClient.mc.method_1507(new ProxiesScreen.EditProxyScreen(this.theme, (Proxy)null, this::reload));
      };
      PointerBuffer filters = BufferUtils.createPointerBuffer(1);
      ByteBuffer txtFilter = MemoryUtil.memASCII("*.txt");
      filters.put(txtFilter);
      filters.rewind();
      WButton importBtn = (WButton)l.add(this.theme.button("Import")).expandX().widget();
      importBtn.action = () -> {
         String selectedFile = TinyFileDialogs.tinyfd_openFileDialog("Import Proxies", (CharSequence)null, filters, (CharSequence)null, false);
         if (selectedFile != null) {
            File file = new File(selectedFile);
            MeteorClient.mc.method_1507(new ProxiesImportScreen(this.theme, file));
         }

      };
   }

   private void initTable(WTable table) {
      table.clear();
      if (!Proxies.get().isEmpty()) {
         Iterator var2 = Proxies.get().iterator();

         while(var2.hasNext()) {
            Proxy proxy = (Proxy)var2.next();
            WCheckbox enabled = (WCheckbox)table.add(this.theme.checkbox((Boolean)proxy.enabled.get())).widget();
            this.checkboxes.add(enabled);
            enabled.action = () -> {
               boolean checked = enabled.checked;
               Proxies.get().setEnabled(proxy, checked);

               WCheckbox checkbox;
               for(Iterator var4 = this.checkboxes.iterator(); var4.hasNext(); checkbox.checked = false) {
                  checkbox = (WCheckbox)var4.next();
               }

               enabled.checked = checked;
            };
            WLabel name = (WLabel)table.add(this.theme.label((String)proxy.name.get())).widget();
            name.color = this.theme.textColor();
            WLabel type = (WLabel)table.add(this.theme.label("(" + String.valueOf(proxy.type.get()) + ")")).widget();
            type.color = this.theme.textSecondaryColor();
            WHorizontalList ipList = (WHorizontalList)table.add(this.theme.horizontalList()).expandCellX().widget();
            ipList.spacing = 0.0D;
            ipList.add(this.theme.label((String)proxy.address.get()));
            ((WLabel)ipList.add(this.theme.label(":")).widget()).color = this.theme.textSecondaryColor();
            ipList.add(this.theme.label(Integer.toString((Integer)proxy.port.get())));
            WButton edit = (WButton)table.add(this.theme.button(GuiRenderer.EDIT)).widget();
            edit.action = () -> {
               MeteorClient.mc.method_1507(new ProxiesScreen.EditProxyScreen(this.theme, proxy, this::reload));
            };
            WMinus remove = (WMinus)table.add(this.theme.minus()).widget();
            remove.action = () -> {
               Proxies.get().remove(proxy);
               this.reload();
            };
            table.row();
         }

      }
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(Proxies.get());
   }

   public boolean fromClipboard() {
      return NbtUtils.fromClipboard((System)Proxies.get());
   }

   protected static class EditProxyScreen extends EditSystemScreen<Proxy> {
      public EditProxyScreen(GuiTheme theme, Proxy value, Runnable reload) {
         super(theme, value, reload);
      }

      public Proxy create() {
         return (new Proxy.Builder()).build();
      }

      public boolean save() {
         return ((Proxy)this.value).resolveAddress() && (!this.isNew || Proxies.get().add((Proxy)this.value));
      }

      public Settings getSettings() {
         return ((Proxy)this.value).settings;
      }
   }
}
