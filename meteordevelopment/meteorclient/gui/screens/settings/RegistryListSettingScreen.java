package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_2378;
import net.minecraft.class_3545;

public abstract class RegistryListSettingScreen<T> extends WindowScreen {
   protected final Setting<?> setting;
   protected final Collection<T> collection;
   private final class_2378<T> registry;
   private WTextBox filter;
   private String filterText = "";
   private WTable table;

   public RegistryListSettingScreen(GuiTheme theme, String title, Setting<?> setting, Collection<T> collection, class_2378<T> registry) {
      super(theme, title);
      this.registry = registry;
      this.setting = setting;
      this.collection = collection;
   }

   public void initWidgets() {
      this.filter = (WTextBox)this.add(this.theme.textBox("")).minWidth(400.0D).expandX().widget();
      this.filter.setFocused(true);
      this.filter.action = () -> {
         this.filterText = this.filter.get().trim();
         this.table.clear();
         this.initWidgets(this.registry);
      };
      this.table = (WTable)this.add(this.theme.table()).expandX().widget();
      this.initWidgets(this.registry);
   }

   private void initWidgets(class_2378<T> registry) {
      WTable left = this.abc((pairs) -> {
         registry.forEach((t) -> {
            if (!this.skipValue(t) && !this.collection.contains(t)) {
               int words = Utils.searchInWords(this.getValueName(t), this.filterText);
               int diff = Utils.searchLevenshteinDefault(this.getValueName(t), this.filterText, false);
               if (words > 0 || diff <= this.getValueName(t).length() / 2) {
                  pairs.add(new class_3545(t, -diff));
               }

            }
         });
      }, true, (t) -> {
         this.addValue(registry, t);
         T v = this.getAdditionalValue(t);
         if (v != null) {
            this.addValue(registry, v);
         }

      });
      if (!left.cells.isEmpty()) {
         this.table.add(this.theme.verticalSeparator()).expandWidgetY();
      }

      this.abc((pairs) -> {
         Iterator var2 = this.collection.iterator();

         while(true) {
            Object value;
            int words;
            int diff;
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  value = var2.next();
               } while(this.skipValue(value));

               words = Utils.searchInWords(this.getValueName(value), this.filterText);
               diff = Utils.searchLevenshteinDefault(this.getValueName(value), this.filterText, false);
            } while(words <= 0 && diff > this.getValueName(value).length() / 2);

            pairs.add(new class_3545(value, -diff));
         }
      }, false, (t) -> {
         this.removeValue(registry, t);
         T v = this.getAdditionalValue(t);
         if (v != null) {
            this.removeValue(registry, v);
         }

      });
   }

   private void addValue(class_2378<T> registry, T value) {
      if (!this.collection.contains(value)) {
         this.collection.add(value);
         this.setting.onChanged();
         this.table.clear();
         this.initWidgets(registry);
      }

   }

   private void removeValue(class_2378<T> registry, T value) {
      if (this.collection.remove(value)) {
         this.setting.onChanged();
         this.table.clear();
         this.initWidgets(registry);
      }

   }

   private WTable abc(Consumer<List<class_3545<T, Integer>>> addValues, boolean isLeft, Consumer<T> buttonAction) {
      Cell<WTable> cell = this.table.add(this.theme.table()).top();
      WTable table = (WTable)cell.widget();
      Consumer<T> forEach = (t) -> {
         if (this.includeValue(t)) {
            table.add(this.getValueWidget(t));
            WPressable button = (WPressable)table.add((WWidget)(isLeft ? this.theme.plus() : this.theme.minus())).expandCellX().right().widget();
            button.action = () -> {
               buttonAction.accept(t);
            };
            table.row();
         }
      };
      List<class_3545<T, Integer>> values = new ArrayList();
      addValues.accept(values);
      if (!this.filterText.isEmpty()) {
         values.sort(Comparator.comparingInt((value) -> {
            return -(Integer)value.method_15441();
         }));
      }

      Iterator var8 = values.iterator();

      while(var8.hasNext()) {
         class_3545<T, Integer> pair = (class_3545)var8.next();
         forEach.accept(pair.method_15442());
      }

      if (!table.cells.isEmpty()) {
         cell.expandX();
      }

      return table;
   }

   protected boolean includeValue(T value) {
      return true;
   }

   protected abstract WWidget getValueWidget(T var1);

   protected abstract String getValueName(T var1);

   protected boolean skipValue(T value) {
      return false;
   }

   protected T getAdditionalValue(T value) {
      return null;
   }
}
