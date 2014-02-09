//  OneMax.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>. * OneMax.java

package jmetal.problems.singleObjective;

import jmetal.core.*;

import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.variable.Int;
import jmetal.encodings.variable.Real;

import jmetal.encodings.variable.BinaryReal;
import jmetal.util.JMException;

import matlabcontrol.*;

import java.io.BufferedReader;
//for mysql connections
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.math.*;

public class StrilingEngineHeatExchangerDesignProblemReal extends Problem {

	MatlabProxyFactory factory;
	MatlabProxy proxy;

	// BufferedReader br;

	/*
	 * int N; // define k, Dx, h int k = 150; int h = 75; int thetaL = 0; int q0
	 * = 200; double L = 0.08;
	 * 
	 * double Dx; double R1 = 0.008;
	 */

	/**
	 * Creates a new OneMax problem instance
	 * 
	 * @param numberOfBits
	 *            Length of the problem
	 */
	public StrilingEngineHeatExchangerDesignProblemReal(String solutionType,
			MatlabProxy proxy) throws ClassNotFoundException,
			MatlabConnectionException, MatlabInvocationException {
		// this.br=br;
		this.proxy = proxy;
		/*
		 * N = numberOfSection; Dx = L / N;
		 */
		numberOfVariables_ = 9; // at this moment, 3 parameters need to be
								// optimized
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 3;
		problemName_ = "StrilingEngineDesignProblem";

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		//cooler
		// enter pipe inside diameter [m], range: 1 mm to 10 mm
		//value(8)
		lowerLimit_[0] = 0.001;
		upperLimit_[0] = 0.004;

		// enter heat exchanger length [m], range: 5cm to 50cm
		//value(9)
		lowerLimit_[1] = 0.1;
		upperLimit_[1] = 0.3;

		// enter number of pipes in bundle
		//value(10)
		lowerLimit_[2] = 80;
		upperLimit_[2] = 240;

		
		//regenrator
		//value(12)
		lowerLimit_[3] = 0.03;
		upperLimit_[3] = 0.15;

		//value(13)
		lowerLimit_[4] = 0.03;
		upperLimit_[4] = 0.15;
		
		//value(14)
		lowerLimit_[5] = 0.005;
		upperLimit_[5] = 0.06;
		
		//heater
		//value(20)
		lowerLimit_[6] = 0.003;
		upperLimit_[6] = 0.01;
		
		//value(21)
		lowerLimit_[7] = 0.370;
		upperLimit_[7] = 0.09;
				
		//value(22)
		lowerLimit_[8] = 5.0;
		upperLimit_[8] = 45.0;
						
		
		// connection to matlab

		proxy.eval("addpath('..\\..\\Simple test\\')");
		// solutionType_ = new BinaryRealSolutionType(this);
		if (solutionType.compareTo("Real") == 0) {
			solutionType_ = new RealSolutionType(this);

		} else {
			System.out.println("FinDesignProblem: solution type "
					+ solutionType + " invalid");
			System.exit(-1);
		}

	} // FinDesignProblem

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 */
	public void evaluate(Solution solution) {

		double eff = 0.0;
		double power = 0.0;

		try {

			proxy.eval("clear all");

			/*
			 * double R[] = new double[solution.numberOfVariables() + 1]; R[0] =
			 * R1; for (int i = 0; i < solution.numberOfVariables(); i++) R[i +
			 * 1] = variable[i].getValue();
			 * 
			 * String Rstr = "R=["; for (int i = 0; i <
			 * solution.numberOfVariables() + 1; i++) { Rstr = Rstr + R[i] +
			 * " "; } Rstr = Rstr + "];";
			 * 
			 * proxy.eval(Rstr); proxy.eval("TestScript1"); for (int i = 0; i <
			 * solution.numberOfVariables(); i++) theta[i] = ((double[])
			 * proxy.getVariable("theta"))[i];
			 * 
			 * error = 0.0; for (int i = 0; i < solution.numberOfVariables() -
			 * 1; i++) {
			 * 
			 * double qi = (-k * (theta[i + 1] - theta[i])) / Dx; error = error
			 * + Math.abs(qi - q0); }
			 * 
			 * double qi = (-k * (thetaL - theta[solution.numberOfVariables() -
			 * 1])) / Dx; error = error + Math.abs(qi - q0);
			 */
			String input = "in = {'s',0.090e-004,1.327e-004,0.090e-004,1.450e-004,90.0,'p',";

			// cooler
			for (int i = 0; i < 2; i++) {
				input = input
						+ ((Real) solution.getDecisionVariables()[i])
								.getValue() + ",";
			}
			input = input
					+ (int) ((Real) solution.getDecisionVariables()[2])
							.getValue() + ",";

			input = input + "'t',";

			// regenerator
			for (int i = 3; i < 6; i++) {
				input = input
						+ ((Real) solution.getDecisionVariables()[i])
								.getValue() + ",";
			}

			input = input + "1,'m',0.700,4.000e-005,'p',";

			// heater
			for (int i = 6; i < solution.getDecisionVariables().length - 1; i++) {
				input = input
						+ ((Real) solution.getDecisionVariables()[i])
								.getValue() + ",";
			}
			input = input
					+ (int) ((Real) solution.getDecisionVariables()[solution
							.getDecisionVariables().length - 1]).getValue()
					+ ",";

			input = input + "'ai',13500000.0,313.0,548.0,4.166}";

			proxy.eval(input + ";");
			proxy.eval("sea");
			eff = ((double[]) proxy.getVariable("acteff"))[0];
			power = ((double[]) proxy.getVariable("actWpower"))[0];

			solution.setEnginePower(power);

		} catch (MatlabInvocationException ob) {
			System.out.print("Matlab Invocation Exception occurred");
		}

		/*
		 * if(eff<0) solution.setObjective(0, 1/0.00001); /*else{ double P0=900;
		 * double DP = 0.05*P0; double fitness; if(power <= P0+DP && power >=
		 * P0-DP ){ //double gw = 0.05 * Math.cos((power-P0)*; double gw =
		 * Math.exp(-1./(1-Math.pow((power-P0)/DP,2)))/Math.exp(-1); fitness =
		 * eff * gw; solution.setObjective(0, 1/fitness);
		 * //exp(-1./(1-((P-P0)/DP).^2))/exp(-1) } else solution.setObjective(0,
		 * 1/0.00001);
		 */
		// else
		solution.setObjective(0, -eff);

	} // evaluate

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		double[] constraint = new double[3]; // 3 constraints

