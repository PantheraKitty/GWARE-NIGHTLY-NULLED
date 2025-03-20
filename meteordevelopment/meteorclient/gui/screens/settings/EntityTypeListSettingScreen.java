package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_1299;
import net.minecraft.class_3545;
import net.minecraft.class_7923;

public class EntityTypeListSettingScreen extends WindowScreen {
   private final EntityTypeListSetting setting;
   private WVerticalList list;
   private final WTextBox filter;
   private String filterText = "";
   private WSection animals;
   private WSection waterAnimals;
   private WSection monsters;
   private WSection ambient;
   private WSection misc;
   private WTable animalsT;
   private WTable waterAnimalsT;
   private WTable monstersT;
   private WTable ambientT;
   private WTable miscT;
   int hasAnimal = 0;
   int hasWaterAnimal = 0;
   int hasMonster = 0;
   int hasAmbient = 0;
   int hasMisc = 0;

   public EntityTypeListSettingScreen(GuiTheme theme, EntityTypeListSetting setting) {
      super(theme, "Select entities");
      this.setting = setting;
      this.filter = (WTextBox)super.add(theme.textBox("")).minWidth(400.0D).expandX().widget();
      this.filter.setFocused(true);
      this.filter.action = () -> {
         this.filterText = this.filter.get().trim();
         this.list.clear();
         this.initWidgets();
      };
      this.list = (WVerticalList)super.add(theme.verticalList()).expandX().widget();
   }

   public <W extends WWidget> Cell<W> add(W widget) {
      return this.list.add(widget);
   }

