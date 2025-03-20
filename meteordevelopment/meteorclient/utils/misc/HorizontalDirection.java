package meteordevelopment.meteorclient.utils.misc;

public enum HorizontalDirection {
   South("South", "Z+", false, 0.0F, 0, 1),
   SouthEast("South East", "X+ Z+", true, -45.0F, 1, 1),
   West("West", "X-", false, 90.0F, -1, 0),
   NorthWest("North West", "X- Z-", true, 135.0F, -1, -1),
   North("North", "Z-", false, 180.0F, 0, -1),
   NorthEast("North East", "X+ Z-", true, -135.0F, 1, -1),
   East("East", "X+", false, -90.0F, 1, 0),
   SouthWest("South West", "X- Z+", true, 45.0F, -1, 1);

   public final String name;
   public final String axis;
   public final boolean diagonal;
   public final float yaw;
   public final int offsetX;
   public final int offsetZ;

   private HorizontalDirection(String name, String axis, boolean diagonal, float yaw, int offsetX, int offsetZ) {
      this.axis = axis;
      this.name = name;
      this.diagonal = diagonal;
      this.yaw = yaw;
      this.offsetX = offsetX;
      this.offsetZ = offsetZ;
   }

   public HorizontalDirection opposite() {
      HorizontalDirection var10000;
      switch(this.ordinal()) {
      case 0:
         var10000 = North;
         break;
      case 1:
         var10000 = NorthWest;
         break;
      case 2:
         var10000 = East;
         break;
      case 3:
         var10000 = SouthEast;
         break;
      case 4:
         var10000 = South;
         break;
      case 5:
         var10000 = SouthWest;
         break;
      case 6:
         var10000 = West;
         break;
      case 7:
         var10000 = NorthEast;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public HorizontalDirection rotateLeft() {
      HorizontalDirection var10000;
      switch(this.ordinal()) {
      case 0:
         var10000 = SouthEast;
         break;
      case 1:
         var10000 = East;
         break;
      case 2:
         var10000 = SouthWest;
         break;
      case 3:
         var10000 = West;
         break;
      case 4:
         var10000 = NorthWest;
         break;
      case 5:
         var10000 = North;
         break;
      case 6:
         var10000 = NorthEast;
         break;
      case 7:
         var10000 = South;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public HorizontalDirection rotateLeftSkipOne() {
      HorizontalDirection var10000;
      switch(this.ordinal()) {
      case 0:
         var10000 = East;
         break;
      case 1:
         var10000 = NorthEast;
         break;
      case 2:
         var10000 = South;
         break;
      case 3:
         var10000 = SouthWest;
         break;
      case 4:
         var10000 = West;
         break;
      case 5:
         var10000 = NorthWest;
         break;
      case 6:
         var10000 = North;
         break;
      case 7:
         var10000 = SouthEast;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public HorizontalDirection rotateRight() {
      HorizontalDirection var10000;
      switch(this.ordinal()) {
      case 0:
         var10000 = SouthWest;
         break;
      case 1:
         var10000 = South;
         break;
      case 2:
         var10000 = NorthWest;
         break;
      case 3:
         var10000 = North;
         break;
      case 4:
         var10000 = NorthEast;
         break;
      case 5:
         var10000 = East;
         break;
      case 6:
         var10000 = SouthEast;
         break;
      case 7:
         var10000 = West;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static HorizontalDirection get(float yaw) {
      yaw %= 360.0F;
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      if (!((double)yaw >= 337.5D) && !((double)yaw < 22.5D)) {
         if ((double)yaw >= 22.5D && (double)yaw < 67.5D) {
            return SouthWest;
         } else if ((double)yaw >= 67.5D && (double)yaw < 112.5D) {
            return West;
         } else if ((double)yaw >= 112.5D && (double)yaw < 157.5D) {
            return NorthWest;
         } else if ((double)yaw >= 157.5D && (double)yaw < 202.5D) {
            return North;
         } else if ((double)yaw >= 202.5D && (double)yaw < 247.5D) {
            return NorthEast;
         } else if ((double)yaw >= 247.5D && (double)yaw < 292.5D) {
            return East;
         } else {
            return (double)yaw >= 292.5D && (double)yaw < 337.5D ? SouthEast : South;
         }
      } else {
         return South;
      }
   }

   // $FF: synthetic method
   private static HorizontalDirection[] $values() {
      return new HorizontalDirection[]{South, SouthEast, West, NorthWest, North, NorthEast, East, SouthWest};
   }
}
