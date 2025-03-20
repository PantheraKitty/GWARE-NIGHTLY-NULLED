package meteordevelopment.meteorclient.systems.hud.screens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.tabs.builtin.HudTab;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.other.Snapper;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_4587;

public class HudEditorScreen extends WidgetScreen implements Snapper.Container {
   private static final Color SPLIT_LINES_COLOR = new Color(255, 255, 255, 75);
   private static final Color INACTIVE_BG_COLOR = new Color(200, 25, 25, 50);
   private static final Color INACTIVE_OL_COLOR = new Color(200, 25, 25, 200);
   private static final Color HOVER_BG_COLOR = new Color(200, 200, 200, 50);
   private static final Color HOVER_OL_COLOR = new Color(200, 200, 200, 200);
   private static final Color SELECTION_BG_COLOR = new Color(225, 225, 225, 25);
   private static final Color SELECTION_OL_COLOR = new Color(225, 225, 225, 100);
   private final Hud hud = Hud.get();
   private final Snapper snapper = new Snapper(this);
   private Snapper.Element selectionSnapBox;
   private int lastMouseX;
   private int lastMouseY;
   private boolean pressed;
   private int clickX;
   private int clickY;
   private final List<HudElement> selection = new ArrayList();
   private boolean moved;
   private boolean dragging;
   private HudElement addedHoveredToSelectionWhenClickedElement;
   private double splitLinesAnimation;

   public HudEditorScreen(GuiTheme theme) {
      super(theme, "Hud Editor");
   }

   public void initWidgets() {
   }

   public boolean method_25402(double mouseX, double mouseY, int button) {
      double s = MeteorClient.mc.method_22683().method_4495();
      mouseX *= s;
      mouseY *= s;
      if (button == 0) {
         this.pressed = true;
         this.selectionSnapBox = null;
         HudElement hovered = this.getHovered((int)mouseX, (int)mouseY);
         this.dragging = hovered != null;
         if (this.dragging) {
            if (!this.selection.contains(hovered)) {
               this.selection.clear();
               this.selection.add(hovered);
               this.addedHoveredToSelectionWhenClickedElement = hovered;
            }
         } else {
            this.selection.clear();
         }

         this.clickX = (int)mouseX;
         this.clickY = (int)mouseY;
      }

      return false;
   }

   public void method_16014(double mouseX, double mouseY) {
      double s = MeteorClient.mc.method_22683().method_4495();
      mouseX *= s;
      mouseY *= s;
      if (this.dragging && !this.selection.isEmpty()) {
         if (this.selectionSnapBox == null) {
            this.selectionSnapBox = new HudEditorScreen.SelectionBox();
         }

         this.snapper.move(this.selectionSnapBox, (int)mouseX - this.lastMouseX, (int)mouseY - this.lastMouseY);
      }

      if (this.pressed) {
         this.moved = true;
      }

      this.lastMouseX = (int)mouseX;
      this.lastMouseY = (int)mouseY;
   }

   public boolean method_25406(double mouseX, double mouseY, int button) {
      double s = MeteorClient.mc.method_22683().method_4495();
      mouseX *= s;
      mouseY *= s;
      if (button == 0) {
         this.pressed = false;
      }

      if (this.addedHoveredToSelectionWhenClickedElement != null) {
         this.selection.remove(this.addedHoveredToSelectionWhenClickedElement);
         this.addedHoveredToSelectionWhenClickedElement = null;
      }

      if (this.moved) {
         if (button == 0 && !this.dragging) {
            this.fillSelection((int)mouseX, (int)mouseY);
         }
      } else {
         HudElement hovered;
         if (button == 0) {
            hovered = this.getHovered((int)mouseX, (int)mouseY);
            if (hovered != null) {
               hovered.toggle();
            }
         } else if (button == 1) {
            hovered = this.getHovered((int)mouseX, (int)mouseY);
            if (hovered != null) {
               MeteorClient.mc.method_1507(new HudElementScreen(this.theme, hovered));
            } else {
               MeteorClient.mc.method_1507(new AddHudElementScreen(this.theme, this.lastMouseX, this.lastMouseY));
            }
         }
      }

      if (button == 0) {
         this.snapper.unsnap();
         this.moved = this.dragging = false;
      }

      return false;
   }

   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
      if (!this.pressed) {
         HudElement hovered;
         if (keyCode != 257 && keyCode != 335) {
            if (keyCode == 261) {
               hovered = this.getHovered(this.lastMouseX, this.lastMouseY);
               if (hovered != null) {
                  hovered.remove();
               } else {
                  Iterator var5 = this.selection.iterator();

                  while(var5.hasNext()) {
                     HudElement element = (HudElement)var5.next();
                     element.remove();
                  }

                  this.selection.clear();
               }
            }
         } else {
            hovered = this.getHovered(this.lastMouseX, this.lastMouseY);
            if (hovered != null) {
               hovered.toggle();
            }
         }
      }