   public void initWidgets() {
      this.hasAnimal = this.hasWaterAnimal = this.hasMonster = this.hasAmbient = this.hasMisc = 0;
      Iterator var1 = ((Set)this.setting.get()).iterator();

      while(true) {
         class_1299 entityType;
         do {
            if (!var1.hasNext()) {
               boolean first = this.animals == null;
               List<class_1299<?>> animalsE = new ArrayList();
               WCheckbox animalsC = this.theme.checkbox(this.hasAnimal > 0);
               this.animals = this.theme.section("Animals", this.animals != null && this.animals.isExpanded(), animalsC);
               animalsC.action = () -> {
                  this.tableChecked(animalsE, animalsC.checked);
               };
               Cell<WSection> animalsCell = this.add(this.animals).expandX();
               this.animalsT = (WTable)this.animals.add(this.theme.table()).expandX().widget();
               List<class_1299<?>> waterAnimalsE = new ArrayList();
               WCheckbox waterAnimalsC = this.theme.checkbox(this.hasWaterAnimal > 0);
               this.waterAnimals = this.theme.section("Water Animals", this.waterAnimals != null && this.waterAnimals.isExpanded(), waterAnimalsC);
               waterAnimalsC.action = () -> {
                  this.tableChecked(waterAnimalsE, waterAnimalsC.checked);
               };
               Cell<WSection> waterAnimalsCell = this.add(this.waterAnimals).expandX();
               this.waterAnimalsT = (WTable)this.waterAnimals.add(this.theme.table()).expandX().widget();
               List<class_1299<?>> monstersE = new ArrayList();
               WCheckbox monstersC = this.theme.checkbox(this.hasMonster > 0);
               this.monsters = this.theme.section("Monsters", this.monsters != null && this.monsters.isExpanded(), monstersC);
               monstersC.action = () -> {
                  this.tableChecked(monstersE, monstersC.checked);
               };
               Cell<WSection> monstersCell = this.add(this.monsters).expandX();
               this.monstersT = (WTable)this.monsters.add(this.theme.table()).expandX().widget();
               List<class_1299<?>> ambientE = new ArrayList();
               WCheckbox ambientC = this.theme.checkbox(this.hasAmbient > 0);
               this.ambient = this.theme.section("Ambient", this.ambient != null && this.ambient.isExpanded(), ambientC);
               ambientC.action = () -> {
                  this.tableChecked(ambientE, ambientC.checked);
               };
               Cell<WSection> ambientCell = this.add(this.ambient).expandX();
               this.ambientT = (WTable)this.ambient.add(this.theme.table()).expandX().widget();
               List<class_1299<?>> miscE = new ArrayList();
               WCheckbox miscC = this.theme.checkbox(this.hasMisc > 0);
               this.misc = this.theme.section("Misc", this.misc != null && this.misc.isExpanded(), miscC);
               miscC.action = () -> {
                  this.tableChecked(miscE, miscC.checked);
               };
               Cell<WSection> miscCell = this.add(this.misc).expandX();
               this.miscT = (WTable)this.misc.add(this.theme.table()).expandX().widget();
               Consumer<class_1299<?>> entityTypeForEach = (entityTypex) -> {
                  if (this.setting.filter == null || this.setting.filter.test(entityTypex)) {
                     switch(entityTypex.method_5891()) {
                     case field_6294:
                        animalsE.add(entityTypex);
                        this.addEntityType(this.animalsT, animalsC, entityTypex);
                        break;
                     case field_24460:
                     case field_6300:
                     case field_30092:
                     case field_34447:
                        waterAnimalsE.add(entityTypex);
                        this.addEntityType(this.waterAnimalsT, waterAnimalsC, entityTypex);
                        break;
                     case field_6302:
                        monstersE.add(entityTypex);
                        this.addEntityType(this.monstersT, monstersC, entityTypex);
                        break;
                     case field_6303:
                        ambientE.add(entityTypex);
                        this.addEntityType(this.ambientT, ambientC, entityTypex);
                        break;
                     case field_17715:
                        miscE.add(entityTypex);
                        this.addEntityType(this.miscT, miscC, entityTypex);
                     }
                  }

               };
               if (this.filterText.isEmpty()) {
                  class_7923.field_41177.forEach(entityTypeForEach);
               } else {
                  List<class_3545<class_1299<?>, Integer>> entities = new ArrayList();
                  class_7923.field_41177.forEach((entity) -> {
                     int words = Utils.searchInWords(Names.get(entity), this.filterText);
                     int diff = Utils.searchLevenshteinDefault(Names.get(entity), this.filterText, false);
                     if (words > 0 || diff < Names.get(entity).length() / 2) {
                        entities.add(new class_3545(entity, -diff));
                     }

                  });
                  entities.sort(Comparator.comparingInt((value) -> {
                     return -(Integer)value.method_15441();
                  }));
                  Iterator var19 = entities.iterator();

                  while(var19.hasNext()) {
                     class_3545<class_1299<?>, Integer> pair = (class_3545)var19.next();
                     entityTypeForEach.accept((class_1299)pair.method_15442());
                  }
               }

               if (this.animalsT.cells.isEmpty()) {
                  this.list.cells.remove(animalsCell);
               }

               if (this.waterAnimalsT.cells.isEmpty()) {
                  this.list.cells.remove(waterAnimalsCell);
               }

               if (this.monstersT.cells.isEmpty()) {
                  this.list.cells.remove(monstersCell);
               }

               if (this.ambientT.cells.isEmpty()) {
                  this.list.cells.remove(ambientCell);
               }

               if (this.miscT.cells.isEmpty()) {
                  this.list.cells.remove(miscCell);
               }

               if (first) {
                  int totalCount = (this.hasWaterAnimal + this.waterAnimals.cells.size() + this.monsters.cells.size() + this.ambient.cells.size() + this.misc.cells.size()) / 2;
                  if (totalCount <= 20) {
                     if (!this.animalsT.cells.isEmpty()) {
                        this.animals.setExpanded(true);
                     }

                     if (!this.waterAnimalsT.cells.isEmpty()) {
                        this.waterAnimals.setExpanded(true);
                     }

                     if (!this.monstersT.cells.isEmpty()) {
                        this.monsters.setExpanded(true);
                     }

                     if (!this.ambientT.cells.isEmpty()) {
                        this.ambient.setExpanded(true);
                     }

                     if (!this.miscT.cells.isEmpty()) {
                        this.misc.setExpanded(true);
                     }
                  } else {
                     if (!this.animalsT.cells.isEmpty()) {
                        this.animals.setExpanded(false);
                     }

                     if (!this.waterAnimalsT.cells.isEmpty()) {
                        this.waterAnimals.setExpanded(false);
                     }

                     if (!this.monstersT.cells.isEmpty()) {
                        this.monsters.setExpanded(false);
                     }

                     if (!this.ambientT.cells.isEmpty()) {
                        this.ambient.setExpanded(false);
                     }

                     if (!this.miscT.cells.isEmpty()) {
                        this.misc.setExpanded(false);
                     }
                  }
               }

               return;
            }

            entityType = (class_1299)var1.next();
         } while(this.setting.filter != null && !this.setting.filter.test(entityType));

         switch(entityType.method_5891()) {
         case field_6294:
            ++this.hasAnimal;
            break;
         case field_24460:
         case field_6300:
         case field_30092:
         case field_34447:
            ++this.hasWaterAnimal;
            break;
         case field_6302:
            ++this.hasMonster;
            break;
         case field_6303:
            ++this.hasAmbient;
            break;
         case field_17715:
            ++this.hasMisc;
         }
      }
   }

