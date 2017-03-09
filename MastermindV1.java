import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import java.util.*;
import java.lang.Math;
import static java.lang.Math.random;
import java.lang.Number;

public class MastermindV1 extends Application {

	private GridPane grid = addGridPane(); // Grille central du MasterMind (La ou on pose les Boule)
	private GridPane grid2 = addOtherGridPane(); // Grille secondaire ou pose la correction (les rectangles)
	public static int[] reponse = new int[8]; // Reponse -> C'est elle généré aléatoirement
	public int[] reponse2 = new int[8]; // copie de la reponse qui servira pour les fonctions avenirs (Pour les test de chaque ligne par exemple) Si on utilise cette copie c'est parce qu'elle va etre remplie de valeur telle que -1, -2, -3 pour les fonctions de detections
	static int row = 10; // Le nombre de ligne du mastermind, Il ne peut pas être modifié par des soucis de taille de la fenetre qui est dificillement agrandissable en raison des animations
	static int column = 4; // Max 8 // Le nombre de colone, par soucis de taille de la fentrètre il ne peut pas depasser 8 // (de tout façon, plus de 8 colone serait quasiment injouable)
	static int nbCouleur = 4; // Max 8 // Le nombre de couleur actuel, il ne peut depasser 8 
	public int[] proposition = new int[8]; // C'est le tableau qui est remplie par l'utilisateur via les bouton couleurs, c'est lui qui sera comparé la reponse 2
	static int acRow = 0; // La ligne actuel // c'est la ligne qui permet de naviguer dans les grid pane
	static int acColumn = 0; // La colone actuel // C'est la colone qui permet de naviguer dans les grid pane
	static Stage stage; // C'est la fentre... En variable global... pourquoi pas ?

	public static void main(String[] args) {
		launch(MastermindV1.class, args);
	}

	@Override
	public void start(Stage stage) {
		this.stage=stage;
		stage.setResizable(false); // Empecher l'utilisateur de redimensionner la fenetre
		menu(); // Fonction Menu voir Ligne suivante
	}

// 00000000000000000000000000000000000000000000000000000000000000000000000000000
// 0000                               MENU                                  0000
// 00000000000000000000000000000000000000000000000000000000000000000000000000000


	private void menu(){
		Group root = new Group();
		Scene scene = new Scene(root, 530, 550);
		BorderPane borderMenu = new BorderPane();
		VBox vbox = addVBoxMenu();
		borderMenu.setCenter(vbox);
		HBox hboxTitle = mastermindTitle();
		borderMenu.setTop(hboxTitle);
		HBox hbox = addHboxMenu();
		borderMenu.setBottom(hbox);
		stage.setScene(scene);

//  ---- Début de : Animation de Fond du Menu ----

		int u = 0; 
		Group circles = new Group(); // Va creer 42 cercle que l'on va mettre le group cercles
		for (int i = 0; i < 42; i++){
			Circle circle = new Circle();
			circle.setRadius(20.0f);
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(2);
			switch(u){
				case 0:circle.setFill(Color.RED);
				break;
				case 1: circle.setFill(Color.GREEN);
				break;
				case 2: circle.setFill(Color.BLUE);
				break;
				case 3: circle.setFill(Color.MAGENTA);
				break;
				case 4: circle.setFill(Color.YELLOW);
				break;
				case 5: circle.setFill(Color.LIGHTSEAGREEN);
				break;
				case 6: circle.setFill(Color.WHITE);
				break;
			}
			if(u>6)
				u = 0;
			else
				u++;
			circles.getChildren().add(circle);
		}

		Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(), // Le rectangle de fond de la taille de la fenetre, en dégrader
			new LinearGradient(1f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new
				Stop[]{
					new Stop(0, Color.BLACK),
					new Stop(0.14, Color.RED),
					new Stop(0.28, Color.YELLOW),
					new Stop(0.43, Color.GREEN),
					new Stop(0.57, Color.LIGHTSEAGREEN),
					new Stop(0.71, Color.BLUE),
					new Stop(0.85, Color.MAGENTA),
					new Stop(1, Color.WHITE),
				}));
		colors.widthProperty().bind(scene.widthProperty());
		colors.heightProperty().bind(scene.heightProperty());
		root.getChildren().add(colors);
		root.getChildren().add(circles);

