package jmetal.core;

import jmetal.core.Solution;

public class SolutionStrilingEngineHeatExchengerProblem extends Solution{

	private double enginePower;
	
	public SolutionStrilingEngineHeatExchengerProblem(Problem problem) throws ClassNotFoundException{
		super(problem);
	}
	
	public void setEnginePower(double enginePower){
		this.enginePower = enginePower;
	}
	
	public double getEnginePOwer(){
		return enginePower;
	}
}
