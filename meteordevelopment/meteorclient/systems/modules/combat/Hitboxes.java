package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.Set;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1829;

public class Hitboxes extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<Double> value;
   private final Setting<Boolean> ignoreFriends;
   private final Setting<Boolean> onlyOnWeapon;

   public Hitboxes() {
      super(Categories.Combat, "hitboxes", "Expands an entity's hitboxes.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Which entities to target.")).defaultValue(class_1299.field_6097).build());
      this.value = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("expand")).description("How much to expand the hitbox of the entity.")).defaultValue(0.5D).build());
      this.ignoreFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Doesn't expand the hitboxes of friends.")).defaultValue(true)).build());
      this.onlyOnWeapon = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-weapon")).description("Only modifies hitbox when holding a weapon in hand.")).defaultValue(false)).build());
   }

   public double getEntityValue(class_1297 entity) {
      if (this.isActive() && this.testWeapon() && (!(Boolean)this.ignoreFriends.get() || !(entity instanceof class_1657) || !Friends.get().isFriend((class_1657)entity))) {
         return ((Set)this.entities.get()).contains(entity.method_5864()) ? (Double)this.value.get() : 0.0D;
      } else {
         return 0.0D;
      }
   }

   private boolean testWeapon() {
      return !(Boolean)this.onlyOnWeapon.get() ? true : InvUtils.testInHands((itemStack) -> {
         return itemStack.method_7909() instanceof class_1829 || itemStack.method_7909() instanceof class_1743;
      });
   }
}
