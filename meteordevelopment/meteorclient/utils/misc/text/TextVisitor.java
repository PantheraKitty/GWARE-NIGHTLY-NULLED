package meteordevelopment.meteorclient.utils.misc.text;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_7417;
import net.minecraft.class_8828;

@FunctionalInterface
public interface TextVisitor<T> {
   Optional<T> accept(class_2561 var1, class_2583 var2, String var3);

   static <T> Optional<T> visit(class_2561 text, TextVisitor<T> visitor, class_2583 baseStyle) {
      Queue<class_2561> queue = collectSiblings(text);
      return text.method_27658((style, string) -> {
         return visitor.accept((class_2561)queue.remove(), style, string);
      }, baseStyle);
   }

   static ArrayDeque<class_2561> collectSiblings(class_2561 text) {
      ArrayDeque<class_2561> queue = new ArrayDeque();
      collectSiblings(text, queue);
      return queue;
   }

   private static void collectSiblings(class_2561 text, Queue<class_2561> queue) {
      label20: {
         class_7417 var3 = text.method_10851();
         if (var3 instanceof class_8828) {
            class_8828 ptc = (class_8828)var3;
            if (ptc.comp_737().isEmpty()) {
               break label20;
            }
         }

         queue.add(text);
      }

      Iterator var4 = text.method_10855().iterator();

      while(var4.hasNext()) {
         class_2561 sibling = (class_2561)var4.next();
         collectSiblings(sibling, queue);
      }

   }
}
