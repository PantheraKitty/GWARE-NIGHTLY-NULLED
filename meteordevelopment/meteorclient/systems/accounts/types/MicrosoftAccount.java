package meteordevelopment.meteorclient.systems.accounts.types;

import com.mojang.util.UndashedUuid;
import java.util.Optional;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import meteordevelopment.meteorclient.systems.accounts.MicrosoftLogin;
import net.minecraft.class_320;
import net.minecraft.class_320.class_321;
import org.jetbrains.annotations.Nullable;

public class MicrosoftAccount extends Account<MicrosoftAccount> {
   @Nullable
   private String token;

   public MicrosoftAccount(String refreshToken) {
      super(AccountType.Microsoft, refreshToken);
   }

   public boolean fetchInfo() {
      this.token = this.auth();
      return this.token != null;
   }

   public boolean login() {
      if (this.token == null) {
         return false;
      } else {
         super.login();
         this.cache.loadHead();
         setSession(new class_320(this.cache.username, UndashedUuid.fromStringLenient(this.cache.uuid), this.token, Optional.empty(), Optional.empty(), class_321.field_34962));
         return true;
      }
   }

   @Nullable
   private String auth() {
      MicrosoftLogin.LoginData data = MicrosoftLogin.login(this.name);
      if (!data.isGood()) {
         return null;
      } else {
         this.name = data.newRefreshToken;
         this.cache.username = data.username;
         this.cache.uuid = data.uuid;
         return data.mcToken;
      }
   }

   public boolean equals(Object o) {
      return !(o instanceof MicrosoftAccount) ? false : ((MicrosoftAccount)o).name.equals(this.name);
   }
}
