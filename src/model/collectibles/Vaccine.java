package model.collectibles;

import java.awt.Point;
import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import views.Cells;
import views.Map;
import engine.Game;
import model.characters.Hero;
import model.world.Cell;
import model.world.CharacterCell;

public class Vaccine implements Collectible {

	@Override
	public void pickUp(Hero h) {
		h.getVaccineInventory().add(this);
	}

	@Override
	public void use(Hero h) {
		h.getVaccineInventory().remove(this);
		Point p = h.getTarget().getLocation();
		Cell cell = Game.map[p.x][p.y];
		Game.zombies.remove(h.getTarget());
		Hero tba = Game.availableHeroes.get((int) (Math.random() * Game.availableHeroes.size()));
		Game.availableHeroes.remove(tba);
		Game.heroes.add(tba);
		((CharacterCell) cell).setCharacter(tba);
		tba.setLocation(p);
		int x=p.x;
		int y=p.y;
		(Map.CellsArray[y][14-x]).setImage(new ImagePattern(new Image(new File(tba.getName() + ".png").toURI().toString())));
		Cells.curedCells.add(Map.CellsArray[y][14-x]);
		Map.updateRemHeroes();
		//(Map.CellsArray[y][14-x]).setCharacterHandler();
	}

}