		Timeline timeline = new Timeline(); // Animation
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);
		for(Node circle: circles.getChildren()){
			timeline.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO,
					new KeyValue(circle.translateXProperty(), random() * 530),
					new KeyValue(circle.translateYProperty(), random() * 550)
					),
				new KeyFrame(new Duration(30000),
					new KeyValue(circle.translateXProperty(), random() * 530),
					new KeyValue(circle.translateYProperty(), random() * 550)
					)
				);
		}
		root.getChildren().add(borderMenu);
		timeline.play();
		stage.setTitle("Menu Mastermind");
		stage.show();
	}

//  ---- Fin de : Animation de Fond du Menu ----

	private HBox addHboxMenu() { // credit
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.setPadding(new Insets(0, 0, 0, 0));
		hbox.setSpacing(12);


		Button btnCredit = new Button("Crédit");
		btnCredit.setPrefSize(70, 10);
		btnCredit.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				credit();
			}
		});

		hbox.getChildren().addAll(btnCredit);
		return hbox;
	}

	private VBox addVBoxMenu() { // Bouton Jouer, Option, Regle
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(85, 140, 90, 165));
		vbox.setSpacing(20);

		Button btnJouer = new Button("JOUER");
		btnJouer.setPrefSize(200, 50);
		btnJouer.setStyle("-fx-font: 20 arial;");
		btnJouer.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nettoyer();
				mastermind();
			}
		});

		Button btnOption = new Button("OPTION");
		btnOption.setPrefSize(200, 50);
		btnOption.setStyle("-fx-font: 20 arial;");
		btnOption.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				option();
			}
		});

		Button btnRegle = new Button("REGLE");
		btnRegle.setPrefSize(200, 50);
		btnRegle.setStyle("-fx-font: 20 arial;");
		btnRegle.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				regle();
			}
		});

		Button btnQuit= new Button("Quitter");
		//btnQuit.setPrefSize(200, 50);
		//btnQuit.setStyle("-fx-font: 20 arial;");
		btnQuit.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				Platform.exit();
			}
		});

		vbox.getChildren().addAll(btnJouer, btnOption, btnRegle, btnQuit);
		return vbox;
	}

	private HBox mastermindTitle() {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(45, 0, 0, 0));

		Text m = new Text("M");
		m.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		m.setStroke(Color.BLACK);
		m.setStrokeWidth(2);
		m.setFill(Color.RED);

		Text a = new Text("A");
		a.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		a.setStroke(Color.BLACK);
		a.setStrokeWidth(2);
		a.setFill(Color.WHITE);

		Text s = new Text("S");
		s.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		s.setStroke(Color.BLACK);
		s.setStrokeWidth(2);
		s.setFill(Color.MAGENTA);

		Text t = new Text("T");
		t.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		t.setStroke(Color.BLACK);
		t.setStrokeWidth(2);
		t.setFill(Color.BLUE);		

		Text e = new Text("E");
		e.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		e.setStroke(Color.BLACK);
		e.setStrokeWidth(2);
		e.setFill(Color.LIGHTSEAGREEN);

		Text r = new Text("R");
		r.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		r.setStroke(Color.BLACK);
		r.setStrokeWidth(2);
		r.setFill(Color.GREEN);

		Text m2 = new Text("M");
		m2.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		m2.setStroke(Color.BLACK);
		m2.setStrokeWidth(2);
		m2.setFill(Color.YELLOW);

		Text i = new Text("I");
		i.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		i.setStroke(Color.BLACK);
		i.setStrokeWidth(2);
		i.setFill(Color.RED);

		Text n = new Text("N");
		n.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		n.setStroke(Color.BLACK);
		n.setStrokeWidth(2);
		n.setFill(Color.BLACK);

		Text d = new Text("D");
		d.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		d.setStroke(Color.BLACK);
		d.setStrokeWidth(2);
		d.setFill(Color.GREEN);
		hbox.getChildren().addAll(m,a,s,t,e,r,m2,i,n,d);
		return hbox;
	}

