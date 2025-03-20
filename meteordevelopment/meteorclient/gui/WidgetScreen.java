package meteordevelopment.meteorclient.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiDebugRenderer;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WRoot;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.CursorStyle;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_4587;

public abstract class WidgetScreen extends class_437 {
   private static final GuiRenderer RENDERER = new GuiRenderer();
   private static final GuiDebugRenderer DEBUG_RENDERER = new GuiDebugRenderer();
   public Runnable taskAfterRender;
   protected Runnable enterAction;
   public class_437 parent;
   private final WContainer root;
   protected final GuiTheme theme;
   public boolean locked;
   public boolean lockedAllowClose;
   private boolean closed;
   private boolean onClose;
   private boolean debug;
   private double lastMouseX;
   private double lastMouseY;
   public double animProgress;
   private List<Runnable> onClosed;
   protected boolean firstInit = true;

   public WidgetScreen(GuiTheme theme, String title) {
      super(class_2561.method_43470(title));
      this.parent = MeteorClient.mc.field_1755;
      this.root = new WidgetScreen.WFullScreenRoot();
      this.theme = theme;
      this.root.theme = theme;
      if (this.parent != null) {
         this.animProgress = 1.0D;
         if (this instanceof TabScreen && this.parent instanceof TabScreen) {
            this.parent = ((TabScreen)this.parent).parent;
         }
      }

   }

   public <W extends WWidget> Cell<W> add(W widget) {
      return this.root.add(widget);
   }

   public void clear() {
      this.root.clear();
   }

   public void invalidate() {
      this.root.invalidate();
   }

   protected void method_25426() {
      MeteorClient.EVENT_BUS.subscribe((Object)this);
      this.closed = false;
      if (this.firstInit) {
         this.firstInit = false;
         this.initWidgets();
      }

   }

   public abstract void initWidgets();

   public void reload() {
      this.clear();
      this.initWidgets();
   }

   public void onClosed(Runnable action) {
      if (this.onClosed == null) {
         this.onClosed = new ArrayList(2);
      }

      this.onClosed.add(action);
   }

   public boolean method_25402(double mouseX, double mouseY, int button) {
      if (this.locked) {
         return false;
      } else {
         double s = MeteorClient.mc.method_22683().method_4495();
         mouseX *= s;
         mouseY *= s;
         return this.root.mouseClicked(mouseX, mouseY, button, false);
      }
   }

   public boolean method_25406(double mouseX, double mouseY, int button) {
      if (this.locked) {
         return false;
      } else {
         double s = MeteorClient.mc.method_22683().method_4495();
         mouseX *= s;
         mouseY *= s;
         return this.root.mouseReleased(mouseX, mouseY, button);
      }
   }

