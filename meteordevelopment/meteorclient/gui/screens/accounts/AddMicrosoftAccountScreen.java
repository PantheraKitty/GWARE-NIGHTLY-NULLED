package meteordevelopment.meteorclient.gui.screens.accounts;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.MicrosoftLogin;
import meteordevelopment.meteorclient.systems.accounts.types.MicrosoftAccount;

public class AddMicrosoftAccountScreen extends AddAccountScreen {
   public AddMicrosoftAccountScreen(GuiTheme theme, AccountsScreen parent) {
      super(theme, "Add Microsoft Account", parent);
   }

   public void initWidgets() {
      MicrosoftLogin.getRefreshToken((refreshToken) -> {
         if (refreshToken != null) {
            MicrosoftAccount account = new MicrosoftAccount(refreshToken);
            AccountsScreen.addAccount((AddAccountScreen)null, this.parent, account);
         }

         this.method_25419();
      });
      this.add(this.theme.label("Please select the account to log into in your browser."));
      WButton cancel = (WButton)this.add(this.theme.button("Cancel")).expandX().widget();
      cancel.action = () -> {
         MicrosoftLogin.stopServer();
         this.method_25419();
      };
   }

   public void method_25393() {
   }

   public boolean method_25422() {
      return false;
   }
}