// 00000000000000000000000000000000000000000000000000000000000000000000000000000
// 0000                          MASTERMIND                                 0000
// 00000000000000000000000000000000000000000000000000000000000000000000000000000

	public void mastermind(){
		BorderPane border = new BorderPane();
		border.setStyle("-fx-background-color: BURLYWOOD;");
		HBox hbox = addHbox();
		border.setBottom(hbox);
		border.setCenter(grid);
		border.setRight(grid2);
		grid.setAlignment(Pos.CENTER);
		grid2.setAlignment(Pos.CENTER);
		randTabSD();
		afficherTab(reponse);
		Scene scene = new Scene(border, 530, 550);
		stage.setScene(scene);
		stage.setTitle("Mastermind");
	}

	private HBox addHbox() {

		int ugh = nbCouleur;
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: SIENNA;");

		Button rouge = new Button("Rouge");
		rouge.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 0);
				proposition[acColumn] = 0;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else{
					acColumn++;
				}
			}
		});

		Button vert = new Button("Vert");
		vert.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 1);
				proposition[acColumn] = 1;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else{
					acColumn++;
				}
			}
		});

		Button bleu = new Button ("Bleu");
		bleu.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 2);
				proposition[acColumn] = 2;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else
					acColumn++;
			}
		});

		Button magenta = new Button("Magenta");
		magenta.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 3);
				proposition[acColumn] = 3;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else
					acColumn++;
			}
		});

		Button jaune = new Button("Jaune");
		jaune.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 4);
				proposition[acColumn] = 4;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else
					acColumn++;
			}
		});


		Button cyan = new Button("Cyan");
		cyan.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 5);
				proposition[acColumn] = 5;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else
					acColumn++;
			}
		});


		Button blanc = new Button("Blanc");
		blanc.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 6);
				proposition[acColumn] = 6;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else
					acColumn++;
			}
		});


		Button noir = new Button("Noir");
		noir.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				placerBoule(acRow, acColumn, 7);
				proposition[acColumn] = 7;
				if (acColumn == (column-1)){
					completerTab();
					verifier();
					correction();
					acRow++;
					acColumn = 0;
				}
				else
					acColumn++;
			}
		});


		hbox.getChildren().addAll(rouge, vert, bleu, magenta);

		if(ugh > 4)
			hbox.getChildren().add(jaune);

		if(ugh > 5)
			hbox.getChildren().add(cyan);

		if(ugh > 6)
			hbox.getChildren().add(blanc);

		if(ugh > 7)
			hbox.getChildren().add(noir);

		return hbox;
	}

	private void completerTab(){
		for(int i = column+1; i<proposition.length; i++){
			proposition[i]=-3;
		}
	}

	private GridPane addGridPane() {
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setGridLinesVisible(true);
		grid.setPadding(new Insets(5, 5, 5, 5));
		for(int i = 0; i < row; i++)
		{

			for(int u = 0; u < column; u++)
			{
				Circle circle = new Circle();
				circle.setRadius(5.0f);
				Circle invi = new Circle();
				invi.setRadius(20.0f);
				circle.setStroke(Color.rgb(0, 0, 0, 0));
				circle.setStrokeWidth(32);
				
				grid.add(circle, u, i);
				//grid.add(invi, u, i);
			}
		}
		return grid;
	}

	private GridPane addOtherGridPane() {
		GridPane grid2 = new GridPane();
		grid2.setHgap(3);
		grid2.setVgap(5);
		grid2.setGridLinesVisible(true);
		grid.setPadding(new Insets(5,5,5,5));
		for(int i = 0; i < row; i++)
		{
			for(int u = 0; u < column; u++)
			{
				Rectangle rec = new Rectangle();
				rec.setStroke(Color.rgb(0, 0, 0, 0));
				rec.setStrokeWidth(10);
				rec.setWidth(5);
				rec.setHeight(32);

				grid2.add(rec, u, i);
			}
		}
		return grid2;

	}

	public void placerBoule (int lig, int col, int couleur) {
		Circle circle = new Circle();
		circle.setRadius(20.0f);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);
		switch(couleur){
			case 0:circle.setFill(Color.RED);
			break;
			case 1: circle.setFill(Color.GREEN);
			break;
			case 2: circle.setFill(Color.BLUE);
			break;
			case 3: circle.setFill(Color.MAGENTA);
			break;
			case 4: circle.setFill(Color.YELLOW);
			break;
			case 5: circle.setFill(Color.LIGHTSEAGREEN);
			break;
			case 6: circle.setFill(Color.WHITE);
			break;
			case 7: circle.setFill(Color.BLACK);
			break;
		}
		grid.add(circle, col, lig);
	}

	public void placerCorrection (int lig, int col, int couleur){
		Rectangle rec = new Rectangle();
		rec.setStroke(Color.BLACK);
		rec.setWidth(14);
		rec.setHeight(41);
		rec.setStrokeWidth(1);
		switch(couleur){
			case 0: rec.setFill(Color.GREEN);
			break;
			case 1: rec.setFill(Color.RED);
			break;
		}
		grid2.add(rec, col, lig);
	}

