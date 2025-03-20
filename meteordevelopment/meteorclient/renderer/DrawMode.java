package meteordevelopment.meteorclient.renderer;

public enum DrawMode {
   Lines(2),
   Triangles(3);

   public final int indicesCount;

   private DrawMode(int indicesCount) {
      this.indicesCount = indicesCount;
   }

   public int getGL() {
      byte var10000;
      switch(this.ordinal()) {
      case 0:
         var10000 = 1;
         break;
      case 1:
         var10000 = 4;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static DrawMode[] $values() {
      return new DrawMode[]{Lines, Triangles};
   }
}
