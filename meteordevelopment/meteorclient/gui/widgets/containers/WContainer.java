package meteordevelopment.meteorclient.gui.widgets.containers;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_312;

public abstract class WContainer extends WWidget {
   public final List<Cell<?>> cells = new ArrayList();

   public <T extends WWidget> Cell<T> add(T widget) {
      widget.parent = this;
      widget.theme = this.theme;
      Cell<T> cell = (new Cell(widget)).centerY();
      this.cells.add(cell);
      widget.init();
      this.invalidate();
      return cell;
   }

   public void clear() {
      if (!this.cells.isEmpty()) {
         this.cells.clear();
         this.invalidate();
      }

   }

   public void remove(Cell<?> cell) {
      if (this.cells.remove(cell)) {
         this.invalidate();
      }

   }

   public void move(double deltaX, double deltaY) {
      super.move(deltaX, deltaY);
      Iterator var5 = this.cells.iterator();

      while(var5.hasNext()) {
         Cell<?> cell = (Cell)var5.next();
         cell.move(deltaX, deltaY);
      }

   }

   public void moveCells(double deltaX, double deltaY) {
      Iterator var5 = this.cells.iterator();

      while(var5.hasNext()) {
         Cell<?> cell = (Cell)var5.next();
         cell.move(deltaX, deltaY);
         class_312 mouse = MeteorClient.mc.field_1729;
         cell.widget().mouseMoved(mouse.method_1603(), mouse.method_1604(), mouse.method_1603(), mouse.method_1604());
      }

   }

   public void calculateSize() {
      Iterator var1 = this.cells.iterator();

      while(var1.hasNext()) {
         Cell<?> cell = (Cell)var1.next();
         cell.widget().calculateSize();
      }

      super.calculateSize();
   }

   protected void onCalculateSize() {
      this.width = 0.0D;
      this.height = 0.0D;

      Cell cell;
      for(Iterator var1 = this.cells.iterator(); var1.hasNext(); this.height = Math.max(this.height, cell.padTop() + cell.widget().height + cell.padBottom())) {
         cell = (Cell)var1.next();
         this.width = Math.max(this.width, cell.padLeft() + cell.widget().width + cell.padRight());
      }

   }

   public void calculateWidgetPositions() {
      super.calculateWidgetPositions();
      Iterator var1 = this.cells.iterator();

      while(var1.hasNext()) {
         Cell<?> cell = (Cell)var1.next();
         cell.widget().calculateWidgetPositions();
      }

   }

   protected void onCalculateWidgetPositions() {
      Iterator var1 = this.cells.iterator();

      while(var1.hasNext()) {
         Cell<?> cell = (Cell)var1.next();
         cell.x = this.x + cell.padLeft();
         cell.y = this.y + cell.padTop();
         cell.width = this.width - cell.padLeft() - cell.padRight();
         cell.height = this.height - cell.padTop() - cell.padBottom();
         cell.alignWidget();
      }

   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (super.render(renderer, mouseX, mouseY, delta)) {
         return true;
      } else {
         Iterator var8 = this.cells.iterator();

         while(var8.hasNext()) {
            Cell<?> cell = (Cell)var8.next();
            double y = cell.widget().y;
            if (y > (double)Utils.getWindowHeight()) {
               break;
            }

            if (y + cell.widget().height > 0.0D) {
               this.renderWidget(cell.widget(), renderer, mouseX, mouseY, delta);
            }
         }

         return false;
      }
   }

   protected void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      widget.render(renderer, mouseX, mouseY, delta);
   }

   protected boolean propagateEvents(WWidget widget) {
      return true;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button, boolean used) {
      try {
         Iterator var7 = this.cells.iterator();

         while(var7.hasNext()) {
            Cell<?> cell = (Cell)var7.next();
            if (this.propagateEvents(cell.widget()) && cell.widget().mouseClicked(mouseX, mouseY, button, used)) {
               used = true;
            }
         }
      } catch (ConcurrentModificationException var9) {
      }

      return super.mouseClicked(mouseX, mouseY, button, used) || used;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      try {
         Iterator var6 = this.cells.iterator();

         while(var6.hasNext()) {
            Cell<?> cell = (Cell)var6.next();
            if (this.propagateEvents(cell.widget()) && cell.widget().mouseReleased(mouseX, mouseY, button)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var8) {
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public void mouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      try {
         Iterator var9 = this.cells.iterator();

         while(var9.hasNext()) {
            Cell<?> cell = (Cell)var9.next();
            if (this.propagateEvents(cell.widget())) {
               cell.widget().mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
            }
         }
      } catch (ConcurrentModificationException var11) {
      }

      super.mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
   }

   public boolean mouseScrolled(double amount) {
      try {
         Iterator var3 = this.cells.iterator();

         while(var3.hasNext()) {
            Cell<?> cell = (Cell)var3.next();
            if (this.propagateEvents(cell.widget()) && cell.widget().mouseScrolled(amount)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var5) {
      }

      return super.mouseScrolled(amount);
   }

   public boolean keyPressed(int key, int modifiers) {
      try {
         Iterator var3 = this.cells.iterator();

         while(var3.hasNext()) {
            Cell<?> cell = (Cell)var3.next();
            if (this.propagateEvents(cell.widget()) && cell.widget().keyPressed(key, modifiers)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var5) {
      }

      return this.onKeyPressed(key, modifiers);
   }

   public boolean keyRepeated(int key, int modifiers) {
      try {
         Iterator var3 = this.cells.iterator();

         while(var3.hasNext()) {
            Cell<?> cell = (Cell)var3.next();
            if (this.propagateEvents(cell.widget()) && cell.widget().keyRepeated(key, modifiers)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var5) {
      }

      return this.onKeyRepeated(key, modifiers);
   }

   public boolean charTyped(char c) {
      try {
         Iterator var2 = this.cells.iterator();

         while(var2.hasNext()) {
            Cell<?> cell = (Cell)var2.next();
            if (this.propagateEvents(cell.widget()) && cell.widget().charTyped(c)) {
               return true;
            }
         }
      } catch (ConcurrentModificationException var4) {
      }

      return super.charTyped(c);
   }
}
