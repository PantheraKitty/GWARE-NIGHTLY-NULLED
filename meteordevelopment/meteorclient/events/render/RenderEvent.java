package meteordevelopment.meteorclient.events.render;

public class RenderEvent {
   private final float tickDelta;

   public RenderEvent(float tickDelta) {
      this.tickDelta = tickDelta;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }
}
