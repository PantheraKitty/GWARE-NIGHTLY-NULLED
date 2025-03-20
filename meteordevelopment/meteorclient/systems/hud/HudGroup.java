package meteordevelopment.meteorclient.systems.hud;

public record HudGroup(String title) {
   public HudGroup(String title) {
      this.title = title;
   }

   public String title() {
      return this.title;
   }
}
