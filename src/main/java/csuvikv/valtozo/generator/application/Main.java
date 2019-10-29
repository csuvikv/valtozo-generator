package csuvikv.valtozo.generator.application;

import java.io.File;

import csuvikv.valtozo.generator.controller.GeneratorController;

public class Main {
   public static GeneratorController controller;

   public static void main(String[] args) {
      (new File("temp")).mkdirs();
      controller = new GeneratorController();
      controller.startDesktop();
   }
}
