package pl.com.game.component;

/**
 * @author JNartowicz
 */
public class MenuHigh extends Wrapper {

    private MenuHighModel menuHighModel;

    public MenuHigh() {
        super("game-menu-highscore");
        menuHighModel = getLoader().getController();
    }

    public MenuHighModel getMenuHighModel() {
        return menuHighModel;
    }
}
