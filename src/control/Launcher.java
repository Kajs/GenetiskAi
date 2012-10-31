package control;

import control.Controller;

public class Launcher {
	
	public static boolean isAutomatic = true;

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Controller control = new Controller(isAutomatic);
	  }
}