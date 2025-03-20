package meteordevelopment.meteorclient.systems.accounts.types;

import java.util.Optional;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import net.minecraft.class_320;
import net.minecraft.class_4844;
import net.minecraft.class_320.class_321;

public class CrackedAccount extends Account<CrackedAccount> {
   public CrackedAccount(String name) {
      super(AccountType.Cracked, name);
   }

   public boolean fetchInfo() {
      this.cache.username = this.name;
      return true;
   }

   public boolean login() {
      super.login();
      this.cache.loadHead();
      setSession(new class_320(this.name, class_4844.method_43344(this.name), "", Optional.empty(), Optional.empty(), class_321.field_1988));
      return true;
   }

   public boolean equals(Object o) {
      return !(o instanceof CrackedAccount) ? false : ((CrackedAccount)o).getUsername().equals(this.getUsername());
   }
}
