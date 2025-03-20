package meteordevelopment.meteorclient.gui.renderer;

import java.util.Iterator;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.renderer.DrawMode;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.renderer.ShaderMesh;
import meteordevelopment.meteorclient.renderer.Shaders;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_4587;

public class GuiDebugRenderer {
   private static final Color CELL_COLOR = new Color(25, 225, 25);
   private static final Color WIDGET_COLOR = new Color(25, 25, 225);
   private final Mesh mesh;

   public GuiDebugRenderer() {
      this.mesh = new ShaderMesh(Shaders.POS_COLOR, DrawMode.Lines, new Mesh.Attrib[]{Mesh.Attrib.Vec2, Mesh.Attrib.Color});
   }

   public void render(WWidget widget, class_4587 matrices) {
      if (widget != null) {
         this.mesh.begin();
         this.renderWidget(widget);
         this.mesh.end();
         this.mesh.render(matrices);
      }
   }

   private void renderWidget(WWidget widget) {
      this.lineBox(widget.x, widget.y, widget.width, widget.height, WIDGET_COLOR);
      if (widget instanceof WContainer) {
         Iterator var2 = ((WContainer)widget).cells.iterator();

         while(var2.hasNext()) {
            Cell<?> cell = (Cell)var2.next();
            this.lineBox(cell.x, cell.y, cell.width, cell.height, CELL_COLOR);
            this.renderWidget(cell.widget());
         }
      }

   }

   private void lineBox(double x, double y, double width, double height, Color color) {
      this.line(x, y, x + width, y, color);
      this.line(x + width, y, x + width, y + height, color);
      this.line(x, y, x, y + height, color);
      this.line(x, y + height, x + width, y + height, color);
   }

   private void line(double x1, double y1, double x2, double y2, Color color) {
      this.mesh.line(this.mesh.vec2(x1, y1).color(color).next(), this.mesh.vec2(x2, y2).color(color).next());
   }
}
