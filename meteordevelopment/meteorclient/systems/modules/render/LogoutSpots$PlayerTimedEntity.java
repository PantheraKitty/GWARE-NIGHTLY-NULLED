package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import net.minecraft.class_1657;
import net.minecraft.class_243;

class LogoutSpots$PlayerTimedEntity {
   private long lastSeenTime;
   private class_1657 playerEntity;
   private class_243 pos = new class_243(0.0D, 0.0D, 0.0D);
   private List<WireframeEntityRenderer.RenderablePart> parts = null;

   private LogoutSpots$PlayerTimedEntity(final LogoutSpots param1) {
   }

   public void cacheRenderParts(Render3DEvent event) {
      this.parts = WireframeEntityRenderer.cloneEntityForRendering(event, this.playerEntity, this.pos);
   }
}
