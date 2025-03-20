package meteordevelopment.meteorclient.utils.render.prompts;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.class_437;

public class YesNoPrompt extends Prompt<YesNoPrompt> {
   private Runnable onYes = () -> {
   };
   private Runnable onNo = () -> {
   };

   private YesNoPrompt(GuiTheme theme, class_437 parent) {
      super(theme, parent);
   }

   public static YesNoPrompt create() {
      return new YesNoPrompt(GuiThemes.get(), MeteorClient.mc.field_1755);
   }

   public static YesNoPrompt create(GuiTheme theme, class_437 parent) {
      return new YesNoPrompt(theme, parent);
   }

   public YesNoPrompt onYes(Runnable action) {
      this.onYes = action;
      return this;
   }

   public YesNoPrompt onNo(Runnable action) {
      this.onNo = action;
      return this;
   }

   protected void initialiseWidgets(Prompt<YesNoPrompt>.PromptScreen screen) {
      WButton yesButton = (WButton)screen.list.add(this.theme.button("Yes")).expandX().widget();
      yesButton.action = () -> {
         if (screen.dontShowAgainCheckbox != null && screen.dontShowAgainCheckbox.checked) {
            Config.get().dontShowAgainPrompts.add(this.id);
         }

         this.onYes.run();
         screen.method_25419();
      };
      WButton noButton = (WButton)screen.list.add(this.theme.button("No")).expandX().widget();
      noButton.action = () -> {
         if (screen.dontShowAgainCheckbox != null && screen.dontShowAgainCheckbox.checked) {
            Config.get().dontShowAgainPrompts.add(this.id);
         }

         this.onNo.run();
         screen.method_25419();
      };
   }
}
