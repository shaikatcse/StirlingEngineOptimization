package jmetal.core;

import jmetal.core.Solution;

public class SolutionStrilingEngineHeatExchengerProblem{

	private double enginePower;
	
	public SolutionStrilingEngineHeatExchengerProblem() {
		enginePower = -1;
	}
	
	public void setEnginePower(double enginePower){
		this.enginePower = enginePower;
	}
	
	public double getEnginePOwer(){
		return enginePower;
	}
}
