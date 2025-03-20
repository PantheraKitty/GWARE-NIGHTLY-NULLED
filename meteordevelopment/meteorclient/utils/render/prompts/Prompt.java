package meteordevelopment.meteorclient.utils.render.prompts;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.class_437;

public abstract class Prompt<T> {
   final GuiTheme theme;
   final class_437 parent;
   String title = "";
   final List<String> messages = new ArrayList();
   boolean dontShowAgainCheckboxVisible = true;
   String id = null;

   protected Prompt(GuiTheme theme, class_437 parent) {
      this.theme = theme;
      this.parent = parent;
   }

   public T title(String title) {
      this.title = title;
      return this;
   }

   public T message(String message) {
      this.messages.add(message);
      return this;
   }

   public T message(String message, Object... args) {
      this.messages.add(String.format(message, args));
      return this;
   }

   public T dontShowAgainCheckboxVisible(boolean visible) {
      this.dontShowAgainCheckboxVisible = visible;
      return this;
   }

   public T id(String from) {
      this.id = from;
      return this;
   }

   public boolean show() {
      if (this.id == null) {
         this.id(this.title);
      }

      if (Config.get().dontShowAgainPrompts.contains(this.id)) {
         return false;
      } else {
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               MeteorClient.mc.method_1507(new Prompt.PromptScreen(this.theme));
            });
         } else {
            MeteorClient.mc.method_1507(new Prompt.PromptScreen(this.theme));
         }

         return true;
      }
   }

   abstract void initialiseWidgets(Prompt<T>.PromptScreen var1);

   protected class PromptScreen extends WindowScreen {
      WCheckbox dontShowAgainCheckbox;
      WHorizontalList list;

      public PromptScreen(GuiTheme theme) {
         super(theme, Prompt.this.title);
         this.parent = Prompt.this.parent;
      }

      public void initWidgets() {
         Iterator var1 = Prompt.this.messages.iterator();

         while(var1.hasNext()) {
            String line = (String)var1.next();
            this.add(this.theme.label(line)).expandX();
         }

         this.add(this.theme.horizontalSeparator()).expandX();
         if (Prompt.this.dontShowAgainCheckboxVisible) {
            WHorizontalList checkboxContainer = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
            this.dontShowAgainCheckbox = (WCheckbox)checkboxContainer.add(this.theme.checkbox(false)).widget();
            checkboxContainer.add(this.theme.label("Don't show this again.")).expandX();
         } else {
            this.dontShowAgainCheckbox = null;
         }

         this.list = (WHorizontalList)this.add(this.theme.horizontalList()).expandX().widget();
         Prompt.this.initialiseWidgets(this);
      }
   }
}
