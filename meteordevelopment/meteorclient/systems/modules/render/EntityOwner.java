package meteordevelopment.meteorclient.systems.modules.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1321;
import net.minecraft.class_1657;
import net.minecraft.class_4587;
import org.joml.Vector3d;

public class EntityOwner extends Module {
   private static final Color BACKGROUND = new Color(0, 0, 0, 75);
   private static final Color TEXT = new Color(255, 255, 255);
   private final SettingGroup sgGeneral;
   private final Setting<Double> scale;
   private final Vector3d pos;
   private final Map<UUID, String> uuidToName;

   public EntityOwner() {
      super(Categories.Render, "entity-owner", "Displays the name of the player who owns the entity you're looking at.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the text.")).defaultValue(1.0D).min(0.0D).build());
      this.pos = new Vector3d();
      this.uuidToName = new HashMap();
   }

   public void onDeactivate() {
      this.uuidToName.clear();
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1321) {
            class_1321 tameable = (class_1321)entity;
            UUID ownerUuid = tameable.method_6139();
            if (ownerUuid != null) {
               Utils.set(this.pos, entity, (double)event.tickDelta);
               this.pos.add(0.0D, (double)entity.method_18381(entity.method_18376()) + 0.75D, 0.0D);
               if (NametagUtils.to2D(this.pos, (Double)this.scale.get())) {
                  this.renderNametag(this.getOwnerName(ownerUuid));
               }
            }
         }
      }

   }

   private void renderNametag(String name) {
      TextRenderer text = TextRenderer.get();
      NametagUtils.begin(this.pos);
      text.beginBig();
      double w = text.getWidth(name);
      double x = -w / 2.0D;
      double y = -text.getHeight();
      Renderer2D.COLOR.begin();
      Renderer2D.COLOR.quad(x - 1.0D, y - 1.0D, w + 2.0D, text.getHeight() + 2.0D, BACKGROUND);
      Renderer2D.COLOR.render((class_4587)null);
      text.render(name, x, y, TEXT);
      text.end();
      NametagUtils.end();
   }

   private String getOwnerName(UUID uuid) {
      class_1657 player = this.mc.field_1687.method_18470(uuid);
      if (player != null) {
         return player.method_5477().getString();
      } else {
         String name = (String)this.uuidToName.get(uuid);
         if (name != null) {
            return name;
         } else {
            MeteorExecutor.execute(() -> {
               if (this.isActive()) {
                  String var10000 = uuid.toString();
                  EntityOwner.ProfileResponse res = (EntityOwner.ProfileResponse)Http.get("https://sessionserver.mojang.com/session/minecraft/profile/" + var10000.replace("-", "")).sendJson(EntityOwner.ProfileResponse.class);
                  if (this.isActive()) {
                     if (res == null) {
                        this.uuidToName.put(uuid, "Failed to get name");
                     } else {
                        this.uuidToName.put(uuid, res.name);
                     }
                  }
               }

            });
            name = "Retrieving";
            this.uuidToName.put(uuid, name);
            return name;
         }
      }
   }

   private static class ProfileResponse {
      public String name;
   }
}
