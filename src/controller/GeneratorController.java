// Decompiled using: fernflower
// Took: 31ms

package controller;

import view.GeneratorGUI;

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
