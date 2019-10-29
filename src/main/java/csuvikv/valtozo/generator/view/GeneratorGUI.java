package csuvikv.valtozo.generator.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import csuvikv.valtozo.generator.application.Main;
import csuvikv.valtozo.generator.controller.GeneratorController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class GeneratorGUI extends Application {
   @FXML
   private Spinner<Integer> szam0;
   @FXML
   private Spinner<Integer> szam1;
   @FXML
   private Spinner<Integer> szam2;
   private File txtFile;
   private Label label;
   @SuppressWarnings("unused")
private final GeneratorController controller;
   @SuppressWarnings("unused")
private static Stage stage;
   private static Scene scene;
   private String valtozoFile;
   private String kiadottFile;
   private Map<String, List<String>> emberek;
   private List<String> nevek;
   private List<String> valtozok;
   private List<String> szervesValtozok;
   private List<String> szervetlenValtozok;
   private List<String> kiadottSzerves;
   private List<String> kiadottSzervetlen;

   public GeneratorGUI() {
      this.controller = Main.controller;
      this.valtozoFile = "valtozok.txt";
      this.kiadottFile = "kiadott.txt";
   }

   public void start(final Stage primaryStage) {
      primaryStage.setTitle("Valtozo Generator");
      stage = primaryStage;
      Parent root = null;

      try {
			root = FXMLLoader.load(getClass().getResource("ui.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

      if (root != null) {
         scene = new Scene(root);
         primaryStage.setScene(scene);
         VBox parent = (VBox)primaryStage.getScene().lookup("#vertical_container");
         final VBox container = new VBox();
         ScrollPane scroll = new ScrollPane();
         scroll.setContent(container);
         parent.getChildren().add(scroll);
         final FileChooser fileChooser = new FileChooser();
         Button loadFile = (Button)primaryStage.getScene().lookup("#loadFile");
         loadFile.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
               GeneratorGUI.configureFileChooser(fileChooser);
               GeneratorGUI.this.txtFile = fileChooser.showOpenDialog(primaryStage);
               GeneratorGUI.this.label = (Label)primaryStage.getScene().lookup("#fileLabel");

               if (GeneratorGUI.this.txtFile != null) {
                  GeneratorGUI.this.label.setText(GeneratorGUI.this.txtFile.getName());
                  GeneratorGUI.this.valtozoFile = GeneratorGUI.this.txtFile.getAbsolutePath();
                  GeneratorGUI.this.readVariables();
                  container.getChildren().clear();
                  
                  for (String nev : nevek) {
                     GridPane grid = GeneratorGUI.this.constructGrid(nev);
                     container.getChildren().add(grid);
                     container.getChildren().add(new Separator());
                  }

                  createAlert(AlertType.INFORMATION, "Sikeres betöltés", "A változó fájl betöltése sikeres!", "A szöveges fájl elérési útvonala: "  + GeneratorGUI.this.valtozoFile + "\n Az adatok frissülnek az Ok gomb lenyomása után.").showAndWait();
               } else {
                  GeneratorGUI.this.label.setText("Nincs fájl kiválasztva");
                  createAlert(AlertType.ERROR, "A betöltés sikertelen", "A változók betöltése nem sikerült!", "Az elérni kívánt szöveges fájl útvonala: " +  GeneratorGUI.this.valtozoFile).showAndWait();
               }

            }

         });
         this.readVariables();

         for (String nev : nevek) {
            GridPane grid = this.constructGrid(nev);
            container.getChildren().add(grid);
            container.getChildren().add(new Separator());
         }

         this.initializeSpinners();
         primaryStage.show();
      } else {
         System.out.println("Failed to load ui.fxml!");
      }

   }

   @SuppressWarnings("unchecked")
private void initializeSpinners() {
      SpinnerValueFactory<Integer> valueFactory = new IntegerSpinnerValueFactory(1, 1000, 1);
      this.szam0 = (Spinner<Integer>)scene.lookup("#szam0");
      this.szam0.setValueFactory(valueFactory);
      valueFactory = new IntegerSpinnerValueFactory(1, 1000, 2);
      this.szam1 = (Spinner<Integer>)scene.lookup("#szam1");
      this.szam1.setValueFactory(valueFactory);
      valueFactory = new IntegerSpinnerValueFactory(1, 1000, 3);
      this.szam2 = (Spinner<Integer>)scene.lookup("#szam2");
      this.szam2.setValueFactory(valueFactory);
   }

   private GridPane constructGrid(String name) {
      GridPane grid = new GridPane();
      grid.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
      grid.setVgap(5.0D);
      grid.setHgap(5.0D);
      List<String> kiadottValtozok = this.emberek.get(name);
      List<String> megmaradtValtozok = new ArrayList<>();

      for(String valtozo : this.valtozok) {
         if (!kiadottValtozok.contains(valtozo) && !kiadottValtozok.contains(valtozo.toLowerCase())) {
            megmaradtValtozok.add(valtozo);
         }
      }

      Label nameLabel = new Label("Név: " + name);
      nameLabel.setStyle("-fx-font-weight: bold");
      this.addToGrid(grid, nameLabel, 0, 0);
      this.addToGrid(grid, new Label("Kiadott változók"), 1, 0);
      ListView<String> kiadottList = new ListView<>(FXCollections.observableArrayList(kiadottValtozok)); //FXCollections.observableArrayList(kiadottValtozok)
      kiadottList.setPrefSize(200.0D, 100.0D);
      this.addToGrid(grid, kiadottList, 1, 1);
      this.addToGrid(grid, new Label("Megmaradt változók"), 2, 0);
      ListView<String> megmaradtList = new ListView<>(FXCollections.observableArrayList(megmaradtValtozok)); //FXCollections.observableArrayList(megmaradtValtozok)
      megmaradtList.setPrefSize(200.0D, 100.0D);
      this.addToGrid(grid, megmaradtList, 2, 1);
      this.addToGrid(grid, new Label("szervetlen"), 1, 2);
      //this.addToGrid(grid, new Label("szervetlen"), 2, 2);
      this.addToGrid(grid, new Label("változók: "), 0, 3);
      /*ChoiceBox<String> szervesChoiceBox = new ChoiceBox<>();
      szervesChoiceBox.getItems().addAll(this.getAvaivalbeSzervesFor(name, false));
      szervesChoiceBox.getSelectionModel().select(this.getNewSzerves(name));
      szervesChoiceBox.setId("szerves_" + name);
      this.addToGrid(grid, szervesChoiceBox, 1, 3);*/

      for(int i = 0; i < 3; ++i) {
         ChoiceBox<String> szervetlenChoiceBox = new ChoiceBox<>();
         szervetlenChoiceBox.getItems().addAll(this.getAvaivalbeSzervetlenFor(name, false));
         szervetlenChoiceBox.getSelectionModel().select(this.getNewSzervetlen(name));
         szervetlenChoiceBox.setId("szervetlen_" + i + "_" + name);
         this.addToGrid(grid, szervetlenChoiceBox, i + 1, 3);
      }

      return grid;
   }

   private void addToGrid(GridPane grid, Control control, int x, int y) {
      grid.getChildren().add(control);
      GridPane.setConstraints(control, x, y);
   }

   private String getNewSzervetlen(String name) {
      List<String> adhatoValtozok = this.getAvaivalbeSzervetlenFor(name, true);
      if (adhatoValtozok.size() == 0) {
         return null;
      } else {
         Random random = new Random();
         int r = random.nextInt(adhatoValtozok.size());
         String newSzervetlen = String.valueOf(adhatoValtozok.get(r));
         this.kiadottSzervetlen.add(newSzervetlen);
         return newSzervetlen;
      }
   }

   private List<String> getAvaivalbeSzervetlenFor(String name, boolean removeKiadott) {
      List<String> adhatoValtozok = new ArrayList<>();
      adhatoValtozok.addAll(this.szervetlenValtozok);
      adhatoValtozok.removeAll((Collection<String>)this.emberek.get(name));
      if (removeKiadott) {
         adhatoValtozok.removeAll(this.kiadottSzervetlen);
      }

      if (adhatoValtozok.size() == 0) {
         adhatoValtozok.addAll(this.szervetlenValtozok);
         adhatoValtozok.removeAll((Collection<String>)this.emberek.get(name));
      }

      return adhatoValtozok;
   }

   private List<String> getAvaivalbeSzervesFor(String name, boolean removeKiadott) {
      List<String> adhatoValtozok = new ArrayList<>();
      adhatoValtozok.addAll(this.szervesValtozok);
      adhatoValtozok.removeAll((Collection<String>)this.emberek.get(name));
      if (removeKiadott) {
         adhatoValtozok.removeAll(this.kiadottSzerves);
      }

      if (adhatoValtozok.size() == 0) {
         adhatoValtozok.addAll(this.szervesValtozok);
         adhatoValtozok.removeAll((Collection<String>)this.emberek.get(name));
      }

      return adhatoValtozok;
   }

   @SuppressWarnings("unused")
private String getNewSzerves(String name) {
      List<String> adhatoValtozok = this.getAvaivalbeSzervesFor(name, true);
      if (adhatoValtozok.size() == 0) {
         return null;
      } else {
         Random random = new Random();
         int r = random.nextInt(adhatoValtozok.size());
         String newSzerves = String.valueOf(adhatoValtozok.get(r));
         this.kiadottSzerves.add(newSzerves);
         ((List<String>)this.emberek.get(name)).add(newSzerves);
         return newSzerves;
      }
   }

   private static void configureFileChooser(FileChooser fileChooser) {
      fileChooser.setTitle("Szöveges fájl betöltése");
      fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
      fileChooser.getExtensionFilters().clear();
      fileChooser.getExtensionFilters().addAll(new ExtensionFilter[]{new ExtensionFilter("TXT", new String[]{"*.txt"})});
   }

   private void readVariables() {
      this.emberek = new HashMap<>();
      this.nevek = new ArrayList<>();
      this.valtozok = new ArrayList<>();
      this.szervesValtozok = new ArrayList<>();
      this.szervetlenValtozok = new ArrayList<>();
      this.kiadottSzerves = new ArrayList<>();
      this.kiadottSzervetlen = new ArrayList<>();

      String line;
      try (BufferedReader br = new BufferedReader(new FileReader(this.valtozoFile));){
    	  
           boolean szervetlen = true;
           while((line = br.readLine()) != null) {
              this.valtozok.add(line);
              if (szervetlen) {
                 this.szervetlenValtozok.add(line);
              } else {
                 this.szervesValtozok.add(line);
              }

              if (szervetlen && "KNO3".equals(line)) {
                 szervetlen = false;
              }
           }
      } catch (Exception e) {
         createAlert(AlertType.ERROR, "A betöltés sikertelen", "A változók betöltése nem sikerült!", "Az elérni kívánt szöveges fájl útvonala: " + this.valtozoFile).showAndWait();
      }


	 try (BufferedReader br = new BufferedReader(new FileReader(this.kiadottFile));) {
	    
	   String[] splitted;
       for(; (line = br.readLine()) != null; this.nevek.add(splitted[0])) {
          splitted = line.split(":");
          
		  if (splitted.length > 1) {
		     this.emberek.put(splitted[0], new ArrayList<>(Arrays.asList(splitted[1].split(","))));
          } else {
             this.emberek.put(splitted[0], new ArrayList<>());
          }
       } 
	 } catch (Exception e) {
         createAlert(AlertType.ERROR, "A betöltés sikertelen", "A kiadott változók betöltése nem sikerült!", "Az elérni kívánt szöveges fájl útvonala: " + this.kiadottFile).showAndWait();
      } 

   }

   public void startGUI() {
      launch(new String[0]);
   }

   public void quitMenuAction() {
      System.exit(0);
   }

   public void szamClick() {
      this.szam1.getValueFactory().setValue((Integer)this.szam0.getValue() + 1);
      this.szam2.getValueFactory().setValue((Integer)this.szam0.getValue() + 2);
   }

   public void saveMenuAction() {
      this.readVariables();

         try (BufferedWriter writer = new BufferedWriter(new FileWriter("kiadott.txt", false));
        		 BufferedWriter csvWriter = new BufferedWriter(new FileWriter("ism.csv", false))) {
            int j = 0;
			  for (String nev : nevek) {
				  writer.write(nev + ":");
				  
				  List<String> kiadottValtozok = new ArrayList<>();
                  kiadottValtozok.addAll((Collection<String>)this.emberek.get(nev));
                  List<String> newVariables = new ArrayList<>(this.getNewVariables(nev));
                  
                  if (newVariables.size() > 2) {
                     csvWriter.write(j++ + "," + this.szam0.getValue() + " " + (String)newVariables.get(0) + "," + this.szam1.getValue() + " " + (String)newVariables.get(1) + "," + this.szam2.getValue() + " " + (String)newVariables.get(2) + "\n");
                  } else {
                     csvWriter.write(j++ + "\n");
                     createAlert(AlertType.WARNING, "Figyelmeztetés", "Az ism.csv fájl írása közben hiba lépett fel!", "Az alábbi hallgatók változói nem egyediak, a fájlba üres sor kerül beírásra: " + nev).showAndWait();
                  }
                  
                  kiadottValtozok.addAll(newVariables);
                  int i = 0;
                  for (String valtozo : kiadottValtozok) {
                     writer.write(valtozo);
                     if (i == kiadottValtozok.size() - 1) {
                        writer.write("\n");
                     } else {
                        writer.write(",");
                     }
                     i++;
                  }
			   }
			  
      } catch (Exception e) {
    	 createAlert(AlertType.ERROR, "Sikertelen mentés", "A változók mentése nem sikerült!", "Az elérni kívánt szöveges fájl útvonala: " + this.kiadottFile).showAndWait();
      }

   }
   
   private Alert createAlert(AlertType type, String title, String header, String context) {
	   Alert alert = new Alert(type);
       alert.setTitle(title);
       alert.setHeaderText(header);
       alert.setContentText(context);
       alert.showAndWait();
       return alert;
   }
   
   @SuppressWarnings("unchecked")
   private Set<String> getNewVariables(String nev) {
      
	  //String szerves = (String)((ChoiceBox<String>)scene.lookup("#szerves_" + nev)).getValue();
      String szervetlen0 = (String)((ChoiceBox<String>)scene.lookup("#szervetlen_0_" + nev)).getValue();
      String szervetlen1 = (String)((ChoiceBox<String>)scene.lookup("#szervetlen_1_" + nev)).getValue();
      String szervetlen2 = (String)((ChoiceBox<String>)scene.lookup("#szervetlen_2_" + nev)).getValue();
      Set<String> toReturn = new HashSet<>();
      /*if (szerves != null && !"".equals(szerves)) {
         toReturn.add(szerves);
      }*/

      if (szervetlen0 != null && !"".equals(szervetlen0)) {
         toReturn.add(szervetlen0);
      }

      if (szervetlen1 != null && !"".equals(szervetlen1)) {
         toReturn.add(szervetlen1);
      }
      
      if (szervetlen2 != null && !"".equals(szervetlen2)) {
          toReturn.add(szervetlen2);
       }

      return toReturn;
   }
}
