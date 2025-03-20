package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.class_2561;
import net.minecraft.class_2572;
import net.minecraft.class_2588;
import net.minecraft.class_5250;
import net.minecraft.class_7417;
import net.minecraft.class_7743;
import net.minecraft.class_8828.class_2585;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_7743.class})
public abstract class AbstractSignEditScreenMixin {
   @ModifyExpressionValue(
      method = {"<init>(Lnet/minecraft/block/entity/SignBlockEntity;ZZLnet/minecraft/text/Text;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/stream/IntStream;mapToObj(Ljava/util/function/IntFunction;)Ljava/util/stream/Stream;"
)}
   )
   private Stream<class_2561> modifyTranslatableText(Stream<class_2561> original) {
      return original.map(this::modifyText);
   }

   @Unique
   private class_2561 modifyText(class_2561 message) {
      class_5250 modified = class_5250.method_43477(message.method_10851());
      class_7417 var4 = message.method_10851();
      String key;
      if (var4 instanceof class_2572) {
         class_2572 content = (class_2572)var4;
         key = content.method_10901();
         if (key.contains("meteor-client")) {
            modified = class_5250.method_43477(new class_2585(key));
         }
      }

      var4 = message.method_10851();
      if (var4 instanceof class_2588) {
         class_2588 content = (class_2588)var4;
         key = content.method_11022();
         if (key.contains("meteor-client")) {
            modified = class_5250.method_43477(new class_2585(key));
         }
      }

      modified.method_10862(message.method_10866());
      Iterator var6 = message.method_10855().iterator();

      while(var6.hasNext()) {
         class_2561 sibling = (class_2561)var6.next();
         modified.method_10852(this.modifyText(sibling));
      }

      return modified;
   }
}
