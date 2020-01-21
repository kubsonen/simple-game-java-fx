package pl.com.game.component;

public class Menu extends Wrapper {

    private MenuModel menuModel;

    public Menu() {
        super("game-menu");
        menuModel = getLoader().getController();
    }

    public MenuModel getMenuModel() {
        return menuModel;
    }
}
