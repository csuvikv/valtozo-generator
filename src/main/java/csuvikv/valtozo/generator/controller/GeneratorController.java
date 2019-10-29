package csuvikv.valtozo.generator.controller;

import csuvikv.valtozo.generator.view.GeneratorGUI;

public class GeneratorController {
    @SuppressWarnings("unused")
	private GeneratorGUI gui;
    
    public GeneratorController() {
        super();
    }
    
    public void startDesktop() {
        (this.gui = new GeneratorGUI()).startGUI();
    }
}