		//1. power
		double P0 = 5160;
		double percentage = 0.05;

		// constraint[0] = Math.abs(solution.getEnginePOwer() - P0*percentage) -
		// P0;
		if (solution.getEnginePOwer() < P0 - P0 * percentage)
			constraint[0] = solution.getEnginePOwer() - P0 - P0 * percentage;
		else if (solution.getEnginePOwer() > P0 + P0 * percentage)
			constraint[0] = P0 + P0 * percentage - solution.getEnginePOwer();
		else
			constraint[0] = 0.0;

		//heater
		// 2. Pi * (value(20)+0.001) * value(21)*value(22)> 0.58
		
		double value20 = ((Real) solution.getDecisionVariables()[6]).getValue();
		double value21 = ((Real) solution.getDecisionVariables()[7]).getValue();
		int value22 = (int) ((Real) solution.getDecisionVariables()[8]).getValue();
		
		constraint[1] = Math.PI * (value20+0.001) * value21 * value22 - 0.58;
		
		//regenerator
		//3. ((value(12) - value(13))/2) – 0.003> 0
		double value12 = ((Real) solution.getDecisionVariables()[3]).getValue();
		double value13 = ((Real) solution.getDecisionVariables()[4]).getValue();
		
		constraint[2] = ((value12 - value13)/2) -0.003;
		
		/*double length = ((Real) solution.getDecisionVariables()[1]).getValue();
		double diameter = ((Real) solution.getDecisionVariables()[0])
				.getValue();

		constraint[1] = (length / diameter) - 108.0;*/

		double total = 0.0;
		int number = 0;
		for (int i = 0; i < numberOfConstraints_; i++) {
			if (constraint[i] < 0.0) {
				total += constraint[i];
				number++;
			} // int
		} // for

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);
	} // evaluateConstraints*/

} // FinDesignProblem

