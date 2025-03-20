package meteordevelopment.meteorclient.gui.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WRoot;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import net.minecraft.class_3532;

public abstract class WDropdown<T> extends WPressable {
   public Runnable action;
   protected T[] values;
   protected T value;
   protected double maxValueWidth;
   protected WDropdown.WDropdownRoot root;
   protected boolean expanded;
   protected double animProgress;

   public WDropdown(T[] values, T value) {
      this.values = values;
      this.set(value);
   }

   public void init() {
      this.root = this.createRootWidget();
      this.root.theme = this.theme;
      this.root.spacing = 0.0D;

      for(int i = 0; i < this.values.length; ++i) {
         WDropdown<T>.WDropdownValue widget = this.createValueWidget();
         widget.theme = this.theme;
         widget.value = this.values[i];
         Cell<?> cell = this.root.add(widget).padHorizontal(2.0D).expandWidgetX();
         if (i >= this.values.length - 1) {
            cell.padBottom(2.0D);
         }
      }

   }

   protected abstract WDropdown.WDropdownRoot createRootWidget();

   protected abstract WDropdown<T>.WDropdownValue createValueWidget();

   protected void onCalculateSize() {
      double pad = this.pad();
      this.maxValueWidth = 0.0D;
      Object[] var3 = this.values;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         T value = var3[var5];
         double valueWidth = this.theme.textWidth(value.toString());
         this.maxValueWidth = Math.max(this.maxValueWidth, valueWidth);
      }

      this.root.calculateSize();
      this.width = pad + this.maxValueWidth + pad + this.theme.textHeight() + pad;
      this.height = pad + this.theme.textHeight() + pad;
      this.root.width = this.width;
   }

   protected void onCalculateWidgetPositions() {
      super.onCalculateWidgetPositions();
      this.root.x = this.x;
      this.root.y = this.y + this.height;
      this.root.calculateWidgetPositions();
   }

   protected void onPressed(int button) {
      this.expanded = !this.expanded;
   }

   public T get() {
      return this.value;
   }

   public void set(T value) {
      this.value = value;
   }

   public void move(double deltaX, double deltaY) {
      super.move(deltaX, deltaY);
      this.root.move(deltaX, deltaY);
   }

   public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      boolean render = super.render(renderer, mouseX, mouseY, delta);
      this.animProgress += (double)(this.expanded ? 1 : -1) * delta * 14.0D;
      this.animProgress = class_3532.method_15350(this.animProgress, 0.0D, 1.0D);
      if (!render && this.animProgress > 0.0D) {
         renderer.absolutePost(() -> {
            renderer.scissorStart(this.x, this.y + this.height, this.width, this.root.height * this.animProgress);
            this.root.render(renderer, mouseX, mouseY, delta);
            renderer.scissorEnd();
         });
      }

      if (this.expanded && this.root.mouseOver) {
         this.theme.disableHoverColor = true;
      }

      return render;
   }

   public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
      if (!this.mouseOver && !this.root.mouseOver) {
         this.expanded = false;
      }

      if (super.onMouseClicked(mouseX, mouseY, button, used)) {
         used = true;
      }

      if (this.expanded && this.root.mouseClicked(mouseX, mouseY, button, used)) {
         used = true;
      }

      return used;
   }

   public boolean onMouseReleased(double mouseX, double mouseY, int button) {
      if (super.onMouseReleased(mouseX, mouseY, button)) {
         return true;
      } else {
         return this.expanded && this.root.mouseReleased(mouseX, mouseY, button);
      }
   }

   public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
      super.onMouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
      if (this.expanded) {
         this.root.mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);
      }

   }

   public boolean onMouseScrolled(double amount) {
      if (super.onMouseScrolled(amount)) {
         return true;
      } else {
         return this.expanded ? this.root.mouseScrolled(amount) : false;
      }
   }

   public boolean onKeyPressed(int key, int mods) {
      if (super.onKeyPressed(key, mods)) {
         return true;
      } else {
         return this.expanded && this.root.keyPressed(key, mods);
      }
   }

   public boolean onKeyRepeated(int key, int mods) {
      if (super.onKeyRepeated(key, mods)) {
         return true;
      } else {
         return this.expanded && this.root.keyRepeated(key, mods);
      }
   }

   public boolean onCharTyped(char c) {
      if (super.onCharTyped(c)) {
         return true;
      } else {
         return this.expanded && this.root.charTyped(c);
      }
   }

   protected abstract static class WDropdownRoot extends WVerticalList implements WRoot {
      public void invalidate() {
      }
   }

   protected abstract class WDropdownValue extends WPressable {
      protected T value;

      protected void onPressed(int button) {
         boolean isNew = !WDropdown.this.value.equals(this.value);
         WDropdown.this.value = this.value;
         WDropdown.this.expanded = false;
         if (isNew && WDropdown.this.action != null) {
            WDropdown.this.action.run();
         }

      }
   }
}
