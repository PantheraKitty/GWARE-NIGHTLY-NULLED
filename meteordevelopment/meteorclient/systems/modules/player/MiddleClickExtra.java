package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.VanillaFakeFly;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1802;

public class MiddleClickExtra extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<MiddleClickExtra.Mode> mode;
   private final Setting<Boolean> message;
   private final Setting<Boolean> rocketInAir;

   public MiddleClickExtra() {
      super(Categories.Player, "middle-click-extra", "Perform various actions when you middle click.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which item to use when you middle click.")).defaultValue(MiddleClickExtra.Mode.Pearl)).build());
      this.message = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("message")).description("Sends a message to the player when you add them as a friend.")).defaultValue(false)).visible(() -> {
         return this.mode.get() == MiddleClickExtra.Mode.AddFriend;
      })).build());
      this.rocketInAir = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("firework-while-flying")).description("Uses a rocket when flying.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onMouseButton(MouseButtonEvent event) {
      if (event.action == KeyAction.Press && event.button == 2 && this.mc.field_1755 == null) {
         VanillaFakeFly vanillaFakeFly = (VanillaFakeFly)Modules.get().get(VanillaFakeFly.class);
         if (!(Boolean)this.rocketInAir.get() || !this.mc.field_1724.method_6128() && !vanillaFakeFly.isFlying()) {
            if (this.mode.get() == MiddleClickExtra.Mode.AddFriend) {
               if (this.mc.field_1692 == null) {
                  return;
               }

               class_1297 var4 = this.mc.field_1692;
               if (!(var4 instanceof class_1657)) {
                  return;
               }

               class_1657 player = (class_1657)var4;
               if (!Friends.get().isFriend(player)) {
                  Friends.get().add(new Friend(player, Friend.FriendType.Friend));
                  this.info("Added %s to friends", new Object[]{player.method_5477().getString()});
                  if ((Boolean)this.message.get()) {
                     ChatUtils.sendPlayerMsg("/msg " + String.valueOf(player.method_5477()) + " I just friended you on Meteor.");
                  }
               } else {
                  Friends.get().remove(Friends.get().get(player));
                  this.info("Removed %s from friends", new Object[]{player.method_5477().getString()});
               }
            }

            if (this.mode.get() == MiddleClickExtra.Mode.Pearl) {
               if (MeteorClient.SWAP.beginSwap(class_1802.field_8634, true)) {
                  this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                  MeteorClient.SWAP.endSwap(true);
               } else {
                  this.warning("Unable to use the item.", new Object[0]);
               }
            }

            event.cancel();
         } else {
            if (vanillaFakeFly.isFlying()) {
               vanillaFakeFly.requestFirework();
            } else if (MeteorClient.SWAP.beginSwap(class_1802.field_8639, true)) {
               this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
               MeteorClient.SWAP.endSwap(true);
            } else {
               this.warning("Unable to use the item.", new Object[0]);
            }

            event.cancel();
         }
      }
   }

   public static enum Mode {
      Pearl,
      Rocket,
      AddFriend;

      // $FF: synthetic method
      private static MiddleClickExtra.Mode[] $values() {
         return new MiddleClickExtra.Mode[]{Pearl, Rocket, AddFriend};
      }
   }
}
