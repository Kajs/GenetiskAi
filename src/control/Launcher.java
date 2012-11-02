package control;

import control.Controller;

public class Launcher {
	
	public static boolean isAutomatic = true;
	public static boolean displayAutomatic = true;
	public static boolean allowActionOutput = true;
	public static boolean allowRoundDelay = true;
	public static boolean allowShieldOutput = false;
	public static boolean allowHpOutput = false;
	public static boolean toggleRoundSeparator = false;
	public static boolean allowStunOutput = false;
	public static boolean allowRoundFitnessOutput = false;
	public static boolean allowGenAlgAnnounce = false;
	public static boolean allowBestTeamsFitnessOutput = false;
	public static boolean isPaused = false;
	public static boolean stop = false;

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Controller control = new Controller(isAutomatic, displayAutomatic);
	  }
}