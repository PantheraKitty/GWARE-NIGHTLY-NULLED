package meteordevelopment.meteorclient.gui.screens.accounts;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

public abstract class AddAccountScreen extends WindowScreen {
   public final AccountsScreen parent;
   public WButton add;
   private int timer;

   protected AddAccountScreen(GuiTheme theme, String title, AccountsScreen parent) {
      super(theme, title);
      this.parent = parent;
   }

   public void method_25393() {
      if (this.locked) {
         if (this.timer > 2) {
            this.add.set(this.getNext(this.add));
            this.timer = 0;
         } else {
            ++this.timer;
         }
      } else if (!this.add.getText().equals("Add")) {
         this.add.set("Add");
      }

   }

   private String getNext(WButton add) {
      String var2 = add.getText();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case 49680:
         if (var2.equals("0oo")) {
            var3 = 3;
         }
         break;
      case 65665:
         if (var2.equals("Add")) {
            var3 = 0;
         }
         break;
      case 108270:
         if (var2.equals("o0o")) {
            var3 = 4;
         }
         break;
      case 110160:
         if (var2.equals("oo0")) {
            var3 = 1;
         }
         break;
      case 110223:
         if (var2.equals("ooo")) {
            var3 = 2;
         }
      }

      String var10000;
      switch(var3) {
      case 0:
      case 1:
         var10000 = "ooo";
         break;
      case 2:
         var10000 = "0oo";
         break;
      case 3:
         var10000 = "o0o";
         break;
      case 4:
         var10000 = "oo0";
         break;
      default:
         var10000 = "Add";
      }

      return var10000;
   }
}
