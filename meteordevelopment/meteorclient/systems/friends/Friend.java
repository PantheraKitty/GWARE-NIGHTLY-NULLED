package meteordevelopment.meteorclient.systems.friends;

import com.mojang.util.UndashedUuid;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.render.PlayerHeadTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Friend implements ISerializable<Friend>, Comparable<Friend> {
   public volatile String name;
   @Nullable
   private volatile UUID id;
   @Nullable
   private volatile PlayerHeadTexture headTexture;
   private volatile boolean updating;
   private volatile Friend.FriendType type;

   public Friend(String name, @Nullable UUID id, Friend.FriendType type) {
      this.type = Friend.FriendType.Friend;
      this.name = name;
      this.id = id;
      this.headTexture = null;
      this.type = type;
   }

   public Friend(class_1657 player, Friend.FriendType type) {
      this(player.method_5477().getString(), player.method_5667(), type);
   }

   public Friend(String name, Friend.FriendType type) {
      this(name, (UUID)null, type);
   }

   public String getName() {
      return this.name;
   }

   public PlayerHeadTexture getHead() {
      return this.headTexture != null ? this.headTexture : PlayerHeadUtils.STEVE_HEAD;
   }

   public void updateInfo() {
      this.updating = true;
      Friend.APIResponse res = (Friend.APIResponse)Http.get("https://api.mojang.com/users/profiles/minecraft/" + this.name).sendJson(Friend.APIResponse.class);
      if (res != null && res.name != null && res.id != null) {
         this.name = res.name;
         this.id = UndashedUuid.fromStringLenient(res.id);
         this.headTexture = PlayerHeadUtils.fetchHead(this.id);
         this.updating = false;
      }
   }

   public boolean headTextureNeedsUpdate() {
      return !this.updating && this.headTexture == null;
   }

   public Friend.FriendType getFriendType() {
      return this.type;
   }

   public void setfFriendType(Friend.FriendType type) {
      this.type = type;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("name", this.name);
      if (this.id != null) {
         tag.method_10582("id", UndashedUuid.toString(this.id));
      }

      switch(this.type.ordinal()) {
      case 0:
         tag.method_10582("friendType", "Friend");
         break;
      case 1:
         tag.method_10582("friendType", "Enemy");
      }

      return tag;
   }

   public Friend fromTag(class_2487 tag) {
      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Friend friend = (Friend)o;
         return Objects.equals(this.name, friend.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name});
   }

   public int compareTo(@NotNull Friend friend) {
      return this.name.compareTo(friend.name);
   }

   public static enum FriendType {
      Friend,
      Enemy;

      // $FF: synthetic method
      private static Friend.FriendType[] $values() {
         return new Friend.FriendType[]{Friend, Enemy};
      }
   }

   private static class APIResponse {
      String name;
      String id;
   }
}
