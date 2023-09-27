package views;

import java.io.File;


import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class selectHero {

	public void getView() {
		Main.gridPane.setHgap(10);
		// add buttons to Main.gridPane
		for (int i = 0; i < engine.Game.availableHeroes.size(); i++) {
			Button b = new Button(engine.Game.availableHeroes.get(i).getName());
			b.setPrefSize(300, 200); // (width,height)
			b.setFont(new Font("Stencil", 25));
			b.setStyle("-fx-text-fill: red;");

			// display hero image upon hovering
			b.setOnMouseEntered(this::handleButtonHoverforSelectHero);

			// select hero to start game
			b.setOnMouseClicked(this::handleButtonClickforSelectHero);

			Main.gridPane.add(b, 0, i); // (element,column,row)
			Label l = new Label(engine.Game.availableHeroes.get(i).getInfo());
			l.setFont(new Font("Verdana", 15));
			l.setTextFill(Color.WHITE);
			Main.gridPane.add(l, 1, i);
		}

		Main.borderPane.setLeft(Main.gridPane);

		Label l = new Label("Choose your hero");
		l.setTextFill(Color.WHITE);
		l.setFont(new Font("Stencil", 40));
		Main.borderPane.setTop(l);
		Main.borderPane.setStyle("-fx-background-color: black;");

	}

	public void handleButtonHoverforSelectHero(MouseEvent event) {
		Node source = (Node) event.getSource();
		Image image = new Image(new File(((Button) source).getText() + ".png")
				.toURI().toString());
		// Creating the image view
		ImageView imageView = new ImageView(image);
		Main.borderPane.setCenter(imageView);
		Media media = new Media(new File("warning.mp3").toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);
	}

	public void handleButtonClickforSelectHero(MouseEvent event) {
		Media media = new Media(new File("chooseHero.mp3").toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);

		Node source = (Node) event.getSource();
		int j = GridPane.getRowIndex((Button) source);
		engine.Game.startGame(engine.Game.availableHeroes.get(j));
		// switch scene
		Image icon = new Image(new File("icon.png").toURI().toString());
        // Add the Image object to the getIcons() list of the stage.
        Main.primaryStage.getIcons().add(icon);
        Main.primaryStage.setTitle("Last of Us");
		Main.primaryStage.setScene(Map.MapScene);
		Main.primaryStage.show();
		Main.primaryStage.setFullScreen(true);
		

	}

}
