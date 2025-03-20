package meteordevelopment.meteorclient.gui.widgets.containers;

import java.util.Iterator;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.WindowConfig;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_3532;

public abstract class WWindow extends WVerticalList {
   public double padding = 8.0D;
   public Consumer<WContainer> beforeHeaderInit;
   public String id;
   public final WWidget icon;
   protected final String title;
   protected WWindow.WHeader header;
   public WView view;
   protected boolean dragging;
   protected boolean expanded = true;
   protected boolean dragged;
   protected double animProgress = 1.0D;
   protected boolean moved = false;
   protected double movedX;
   protected double movedY;
   private boolean propagateEventsExpanded;

   public WWindow(WWidget icon, String title) {
      this.icon = icon;
      this.title = title;
   }

   public void init() {
      this.header = this.header(this.icon);
      this.header.theme = this.theme;
      super.add(this.header).expandWidgetX().widget();
      this.view = (WView)super.add(this.theme.view()).expandX().pad(this.padding).widget();
      if (this.id != null) {
         this.expanded = this.theme.getWindowConfig(this.id).expanded;
         this.animProgress = this.expanded ? 1.0D : 0.0D;
      }

   }

   protected abstract WWindow.WHeader header(WWidget var1);

   public <T extends WWidget> Cell<T> add(T widget) {
      return this.view.add(widget);
   }

   public void clear() {
      this.view.clear();
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
      if (this.id != null) {
         WindowConfig config = this.theme.getWindowConfig(this.id);
         config.expanded = expanded;
      }

   }

   protected void onCalculateWidgetPositions() {
      if (this.id != null) {
         WindowConfig config = this.theme.getWindowConfig(this.id);
         if (config.x != -1.0D) {
            this.x = config.x;
            if (this.x + this.width > (double)Utils.getWindowWidth()) {
               this.x = (double)Utils.getWindowWidth() - this.width;
            }
         }

         if (config.y != -1.0D) {
            this.y = config.y;
            if (this.y + this.height > (double)Utils.getWindowHeight()) {
               this.y = (double)Utils.getWindowHeight() - this.height;
            }
         }
      }

      super.onCalculateWidgetPositions();
      if (this.moved) {
         this.move(this.movedX - this.x, this.movedY - this.y);
      }

   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (!this.visible) {
         return true;
      } else {
         boolean scissor = this.animProgress != 0.0D && this.animProgress != 1.0D || this.expanded && this.animProgress != 1.0D;
         if (scissor) {
            renderer.scissorStart(this.x, this.y, this.width, (this.height - this.header.height) * this.animProgress + this.header.height);
         }

         boolean toReturn = super.render(renderer, mouseX, mouseY, delta);
         if (scissor) {
            renderer.scissorEnd();
         }

         return toReturn;
      }
   }

   protected void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.expanded || this.animProgress > 0.0D || widget instanceof WWindow.WHeader) {
         widget.render(renderer, mouseX, mouseY, delta);
      }

      this.propagateEventsExpanded = this.expanded;
   }

   protected boolean propagateEvents(WWidget widget) {
      return widget instanceof WWindow.WHeader || this.propagateEventsExpanded;
   }

   protected abstract class WHeader extends WContainer {
      protected final WWidget icon;
      protected WTriangle triangle;
      protected WHorizontalList list;

      public WHeader(WWidget icon) {
         this.icon = icon;
      }

      public void init() {
         if (this.icon != null) {
            this.createList();
            this.add(this.icon).centerY();
         }

         if (WWindow.this.beforeHeaderInit != null) {
            this.createList();
            WWindow.this.beforeHeaderInit.accept(this);
         }

         this.add(this.theme.label(WWindow.this.title, true)).expandCellX().center().pad(4.0D);
         this.triangle = (WTriangle)this.add(this.theme.triangle()).pad(4.0D).right().centerY().widget();
         this.triangle.action = () -> {
            WWindow.this.setExpanded(!WWindow.this.expanded);
         };
      }

      protected void createList() {
         this.list = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         this.list.spacing = 0.0D;
      }

      public <T extends WWidget> Cell<T> add(T widget) {
         return this.list != null ? this.list.add(widget) : super.add(widget);
      }

      protected void onCalculateSize() {
         this.width = 0.0D;
         this.height = 0.0D;

         Cell cell;
         for(Iterator var1 = this.cells.iterator(); var1.hasNext(); this.height = Math.max(this.height, cell.padTop() + cell.widget().height + cell.padBottom())) {
            cell = (Cell)var1.next();
            double w = cell.padLeft() + cell.widget().width + cell.padRight();
            if (cell.widget() instanceof WTriangle) {
               w *= 2.0D;
            }

            this.width += w;
         }

      }

      public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
         if (this.mouseOver && !used) {
            if (button == 1) {
               WWindow.this.setExpanded(!WWindow.this.expanded);
            } else {
               WWindow.this.dragging = true;
               WWindow.this.dragged = false;
            }

            return true;
         } else {
            return false;
         }
      }

      public boolean onMouseReleased(double mouseX, double mouseY, int button) {
         if (WWindow.this.dragging) {
            WWindow.this.dragging = false;
            if (!WWindow.this.dragged) {
               WWindow.this.setExpanded(!WWindow.this.expanded);
            }
         }

         return false;
      }

      public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
         if (WWindow.this.dragging) {
            WWindow.this.move(mouseX - lastMouseX, mouseY - lastMouseY);
            WWindow.this.moved = true;
            WWindow.this.movedX = this.x;
            WWindow.this.movedY = this.y;
            if (WWindow.this.id != null) {
               WindowConfig config = this.theme.getWindowConfig(WWindow.this.id);
               config.x = this.x;
               config.y = this.y;
            }

            WWindow.this.dragged = true;
         }

      }

      public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         WWindow var10000 = WWindow.this;
         var10000.animProgress += (double)(WWindow.this.expanded ? 1 : -1) * delta * 14.0D;
         WWindow.this.animProgress = class_3532.method_15350(WWindow.this.animProgress, 0.0D, 1.0D);
         this.triangle.rotation = (1.0D - WWindow.this.animProgress) * -90.0D;
         return super.render(renderer, mouseX, mouseY, delta);
      }
   }
}
