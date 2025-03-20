package meteordevelopment.meteorclient.systems.modules.movement.elytrafly;

public enum ElytraFlightModes {
   Vanilla,
   Packet,
   Pitch40,
   Bounce,
   Slide;

   // $FF: synthetic method
   private static ElytraFlightModes[] $values() {
      return new ElytraFlightModes[]{Vanilla, Packet, Pitch40, Bounce, Slide};
   }
}
