package meteordevelopment.meteorclient.gui.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.renderer.packer.TexturePacker;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_3532;

public class GuiRenderer {
   private static final Color WHITE = new Color(255, 255, 255);
   private static final TexturePacker TEXTURE_PACKER = new TexturePacker();
   private static ByteTexture TEXTURE;
   public static GuiTexture CIRCLE;
   public static GuiTexture TRIANGLE;
   public static GuiTexture EDIT;
   public static GuiTexture RESET;
   public static GuiTexture FAVORITE_NO;
   public static GuiTexture FAVORITE_YES;
   public GuiTheme theme;
   private final Renderer2D r = new Renderer2D(false);
   private final Renderer2D rTex = new Renderer2D(true);
   private final Pool<Scissor> scissorPool = new Pool(Scissor::new);
   private final Stack<Scissor> scissorStack = new Stack();
   private final Pool<TextOperation> textPool = new Pool(TextOperation::new);
   private final List<TextOperation> texts = new ArrayList();
   private final List<Runnable> postTasks = new ArrayList();
   public String tooltip;
   public String lastTooltip;
   public WWidget tooltipWidget;
   private double tooltipAnimProgress;
   private class_332 drawContext;

   public static GuiTexture addTexture(class_2960 id) {
      return TEXTURE_PACKER.add(id);
   }

   @PostInit
   public static void init() {
      CIRCLE = addTexture(MeteorClient.identifier("textures/icons/gui/circle.png"));
      TRIANGLE = addTexture(MeteorClient.identifier("textures/icons/gui/triangle.png"));
      EDIT = addTexture(MeteorClient.identifier("textures/icons/gui/edit.png"));
      RESET = addTexture(MeteorClient.identifier("textures/icons/gui/reset.png"));
      FAVORITE_NO = addTexture(MeteorClient.identifier("textures/icons/gui/favorite_no.png"));
      FAVORITE_YES = addTexture(MeteorClient.identifier("textures/icons/gui/favorite_yes.png"));
      TEXTURE = TEXTURE_PACKER.pack();
   }

   public void begin(class_332 drawContext) {
      this.drawContext = drawContext;
      GL.enableBlend();
      GL.enableScissorTest();
      this.scissorStart(0.0D, 0.0D, (double)Utils.getWindowWidth(), (double)Utils.getWindowHeight());
   }

   public void end() {
      this.scissorEnd();
      Iterator var1 = this.postTasks.iterator();

      while(var1.hasNext()) {
         Runnable task = (Runnable)var1.next();
         task.run();
      }

      this.postTasks.clear();
      GL.disableScissorTest();
   }

   public void beginRender() {
      this.r.begin();
      this.rTex.begin();
   }

   public void endRender() {
      this.r.end();
      this.rTex.end();
      this.r.render(this.drawContext.method_51448());
      GL.bindTexture(TEXTURE.method_4624());
      this.rTex.render(this.drawContext.method_51448());
      this.theme.textRenderer().begin(this.theme.scale(1.0D));
      Iterator var1 = this.texts.iterator();

      TextOperation text;
      while(var1.hasNext()) {
         text = (TextOperation)var1.next();
         if (!text.title) {
            text.run(this.textPool);
         }
      }

      this.theme.textRenderer().end(this.drawContext.method_51448());
      this.theme.textRenderer().begin(this.theme.scale(1.25D));
      var1 = this.texts.iterator();

      while(var1.hasNext()) {
         text = (TextOperation)var1.next();
         if (text.title) {
            text.run(this.textPool);
         }
      }

      this.theme.textRenderer().end(this.drawContext.method_51448());
      this.texts.clear();
   }

   public void scissorStart(double x, double y, double width, double height) {
      if (!this.scissorStack.isEmpty()) {
         Scissor parent = (Scissor)this.scissorStack.peek();
         if (x < (double)parent.x) {
            x = (double)parent.x;
         } else if (x + width > (double)(parent.x + parent.width)) {
            width -= x + width - (double)(parent.x + parent.width);
         }

         if (y < (double)parent.y) {
            y = (double)parent.y;
         } else if (y + height > (double)(parent.y + parent.height)) {
            height -= y + height - (double)(parent.y + parent.height);
         }

         parent.apply();
         this.endRender();
      }

      this.scissorStack.push(((Scissor)this.scissorPool.get()).set(x, y, width, height));
      this.beginRender();
   }

