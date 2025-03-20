package meteordevelopment.meteorclient.systems.modules.render;

import java.util.UUID;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1657;
import net.minecraft.class_4587;

class LogoutSpots$Entry {
   public final double x;
   public final double y;
   public final double z;
   public final double xWidth;
   public final double zWidth;
   public final double halfWidth;
   public final double height;
   public final UUID uuid;
   public final String name;
   public final int health;
   public final int maxHealth;
   public final String healthText;
   // $FF: synthetic field
   final LogoutSpots this$0;

   public LogoutSpots$Entry(final LogoutSpots param1, class_1657 entity) {
      this.this$0 = var1;
      this.halfWidth = (double)(entity.method_17681() / 2.0F);
      this.x = entity.method_23317() - this.halfWidth;
      this.y = entity.method_23318();
      this.z = entity.method_23321() - this.halfWidth;
      this.xWidth = entity.method_5829().method_17939();
      this.zWidth = entity.method_5829().method_17941();
      this.height = entity.method_5829().method_17940();
      this.uuid = entity.method_5667();
      this.name = entity.method_5477().getString();
      this.health = Math.round(entity.method_6032() + entity.method_6067());
      this.maxHealth = Math.round(entity.method_6063() + entity.method_6067());
      this.healthText = " " + this.health;
   }

   public void render3D(Render3DEvent event) {
      if ((Boolean)this.this$0.fullHeight.get()) {
         event.renderer.box(this.x, this.y, this.z, this.x + this.xWidth, this.y + this.height, this.z + this.zWidth, (Color)this.this$0.sideColor.get(), (Color)this.this$0.lineColor.get(), (ShapeMode)this.this$0.shapeMode.get(), 0);
      } else {
         event.renderer.sideHorizontal(this.x, this.y, this.z, this.x + this.xWidth, this.z, (Color)this.this$0.sideColor.get(), (Color)this.this$0.lineColor.get(), (ShapeMode)this.this$0.shapeMode.get());
      }

   }

   public void render2D() {
      if (PlayerUtils.isWithinCamera(this.x, this.y, this.z, (double)((Integer)LogoutSpots.access$000(this.this$0).field_1690.method_42503().method_41753() * 16))) {
         TextRenderer text = TextRenderer.get();
         double scale = (Double)this.this$0.scale.get();
         LogoutSpots.pos.set(this.x + this.halfWidth, this.y + this.height + 0.5D, this.z + this.halfWidth);
         if (NametagUtils.to2D(LogoutSpots.pos, scale)) {
            NametagUtils.begin(LogoutSpots.pos);
            double healthPercentage = (double)this.health / (double)this.maxHealth;
            Color healthColor;
            if (healthPercentage <= 0.333D) {
               healthColor = LogoutSpots.RED;
            } else if (healthPercentage <= 0.666D) {
               healthColor = LogoutSpots.ORANGE;
            } else {
               healthColor = LogoutSpots.GREEN;
            }

            double i = text.getWidth(this.name) / 2.0D + text.getWidth(this.healthText) / 2.0D;
            Renderer2D.COLOR.begin();
            Renderer2D.COLOR.quad(-i, 0.0D, i * 2.0D, text.getHeight(), (Color)this.this$0.nameBackgroundColor.get());
            Renderer2D.COLOR.render((class_4587)null);
            text.beginBig();
            double hX = text.render(this.name, -i, 0.0D, (Color)this.this$0.nameColor.get());
            text.render(this.healthText, hX, 0.0D, healthColor);
            text.end();
            NametagUtils.end();
         }
      }
   }
}