// --------------------------------------------------------------------------

	public void correction()
	{
		int bonneCouleur = nbCP(proposition, reponse);
		int bienPlacer = nbBp(proposition);
		int column2 = 0;
		for(int i = 0; i < bienPlacer; i++)
		{
			placerCorrection(acRow, column2, 0);
			column2++;
		}
		for(int u = 0; u < bonneCouleur;u++)
		{
			placerCorrection(acRow, column2, 1);
			column2++;
		}
	}

	public int nbCP (int[] proposition, int[] reponse)
	{
		int i;
		int acc;
		acc = 0;
		for(int j = 0; j<column; j++)
		{
			reponse2[j]=reponse[j];
		}
		

		for(i = 0; i<column; i++)
		{
			if(proposition[i]==reponse2[i])
			{
				reponse2[i]=-1;
			}
		}
		for(i = 0; i < column; i++)
		{
			if(present(proposition[i]))
			{
				acc++;
			}
		}
		return acc;

	}

	public int nbBp(int[] proposition){
		int i;
		int acc;
		acc = 0;
		for(i = 0; i < column; i++)
		{
			if(reponse2[i] == -1)
				acc++;
		}
		return acc;
	}

	public boolean present(int n)
	{
		int i;
		for(i = 0; i < column; i++)
		{
			if(reponse2[i]==n){
				reponse2[i]=-2;
				return true;
			}
		}
		return false;
	}

// 666666666666666666666666666666666666666666666666666666666666666666666666666

	public void randTab()
	{
		int tmp;
		for(int i = 0; i<reponse.length; i++)
		{
			reponse[i]=-3;
		}
		for(int i = 0; i<column; i++)
		{
			tmp = (int) (Math.random() * nbCouleur);
			reponse[i]=tmp;

		}
	}



	public void randTabSD()
	{
		int tmp;
		int i = 0;
		for(int u = 0; u<reponse.length; u++)
		{
			reponse[u]=-3;
		}
		while(i < column){
			tmp = (int) (Math.random() * nbCouleur);
			if(!present2(tmp)){
				reponse[i]=tmp;
				i++;
			}

		}
	}

	public boolean present2(int n)
	{
		int i;
		for(i = 0; i < column; i++)
		{
			if(reponse[i]==n){
				return true;
			}
		}
		return false;
	}

	public void verifier()
	{
		if(egal(proposition, reponse)==1)
		{
			BorderPane border2 = new BorderPane();
			border2.setCenter(addVictory());
			border2.setBottom(rejouer());

			Scene sceneVic = new Scene(border2, 530, 550);
			stage.setScene(sceneVic);
		}
		else{
			if(acRow == (row-1)){
				BorderPane border2 = new BorderPane();
				border2.setCenter(addDefeat());
				border2.setBottom(rejouer());


				Scene sceneDef = new Scene(border2, 530, 550);
				stage.setScene(sceneDef);
			}
		}
	}

	public Text addVictory()
	{

		Text t= new Text(60, 50, "Gagné !!");
		t.setFont(new Font(20));
		return t;
	}

		public Text addDefeat()
	{

		Text t= new Text(60, 50, "Perdu :(");
		t.setFont(new Font(20));
		return t;
	}

	private HBox rejouer(){

		HBox hboxR = new HBox();
		hboxR.setSpacing(50);
		hboxR.setPadding(new Insets(40));


		Button btnRejouer = new Button("Rejouer");
		btnRejouer.setPrefSize(200, 50);
		btnRejouer.setStyle("-fx-font: 20 arial;");
		btnRejouer.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nettoyer();
				mastermind();
			}
		});

		Button btnMenu = new Button("Menu");
		btnMenu.setPrefSize(200, 50);
		btnMenu.setStyle("-fx-font: 20 arial;");
		btnMenu.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nettoyer();
				menu();
			}
		});

		hboxR.getChildren().addAll(btnRejouer, btnMenu);

		return hboxR;
	}

	public void afficherTab(int[] tab){
		for(int i = 0; i<tab.length; i++)
		{
			System.out.print(tab[i]);
		}
		System.out.println(" ");
	}

	public int egal(int[] tab1, int[] tab2)
	{
		for(int i = 0; i < column; i++)
		{
			if(tab1[i]!=tab2[i]){
				return 0;
			}
		}
		return 1;
	}

	public void nettoyer(){
		for(int i = 0; i < row; i++)
		{

			for(int u = 0; u < column; u++)
			{
				Circle cirLav = new Circle();
				cirLav.setRadius(20.0f);
				cirLav.setFill(Color.WHITE);
				cirLav.setStroke(Color.GREY);
				cirLav.setStrokeWidth(2);

				grid.add(cirLav, u, i);

				Circle circle = new Circle();
				circle.setRadius(5.0f);
				circle.setStroke(Color.rgb(0, 0, 0, 0));
				circle.setStrokeWidth(32);
				
				grid.add(circle, u, i);

				Rectangle recLav = new Rectangle();
				recLav.setStroke(Color.GREY);
				recLav.setFill(Color.WHITE);
				recLav.setWidth(14);
				recLav.setHeight(41);
				recLav.setStrokeWidth(1);

				grid2.add(recLav, u, i);

				Rectangle rec = new Rectangle();
				rec.setStroke(Color.rgb(0, 0, 0, 0));
				rec.setStrokeWidth(10);
				rec.setWidth(5);
				rec.setHeight(32);

				grid2.add(rec, u, i);
			}
		}
		acRow = 0;
		acColumn = 0;
	}