   public void scissorEnd() {
      Scissor scissor = (Scissor)this.scissorStack.pop();
      scissor.apply();
      this.endRender();
      Iterator var2 = scissor.postTasks.iterator();

      while(var2.hasNext()) {
         Runnable task = (Runnable)var2.next();
         task.run();
      }

      if (!this.scissorStack.isEmpty()) {
         this.beginRender();
      }

      this.scissorPool.free(scissor);
   }

   public boolean renderTooltip(class_332 drawContext, double mouseX, double mouseY, double delta) {
      this.tooltipAnimProgress += (double)(this.tooltip != null ? 1 : -1) * delta * 14.0D;
      this.tooltipAnimProgress = class_3532.method_15350(this.tooltipAnimProgress, 0.0D, 1.0D);
      boolean toReturn = false;
      if (this.tooltipAnimProgress > 0.0D) {
         if (this.tooltip != null && !this.tooltip.equals(this.lastTooltip)) {
            this.tooltipWidget = this.theme.tooltip(this.tooltip);
            this.tooltipWidget.init();
         }

         this.tooltipWidget.move(-this.tooltipWidget.x + mouseX + 12.0D, -this.tooltipWidget.y + mouseY + 12.0D);
         this.setAlpha(this.tooltipAnimProgress);
         this.begin(drawContext);
         this.tooltipWidget.render(this, mouseX, mouseY, delta);
         this.end();
         this.setAlpha(1.0D);
         this.lastTooltip = this.tooltip;
         toReturn = true;
      }

      this.tooltip = null;
      return toReturn;
   }

   public void setAlpha(double a) {
      this.r.setAlpha(a);
      this.rTex.setAlpha(a);
      this.theme.textRenderer().setAlpha(a);
   }

   public void tooltip(String text) {
      this.tooltip = text;
   }

   public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
      this.r.quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
   }

   public void quad(double x, double y, double width, double height, Color colorLeft, Color colorRight) {
      this.quad(x, y, width, height, colorLeft, colorRight, colorRight, colorLeft);
   }

   public void quad(double x, double y, double width, double height, Color color) {
      this.quad(x, y, width, height, color, color);
   }

   public void quad(WWidget widget, Color color) {
      this.quad(widget.x, widget.y, widget.width, widget.height, color);
   }

   public void quad(double x, double y, double width, double height, GuiTexture texture, Color color) {
      this.rTex.texQuad(x, y, width, height, texture.get(width, height), color);
   }

   public void rotatedQuad(double x, double y, double width, double height, double rotation, GuiTexture texture, Color color) {
      this.rTex.texQuad(x, y, width, height, rotation, texture.get(width, height), color);
   }

   public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
      this.r.triangle(x1, y1, x2, y2, x3, y3, color);
   }

   public void text(String text, double x, double y, Color color, boolean title) {
      this.texts.add(((TextOperation)this.getOp(this.textPool, x, y, color)).set(text, this.theme.textRenderer(), title));
   }

   public void texture(double x, double y, double width, double height, double rotation, Texture texture) {
      this.post(() -> {
         this.rTex.begin();
         this.rTex.texQuad(x, y, width, height, rotation, 0.0D, 0.0D, 1.0D, 1.0D, WHITE);
         this.rTex.end();
         texture.bind();
         this.rTex.render(this.drawContext.method_51448());
      });
   }

   public void post(Runnable task) {
      ((Scissor)this.scissorStack.peek()).postTasks.add(task);
   }

   public void item(class_1799 itemStack, int x, int y, float scale, boolean overlay) {
      RenderUtils.drawItem(this.drawContext, itemStack, x, y, scale, overlay);
   }

   public void absolutePost(Runnable task) {
      this.postTasks.add(task);
   }

   private <T extends GuiRenderOperation<T>> T getOp(Pool<T> pool, double x, double y, Color color) {
      T op = (GuiRenderOperation)pool.get();
      op.set(x, y, color);
      return op;
   }
}
