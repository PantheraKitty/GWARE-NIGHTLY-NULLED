package meteordevelopment.meteorclient.addons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

public class AddonManager {
   public static final List<MeteorAddon> ADDONS = new ArrayList();

   public static void init() {
      MeteorClient.ADDON = new MeteorAddon() {
         public void onInitialize() {
         }

         public String getPackage() {
            return "meteordevelopment.meteorclient";
         }

         public String getWebsite() {
            return "https://meteorclient.com";
         }

         public GithubRepo getRepo() {
            return new GithubRepo("MeteorDevelopment", "meteor-client");
         }

         public String getCommit() {
            String commit = MeteorClient.MOD_META.getCustomValue("meteor-client:commit").getAsString();
            return commit.isEmpty() ? null : commit;
         }
      };
      ModMetadata metadata = ((ModContainer)FabricLoader.getInstance().getModContainer("meteor-client").get()).getMetadata();
      MeteorClient.ADDON.name = metadata.getName();
      MeteorClient.ADDON.authors = new String[metadata.getAuthors().size()];
      if (metadata.containsCustomValue("meteor-client:color")) {
         MeteorClient.ADDON.color.parse(metadata.getCustomValue("meteor-client:color").getAsString());
      }

      int i = 0;

      Person author;
      for(Iterator var2 = metadata.getAuthors().iterator(); var2.hasNext(); MeteorClient.ADDON.authors[i++] = author.getName()) {
         author = (Person)var2.next();
      }

      ADDONS.add(MeteorClient.ADDON);
      Iterator var8 = FabricLoader.getInstance().getEntrypointContainers("meteor", MeteorAddon.class).iterator();

      while(var8.hasNext()) {
         EntrypointContainer<MeteorAddon> entrypoint = (EntrypointContainer)var8.next();
         ModMetadata metadata = entrypoint.getProvider().getMetadata();

         MeteorAddon addon;
         try {
            addon = (MeteorAddon)entrypoint.getEntrypoint();
         } catch (Throwable var7) {
            throw new RuntimeException("Exception during addon init \"%s\".".formatted(new Object[]{metadata.getName()}), var7);
         }

         addon.name = metadata.getName();
         if (metadata.getAuthors().isEmpty()) {
            throw new RuntimeException("Addon \"%s\" requires at least 1 author to be defined in it's fabric.mod.json. See https://fabricmc.net/wiki/documentation:fabric_mod_json_spec".formatted(new Object[]{addon.name}));
         }

         addon.authors = new String[metadata.getAuthors().size()];
         if (metadata.containsCustomValue("meteor-client:color")) {
            addon.color.parse(metadata.getCustomValue("meteor-client:color").getAsString());
         }

         int i = 0;

         Person author;
         for(Iterator var5 = metadata.getAuthors().iterator(); var5.hasNext(); addon.authors[i++] = author.getName()) {
            author = (Person)var5.next();
         }

         ADDONS.add(addon);
      }

   }
}
