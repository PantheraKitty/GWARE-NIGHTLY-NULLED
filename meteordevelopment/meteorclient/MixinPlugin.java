package meteordevelopment.meteorclient;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.asm.Asm;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import sun.misc.Unsafe;

public class MixinPlugin implements IMixinConfigPlugin {
   private static final String mixinPackage = "meteordevelopment.meteorclient.mixin";
   private static boolean loaded;
   private static boolean isOriginsPresent;
   private static boolean isIndigoPresent;
   public static boolean isSodiumPresent;
   private static boolean isLithiumPresent;
   public static boolean isIrisPresent;
   private static boolean isVFPPresent;

   public void onLoad(String mixinPackage) {
      if (!loaded) {
         try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> classLoaderClass = classLoader.getClass();
            Field delegateField = classLoaderClass.getDeclaredField("delegate");
            delegateField.setAccessible(true);
            Object delegate = delegateField.get(classLoader);
            Class<?> delegateClass = delegate.getClass();
            Field mixinTransformerField = delegateClass.getDeclaredField("mixinTransformer");
            mixinTransformerField.setAccessible(true);
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe)unsafeField.get((Object)null);
            Asm.init();
            Asm.Transformer mixinTransformer = (Asm.Transformer)unsafe.allocateInstance(Asm.Transformer.class);
            mixinTransformer.delegate = (IMixinTransformer)mixinTransformerField.get(delegate);
            mixinTransformerField.set(delegate, mixinTransformer);
         } catch (IllegalAccessException | InstantiationException | NoSuchFieldException var11) {
            var11.printStackTrace();
         }

         isIndigoPresent = FabricLoader.getInstance().isModLoaded("fabric-renderer-indigo");
         isOriginsPresent = FabricLoader.getInstance().isModLoaded("origins");
         isSodiumPresent = FabricLoader.getInstance().isModLoaded("sodium");
         isLithiumPresent = FabricLoader.getInstance().isModLoaded("lithium");
         isIrisPresent = FabricLoader.getInstance().isModLoaded("iris");
         isVFPPresent = FabricLoader.getInstance().isModLoaded("viafabricplus");
         loaded = true;
      }
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (!mixinClassName.startsWith("meteordevelopment.meteorclient.mixin")) {
         throw new RuntimeException("Mixin " + mixinClassName + " is not in the mixin package");
      } else if (mixinClassName.endsWith("PlayerEntityRendererMixin")) {
         return !isOriginsPresent;
      } else if (mixinClassName.startsWith("meteordevelopment.meteorclient.mixin.sodium")) {
         return isSodiumPresent;
      } else if (mixinClassName.startsWith("meteordevelopment.meteorclient.mixin.indigo")) {
         return isIndigoPresent;
      } else if (mixinClassName.startsWith("meteordevelopment.meteorclient.mixin.lithium")) {
         return isLithiumPresent;
      } else {
         return mixinClassName.startsWith("meteordevelopment.meteorclient.mixin.viafabricplus") ? isVFPPresent : true;
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
