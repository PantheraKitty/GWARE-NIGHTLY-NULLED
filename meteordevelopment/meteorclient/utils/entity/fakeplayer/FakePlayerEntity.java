package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1657;
import net.minecraft.class_640;
import net.minecraft.class_745;
import net.minecraft.class_1297.class_5529;
import org.jetbrains.annotations.Nullable;

public class FakePlayerEntity extends class_745 {
   public boolean doNotPush;
   public boolean hideWhenInsideCamera;

   public FakePlayerEntity(class_1657 player, String name, float health, boolean copyInv) {
      super(MeteorClient.mc.field_1687, new GameProfile(UUID.randomUUID(), name));
      this.method_5719(player);
      this.field_5982 = this.method_36454();
      this.field_6004 = this.method_36455();
      this.field_6241 = player.field_6241;
      this.field_6259 = this.field_6241;
      this.field_6283 = player.field_6283;
      this.field_6220 = this.field_6283;
      Byte playerModel = (Byte)player.method_5841().method_12789(class_1657.field_7518);
      this.field_6011.method_12778(class_1657.field_7518, playerModel);
      this.method_6127().method_26846(player.method_6127());
      this.method_18380(player.method_18376());
      this.field_7500 = this.method_23317();
      this.field_7521 = this.method_23318();
      this.field_7499 = this.method_23321();
      if (health <= 20.0F) {
         this.method_6033(health);
      } else {
         this.method_6033(health);
         this.method_6073(health - 20.0F);
      }

      if (copyInv) {
         this.method_31548().method_7377(player.method_31548());
      }

   }

   public void spawn() {
      this.method_31482();
      MeteorClient.mc.field_1687.method_53875(this);
   }

   public void despawn() {
      MeteorClient.mc.field_1687.method_2945(this.method_5628(), class_5529.field_26999);
      this.method_31745(class_5529.field_26999);
   }

   @Nullable
   protected class_640 method_3123() {
      if (this.field_3901 == null) {
         this.field_3901 = MeteorClient.mc.method_1562().method_2871(MeteorClient.mc.field_1724.method_5667());
      }

      return this.field_3901;
   }
}
