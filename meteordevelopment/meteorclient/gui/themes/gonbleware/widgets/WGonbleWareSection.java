package meteordevelopment.meteorclient.gui.themes.gonbleware.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WGonbleWareSection extends WSection {
   public WGonbleWareSection(String title, boolean expanded, WWidget headerWidget) {
      super(title, expanded, headerWidget);
   }

   protected WSection.WHeader createHeader() {
      return new WGonbleWareSection.WMeteorHeader(this.title);
   }

   protected class WMeteorHeader extends WSection.WHeader {
      private WTriangle triangle;

      public WMeteorHeader(String title) {
         super(title);
      }

      public void init() {
         this.add(this.theme.horizontalSeparator(this.title)).expandX();
         if (WGonbleWareSection.this.headerWidget != null) {
            this.add(WGonbleWareSection.this.headerWidget);
         }

         this.triangle = new WGonbleWareSection.WHeaderTriangle();
         this.triangle.theme = this.theme;
         this.triangle.action = () -> {
            this.onClick();
         };
         this.add(this.triangle);
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         this.triangle.rotation = (1.0D - WGonbleWareSection.this.animProgress) * -90.0D;
      }
   }

   protected static class WHeaderTriangle extends WTriangle implements GonbleWareWidget {
      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         renderer.rotatedQuad(this.x, this.y, this.width, this.height, this.rotation, GuiRenderer.TRIANGLE, (Color)this.theme().textColor.get());
      }
   }
}