// 00000000000000000000000000000000000000000000000000000000000000000000000000000
// 0000                              OPTION                                 0000
// 00000000000000000000000000000000000000000000000000000000000000000000000000000

	public void option(){
		Group root = new Group();
		Scene scene = new Scene(root, 530, 550);
		BorderPane borderOption = new BorderPane();
		borderOption.setTop(optionTitle());
		borderOption.setCenter(menuOptionV());
		borderOption.setBottom(retourButton());

		stage.setScene(scene);
		stage.setTitle("Option Mastermind");

		Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
			new LinearGradient(1f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new
				Stop[]{
					new Stop(0, Color.BLACK),
					new Stop(0.14, Color.RED),
					new Stop(0.28, Color.YELLOW),
					new Stop(0.43, Color.GREEN),
					new Stop(0.57, Color.LIGHTSEAGREEN),
					new Stop(0.71, Color.BLUE),
					new Stop(0.85, Color.MAGENTA),
					new Stop(1, Color.WHITE),
				}));
		colors.widthProperty().bind(scene.widthProperty());
		colors.heightProperty().bind(scene.heightProperty());
		root.getChildren().add(colors);

		root.getChildren().add(borderOption);
	}

	public Text optionTitle(){
		Text optionTitle = new Text("          Option");
		optionTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		optionTitle.setStroke(Color.WHITE);
		optionTitle.setStrokeWidth(2);
		optionTitle.setFill(Color.BLACK);
		return optionTitle;
	}

	public VBox menuOptionV(){
		VBox vboption = new VBox();
		vboption.getChildren().addAll(nbColumnV(), nbCouleurV());
		return vboption;
	}

	private HBox nbColumnV(){

		int tt = 40;

		HBox nbColumnV = new HBox();
		nbColumnV.setPadding(new Insets(80, 0, 0, 8));
		Text nbColumnT = new Text("Nombre de Colone  :");
		nbColumnT.setFont(Font.font("Tahoma",30));

		ToggleButton ctbn4 = new ToggleButton("4");
		ctbn4.setPrefSize(tt, tt);
		ctbn4.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				column = 4;
			}
		});

		ToggleButton ctbn5 = new ToggleButton("5");
		ctbn5.setPrefSize(tt, tt);
		ctbn5.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				column = 5;
			}
		});

		ToggleButton ctbn6 = new ToggleButton("6");
		ctbn6.setPrefSize(tt, tt);
		ctbn6.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				column = 6;
			}
		});

		ToggleButton ctbn7 = new ToggleButton("7");
		ctbn7.setPrefSize(tt, tt);
		ctbn7.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				column = 7;
			}
		});

		ToggleButton ctbn8 = new ToggleButton("8");
		ctbn8.setPrefSize(tt, tt);
		ctbn8.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				column = 8;
			}
		});

		ToggleGroup group1 = new ToggleGroup();
		ctbn4.setToggleGroup(group1);
		ctbn5.setToggleGroup(group1);
		ctbn6.setToggleGroup(group1);
		ctbn7.setToggleGroup(group1);
		ctbn8.setToggleGroup(group1);

		group1.selectToggle(ctbn4);

		if (column == 4)
			group1.selectToggle(ctbn4);

		if (column == 5)
			group1.selectToggle(ctbn5);

		if (column == 6)
			group1.selectToggle(ctbn6);

		if (column == 7)
			group1.selectToggle(ctbn7);

		if (column == 8)
			group1.selectToggle(ctbn8);

		nbColumnV.getChildren().addAll(nbColumnT, ctbn4, ctbn5, ctbn6, ctbn7, ctbn8);

		return nbColumnV;
	}

	public HBox nbCouleurV(){
		int tt = 40;

		HBox nbCouleurV = new HBox();
		nbCouleurV.setPadding(new Insets(80, 0, 180, 8));
		Text nbCouleurT = new Text("Nombre de Couleur :");
		nbCouleurT.setFont(Font.font("Tahoma",30));

		ToggleButton ctbn4 = new ToggleButton("4");
		ctbn4.setPrefSize(tt, tt);
		ctbn4.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nbCouleur = 4;
			}
		});

		ToggleButton ctbn5 = new ToggleButton("5");
		ctbn5.setPrefSize(tt, tt);
		ctbn5.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nbCouleur = 5;
			}
		});

		ToggleButton ctbn6 = new ToggleButton("6");
		ctbn6.setPrefSize(tt, tt);
		ctbn6.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nbCouleur = 6;
			}
		});

		ToggleButton ctbn7 = new ToggleButton("7");
		ctbn7.setPrefSize(tt, tt);
		ctbn7.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nbCouleur = 7;
			}
		});

		ToggleButton ctbn8 = new ToggleButton("8");
		ctbn8.setPrefSize(tt, tt);
		ctbn8.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				nbCouleur = 8;
			}
		});

		ToggleGroup group1 = new ToggleGroup();
		ctbn4.setToggleGroup(group1);
		ctbn5.setToggleGroup(group1);
		ctbn6.setToggleGroup(group1);
		ctbn7.setToggleGroup(group1);
		ctbn8.setToggleGroup(group1);

		if (nbCouleur == 4)
			group1.selectToggle(ctbn4);

		if (nbCouleur == 5)
			group1.selectToggle(ctbn5);

		if (nbCouleur == 6)
			group1.selectToggle(ctbn6);

		if (nbCouleur == 7)
			group1.selectToggle(ctbn7);

		if (nbCouleur == 8)
			group1.selectToggle(ctbn8);

		nbCouleurV.getChildren().addAll(nbCouleurT, ctbn4, ctbn5, ctbn6, ctbn7, ctbn8);

		return nbCouleurV;
	}

	private HBox retourButton(){
		HBox retourHbox = new HBox();
		Button retourBtn = new Button("Retour");
		retourHbox.setPadding(new Insets(0, 0, 0, 420));
		retourBtn.setPrefSize(100, 50);
		retourBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				menu();
			}
		});
		retourHbox.getChildren().add(retourBtn);
		return retourHbox;
	}