      return super.method_25404(keyCode, scanCode, modifiers);
   }

   private void fillSelection(int mouseX, int mouseY) {
      int x1 = Math.min(this.clickX, mouseX);
      int x2 = Math.max(this.clickX, mouseX);
      int y1 = Math.min(this.clickY, mouseY);
      int y2 = Math.max(this.clickY, mouseY);
      Iterator var7 = this.hud.iterator();

      while(var7.hasNext()) {
         HudElement e = (HudElement)var7.next();
         if (e.getX() <= x2 && e.getX2() >= x1 && e.getY() <= y2 && e.getY2() >= y1) {
            this.selection.add(e);
         }
      }

   }

   public Iterable<Snapper.Element> getElements() {
      return () -> {
         return new Iterator<Snapper.Element>() {
            private final Iterator<HudElement> it;

            {
               this.it = HudEditorScreen.this.hud.iterator();
            }

            public boolean hasNext() {
               return this.it.hasNext();
            }

            public Snapper.Element next() {
               return (Snapper.Element)this.it.next();
            }
         };
      };
   }

   public boolean shouldNotSnapTo(Snapper.Element element) {
      return this.selection.contains((HudElement)element);
   }

   public int getSnappingRange() {
      return (Integer)this.hud.snappingRange.get();
   }

   private void onRender(int mouseX, int mouseY) {
      Iterator var3 = this.hud.iterator();

      HudElement element;
      while(var3.hasNext()) {
         element = (HudElement)var3.next();
         if (!element.isActive()) {
            this.renderElement(element, INACTIVE_BG_COLOR, INACTIVE_OL_COLOR);
         }
      }

      if (this.pressed && !this.dragging) {
         this.fillSelection(mouseX, mouseY);
      }

      var3 = this.selection.iterator();

      while(var3.hasNext()) {
         element = (HudElement)var3.next();
         this.renderElement(element, HOVER_BG_COLOR, HOVER_OL_COLOR);
      }

      if (this.pressed && !this.dragging) {
         this.selection.clear();
      }

      if (this.pressed && !this.dragging) {
         int x1 = Math.min(this.clickX, mouseX);
         int x2 = Math.max(this.clickX, mouseX);
         int y1 = Math.min(this.clickY, mouseY);
         int y2 = Math.max(this.clickY, mouseY);
         this.renderQuad((double)x1, (double)y1, (double)(x2 - x1), (double)(y2 - y1), SELECTION_BG_COLOR, SELECTION_OL_COLOR);
      }

      if (!this.pressed) {
         HudElement hovered = this.getHovered(mouseX, mouseY);
         if (hovered != null) {
            this.renderElement(hovered, HOVER_BG_COLOR, HOVER_OL_COLOR);
         }
      }

   }

   private HudElement getHovered(int mouseX, int mouseY) {
      Iterator var3 = this.hud.iterator();

      HudElement element;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         element = (HudElement)var3.next();
      } while(mouseX < element.x || mouseX > element.x + element.getWidth() || mouseY < element.y || mouseY > element.y + element.getHeight());

      return element;
   }

   private void renderQuad(double x, double y, double w, double h, Color bgColor, Color olColor) {
      Renderer2D.COLOR.quad(x + 1.0D, y + 1.0D, w - 2.0D, h - 2.0D, bgColor);
      Renderer2D.COLOR.quad(x, y, w, 1.0D, olColor);
      Renderer2D.COLOR.quad(x, y + h - 1.0D, w, 1.0D, olColor);
      Renderer2D.COLOR.quad(x, y + 1.0D, 1.0D, h - 2.0D, olColor);
      Renderer2D.COLOR.quad(x + w - 1.0D, y + 1.0D, 1.0D, h - 2.0D, olColor);
   }

   private void renderElement(HudElement element, Color bgColor, Color olColor) {
      this.renderQuad((double)element.x, (double)element.y, (double)element.getWidth(), (double)element.getHeight(), bgColor, olColor);
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      if (!Utils.canUpdate()) {
         this.method_25420(context, mouseX, mouseY, delta);
      }

      double s = MeteorClient.mc.method_22683().method_4495();
      mouseX = (int)((double)mouseX * s);
      mouseY = (int)((double)mouseY * s);
      Utils.unscaledProjection();
      boolean renderSplitLines = this.pressed && !this.selection.isEmpty() && this.moved;
      if (renderSplitLines || this.splitLinesAnimation > 0.0D) {
         this.renderSplitLines(renderSplitLines, (double)(delta / 20.0F));
      }

      renderElements(context);
      Renderer2D.COLOR.begin();
      this.onRender(mouseX, mouseY);
      Renderer2D.COLOR.render(new class_4587());
      Utils.scaledProjection();
      this.runAfterRenderTasks();
   }

   public static void renderElements(class_332 drawContext) {
      Hud hud = Hud.get();
      boolean inactiveOnly = Utils.canUpdate() && hud.active;
      HudRenderer.INSTANCE.begin(drawContext);
      Iterator var3 = hud.iterator();

      while(var3.hasNext()) {
         HudElement element = (HudElement)var3.next();
         element.updatePos();
         if (inactiveOnly) {
            if (!element.isActive()) {
               element.render(HudRenderer.INSTANCE);
            }
         } else {
            element.render(HudRenderer.INSTANCE);
         }
      }

      HudRenderer.INSTANCE.end();
   }

   private void renderSplitLines(boolean increment, double delta) {
      if (increment) {
         this.splitLinesAnimation += delta * 6.0D;
      } else {
         this.splitLinesAnimation -= delta * 6.0D;
      }

      this.splitLinesAnimation = class_3532.method_15350(this.splitLinesAnimation, 0.0D, 1.0D);
      Renderer2D renderer = Renderer2D.COLOR;
      renderer.begin();
      double w = (double)Utils.getWindowWidth();
      double h = (double)Utils.getWindowHeight();
      double w3 = w / 3.0D;
      double h3 = h / 3.0D;
      int prevA = SPLIT_LINES_COLOR.a;
      Color var10000 = SPLIT_LINES_COLOR;
      var10000.a = (int)((double)var10000.a * this.splitLinesAnimation);
      this.renderSplitLine(renderer, w3, 0.0D, w3, h);
      this.renderSplitLine(renderer, w3 * 2.0D, 0.0D, w3 * 2.0D, h);
      this.renderSplitLine(renderer, 0.0D, h3, w, h3);
      this.renderSplitLine(renderer, 0.0D, h3 * 2.0D, w, h3 * 2.0D);
      SPLIT_LINES_COLOR.a = prevA;
      renderer.render(new class_4587());
   }

   private void renderSplitLine(Renderer2D renderer, double x, double y, double destX, double destY) {
      double incX = 0.0D;
      double incY = 0.0D;
      if (x == destX) {
         incY = (double)Utils.getWindowWidth() / 25.0D;
      } else {
         incX = (double)Utils.getWindowWidth() / 25.0D;
      }

      do {
         do {
            renderer.line(x, y, x + incX, y + incY, SPLIT_LINES_COLOR);
            x += incX * 2.0D;
            y += incY * 2.0D;
         } while(!(x >= destX));
      } while(!(y >= destY));

   }

   public static boolean isOpen() {
      class_437 s = MeteorClient.mc.field_1755;
      return s instanceof HudEditorScreen || s instanceof AddHudElementScreen || s instanceof HudElementPresetsScreen || s instanceof HudElementScreen || s instanceof HudTab.HudScreen;
   }

   private class SelectionBox implements Snapper.Element {
      private int x;
      private int y;
      private final int width;
      private final int height;

      public SelectionBox() {
         int x1 = Integer.MAX_VALUE;
         int y1 = Integer.MAX_VALUE;
         int x2 = 0;
         int y2 = 0;
         Iterator var6 = HudEditorScreen.this.selection.iterator();

         while(var6.hasNext()) {
            HudElement element = (HudElement)var6.next();
            if (element.getX() < x1) {
               x1 = element.getX();
            } else if (element.getX() > x2) {
               x2 = element.getX();
            }

            if (element.getX2() < x1) {
               x1 = element.getX2();
            } else if (element.getX2() > x2) {
               x2 = element.getX2();
            }

            if (element.getY() < y1) {
               y1 = element.getY();
            } else if (element.getY() > y2) {
               y2 = element.getY();
            }

            if (element.getY2() < y1) {
               y1 = element.getY2();
            } else if (element.getY2() > y2) {
               y2 = element.getY2();
            }
         }

         this.x = x1;
         this.y = y1;
         this.width = x2 - x1;
         this.height = y2 - y1;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public void setPos(int x, int y) {
         Iterator var3 = HudEditorScreen.this.selection.iterator();

         while(var3.hasNext()) {
            HudElement element = (HudElement)var3.next();
            element.setPos(x + (element.x - this.x), y + (element.y - this.y));
         }

         this.x = x;
         this.y = y;
      }

      public void move(int deltaX, int deltaY) {
         int prevX = this.x;
         int prevY = this.y;
         int border = (Integer)Hud.get().border.get();
         this.x = class_3532.method_15340(this.x + deltaX, border, Utils.getWindowWidth() - this.width - border);
         this.y = class_3532.method_15340(this.y + deltaY, border, Utils.getWindowHeight() - this.height - border);
         Iterator var6 = HudEditorScreen.this.selection.iterator();

         while(var6.hasNext()) {
            HudElement element = (HudElement)var6.next();
            element.move(this.x - prevX, this.y - prevY);
         }

      }
   }
}
