package meteordevelopment.meteorclient.gui.themes.gonbleware.widgets;

import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareWidget;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WGonbleWareAccount extends WAccount implements GonbleWareWidget {
   public WGonbleWareAccount(WidgetScreen screen, Account<?> account) {
      super(screen, account);
   }

   protected Color loggedInColor() {
      return (Color)this.theme().loggedInColor.get();
   }

   protected Color accountTypeColor() {
      return (Color)this.theme().textSecondaryColor.get();
   }
}
