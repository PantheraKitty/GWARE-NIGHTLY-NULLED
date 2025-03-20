package meteordevelopment.meteorclient.utils.other;

import java.util.Iterator;

public class Snapper {
   private final Snapper.Container container;
   private Snapper.Element snappedTo;
   private Snapper.Direction mainDir;
   private int mainPos;
   private boolean secondary;
   private int secondaryPos;

   public Snapper(Snapper.Container container) {
      this.container = container;
   }

   public void move(Snapper.Element element, int deltaX, int deltaY) {
      if (this.container.getSnappingRange() == 0) {
         element.move(deltaX, deltaY);
      } else {
         if (this.snappedTo == null) {
            this.moveUnsnapped(element, deltaX, deltaY);
         } else {
            this.moveSnapped(element, deltaX, deltaY);
         }

      }
   }

   public void unsnap() {
      this.snappedTo = null;
   }

   private void moveUnsnapped(Snapper.Element element, int deltaX, int deltaY) {
      element.move(deltaX, deltaY);
      Snapper.Element closest;
      int closestDist;
      Iterator var6;
      Snapper.Element e;
      int dist;
      if (deltaX > 0) {
         closest = null;
         closestDist = Integer.MAX_VALUE;
         var6 = this.container.getElements().iterator();

         while(true) {
            do {
               do {
                  do {
                     do {
                        if (!var6.hasNext()) {
                           if (closest != null) {
                              element.setPos(closest.getX() - element.getWidth(), element.getY());
                              this.snapMain(closest, Snapper.Direction.Right);
                           }

                           return;
                        }

                        e = (Snapper.Element)var6.next();
                     } while(this.container.shouldNotSnapTo(e));

                     dist = e.getX() - element.getX2();
                  } while(dist <= 0);
               } while(dist > this.container.getSnappingRange());
            } while(closest != null && dist >= closestDist);

            if (this.isNextToHorizontally(element, e)) {
               closest = e;
               closestDist = dist;
            }
         }
      } else if (deltaX < 0) {
         closest = null;
         closestDist = Integer.MAX_VALUE;
         var6 = this.container.getElements().iterator();

         while(true) {
            do {
               do {
                  do {
                     do {
                        if (!var6.hasNext()) {
                           if (closest != null) {
                              element.setPos(closest.getX2(), element.getY());
                              this.snapMain(closest, Snapper.Direction.Left);
                           }

                           return;
                        }

                        e = (Snapper.Element)var6.next();
                     } while(this.container.shouldNotSnapTo(e));

                     dist = element.getX() - e.getX2();
                  } while(dist <= 0);
               } while(dist > this.container.getSnappingRange());
            } while(closest != null && dist >= closestDist);

            if (this.isNextToHorizontally(element, e)) {
               closest = e;
               closestDist = dist;
            }
         }
      } else if (deltaY > 0) {
         closest = null;
         closestDist = Integer.MAX_VALUE;
         var6 = this.container.getElements().iterator();

         while(true) {
            do {
               do {
                  do {
                     do {
                        if (!var6.hasNext()) {
                           if (closest != null) {
                              element.setPos(element.getX(), closest.getY() - element.getHeight());
                              this.snapMain(closest, Snapper.Direction.Top);
                           }

                           return;
                        }

                        e = (Snapper.Element)var6.next();
                     } while(this.container.shouldNotSnapTo(e));

                     dist = e.getY() - element.getY2();
                  } while(dist <= 0);
               } while(dist > this.container.getSnappingRange());
            } while(closest != null && dist >= closestDist);

            if (this.isNextToVertically(element, e)) {
               closest = e;
               closestDist = dist;
            }
         }
      } else if (deltaY < 0) {
         closest = null;
         closestDist = Integer.MAX_VALUE;
         var6 = this.container.getElements().iterator();

         while(true) {
            do {
               do {
                  do {
                     do {
                        if (!var6.hasNext()) {
                           if (closest != null) {
                              element.setPos(element.getX(), closest.getY2());
                              this.snapMain(closest, Snapper.Direction.Bottom);
                           }

                           return;
                        }

                        e = (Snapper.Element)var6.next();
                     } while(this.container.shouldNotSnapTo(e));

                     dist = element.getY() - e.getY2();
                  } while(dist <= 0);
               } while(dist > this.container.getSnappingRange());
            } while(closest != null && dist >= closestDist);

            if (this.isNextToVertically(element, e)) {
               closest = e;
               closestDist = dist;
            }
         }
      }
   }