   public void method_16014(double mouseX, double mouseY) {
      if (!this.locked) {
         double s = MeteorClient.mc.method_22683().method_4495();
         mouseX *= s;
         mouseY *= s;
         this.root.mouseMoved(mouseX, mouseY, this.lastMouseX, this.lastMouseY);
         this.lastMouseX = mouseX;
         this.lastMouseY = mouseY;
      }
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.locked) {
         return false;
      } else {
         this.root.mouseScrolled(verticalAmount);
         return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   public boolean method_16803(int keyCode, int scanCode, int modifiers) {
      if (this.locked) {
         return false;
      } else if ((modifiers == 2 || modifiers == 8) && keyCode == 57) {
         this.debug = !this.debug;
         return true;
      } else if ((keyCode == 257 || keyCode == 335) && this.enterAction != null) {
         this.enterAction.run();
         return true;
      } else {
         return super.method_16803(keyCode, scanCode, modifiers);
      }
   }

   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
      if (this.locked) {
         return false;
      } else {
         boolean shouldReturn = this.root.keyPressed(keyCode, modifiers) || super.method_25404(keyCode, scanCode, modifiers);
         if (shouldReturn) {
            return true;
         } else if (keyCode == 258) {
            AtomicReference<WTextBox> firstTextBox = new AtomicReference((Object)null);
            AtomicBoolean done = new AtomicBoolean(false);
            AtomicBoolean foundFocused = new AtomicBoolean(false);
            this.loopWidgets(this.root, (wWidget) -> {
               if (!done.get() && wWidget instanceof WTextBox) {
                  WTextBox textBox = (WTextBox)wWidget;
                  if (foundFocused.get()) {
                     textBox.setFocused(true);
                     textBox.setCursorMax();
                     done.set(true);
                  } else if (textBox.isFocused()) {
                     textBox.setFocused(false);
                     foundFocused.set(true);
                  }

                  if (firstTextBox.get() == null) {
                     firstTextBox.set(textBox);
                  }

               }
            });
            if (!done.get() && firstTextBox.get() != null) {
               ((WTextBox)firstTextBox.get()).setFocused(true);
               ((WTextBox)firstTextBox.get()).setCursorMax();
            }

            return true;
         } else {
            boolean control = class_310.field_1703 ? modifiers == 8 : modifiers == 2;
            if (control && keyCode == 67 && this.toClipboard()) {
               return true;
            } else if (control && keyCode == 86 && this.fromClipboard()) {
               this.reload();
               class_437 var7 = this.parent;
               if (var7 instanceof WidgetScreen) {
                  WidgetScreen wScreen = (WidgetScreen)var7;
                  wScreen.reload();
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void keyRepeated(int key, int modifiers) {
      if (!this.locked) {
         this.root.keyRepeated(key, modifiers);
      }
   }

   public boolean method_25400(char chr, int keyCode) {
      return this.locked ? false : this.root.charTyped(chr);
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      if (!Utils.canUpdate()) {
         this.method_25420(context, mouseX, mouseY, delta);
      }

      double s = MeteorClient.mc.method_22683().method_4495();
      mouseX = (int)((double)mouseX * s);
      mouseY = (int)((double)mouseY * s);
      this.animProgress += (double)(delta / 20.0F * 14.0F);
      this.animProgress = class_3532.method_15350(this.animProgress, 0.0D, 1.0D);
      GuiKeyEvents.canUseKeys = true;
      Utils.unscaledProjection();
      this.onRenderBefore(context, delta);
      RENDERER.theme = this.theme;
      this.theme.beforeRender();
      RENDERER.begin(context);
      RENDERER.setAlpha(this.animProgress);
      this.root.render(RENDERER, (double)mouseX, (double)mouseY, (double)(delta / 20.0F));
      RENDERER.setAlpha(1.0D);
      RENDERER.end();
      boolean tooltip = RENDERER.renderTooltip(context, (double)mouseX, (double)mouseY, (double)(delta / 20.0F));
      if (this.debug) {
         class_4587 matrices = context.method_51448();
         DEBUG_RENDERER.render(this.root, matrices);
         if (tooltip) {
            DEBUG_RENDERER.render(RENDERER.tooltipWidget, matrices);
         }
      }

      Utils.scaledProjection();
      this.runAfterRenderTasks();
   }

   protected void runAfterRenderTasks() {
      if (this.taskAfterRender != null) {
         this.taskAfterRender.run();
         this.taskAfterRender = null;
      }

   }

   protected void onRenderBefore(class_332 drawContext, float delta) {
   }

   public void method_25410(class_310 client, int width, int height) {
      super.method_25410(client, width, height);
      this.root.invalidate();
   }

   public void method_25419() {
      if (!this.locked || this.lockedAllowClose) {
         boolean preOnClose = this.onClose;
         this.onClose = true;
         this.method_25432();
         this.onClose = preOnClose;
      }

   }

   public void method_25432() {
      if (!this.closed || this.lockedAllowClose) {
         this.closed = true;
         this.onClosed();
         Input.setCursorStyle(CursorStyle.Default);
         this.loopWidgets(this.root, (widget) -> {
            if (widget instanceof WTextBox) {
               WTextBox textBox = (WTextBox)widget;
               if (textBox.isFocused()) {
                  textBox.setFocused(false);
               }
            }

         });
         MeteorClient.EVENT_BUS.unsubscribe((Object)this);
         GuiKeyEvents.canUseKeys = true;
         if (this.onClosed != null) {
            Iterator var1 = this.onClosed.iterator();

            while(var1.hasNext()) {
               Runnable action = (Runnable)var1.next();
               action.run();
            }
         }

         if (this.onClose) {
            this.taskAfterRender = () -> {
               this.locked = true;
               MeteorClient.mc.method_1507(this.parent);
            };
         }
      }

   }

   private void loopWidgets(WWidget widget, Consumer<WWidget> action) {
      action.accept(widget);
      if (widget instanceof WContainer) {
         Iterator var3 = ((WContainer)widget).cells.iterator();

         while(var3.hasNext()) {
            Cell<?> cell = (Cell)var3.next();
            this.loopWidgets(cell.widget(), action);
         }
      }

   }

   protected void onClosed() {
   }

   public boolean toClipboard() {
      return false;
   }

   public boolean fromClipboard() {
      return false;
   }

   public boolean method_25422() {
      return !this.locked || this.lockedAllowClose;
   }

   public boolean method_25421() {
      return false;
   }

   private static class WFullScreenRoot extends WContainer implements WRoot {
      private boolean valid;

      public void invalidate() {
         this.valid = false;
      }

      protected void onCalculateSize() {
         this.width = (double)Utils.getWindowWidth();
         this.height = (double)Utils.getWindowHeight();
      }

      protected void onCalculateWidgetPositions() {
         Iterator var1 = this.cells.iterator();

         while(var1.hasNext()) {
            Cell<?> cell = (Cell)var1.next();
            cell.x = 0.0D;
            cell.y = 0.0D;
            cell.width = this.width;
            cell.height = this.height;
            cell.alignWidget();
         }

      }

      public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         if (!this.valid) {
            this.calculateSize();
            this.calculateWidgetPositions();
            this.valid = true;
            this.mouseMoved(MeteorClient.mc.field_1729.method_1603(), MeteorClient.mc.field_1729.method_1604(), MeteorClient.mc.field_1729.method_1603(), MeteorClient.mc.field_1729.method_1604());
         }

         return super.render(renderer, mouseX, mouseY, delta);
      }
   }
}
