package meteordevelopment.meteorclient.gui.widgets;

public abstract class WHorizontalSeparator extends WWidget {
   protected String text;
   protected double textWidth;

   public WHorizontalSeparator(String text) {
      this.text = text;
   }

   protected void onCalculateSize() {
      if (this.text != null) {
         this.textWidth = this.theme.textWidth(this.text);
      }

      this.width = 1.0D;
      this.height = this.text != null ? this.theme.textHeight() : this.theme.scale(3.0D);
   }
}