   private void moveSnapped(Snapper.Element element, int deltaX, int deltaY) {
      int dist;
      switch(this.mainDir.ordinal()) {
      case 0:
      case 1:
         if (this.secondary) {
            this.secondaryPos += deltaY;
         } else {
            element.move(0, deltaY);
         }

         this.mainPos += deltaX;
         if (!this.isNextToHorizontally(element, this.snappedTo)) {
            this.unsnap();
         } else if (!this.secondary) {
            if (deltaY > 0) {
               dist = this.snappedTo.getY2() - element.getY2();
               if (dist > 0 && dist < this.container.getSnappingRange()) {
                  element.setPos(element.getX(), this.snappedTo.getY2() - element.getHeight());
                  this.snapSecondary();
               }
            } else if (deltaY < 0) {
               dist = this.snappedTo.getY() - element.getY();
               if (dist < 0 && dist > -this.container.getSnappingRange()) {
                  element.setPos(element.getX(), this.snappedTo.getY());
                  this.snapSecondary();
               }
            }
         }
         break;
      case 2:
      case 3:
         if (this.secondary) {
            this.secondaryPos += deltaX;
         } else {
            element.move(deltaX, 0);
         }

         this.mainPos += deltaY;
         if (!this.isNextToVertically(element, this.snappedTo)) {
            this.unsnap();
         } else if (!this.secondary) {
            if (deltaX > 0) {
               dist = this.snappedTo.getX2() - element.getX2();
               if (dist > 0 && dist < this.container.getSnappingRange()) {
                  element.setPos(this.snappedTo.getX2() - element.getWidth(), element.getY());
                  this.snapSecondary();
               }
            } else if (deltaX < 0) {
               dist = element.getX() - this.snappedTo.getX();
               if (dist > 0 && dist < this.container.getSnappingRange()) {
                  element.setPos(this.snappedTo.getX(), element.getY());
                  this.snapSecondary();
               }
            }
         }
      }

      if (Math.abs(this.mainPos) > this.container.getSnappingRange() * 5) {
         this.unsnap();
      } else if (Math.abs(this.secondaryPos) > this.container.getSnappingRange() * 5) {
         this.secondary = false;
      }

   }

   private void snapMain(Snapper.Element element, Snapper.Direction dir) {
      this.snappedTo = element;
      this.mainDir = dir;
      this.mainPos = 0;
      this.secondary = false;
   }

   private void snapSecondary() {
      this.secondary = true;
      this.secondaryPos = 0;
   }

   private boolean isBetween(int value, int min, int max) {
      return value > min && value < max;
   }

   private boolean isNextToHorizontally(Snapper.Element e1, Snapper.Element e2) {
      int y1 = e1.getY();
      int h1 = e1.getHeight();
      int y2 = e2.getY();
      int h2 = e2.getHeight();
      return this.isBetween(y1, y2, y2 + h2) || this.isBetween(y1 + h1, y2, y2 + h2) || this.isBetween(y2, y1, y1 + h1) || this.isBetween(y2 + h2, y1, y1 + h1);
   }

   private boolean isNextToVertically(Snapper.Element e1, Snapper.Element e2) {
      int x1 = e1.getX();
      int w1 = e1.getWidth();
      int x2 = e2.getX();
      int w2 = e2.getWidth();
      return this.isBetween(x1, x2, x2 + w2) || this.isBetween(x1 + w1, x2, x2 + w2) || this.isBetween(x2, x1, x1 + w1) || this.isBetween(x2 + w2, x1, x1 + w1);
   }

   public interface Container {
      Iterable<Snapper.Element> getElements();

      boolean shouldNotSnapTo(Snapper.Element var1);

      int getSnappingRange();
   }

   public interface Element {
      int getX();

      int getY();

      default int getX2() {
         return this.getX() + this.getWidth();
      }

      default int getY2() {
         return this.getY() + this.getHeight();
      }

      int getWidth();

      int getHeight();

      void setPos(int var1, int var2);

      void move(int var1, int var2);
   }

   private static enum Direction {
      Right,
      Left,
      Top,
      Bottom;

      // $FF: synthetic method
      private static Snapper.Direction[] $values() {
         return new Snapper.Direction[]{Right, Left, Top, Bottom};
      }
   }
}
