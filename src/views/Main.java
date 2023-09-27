package views;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	static Stage primaryStage;
	static BorderPane borderPane = new BorderPane();
	static GridPane gridPane = new GridPane();

	public Main() {
		primaryStage = new Stage();
	}

	public void init() throws IOException {
		engine.Game.loadHeroes("Heroes.csv");
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		Image icon = new Image(new File("icon.png").toURI().toString());
		// Add the Image object to the getIcons() list of the stage
		primaryStage.getIcons().add(icon);
		HBox box = new HBox();
		Button btn = new Button("Start Game");
		btn.setStyle("-fx-background-color: black;-fx-border-color: green;-fx-text-fill: red;");
		btn.setFont(Font.font("Stencil", 70));
		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Image icon = new Image(new File("icon.png").toURI().toString());
				Main.primaryStage.getIcons().add(icon);
				Main.primaryStage.setTitle("Last of Us");
				selectHero sh=new selectHero();
				sh.getView();
				// Set the selectHeroScene (BorderPane inside it GridPane) as the scene
				// of the Stage
				primaryStage.setScene(new Scene(borderPane));
				primaryStage.setFullScreen(true);

				// Show the Stage
				primaryStage.show();
			}

		});
		Image img = new Image(new File("start.jpg").toURI().toString());
		box.setBackground(new Background(new BackgroundImage(img,
				BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
		box.getChildren().add(btn);
		box.setAlignment(Pos.CENTER);
		primaryStage.setScene(new Scene(box));

		primaryStage.setFullScreen(true);
		Media media = new Media(new File("start.mp3").toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);

		// Show the Stage
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	
}
