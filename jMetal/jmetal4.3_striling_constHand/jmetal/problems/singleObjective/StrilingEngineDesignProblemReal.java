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

import matlabcontrol.*;

import java.io.BufferedReader;
//for mysql connections
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StrilingEngineDesignProblemReal extends Problem {

	MatlabProxyFactory factory;
	MatlabProxy proxy;

	//BufferedReader br; 
	
	/*int N;
	// define k, Dx, h
	int k = 150;
	int h = 75;
	int thetaL = 0;
	int q0 = 200;
	double L = 0.08;
	
	double Dx;
	double R1 = 0.008;*/

	/**
	 * Creates a new OneMax problem instance
	 * 
	 * @param numberOfBits
	 *            Length of the problem
	 */
	public StrilingEngineDesignProblemReal(String solutionType, MatlabProxy proxy, BufferedReader br)
			throws ClassNotFoundException, MatlabConnectionException,
			MatlabInvocationException {
	//	this.br=br;
		this.proxy=proxy;
		/*N = numberOfSection;
		Dx = L / N;*/
		numberOfVariables_ = 3; // at this moment, 3 parameters need to be optimized 
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 0;
		problemName_ = "StrilingEngineDesignProblem";

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		//need to check ipper and lower limit
		for (int i = 0; i < numberOfVariables_; i++) {
			lowerLimit_[i] = 0.0;
			upperLimit_[i] = R1;
		}

		// connection to matlab
		
		proxy.eval("addpath('C:\\Users\\Md shahriar Mahbub\\Desktop\\Stirling Engine\\Simple test\\')");
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

		try {

			proxy.eval("clear all");
			
			/*double R[] = new double[solution.numberOfVariables() + 1];
			R[0] = R1;
			for (int i = 0; i < solution.numberOfVariables(); i++)
				R[i + 1] = variable[i].getValue();

			String Rstr = "R=[";
			for (int i = 0; i < solution.numberOfVariables() + 1; i++) {
				Rstr = Rstr + R[i] + " ";
			}
			Rstr = Rstr + "];";

			proxy.eval(Rstr);
			proxy.eval("TestScript1");
			for (int i = 0; i < solution.numberOfVariables(); i++)
				theta[i] = ((double[]) proxy.getVariable("theta"))[i];

			error = 0.0;
			for (int i = 0; i < solution.numberOfVariables() - 1; i++) {

				double qi = (-k * (theta[i + 1] - theta[i])) / Dx;
				error = error + Math.abs(qi - q0);
			}

			double qi = (-k * (thetaL - theta[solution.numberOfVariables() - 1]))
					/ Dx;
			error = error + Math.abs(qi - q0);*/
			String input = "in = {'s',0.090e-004,1.327e-004,0.090e-004,1.450e-004,90.0,'p',1.500e-3,1.700e-001,125,'t',";
			for (int i = 0; i < solution.numberOfVariables() + 1; i++) {
				input = input + ((Real) solution.getDecisionVariables()[i]).getValue() + ",";
			}
			input = input + "'m',0.700,4.000e-005,'p',2.500e-3,2.2500e-001,34,'ai'13500000.0,313.0,548.0,4.166";
			proxy.eval(input);
			eff = ((double[])proxy.getVariable("acteff"))[1];
				
		} catch (MatlabInvocationException ob) {
			System.out.print("Matlab Invocation Exception occurred");
		}
	
		solution.setObjective(0, 1/eff);

	} // evaluate
} // FinDesignProblem