// 00000000000000000000000000000000000000000000000000000000000000000000000000000
// 0000                               Regle                                 0000
// 00000000000000000000000000000000000000000000000000000000000000000000000000000

	private void regle(){
		Group root = new Group();
		Scene scene = new Scene(root, 530, 550);
		BorderPane borderRegle = new BorderPane();
		borderRegle.setTop(regleTitle());
		borderRegle.setCenter(vboxRegle());
		borderRegle.setBottom(retourButton());

		stage.setScene(scene);
		stage.setTitle("Règle Mastermind");

		Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
			new LinearGradient(1f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new
				Stop[]{
					new Stop(0, Color.BLACK),
					new Stop(0.14, Color.RED),
					new Stop(0.28, Color.YELLOW),
					new Stop(0.43, Color.GREEN),
					new Stop(0.57, Color.LIGHTSEAGREEN),
					new Stop(0.71, Color.BLUE),
					new Stop(0.85, Color.MAGENTA),
					new Stop(1, Color.WHITE),
				}));
		colors.widthProperty().bind(scene.widthProperty());
		colors.heightProperty().bind(scene.heightProperty());
		root.getChildren().add(colors);

		root.getChildren().add(borderRegle);
	}

	public Text regleTitle(){
		Text optionTitle = new Text("          Règle");
		optionTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		optionTitle.setStroke(Color.WHITE);
		optionTitle.setStrokeWidth(2);
		optionTitle.setFill(Color.BLACK);
		return optionTitle;
	}

	public VBox vboxRegle(){
		VBox vboxRegle = new VBox();

		Text but = new Text("\nBut du Jeu :");
		but.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.ITALIC, 30));
		but.setStroke(Color.BLACK);
		but.setStrokeWidth(2);
		but.setFill(Color.RED);

		Text butbis = new Text("\nLe but du Mastermind est de gagner un minimum d'essai(Ligne). \nLe joueur  doit trouver la combinaison secrète gagne dès lors \nqu’il y parvient en maximum 10 coups.");
		butbis.setFont(Font.font("Tahoma", 15));

		Text deroulement = new Text("\nDéroulement du jeu :");
		deroulement.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.ITALIC, 30));
		deroulement.setStroke(Color.BLACK);
		deroulement.setStrokeWidth(2);
		deroulement.setFill(Color.RED);

		Text deroulementbis = new Text("\nQuand vous avez rempli une ligne, vous pouvez tester votre \ncombinaison. L'ordinateur affiche alors des points Vert et Rouge. \n - Un point Vert correspond à une bille bien placée \n   (bonne couleur, bon emplacement).\n - Un point rouge correspond à une bille de la bonne couleur mais\n   pas au bon emplacement.\nA l'aide de ces indices, vous pouvez alors commencer une nouvelle \nligne. Au bout de 10 lignes échouées, vous avez perdu.\n\n\n");
		deroulementbis.setFont(Font.font("Tahoma", 15));


		vboxRegle.getChildren().addAll(but, butbis, deroulement, deroulementbis );
		return vboxRegle;

	}


