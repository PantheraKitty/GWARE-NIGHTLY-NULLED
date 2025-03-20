package meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable;

import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WGonbleWareFavorite extends WFavorite implements GonbleWareWidget {
   public WGonbleWareFavorite(boolean checked) {
      super(checked);
   }

   protected Color getColor() {
      return (Color)this.theme().favoriteColor.get();
   }
}
