package meteordevelopment.meteorclient.gui.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.class_1802;

public class ModulesScreen extends TabScreen {
   private ModulesScreen.WCategoryController controller;
   private final Map<Module, WWidget> moduleWidges = new HashMap();
   private final Map<Category, Integer> searchCategoryBuckets = new HashMap();

   public ModulesScreen(GuiTheme theme) {
      super(theme, (Tab)Tabs.get().getFirst());
   }

   public void initWidgets() {
      this.controller = (ModulesScreen.WCategoryController)this.add(new ModulesScreen.WCategoryController()).widget();
      WVerticalList help = (WVerticalList)this.add(this.theme.verticalList()).pad(4.0D).bottom().widget();
      help.add(this.theme.label("Left click - Toggle module"));
      help.add(this.theme.label("Right click - Open module settings"));
   }

   protected void method_25426() {
      super.method_25426();
      this.controller.refresh();
   }

   protected WWindow createCategory(WContainer c, Category category) {
      WWindow w = this.theme.window(category.name);
      w.id = category.name;
      w.padding = 0.0D;
      w.spacing = 0.0D;
      if (this.theme.categoryIcons()) {
         w.beforeHeaderInit = (wContainer) -> {
            wContainer.add(this.theme.item(category.icon)).pad(2.0D);
         };
      }

      c.add(w);
      w.view.scrollOnlyWhenMouseOver = true;
      w.view.hasScrollBar = false;
      w.view.spacing = 0.0D;
      Iterator var4 = Modules.get().getGroup(category).iterator();

      while(var4.hasNext()) {
         Module module = (Module)var4.next();
         WWidget wid = this.theme.module(module);
         w.add(wid).expandX();
         this.moduleWidges.put(module, wid);
      }

      return w;
   }

   public void searchSetHighlight(String text, Map<Module, Integer> modules, Module module, WWidget widget) {
      if (text.isEmpty()) {
         widget.highlight = false;
         widget.deactivate = false;
      } else {
         if (modules.containsKey(module)) {
            int score = (Integer)modules.get(module);
            if (score < 10) {
               widget.highlight = true;
               widget.deactivate = false;
            } else {
               widget.highlight = false;
               widget.deactivate = true;
            }
         } else {
            widget.highlight = false;
            widget.deactivate = true;
         }

      }
   }

   protected void runSearchW(String text) {
      this.searchCategoryBuckets.clear();
      Map<Module, Integer> modules = Modules.get().searchTitles(text);
      if (!modules.isEmpty()) {
         Iterator var3 = this.moduleWidges.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<Module, WWidget> moduleWidget = (Entry)var3.next();
            if (!modules.isEmpty()) {
               this.searchSetHighlight(text, modules, (Module)moduleWidget.getKey(), (WWidget)moduleWidget.getValue());
            }
         }

      }
   }

   protected WWindow createSearch(WContainer c) {
      WWindow w = this.theme.window("Search");
      w.id = "search";
      if (this.theme.categoryIcons()) {
         w.beforeHeaderInit = (wContainer) -> {
            wContainer.add(this.theme.item(class_1802.field_8251.method_7854())).pad(2.0D);
         };
      }

      c.add(w);
      w.view.scrollOnlyWhenMouseOver = true;
      w.view.hasScrollBar = false;
      WView var10000 = w.view;
      var10000.maxHeight -= 20.0D;
      WTextBox text = (WTextBox)w.add(this.theme.textBox("")).minWidth(140.0D).expandX().widget();
      text.setFocused(true);
      text.action = () -> {
         this.runSearchW(text.get());
      };
      return w;
   }

   protected Cell<WWindow> createFavorites(WContainer c) {
      boolean hasFavorites = Modules.get().getAll().stream().anyMatch((module) -> {
         return module.favorite;
      });
      if (!hasFavorites) {
         return null;
      } else {
         WWindow w = this.theme.window("Favorites");
         w.id = "favorites";
         w.padding = 0.0D;
         w.spacing = 0.0D;
         if (this.theme.categoryIcons()) {
            w.beforeHeaderInit = (wContainer) -> {
               wContainer.add(this.theme.item(class_1802.field_8137.method_7854())).pad(2.0D);
            };
         }

         Cell<WWindow> cell = c.add(w);
         w.view.scrollOnlyWhenMouseOver = true;
         w.view.hasScrollBar = false;
         w.view.spacing = 0.0D;
         this.createFavoritesW(w);
         return cell;
      }
   }

   protected boolean createFavoritesW(WWindow w) {
      List<Module> modules = new ArrayList();
      Iterator var3 = Modules.get().getAll().iterator();

      Module module;
      while(var3.hasNext()) {
         module = (Module)var3.next();
         if (module.favorite) {
            modules.add(module);
         }
      }

      modules.sort((o1, o2) -> {
         return String.CASE_INSENSITIVE_ORDER.compare(o1.name, o2.name);
      });
      var3 = modules.iterator();

      while(var3.hasNext()) {
         module = (Module)var3.next();
         w.add(this.theme.module(module)).expandX();
      }

      return !modules.isEmpty();
   }

   public boolean toClipboard() {
      return NbtUtils.toClipboard(Modules.get());
   }

   public boolean fromClipboard() {
      return NbtUtils.fromClipboard((System)Modules.get());
   }

   public void reload() {
   }

   protected class WCategoryController extends WContainer {
      public final List<WWindow> windows = new ArrayList();
      private Cell<WWindow> favorites;

      public void init() {
         Iterator var1 = Modules.loopCategories().iterator();

         while(var1.hasNext()) {
            Category category = (Category)var1.next();
            this.windows.add(ModulesScreen.this.createCategory(this, category));
         }

         this.windows.add(ModulesScreen.this.createSearch(this));
         this.refresh();
      }

      protected void refresh() {
         if (this.favorites == null) {
            this.favorites = ModulesScreen.this.createFavorites(this);
            if (this.favorites != null) {
               this.windows.add((WWindow)this.favorites.widget());
            }
         } else {
            ((WWindow)this.favorites.widget()).clear();
            if (!ModulesScreen.this.createFavoritesW((WWindow)this.favorites.widget())) {
               this.remove(this.favorites);
               this.windows.remove(this.favorites.widget());
               this.favorites = null;
            }
         }

      }

      protected void onCalculateWidgetPositions() {
         double pad = this.theme.scale(4.0D);
         double h = this.theme.scale(40.0D);
         double x = this.x + pad;
         double y = this.y;

         Cell cell;
         for(Iterator var9 = this.cells.iterator(); var9.hasNext(); x += cell.width + pad) {
            cell = (Cell)var9.next();
            double windowWidth = (double)Utils.getWindowWidth();
            double windowHeight = (double)Utils.getWindowHeight();
            if (x + cell.width > windowWidth) {
               x += pad;
               y += h;
            }

            if (x > windowWidth) {
               x = windowWidth / 2.0D - cell.width / 2.0D;
               if (x < 0.0D) {
                  x = 0.0D;
               }
            }

            if (y > windowHeight) {
               y = windowHeight / 2.0D - cell.height / 2.0D;
               if (y < 0.0D) {
                  y = 0.0D;
               }
            }

            cell.x = x;
            cell.y = y;
            cell.width = cell.widget().width;
            cell.height = cell.widget().height;
            cell.alignWidget();
         }

      }
   }
}
