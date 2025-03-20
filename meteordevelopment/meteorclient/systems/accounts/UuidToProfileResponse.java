package meteordevelopment.meteorclient.systems.accounts;

public class UuidToProfileResponse {
   public UuidToProfileResponse.Property[] properties;

   public String getPropertyValue(String name) {
      UuidToProfileResponse.Property[] var2 = this.properties;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         UuidToProfileResponse.Property property = var2[var4];
         if (property.name.equals(name)) {
            return property.value;
         }
      }

      return null;
   }

   public static class Property {
      public String name;
      public String value;
   }
}
