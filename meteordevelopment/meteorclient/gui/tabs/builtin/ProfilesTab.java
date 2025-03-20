package meteordevelopment.meteorclient.gui.tabs.builtin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.profiles.Profile;
import meteordevelopment.meteorclient.systems.profiles.Profiles;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_437;

public class ProfilesTab extends Tab {
   public ProfilesTab() {
      super("Profiles");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new ProfilesTab.ProfilesScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof ProfilesTab.ProfilesScreen;
   }

   private static class ProfilesScreen extends WindowTabScreen {
      public ProfilesScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
      }

      public void initWidgets() {
         WTable table = (WTable)this.add(this.theme.table()).expandX().minWidth(400.0D).widget();
         this.initTable(table);
         this.add(this.theme.horizontalSeparator()).expandX();
         WButton create = (WButton)this.add(this.theme.button("Create")).expandX().widget();
         create.action = () -> {
            MeteorClient.mc.method_1507(new ProfilesTab.EditProfileScreen(this.theme, (Profile)null, this::reload));
         };
      }

      private void initTable(WTable table) {
         table.clear();
         if (!Profiles.get().isEmpty()) {
            Iterator var2 = Profiles.get().iterator();

            while(var2.hasNext()) {
               Profile profile = (Profile)var2.next();
               table.add(this.theme.label((String)profile.name.get())).expandCellX();
               WButton save = (WButton)table.add(this.theme.button("Save")).widget();
               Objects.requireNonNull(profile);
               save.action = profile::save;
               WButton load = (WButton)table.add(this.theme.button("Load")).widget();
               Objects.requireNonNull(profile);
               load.action = profile::load;
               WButton edit = (WButton)table.add(this.theme.button(GuiRenderer.EDIT)).widget();
               edit.action = () -> {
                  MeteorClient.mc.method_1507(new ProfilesTab.EditProfileScreen(this.theme, profile, this::reload));
               };
               WMinus remove = (WMinus)table.add(this.theme.minus()).widget();
               remove.action = () -> {
                  Profiles.get().remove(profile);
                  this.reload();
               };
               table.row();
            }

         }
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(Profiles.get());
      }

      public boolean fromClipboard() {
         return NbtUtils.fromClipboard((System)Profiles.get());
      }
   }

   private static class EditProfileScreen extends WindowScreen {
      private WContainer settingsContainer;
      private final Profile profile;
      private final boolean isNew;
      private final Runnable action;

      public EditProfileScreen(GuiTheme theme, Profile profile, Runnable action) {
         super(theme, profile == null ? "New Profile" : "Edit Profile");
         this.isNew = profile == null;
         this.profile = this.isNew ? new Profile() : profile;
         this.action = action;
      }

      public void initWidgets() {
         this.settingsContainer = (WContainer)this.add(this.theme.verticalList()).expandX().minWidth(400.0D).widget();
         this.settingsContainer.add(this.theme.settings(this.profile.settings)).expandX();
         this.add(this.theme.horizontalSeparator()).expandX();
         WButton save = (WButton)this.add(this.theme.button(this.isNew ? "Create" : "Save")).expandX().widget();
         save.action = () -> {
            if (!((String)this.profile.name.get()).isEmpty()) {
               if (this.isNew) {
                  Iterator var1 = Profiles.get().iterator();

                  while(var1.hasNext()) {
                     Profile p = (Profile)var1.next();
                     if (this.profile.equals(p)) {
                        return;
                     }
                  }
               }

               List<String> valid = new ArrayList();
               Iterator var5 = ((List)this.profile.loadOnJoin.get()).iterator();

               while(var5.hasNext()) {
                  String address = (String)var5.next();
                  if (Utils.resolveAddress(address)) {
                     valid.add(address);
                  }
               }

               this.profile.loadOnJoin.set(valid);
               if (this.isNew) {
                  Profiles.get().add(this.profile);
               } else {
                  Profiles.get().save();
               }

               this.method_25419();
            }
         };
         this.enterAction = save.action;
      }

      public void method_25393() {
         this.profile.settings.tick(this.settingsContainer, this.theme);
      }

      protected void onClosed() {
         if (this.action != null) {
            this.action.run();
         }

      }
   }
}
