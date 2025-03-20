package meteordevelopment.meteorclient.systems.friends;

import com.mojang.util.UndashedUuid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_640;
import org.jetbrains.annotations.NotNull;

public class Friends extends System<Friends> {
   private final List<Friend> friends = new ArrayList();

   public Friends() {
      super("friends");
   }

   public static Friends get() {
      return (Friends)Systems.get(Friends.class);
   }

   public boolean add(Friend friend) {
      if (!friend.name.isEmpty() && !friend.name.contains(" ")) {
         if (!this.friends.contains(friend)) {
            this.friends.add(friend);
            this.save();
            return true;
         } else {
            Friend friendListFriend = (Friend)this.friends.get(this.friends.indexOf(friend));
            if (friendListFriend.getFriendType() != friend.getFriendType()) {
               friendListFriend.setfFriendType(friend.getFriendType());
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean remove(Friend friend) {
      if (this.friends.remove(friend)) {
         this.save();
         return true;
      } else {
         return false;
      }
   }

   public Friend get(String name) {
      Iterator var2 = this.friends.iterator();

      Friend friend;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         friend = (Friend)var2.next();
      } while(!friend.name.equalsIgnoreCase(name));

      return friend;
   }

   public Friend get(class_1657 player) {
      return this.get(player.method_5477().getString());
   }

   public Friend get(class_640 player) {
      return this.get(player.method_2966().getName());
   }

   public boolean isFriend(class_1657 player) {
      return player != null && this.get(player) != null && this.get(player).getFriendType() == Friend.FriendType.Friend;
   }

   public boolean isFriend(class_640 player) {
      return this.get(player) != null && this.get(player).getFriendType() == Friend.FriendType.Friend;
   }

   public boolean isEnemy(class_1657 player) {
      return player != null && this.get(player) != null && this.get(player).getFriendType() == Friend.FriendType.Enemy;
   }

   public boolean isEnemy(class_640 player) {
      return this.get(player) != null && this.get(player).getFriendType() == Friend.FriendType.Enemy;
   }

   public boolean shouldAttack(class_1657 player) {
      return !this.isFriend(player) || this.isEnemy(player);
   }

   public int count() {
      return this.friends.size();
   }

   public boolean isEmpty() {
      return this.friends.isEmpty();
   }

   @NotNull
   public Stream<Friend> friendStream() {
      return this.friends.stream().filter((x) -> {
         return x.getFriendType() == Friend.FriendType.Friend;
      });
   }

   @NotNull
   public Stream<Friend> enemyStream() {
      return this.friends.stream().filter((x) -> {
         return x.getFriendType() == Friend.FriendType.Enemy;
      });
   }

   @NotNull
   public Stream<Friend> stream() {
      return this.friends.stream();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("friends", NbtUtils.listToTag(this.friends));
      return tag;
   }

   public Friends fromTag(class_2487 tag) {
      this.friends.clear();
      Iterator var2 = tag.method_10554("friends", 10).iterator();

      while(var2.hasNext()) {
         class_2520 itemTag = (class_2520)var2.next();
         class_2487 friendTag = (class_2487)itemTag;
         if (friendTag.method_10545("name")) {
            String name = friendTag.method_10558("name");
            if (this.get(name) == null) {
               String s_friendType = friendTag.method_10558("friendType");
               Friend.FriendType type = Friend.FriendType.Friend;
               if (s_friendType != null) {
                  if (s_friendType.equals("Friend")) {
                     type = Friend.FriendType.Friend;
                  } else if (s_friendType.equals("Enemy")) {
                     type = Friend.FriendType.Enemy;
                  }
               }

               String uuid = friendTag.method_10558("id");
               Friend friend = !uuid.isBlank() ? new Friend(name, UndashedUuid.fromStringLenient(uuid), type) : new Friend(name, type);
               this.friends.add(friend);
            }
         }
      }

      Collections.sort(this.friends);
      MeteorExecutor.execute(() -> {
         this.friends.forEach(Friend::updateInfo);
      });
      return this;
   }
}
