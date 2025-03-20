package meteordevelopment.meteorclient.systems.accounts;

import com.mojang.util.UndashedUuid;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import meteordevelopment.meteorclient.utils.render.PlayerHeadTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.minecraft.class_2487;

public class AccountCache implements ISerializable<AccountCache> {
   public String username = "";
   public String uuid = "";
   private PlayerHeadTexture headTexture;

   public PlayerHeadTexture getHeadTexture() {
      return this.headTexture != null ? this.headTexture : PlayerHeadUtils.STEVE_HEAD;
   }

   public void loadHead() {
      if (this.uuid != null && !this.uuid.isBlank()) {
         this.headTexture = PlayerHeadUtils.fetchHead(UndashedUuid.fromStringLenient(this.uuid));
      }
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("username", this.username);
      tag.method_10582("uuid", this.uuid);
      return tag;
   }

   public AccountCache fromTag(class_2487 tag) {
      if (tag.method_10545("username") && tag.method_10545("uuid")) {
         this.username = tag.method_10558("username");
         this.uuid = tag.method_10558("uuid");
         this.loadHead();
         return this;
      } else {
         throw new NbtException();
      }
   }
}
