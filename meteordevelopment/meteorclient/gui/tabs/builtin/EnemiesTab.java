package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_437;

public class EnemiesTab extends Tab {
   public EnemiesTab() {
      super("Enemies");
   }

   public TabScreen createScreen(GuiTheme theme) {
      return new EnemiesTab.FriendsScreen(theme, this);
   }

   public boolean isScreen(class_437 screen) {
      return screen instanceof EnemiesTab.FriendsScreen;
   }

   private static class FriendsScreen extends WindowTabScreen {
      public FriendsScreen(GuiTheme theme, Tab tab) {
         super(theme, tab);
      }

      public void initWidgets() {
         WTable table = (WTable)this.add(this.theme.table()).expandX().minWidth(400.0D).widget();
         this.initTable(table);
         this.add(this.theme.horizontalSeparator()).expandX();
         WHorizontalList list = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         WTextBox nameW = (WTextBox)list.add(this.theme.textBox("", (text, c) -> {
            return c != ' ';
         })).expandX().widget();
         nameW.setFocused(true);
         WPlus add = (WPlus)list.add(this.theme.plus()).widget();
         add.action = () -> {
            String name = nameW.get().trim();
            Friend friend = new Friend(name, Friend.FriendType.Enemy);
            if (Friends.get().add(friend)) {
               nameW.set("");
               this.reload();
               MeteorExecutor.execute(() -> {
                  friend.updateInfo();
                  this.reload();
               });
            }

         };
         this.enterAction = add.action;
      }

      private void initTable(WTable table) {
         table.clear();
         if (!Friends.get().isEmpty()) {
            Friends.get().enemyStream().forEach((friend) -> {
               MeteorExecutor.execute(() -> {
                  if (friend.headTextureNeedsUpdate()) {
                     friend.updateInfo();
                     this.reload();
                  }

               });
            });
            Friends.get().enemyStream().forEach((friend) -> {
               table.add(this.theme.texture(32.0D, 32.0D, friend.getHead().needsRotate() ? 90.0D : 0.0D, friend.getHead()));
               table.add(this.theme.label(friend.getName()));
               WMinus remove = (WMinus)table.add(this.theme.minus()).expandCellX().right().widget();
               remove.action = () -> {
                  Friends.get().remove(friend);
                  this.reload();
               };
               table.row();
            });
         }
      }

      public boolean toClipboard() {
         return NbtUtils.toClipboard(Friends.get());
      }

      public boolean fromClipboard() {
         return NbtUtils.fromClipboard((System)Friends.get());
      }
   }
}
