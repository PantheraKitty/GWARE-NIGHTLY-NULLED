package meteordevelopment.meteorclient.gui.widgets;

public class WVerticalSeparator extends WWidget {
   protected void onCalculateSize() {
      this.width = this.theme.scale(3.0D);
      this.height = 1.0D;
   }
}
