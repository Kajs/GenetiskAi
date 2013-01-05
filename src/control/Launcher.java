package control;

import control.Controller;

public class Launcher {
	
	public static boolean allowActionOutput = false;
	public static boolean allowAdjacentAiOutput = false;
	public static boolean allowAngleOutput = false;
	public static boolean allowAreaDamageOutput = false;
	public static boolean allowBestTeamsFitnessOutput = false;
	public static boolean allowBoostOutput = false;
	public static boolean allowGenAlgAnnounce = false;
	public static boolean allowHealOutput = false;
	public static boolean allowHpOutput = false;
	public static boolean allowNormalDamageOutput = false;
	public static boolean allowRoundDelay = false;
	public static boolean allowRoundFitnessOutput = false;
	public static boolean allowShieldOutput = false;
	public static boolean allowStunOutput = false;
	public static boolean countDuplicateFitnessValues = false;
	public static boolean displayAutomatic = true;
	public static boolean individualGeneticFitnessValues = false;
	public static boolean insertStoredTeam = false;
	public static int insertStoredTeamPosition = 0;
	public static boolean isAutomatic = true;
	public static boolean isPaused = false;
	public static boolean stop = false;	
	public static boolean switchBestTeam = false;
	public static int switchBestTeamNumber = 0;
	public static boolean testDrasticLikelihood = false;
	public static boolean testFitnessSubset = false;
	public static boolean testMutateLikelihood = false;
	public static boolean testMutateLikelihoodRange = false;
	public static double testPrintCurrentGame = 0;
	public static boolean testUnscaledFitness = false;
	public static boolean toggleRoundSeparator = false;
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Controller control = new Controller(isAutomatic, displayAutomatic);
	  }
}