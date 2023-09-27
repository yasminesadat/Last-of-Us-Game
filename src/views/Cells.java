package views;

import java.io.File;
import java.util.ArrayList;

import engine.Game;
import exceptions.GameActionException;
import model.characters.Direction;
import model.characters.Fighter;
import model.characters.Hero;
import model.characters.Medic;
import model.characters.Zombie;
import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Cells extends Rectangle {

	private static final double d = 71; // dimension of cell for 16.1in screen
	public static Hero selected = null;
	static int colPos, rowPos;
	public static ArrayList<Cells> curedCells = new ArrayList<Cells>(); // cured
																		// heroes
																		// to
																		// disable
																		// using
																		// them
																		// until
																		// next
																		// turn
	static HBox hbox1 = new HBox(); // for target setting arrows
	static VBox chooseAction = new VBox();
	static Label selectTarget = new Label(); // label to tell player to select
												// target
	static String selectedButton = ""; // know if attack/useSpecial/cure

	ImagePattern img = null;

	public void setCharacterHandler() {
		this.setOnMouseClicked(this::handleCurrentCharacter);
	}

	public void setImage(ImagePattern img) {
		this.setFill(img);
	}

	public Cells(int x, int y) {
		setWidth(d);
		setHeight(d);
		// relocate(x * d, y * d);
		Cell cell = engine.Game.map[x][y];
		String s = null;
		setFill(cell.isVisible() ? Color.valueOf("ff0000") : Color
				.valueOf("000000"));
		if (isVaccineCell(cell)) {
			s = "Vaccine.png";

		} else if (isSupplyCell(cell)) {
			s = "Supply.png";

		} else if (isZombieCell(cell)) {
			s = "Zombie.png";

		} else if (isHeroCell(cell)) {
			Hero h = (Hero) ((CharacterCell) cell).getCharacter();
			s = h.getName() + ".png";
			this.setOnMouseClicked(this::handleCurrentCharacter); // any
																	// character
																	// click

		}
		if (s != null) {
			Image image = new Image(new File(s).toURI().toString());
			img = new ImagePattern(image);
			this.setFill(img);
		}
	}

	public void update(int x, int y) {
		Cell cell = engine.Game.map[x][y];
		String s = null;
		setFill(cell.isVisible() ? Color.valueOf("ff0000") : Color
				.valueOf("000000"));
		if (isVaccineCell(cell)) {
			s = "Vaccine.png";

		} else if (isSupplyCell(cell)) {
			s = "Supply.png";

		} else if (isZombieCell(cell)) {
			s = "Zombie.png";

		} else if (isHeroCell(cell)) {
			Hero h = (Hero) ((CharacterCell) cell).getCharacter();
			s = h.getName() + ".png";
			this.setOnMouseClicked(this::handleCurrentCharacter); // any
																	// character
																	// click

		}
		if (s != null) {
			Image image = new Image(new File(s).toURI().toString());
			img = new ImagePattern(image);
			this.setFill(img);
		}
	}

	public static void editHeroDetails(Hero h) {
		Map.leftvBox.getChildren().clear();
		// set labels
		Label l = new Label("Selected Hero");
		l.setFont(Font.font("Stencil", 36));
		l.setTextFill(Color.RED);
		Label l1 = new Label(h.getDetails());
		Label l2 = new Label("Current Hp: " + h.getCurrentHp());
		Label l3 = new Label("Actions Available: " + h.getActionsAvailable());
		Label[] array = { l1, l2, l3 };
		for (Label y : array) {
			y.setFont(Font.font("Verdana", 20));
			y.setTextFill(Color.BLACK);
		}

		// set progress bars
		ProgressBar currentHp = new ProgressBar();
		currentHp.setProgress((double) h.getCurrentHp() / h.getMaxHp());
		ProgressBar currentActions = new ProgressBar();
		currentActions.setProgress((double) h.getActionsAvailable()
				/ h.getMaxActions());
		ProgressBar[] pb = { currentHp, currentActions };
		for (ProgressBar b : pb) {
			b.setPrefWidth(380);
			b.setPrefHeight(40);
			b.setStyle("-fx-accent: green;");
		}
		Map.leftvBox.getChildren().addAll(l, l1, l2, currentHp, l3,
				currentActions, chooseAction);
		Map.borderPane.setLeft(Map.leftvBox);

	}

	public void handleCurrentCharacter(MouseEvent event) {
		Node source = (Node) event.getSource();
		int rowIndex = GridPane.getRowIndex(source);
		int columnIndex = GridPane.getColumnIndex(source);
		selected = (Hero) ((CharacterCell) Game.map[14 - rowIndex][columnIndex])
				.getCharacter();
		Map.updateRemHeroes();
		rowPos = rowIndex;
		colPos = columnIndex;
		editHeroDetails(selected);
		HBox hbox = new HBox();
		hbox.setSpacing(20);
		Button left = new Button("LEFT");
		Button right = new Button("RIGHT");
		Button up = new Button("UP");
		Button down = new Button("DOWN");
		Button[] b = { left, right, up, down };
		for (Button button : b) {
			button.setFont(Font.font("Verdana", 15));
			button.setStyle("-fx-text-fill: black;");
			// image constructors at once
			ImageView img = new ImageView(new Image(new File(button.getText()
					+ ".png").toURI().toString()));
			button.setGraphic(img);
			img.setFitWidth(15);
			img.setFitHeight(15);
			// move handling
			button.setOnMouseClicked(this::handleArrowButtons);
		}
		hbox.getChildren().addAll(up, down, left, right);
		Map.leftvBox.getChildren().add(hbox);
		Button attack = new Button("attack");
		Button useSpecial;
		if (!(selected instanceof Medic)) {
			useSpecial = new Button("use special");
			if (selected instanceof Fighter)
				useSpecial.setOnMouseClicked(this::handleSetTarget);
			else
				useSpecial.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {

						selectedButton = "use special";
						callOnSelectedButton();

					}
				});

		} else {
			useSpecial = new Button("self-healing");
			useSpecial.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {

					selected.setTarget(selected);
					try {
						selected.useSpecial();
						editHeroDetails(selected);
						Media media = new Media(new File("useSpecial.mp3")
								.toURI().toString());
						MediaPlayer mediaPlayer = new MediaPlayer(media);
						mediaPlayer.setAutoPlay(true);
					} catch (GameActionException e) {
						Map.warning.getChildren().clear();
						Label header = new Label("Game Warning");
						header.setFont(Font.font("Stencil", 36));
						header.setTextFill(Color.BLACK);
						Label warning = new Label(e.getMessage());
						warning.setFont(Font.font("Verdana", 20));
						warning.setTextFill(Color.RED);
						Map.warning.getChildren().addAll(header, warning);
						Media media = new Media(new File("warning.mp3").toURI()
								.toString());
						MediaPlayer mediaPlayer = new MediaPlayer(media);
						mediaPlayer.setAutoPlay(true);
					}

				}
			});

		}

		useSpecial.setFont(Font.font("Verdana", 15));
		useSpecial.setStyle("-fx-text-fill: black;");
		useSpecial.setPrefWidth(300);
		Button cure = new Button("cure zombie");
		Button[] b1 = { attack, cure };
		for (Button button : b1) {
			button.setFont(Font.font("Verdana", 15));
			button.setStyle("-fx-text-fill: black;");
			button.setPrefWidth(300);
			button.setOnMouseClicked(this::handleSetTarget);
		}
		hbox1.getChildren().clear();
		selectTarget.setText("");
		chooseAction.getChildren().clear();
		chooseAction.setSpacing(20);
		chooseAction.getChildren().addAll(hbox, attack, cure, useSpecial,
				selectTarget, hbox1);
		// Map.leftvBox.getChildren().addAll(chooseAction);

	}

	public void handleArrowButtons(MouseEvent event) {
		Node source = (Node) event.getSource();
		int x = 0, y = 0;
		Direction d = null;
		switch (((Button) source).getText()) {
		case "UP": {
			y = -1;
			d = Direction.UP;
		}
			break;
		case "DOWN": {
			y = 1;
			d = Direction.DOWN;
		}
			break;
		case "LEFT": {
			x = -1;
			d = Direction.LEFT;
		}
			break;
		case "RIGHT": {
			x = 1;
			d = Direction.RIGHT;
		}
			break;
		default:
			break;
		}
		boolean error = false;
		try {
			selected.move(d);

		} catch (GameActionException e) {
			Map.warning.getChildren().clear();
			Label header = new Label("Game Warning");
			header.setFont(Font.font("Stencil", 36));
			header.setTextFill(Color.BLACK);
			Label warning = new Label(e.getMessage());
			warning.setFont(Font.font("Verdana", 20));
			warning.setTextFill(Color.RED);
			Map.warning.getChildren().addAll(header, warning);
			error = true;
			Media media = new Media(new File("warning.mp3").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setAutoPlay(true);
		}
		if (!error)
			moveCharacter(x, y);
	}

	public void moveCharacter(int x, int y) {
		editHeroDetails(selected);
		hbox1.getChildren().clear();
		selectTarget.setText("");
		(Map.CellsArray[colPos][rowPos]).setFill(Color.valueOf("ff0000"));
		(Map.CellsArray[colPos][rowPos]).removeEventHandler(
				MouseEvent.MOUSE_CLICKED, this::handleCurrentCharacter);
		colPos = colPos + x;
		rowPos = rowPos + y;
		(Map.CellsArray[colPos][rowPos]).setImage(this.img);

		(Map.CellsArray[colPos][rowPos])
				.setOnMouseClicked(this::handleCurrentCharacter);
		// update visibility
		for (int i = colPos - 1; i < colPos + 2; i++) {
			for (int j = rowPos - 1; j < rowPos + 2; j++) {
				if (i >= 0 && i < 15 && j >= 0 && j < 15 &&!(curedCells.contains(Map.CellsArray[i][j]))) {
					Map.gridPane.getChildren().remove(Map.CellsArray[i][j]);
					Map.CellsArray[i][j] = new Cells(-j + 14, i);
					Map.gridPane.add(Map.CellsArray[i][j], i, j);

				}
			}
		}
	}

	public void handleSetTarget(MouseEvent event) {
		Node source = (Node) event.getSource();
		selectedButton = ((Button) source).getText();
		selectTarget.setText("Select your target.");
		selectTarget.setFont(Font.font("Impact", 25));
		selectTarget.setTextFill(Color.GREEN);
		Button right = new Button("right");
		Button left = new Button("left");
		Button up = new Button("upwards");
		Button down = new Button("downwards");
		Button[] b = { left, right, up, down };
		hbox1.getChildren().clear();
		hbox1.setSpacing(20);
		for (Button button : b) {
			button.setFont(Font.font("Verdana", 15));
			button.setStyle("-fx-text-fill: black;");
			button.setOnMouseClicked(this::handleTargetArrows);
			hbox1.getChildren().add(button);
		}

	}

	public void handleTargetArrows(MouseEvent event) {
		Node source = (Node) event.getSource();
		int column = colPos, row = rowPos;
		switch (((Button) source).getText()) {
		case "upwards": {
			row += -1;
		}
			break;
		case "downwards": {
			row += 1;
		}
			break;
		case "left": {
			column += -1;
		}
			break;
		case "right": {
			column += 1;
		}
			break;
		default:
			break;
		}
		if (column >= 0 && column < 15 && row >= 0 && row < 15
				&& engine.Game.map[-row + 14][column] instanceof CharacterCell) {
			selected.setTarget(((CharacterCell) engine.Game.map[-row + 14][column])
					.getCharacter());
			callOnSelectedButton();
		} else {
			Map.warning.getChildren().clear();
			Label header = new Label("Game Warning");
			header.setFont(Font.font("Stencil", 36));
			header.setTextFill(Color.BLACK);
			Label warning = new Label("Choose a character cell as target");
			warning.setFont(Font.font("Verdana", 20));
			warning.setTextFill(Color.RED);
			Map.warning.getChildren().addAll(header, warning);
			Media media = new Media(new File("warning.mp3").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setAutoPlay(true);

		}
	}

	public void callOnSelectedButton() {
		switch (selectedButton) {
		case "cure zombie": {
			try {
				selected.cure();
			} catch (GameActionException e) {
				Map.warning.getChildren().clear();
				Label header = new Label("Game Warning");
				header.setFont(Font.font("Stencil", 36));
				header.setTextFill(Color.BLACK);
				Label warning = new Label(e.getMessage());
				warning.setFont(Font.font("Verdana", 20));
				warning.setTextFill(Color.RED);
				Map.warning.getChildren().addAll(header, warning);
				Media media = new Media(new File("warning.mp3").toURI()
						.toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setAutoPlay(true);
			}
		}
			break;
		case "attack": {
			try {
				selected.attack();
				Media media = new Media(new File("attack.mp3").toURI()
						.toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setAutoPlay(true);
			} catch (GameActionException e) {
				Map.warning.getChildren().clear();
				Label header = new Label("Game Warning");
				header.setFont(Font.font("Stencil", 36));
				header.setTextFill(Color.BLACK);
				Label warning = new Label(e.getMessage());
				warning.setFont(Font.font("Verdana", 20));
				warning.setTextFill(Color.RED);
				Map.warning.getChildren().addAll(header, warning);
				Media media = new Media(new File("warning.mp3").toURI()
						.toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setAutoPlay(true);
			}
		}
			break;
		case ("use special"): {
			try {
				selected.useSpecial();
				Media media = new Media(new File("useSpecial.mp3").toURI()
						.toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setAutoPlay(true);
			} catch (GameActionException e) {
				Map.warning.getChildren().clear();
				Label header = new Label("Game Warning");
				header.setFont(Font.font("Stencil", 36));
				header.setTextFill(Color.BLACK);
				Label warning = new Label(e.getMessage());
				warning.setFont(Font.font("Verdana", 20));
				warning.setTextFill(Color.RED);
				Map.warning.getChildren().addAll(header, warning);
				Media media = new Media(new File("warning.mp3").toURI()
						.toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setAutoPlay(true);
			}
			break;
		}
		}
	}

	public boolean isHeroCell(Cell cell) {
		return cell.isVisible() && cell instanceof CharacterCell
				&& ((CharacterCell) cell).getCharacter() instanceof Hero;
	}

	public boolean isZombieCell(Cell cell) {
		return cell.isVisible() && cell instanceof CharacterCell
				&& ((CharacterCell) cell).getCharacter() instanceof Zombie;
	}

	public boolean isSupplyCell(Cell cell) {
		return cell.isVisible() && cell instanceof CollectibleCell
				&& ((CollectibleCell) cell).getCollectible() instanceof Supply;
	}

	public boolean isVaccineCell(Cell cell) {
		return cell.isVisible() && cell instanceof CollectibleCell
				&& ((CollectibleCell) cell).getCollectible() instanceof Vaccine;
	}

}
