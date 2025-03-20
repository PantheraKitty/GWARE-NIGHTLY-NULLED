package meteordevelopment.meteorclient.gui.widgets;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.screens.accounts.AccountInfoScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import meteordevelopment.meteorclient.systems.accounts.TokenAccount;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class WAccount extends WHorizontalList {
   public Runnable refreshScreenAction;
   private final WidgetScreen screen;
   private final Account<?> account;

   public WAccount(WidgetScreen screen, Account<?> account) {
      this.screen = screen;
      this.account = account;
   }

   protected abstract Color loggedInColor();

   protected abstract Color accountTypeColor();

   public void init() {
      this.add(this.theme.texture(32.0D, 32.0D, this.account.getCache().getHeadTexture().needsRotate() ? 90.0D : 0.0D, this.account.getCache().getHeadTexture()));
      WLabel name = (WLabel)this.add(this.theme.label(this.account.getUsername())).widget();
      if (MeteorClient.mc.method_1548().method_1676().equalsIgnoreCase(this.account.getUsername())) {
         name.color = this.loggedInColor();
      }

      WLabel label = (WLabel)this.add(this.theme.label("(" + String.valueOf(this.account.getType()) + ")")).expandCellX().right().widget();
      label.color = this.accountTypeColor();
      WButton login;
      if (this.account instanceof TokenAccount) {
         login = (WButton)this.add(this.theme.button("Info")).widget();
         login.action = () -> {
            MeteorClient.mc.method_1507(new AccountInfoScreen(this.theme, this.account));
         };
      }

      login = (WButton)this.add(this.theme.button("Login")).widget();
      login.action = () -> {
         login.minWidth = login.width;
         login.set("...");
         this.screen.locked = true;
         MeteorExecutor.execute(() -> {
            if (this.account.fetchInfo() && this.account.login()) {
               name.set(this.account.getUsername());
               Accounts.get().save();
               this.screen.taskAfterRender = this.refreshScreenAction;
            }

            login.minWidth = 0.0D;
            login.set("Login");
            this.screen.locked = false;
         });
      };
      WMinus remove = (WMinus)this.add(this.theme.minus()).widget();
      remove.action = () -> {
         Accounts.get().remove(this.account);
         if (this.refreshScreenAction != null) {
            this.refreshScreenAction.run();
         }

      };
   }
}
