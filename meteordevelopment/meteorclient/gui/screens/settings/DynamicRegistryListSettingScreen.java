package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_151;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3545;
import net.minecraft.class_5321;
import net.minecraft.class_6880.class_6883;

public abstract class DynamicRegistryListSettingScreen<E> extends WindowScreen {
   protected final Setting<?> setting;
   protected final Collection<class_5321<E>> collection;
   private final class_5321<class_2378<E>> registryKey;
   private final Optional<class_2378<E>> registry;
   private WTextBox filter;
   private String filterText = "";
   private WTable table;

   public DynamicRegistryListSettingScreen(GuiTheme theme, String title, Setting<?> setting, Collection<class_5321<E>> collection, class_5321<class_2378<E>> registryKey) {
      super(theme, title);
      this.registryKey = registryKey;
      this.registry = Optional.ofNullable(class_310.method_1551().method_1562()).flatMap((networkHandler) -> {
         return networkHandler.method_29091().method_33310(registryKey);
      });
      this.setting = setting;
      this.collection = collection;
   }

   public void initWidgets() {
      this.filter = (WTextBox)this.add(this.theme.textBox("")).minWidth(400.0D).expandX().widget();
      this.filter.setFocused(true);
      this.filter.action = () -> {
         this.filterText = this.filter.get().trim();
         this.table.clear();
         this.generateWidgets();
      };
      this.table = (WTable)this.add(this.theme.table()).expandX().widget();
      this.generateWidgets();
   }

   private void generateWidgets() {
      WTable left = this.abc((pairs) -> {
         this.registry.ifPresent((registry) -> {
            registry.method_40270().map(class_6883::method_40230).filter(Optional::isPresent).map(Optional::get).forEach((t) -> {
               if (!this.skipValue(t) && !this.collection.contains(t)) {
                  int words = Utils.searchInWords(this.getValueName(t), this.filterText);
                  int diff = Utils.searchLevenshteinDefault(this.getValueName(t), this.filterText, false);
                  if (words > 0 || diff <= this.getValueName(t).length() / 2) {
                     pairs.add(new class_3545(t, -diff));
                  }

               }
            });
         });
      }, true, (t) -> {
         this.addValue(t);
         class_5321<E> v = this.getAdditionalValue(t);
         if (v != null) {
            this.addValue(v);
         }

      });
      if (!left.cells.isEmpty()) {
         left.add(this.theme.horizontalSeparator()).expandX();
         left.row();
      }

      WHorizontalList manualEntry = (WHorizontalList)left.add(this.theme.horizontalList()).expandX().widget();
      WTextBox textBox = (WTextBox)manualEntry.add(this.theme.textBox("minecraft:")).expandX().minWidth(120.0D).widget();
      ((WPlus)manualEntry.add(this.theme.plus()).expandCellX().right().widget()).action = () -> {
         String entry = textBox.get().trim();

         try {
            class_2960 id = entry.contains(":") ? class_2960.method_60654(entry) : class_2960.method_60656(entry);
            this.addValue(class_5321.method_29179(this.registryKey, id));
         } catch (class_151 var4) {
         }

      };
      this.table.add(this.theme.verticalSeparator()).expandWidgetY();
      this.abc((pairs) -> {
         Iterator var2 = this.collection.iterator();

         while(true) {
            class_5321 value;
            int words;
            int diff;
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  value = (class_5321)var2.next();
               } while(this.skipValue(value));

               words = Utils.searchInWords(this.getValueName(value), this.filterText);
               diff = Utils.searchLevenshteinDefault(this.getValueName(value), this.filterText, false);
            } while(words <= 0 && diff > this.getValueName(value).length() / 2);

            pairs.add(new class_3545(value, -diff));
         }
      }, false, (t) -> {
         this.removeValue(t);
         class_5321<E> v = this.getAdditionalValue(t);
         if (v != null) {
            this.removeValue(v);
         }

      });
   }

   private void addValue(class_5321<E> value) {
      if (!this.collection.contains(value)) {
         this.collection.add(value);
         this.setting.onChanged();
         this.table.clear();
         this.generateWidgets();
      }

   }

   private void removeValue(class_5321<E> value) {
      if (this.collection.remove(value)) {
         this.setting.onChanged();
         this.table.clear();
         this.generateWidgets();
      }

   }

   private WTable abc(Consumer<List<class_3545<class_5321<E>, Integer>>> addValues, boolean isLeft, Consumer<class_5321<E>> buttonAction) {
      Cell<WTable> cell = this.table.add(this.theme.table()).top();
      WTable table = (WTable)cell.widget();
      Consumer<class_5321<E>> forEach = (t) -> {
         if (this.includeValue(t)) {
            table.add(this.getValueWidget(t));
            WPressable button = (WPressable)table.add((WWidget)(isLeft ? this.theme.plus() : this.theme.minus())).expandCellX().right().widget();
            button.action = () -> {
               buttonAction.accept(t);
            };
            table.row();
         }
      };
      List<class_3545<class_5321<E>, Integer>> values = new ArrayList();
      addValues.accept(values);
      if (!this.filterText.isEmpty()) {
         values.sort(Comparator.comparingInt((value) -> {
            return -(Integer)value.method_15441();
         }));
      }

      Iterator var8 = values.iterator();

      while(var8.hasNext()) {
         class_3545<class_5321<E>, Integer> pair = (class_3545)var8.next();
         forEach.accept((class_5321)pair.method_15442());
      }

      if (!table.cells.isEmpty()) {
         cell.expandX();
      }

      return table;
   }

   protected boolean includeValue(class_5321<E> value) {
      return true;
   }

   protected abstract WWidget getValueWidget(class_5321<E> var1);

   protected abstract String getValueName(class_5321<E> var1);

   protected boolean skipValue(class_5321<E> value) {
      return false;
   }

   protected class_5321<E> getAdditionalValue(class_5321<E> value) {
      return null;
   }
}
