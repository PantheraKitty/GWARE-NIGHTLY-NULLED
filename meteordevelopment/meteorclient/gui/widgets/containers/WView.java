package meteordevelopment.meteorclient.gui.widgets.containers;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_3532;

public abstract class WView extends WVerticalList {
   public double maxHeight = Double.MAX_VALUE;
   public boolean scrollOnlyWhenMouseOver = true;
   public boolean hasScrollBar = true;
   protected boolean canScroll;
   private double actualHeight;
   private double scroll;
   private double targetScroll;
   private boolean moveAfterPositionWidgets;
   protected boolean handleMouseOver;
   protected boolean handlePressed;

   public void init() {
      this.maxHeight = (double)Utils.getWindowHeight() - this.theme.scale(128.0D);
   }

   protected void onCalculateSize() {
      boolean couldScroll = this.canScroll;
      this.canScroll = false;
      this.widthRemove = 0.0D;
      super.onCalculateSize();
      if (this.height > this.maxHeight) {
         this.actualHeight = this.height;
         this.height = this.maxHeight;
         this.canScroll = true;
         if (this.hasScrollBar) {
            this.widthRemove = this.handleWidth() * 2.0D;
            this.width += this.widthRemove;
         }

         if (couldScroll) {
            this.moveAfterPositionWidgets = true;
         }
      } else {
         this.actualHeight = this.height;
         this.scroll = 0.0D;
         this.targetScroll = 0.0D;
      }

   }

   protected void onCalculateWidgetPositions() {
      super.onCalculateWidgetPositions();
      if (this.moveAfterPositionWidgets) {
         this.scroll = class_3532.method_15350(this.scroll, 0.0D, this.actualHeight - this.height);
         this.targetScroll = this.scroll;
         this.moveCells(0.0D, -this.scroll);
         this.moveAfterPositionWidgets = false;
      }

   }

   public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
      if (this.handleMouseOver && button == 0 && !used) {
         this.handlePressed = true;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseReleased(double mouseX, double mouseY, int button) {
      if (this.handlePressed) {
         this.handlePressed = false;
      }

      return false;
   }

   public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      this.handleMouseOver = false;
      double preScroll;
      double mouseDelta;
      if (this.canScroll && this.hasScrollBar) {
         preScroll = this.handleX();
         mouseDelta = this.handleY();
         if (mouseX >= preScroll && mouseX <= preScroll + this.handleWidth() && mouseY >= mouseDelta && mouseY <= mouseDelta + this.handleHeight()) {
            this.handleMouseOver = true;
         }
      }

      if (this.handlePressed) {
         preScroll = this.scroll;
         mouseDelta = mouseY - lastMouseY;
         this.scroll += (double)Math.round(mouseDelta * ((this.actualHeight - this.handleHeight() / 2.0D) / this.height));
         this.scroll = class_3532.method_15350(this.scroll, 0.0D, this.actualHeight - this.height);
         this.targetScroll = this.scroll;
         double delta = this.scroll - preScroll;
         if (delta != 0.0D) {
            this.moveCells(0.0D, -delta);
         }
      }

   }

   public boolean onMouseScrolled(double amount) {
      if (this.scrollOnlyWhenMouseOver && !this.mouseOver) {
         return false;
      } else {
         this.targetScroll -= (double)Math.round(this.theme.scale(amount * 40.0D));
         this.targetScroll = class_3532.method_15350(this.targetScroll, 0.0D, this.actualHeight - this.height);
         return true;
      }
   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      this.updateScroll(delta);
      if (this.canScroll) {
         renderer.scissorStart(this.x, this.y, this.width, this.height);
      }

      boolean render = super.render(renderer, mouseX, mouseY, delta);
      if (this.canScroll) {
         renderer.scissorEnd();
      }

      return render;
   }

   private void updateScroll(double delta) {
      double preScroll = this.scroll;
      double max = this.actualHeight - this.height;
      if (Math.abs(this.targetScroll - this.scroll) < 1.0D) {
         this.scroll = this.targetScroll;
      } else if (this.targetScroll > this.scroll) {
         this.scroll += (double)Math.round(this.theme.scale(delta * 300.0D + delta * 100.0D * (Math.abs(this.targetScroll - this.scroll) / 10.0D)));
         if (this.scroll > this.targetScroll) {
            this.scroll = this.targetScroll;
         }
      } else if (this.targetScroll < this.scroll) {
         this.scroll -= (double)Math.round(this.theme.scale(delta * 300.0D + delta * 100.0D * (Math.abs(this.targetScroll - this.scroll) / 10.0D)));
         if (this.scroll < this.targetScroll) {
            this.scroll = this.targetScroll;
         }
      }

      this.scroll = class_3532.method_15350(this.scroll, 0.0D, max);
      double change = this.scroll - preScroll;
      if (change != 0.0D) {
         this.moveCells(0.0D, -change);
      }

   }

   protected boolean propagateEvents(WWidget widget) {
      return widget.y >= this.y && widget.y <= this.y + this.height || widget.y + widget.height >= this.y && widget.y + widget.height <= this.y + this.height || this.y >= widget.y && this.y <= widget.y + widget.height || this.y + this.height >= widget.y && this.y + this.height <= widget.y + widget.height;
   }

   protected double handleWidth() {
      return this.theme.scale(6.0D);
   }

   protected double handleHeight() {
      return this.height / this.actualHeight * this.height;
   }

   protected double handleX() {
      return this.x + this.width - this.handleWidth();
   }

   protected double handleY() {
      return this.y + (this.height - this.handleHeight()) * (this.scroll / (this.actualHeight - this.height));
   }
}
