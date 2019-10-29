package application;

import controller.GeneratorController;
import java.io.File;

public class Main {
   public static GeneratorController controller;

   public static void main(String[] args) {
      (new File("temp")).mkdirs();
      controller = new GeneratorController();
      controller.startDesktop();
   }
}