// 00000000000000000000000000000000000000000000000000000000000000000000000000000
// 0000                               crédit                                0000
// 00000000000000000000000000000000000000000000000000000000000000000000000000000

	private void credit(){
		Group root = new Group();
		Scene scene = new Scene(root, 530, 550);
		BorderPane borderOption = new BorderPane();
		borderOption.setTop(creditTitle());
		borderOption.setCenter(textCredit());
		borderOption.setBottom(retourButton());

		stage.setScene(scene);
		stage.setTitle("Option Mastermind");

		Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
			new LinearGradient(1f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new
				Stop[]{
					new Stop(0, Color.BLACK),
					new Stop(0.14, Color.RED),
					new Stop(0.28, Color.YELLOW),
					new Stop(0.43, Color.GREEN),
					new Stop(0.57, Color.LIGHTSEAGREEN),
					new Stop(0.71, Color.BLUE),
					new Stop(0.85, Color.MAGENTA),
					new Stop(1, Color.WHITE),
				}));
		colors.widthProperty().bind(scene.widthProperty());
		colors.heightProperty().bind(scene.heightProperty());
		root.getChildren().add(colors);

		root.getChildren().add(borderOption);
	}

	public Text creditTitle(){
		Text optionTitle = new Text("          Crédit");
		optionTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 50));
		optionTitle.setStroke(Color.WHITE);
		optionTitle.setStrokeWidth(2);
		optionTitle.setFill(Color.BLACK);
		return optionTitle;
	}

	public VBox textCredit(){
		VBox vboxRegle = new VBox();

		Text but = new Text("\nCreateur du projet :");
		but.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.ITALIC, 30));
		but.setStroke(Color.BLACK);
		but.setStrokeWidth(2);
		but.setFill(Color.RED);

		Text butbis = new Text("\nNour-El-Houda ZEROUALLI, N°13410537\nAlexis BEHIER, N°14501367");
		butbis.setFont(Font.font("Tahoma", 15));

		Text deroulement = new Text("\nRemerciment :");
		deroulement.setFont(Font.font("Tahoma", FontWeight.BOLD, FontPosture.ITALIC, 30));
		deroulement.setStroke(Color.BLACK);
		deroulement.setStrokeWidth(2);
		deroulement.setFill(Color.RED);

		Text deroulementbis = new Text("\nNous tenons a remercier Bernard pour avoir rendu le Bocal de \nnouveau vivables en le quittant.\nMais aussi Julie pour sont aide et son ecoute psycologique \nainsi que Wissam pour sont aide sur les class.\n\n\n\n\n\n\n\n");
		deroulementbis.setFont(Font.font("Tahoma", 15));


		vboxRegle.getChildren().addAll(but, butbis, deroulement, deroulementbis );
		return vboxRegle;

	}

}	