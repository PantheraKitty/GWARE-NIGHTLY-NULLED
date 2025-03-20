package meteordevelopment.meteorclient.gui.screens.accounts;

import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import org.jetbrains.annotations.Nullable;

public class AccountsScreen extends WindowScreen {
   public AccountsScreen(GuiTheme theme) {
      super(theme, "Accounts");
   }

   public void initWidgets() {
      WAccount wAccount;
      for(Iterator var1 = Accounts.get().iterator(); var1.hasNext(); wAccount.refreshScreenAction = this::reload) {
         Account<?> account = (Account)var1.next();
         wAccount = (WAccount)this.add(this.theme.account(this, account)).expandX().widget();
      }

      WHorizontalList l = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
      this.addButton(l, "Cracked", () -> {
         MeteorClient.mc.method_1507(new AddCrackedAccountScreen(this.theme, this));
      });
      this.addButton(l, "Altening", () -> {
         MeteorClient.mc.method_1507(new AddAlteningAccountScreen(this.theme, this));
      });
      this.addButton(l, "Microsoft", () -> {
         MeteorClient.mc.method_1507(new AddMicrosoftAccountScreen(this.theme, this));
      });
   }

   private void addButton(WContainer c, String text, Runnable action) {
      WButton button = (WButton)c.add(this.theme.button(text)).expandX().widget();
      button.action = action;
   }

   public static void addAccount(@Nullable AddAccountScreen screen, AccountsScreen parent, Account<?> account) {
      if (screen != null) {
         screen.locked = true;
      }

      MeteorExecutor.execute(() -> {
         if (account.fetchInfo()) {
            account.getCache().loadHead();
            Accounts.get().add(account);
            if (account.login()) {
               Accounts.get().save();
            }

            if (screen != null) {
               screen.locked = false;
               screen.method_25419();
            }

            parent.reload();
         } else {
            if (screen != null) {
               screen.locked = false;
            }

         }
      });
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(Accounts.get());
   }

   public boolean fromClipboard() {
      return NbtUtils.fromClipboard((System)Accounts.get());
   }
}
