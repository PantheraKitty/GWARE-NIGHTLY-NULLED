package meteordevelopment.meteorclient.systems.hud;

import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;

public class HudBox implements ISerializable<HudBox> {
   private final HudElement element;
   public XAnchor xAnchor;
   public YAnchor yAnchor;
   public int x;
   public int y;
   int width;
   int height;

   public HudBox(HudElement element) {
      this.xAnchor = XAnchor.Left;
      this.yAnchor = YAnchor.Top;
      this.element = element;
   }

   public void setSize(double width, double height) {
      if (width >= 0.0D) {
         this.width = (int)Math.ceil(width);
      }

      if (height >= 0.0D) {
         this.height = (int)Math.ceil(height);
      }

   }

   public void setPos(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void setXAnchor(XAnchor anchor) {
      if (this.xAnchor != anchor) {
         int renderX = this.getRenderX();
         switch(anchor) {
         case Left:
            this.x = renderX;
            break;
         case Center:
            this.x = renderX + this.width / 2 - Utils.getWindowWidth() / 2;
            break;
         case Right:
            this.x = renderX + this.width - Utils.getWindowWidth();
         }

         this.xAnchor = anchor;
      }

   }

   public void setYAnchor(YAnchor anchor) {
      if (this.yAnchor != anchor) {
         int renderY = this.getRenderY();
         switch(anchor) {
         case Top:
            this.y = renderY;
            break;
         case Center:
            this.y = renderY + this.height / 2 - Utils.getWindowHeight() / 2;
            break;
         case Bottom:
            this.y = renderY + this.height - Utils.getWindowHeight();
         }

         this.yAnchor = anchor;
      }

   }

   public void updateAnchors() {
      this.setXAnchor(this.getXAnchor((double)this.getRenderX()));
      this.setYAnchor(this.getYAnchor((double)this.getRenderY()));
   }

   public void move(int deltaX, int deltaY) {
      this.x += deltaX;
      this.y += deltaY;
      if (this.element.autoAnchors) {
         this.updateAnchors();
      }

      int border = (Integer)Hud.get().border.get();
      if (this.xAnchor == XAnchor.Left && this.x < border) {
         this.x = border;
      } else if (this.xAnchor == XAnchor.Right && this.x > border) {
         this.x = border;
      }

      if (this.yAnchor == YAnchor.Top && this.y < border) {
         this.y = border;
      } else if (this.yAnchor == YAnchor.Bottom && this.y > border) {
         this.y = border;
      }

   }

   public XAnchor getXAnchor(double x) {
      double splitLeft = (double)Utils.getWindowWidth() / 3.0D;
      double splitRight = splitLeft * 2.0D;
      boolean left = x <= splitLeft;
      boolean right = x + (double)this.width >= splitRight;
      if (left && right || !left && !right) {
         return XAnchor.Center;
      } else {
         return left ? XAnchor.Left : XAnchor.Right;
      }
   }

   public YAnchor getYAnchor(double y) {
      double splitTop = (double)Utils.getWindowHeight() / 3.0D;
      double splitBottom = splitTop * 2.0D;
      boolean top = y <= splitTop;
      boolean bottom = y + (double)this.height >= splitBottom;
      if (top && bottom || !top && !bottom) {
         return YAnchor.Center;
      } else {
         return top ? YAnchor.Top : YAnchor.Bottom;
      }
   }

   public int getRenderX() {
      int var10000;
      switch(this.xAnchor) {
      case Left:
         var10000 = this.x;
         break;
      case Center:
         var10000 = Utils.getWindowWidth() / 2 - this.width / 2 + this.x;
         break;
      case Right:
         var10000 = Utils.getWindowWidth() - this.width + this.x;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int getRenderY() {
      int var10000;
      switch(this.yAnchor) {
      case Top:
         var10000 = this.y;
         break;
      case Center:
         var10000 = Utils.getWindowHeight() / 2 - this.height / 2 + this.y;
         break;
      case Bottom:
         var10000 = Utils.getWindowHeight() - this.height + this.y;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public double alignX(double selfWidth, double width, Alignment alignment) {
      XAnchor anchor = this.xAnchor;
      if (alignment == Alignment.Left) {
         anchor = XAnchor.Left;
      } else if (alignment == Alignment.Center) {
         anchor = XAnchor.Center;
      } else if (alignment == Alignment.Right) {
         anchor = XAnchor.Right;
      }

      double var10000;
      switch(anchor) {
      case Left:
         var10000 = 0.0D;
         break;
      case Center:
         var10000 = selfWidth / 2.0D - width / 2.0D;
         break;
      case Right:
         var10000 = selfWidth - width;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("x-anchor", this.xAnchor.name());
      tag.method_10582("y-anchor", this.yAnchor.name());
      tag.method_10569("x", this.x);
      tag.method_10569("y", this.y);
      return tag;
   }

   public HudBox fromTag(class_2487 tag) {
      if (tag.method_10545("x-anchor")) {
         this.xAnchor = XAnchor.valueOf(tag.method_10558("x-anchor"));
      }

      if (tag.method_10545("y-anchor")) {
         this.yAnchor = YAnchor.valueOf(tag.method_10558("y-anchor"));
      }

      if (tag.method_10545("x")) {
         this.x = tag.method_10550("x");
      }

      if (tag.method_10545("y")) {
         this.y = tag.method_10550("y");
      }

      return this;
   }
}