   private void tableChecked(List<class_1299<?>> entityTypes, boolean checked) {
      boolean changed = false;
      Iterator var4 = entityTypes.iterator();

      while(var4.hasNext()) {
         class_1299<?> entityType = (class_1299)var4.next();
         if (checked) {
            ((Set)this.setting.get()).add(entityType);
            changed = true;
         } else if (((Set)this.setting.get()).remove(entityType)) {
            changed = true;
         }
      }

      if (changed) {
         this.list.clear();
         this.initWidgets();
         this.setting.onChanged();
      }

   }

   private void addEntityType(WTable table, WCheckbox tableCheckbox, class_1299<?> entityType) {
      table.add(this.theme.label(Names.get(entityType)));
      WCheckbox a = (WCheckbox)table.add(this.theme.checkbox(((Set)this.setting.get()).contains(entityType))).expandCellX().right().widget();
      a.action = () -> {
         if (a.checked) {
            ((Set)this.setting.get()).add(entityType);
            switch(entityType.method_5891()) {
            case field_6294:
               if (this.hasAnimal == 0) {
                  tableCheckbox.checked = true;
               }

               ++this.hasAnimal;
               break;
            case field_24460:
            case field_6300:
            case field_30092:
            case field_34447:
               if (this.hasWaterAnimal == 0) {
                  tableCheckbox.checked = true;
               }

               ++this.hasWaterAnimal;
               break;
            case field_6302:
               if (this.hasMonster == 0) {
                  tableCheckbox.checked = true;
               }

               ++this.hasMonster;
               break;
            case field_6303:
               if (this.hasAmbient == 0) {
                  tableCheckbox.checked = true;
               }

               ++this.hasAmbient;
               break;
            case field_17715:
               if (this.hasMisc == 0) {
                  tableCheckbox.checked = true;
               }

               ++this.hasMisc;
            }
         } else if (((Set)this.setting.get()).remove(entityType)) {
            switch(entityType.method_5891()) {
            case field_6294:
               --this.hasAnimal;
               if (this.hasAnimal == 0) {
                  tableCheckbox.checked = false;
               }
               break;
            case field_24460:
            case field_6300:
            case field_30092:
            case field_34447:
               --this.hasWaterAnimal;
               if (this.hasWaterAnimal == 0) {
                  tableCheckbox.checked = false;
               }
               break;
            case field_6302:
               --this.hasMonster;
               if (this.hasMonster == 0) {
                  tableCheckbox.checked = false;
               }
               break;
            case field_6303:
               --this.hasAmbient;
               if (this.hasAmbient == 0) {
                  tableCheckbox.checked = false;
               }
               break;
            case field_17715:
               --this.hasMisc;
               if (this.hasMisc == 0) {
                  tableCheckbox.checked = false;
               }
            }
         }

         this.setting.onChanged();
      };
      table.row();
   }
}
