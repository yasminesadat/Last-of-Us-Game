package views;

import java.io.File;

import exceptions.GameActionException;
import model.characters.Hero;
import model.characters.Medic;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Map {
	public static GridPane gridPane = new GridPane(); // map
	public static BorderPane borderPane = new BorderPane(); // left and right of
															// map
	public static VBox leftvBox = new VBox(); // left side
	public static VBox warning = new VBox(); // Exception handling at the right
	public static BorderPane rightBorderPane = new BorderPane(); // right side
	public static StackPane remHeroes = new StackPane(); // for right
															// center(hbox and
															// vbox nested)
	public static Cells[][] CellsArray = new Cells[15][15]; // access all grid
															// cells
	static Scene MapScene = new Scene(getBorderPane());

	public static void createGrid() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				Cells c = new Cells(i, j);
				CellsArray[j][14 - i] = c;
				gridPane.add(c, j, 14 - i);
			}
		}
	}

	public static void updateGrid() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				CellsArray[j][14 - i].update(i, j);
			}
		}
	}

	

	public static void createBorderPane() {
		createGrid();
		warning.setSpacing(10);
		VBox[] vb = { leftvBox, warning };
		for (VBox v : vb) {
			v.setPrefWidth(400);
			v.setPrefHeight(400);
			v.setSpacing(20);
			v.setBackground(new Background(new BackgroundFill(Color.GREY,
					CornerRadii.EMPTY, Insets.EMPTY)));
		}

		Button endTurn = new Button("end turn");
		endTurn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				try {
					engine.Game.endTurn();
					Media media = new Media(new File("endTurn.mp3").toURI().toString());
					MediaPlayer mediaPlayer = new MediaPlayer(media);
					mediaPlayer.setAutoPlay(true);
					
				} catch (GameActionException e) {

				}
				Map.checkGameOver();

			}
		});
		endTurn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		endTurn.setFont(Font.font("Impact", 40));

		leftvBox.setBackground(new Background(new BackgroundFill(Color.GREY,
				CornerRadii.EMPTY, Insets.EMPTY)));
		gridPane.setBackground(new Background(new BackgroundFill(Color.GREY,
				CornerRadii.EMPTY, Insets.EMPTY)));
		gridPane.setHgap(1);
		gridPane.setVgap(1);
		rightBorderPane.setBackground(new Background(new BackgroundFill(
				Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
		rightBorderPane.setTop(warning); 
		rightBorderPane.setCenter(remHeroes);
		rightBorderPane.setBottom(endTurn);
		borderPane.setCenter(gridPane);
		borderPane.setLeft(leftvBox);
		borderPane.setRight(rightBorderPane);
	}

	public static BorderPane getBorderPane() {
		createBorderPane();
		return borderPane;
	}

	public static void updateRemHeroes() {
		remHeroes.getChildren().clear();
		if (engine.Game.heroes.size() > 1) {
			VBox rem = new VBox();
			rem.setSpacing(15);
			Label label = new Label("Available Heroes");
			label.setFont(Font.font("Impact", 25));
			label.setTextFill(Color.RED);
			rem.getChildren().add(label);
			for (Hero h : engine.Game.heroes) {
				if (h != Cells.selected) {
					HBox box = new HBox();
					Label l = new Label(h.getRemDetails());
					l.setFont(Font.font("Verdana", 15));
					l.setTextFill(Color.BLACK);
					box.getChildren().add(l);
					if (Cells.selected instanceof Medic) {
						Button b = new Button("heal");
						b.setFont(Font.font("Impact", 14));
						b.setOnMouseClicked(new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								try {
									Cells.selected.setTarget(h);
									Cells.selected.useSpecial();
									Media media = new Media(new File("useSpecial.mp3").toURI().toString());
					        		MediaPlayer mediaPlayer = new MediaPlayer(media);
					                mediaPlayer.setAutoPlay(true);
									updateRemHeroes();
								} catch (GameActionException e) {
									Map.warning.getChildren().clear();
									Label header = new Label("Game Warning");
									header.setFont(Font.font("Stencil", 36));
									header.setTextFill(Color.BLACK);
									Label warning = new Label(e.getMessage());
									warning.setFont(Font.font("Verdana", 20));
									warning.setTextFill(Color.RED);
									Map.warning.getChildren().addAll(header,
											warning);
									Media media = new Media(new File(
											"warning.mp3").toURI().toString());
									MediaPlayer mediaPlayer = new MediaPlayer(
											media);
									mediaPlayer.setAutoPlay(true);

								}

							}
						});
						box.getChildren().add(b);
					}

					rem.getChildren().add(box);
				}
			}

			remHeroes.getChildren().add(rem);
		}
	}

	public static void checkGameOver() {
		
		if (engine.Game.checkGameOver()) {
			Media media = new Media(new File("gameOver.mp3").toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setAutoPlay(true);
			VBox box = new VBox();
			box.setAlignment(Pos.CENTER);
			Button btn = new Button("Exit");
			btn.setStyle("-fx-background-color: black;-fx-border-color: grey;-fx-text-fill: white;");
			btn.setFont(Font.font("Impact", 40));
			btn.setOnAction(e -> Platform.exit());
			Label text = new Label();
			text.setFont(Font.font("Stencil", 100));
			if (engine.Game.checkWin()) {
				text.setText("You won!");
				text.setTextFill(Color.GREEN);
			} else {
				text.setText("You lost!");
				text.setTextFill(Color.RED);
			}
			box.getChildren().addAll(text, btn);
		    Image img=new Image(new File("end.jpg").toURI().toString());
			box.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
			Image icon = new Image(new File("icon.png").toURI().toString());
			Main.primaryStage.getIcons().add(icon);
			Main.primaryStage.setTitle("Last of Us");
			Main.primaryStage.setScene(new Scene(box));
			Main.primaryStage.setFullScreen(true);
		}
	}

}
