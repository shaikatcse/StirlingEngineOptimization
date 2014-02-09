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
		numberOfVariables_ = 3; // at this moment, 3 parameters need to be
								// optimized
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 0;
		problemName_ = "StrilingEngineDesignProblem";

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		// enter pipe inside diameter [m], range: 1 mm to 10 mm
		lowerLimit_[0] = 0.001;
		upperLimit_[0] = 0.010;

		// enter heat exchanger length [m], range: 5cm to 50cm
		lowerLimit_[1] = 0.05;
		upperLimit_[1] = 0.50;

		// enter number of pipes in bundle
		lowerLimit_[2] = 10;
		upperLimit_[2] = 100;

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
		double power = 0.0;

		double length = ((Real) solution.getDecisionVariables()[1]).getValue();
		double diameter = ((Real) solution.getDecisionVariables()[0])
				.getValue();

		if (length / diameter >= 150.0)
			solution.setObjective(0, 1 / 0.00001);
		else {

			try {

				proxy.eval("clear all");

				String input = "in = {'s',0.090e-004,1.327e-004,0.090e-004,1.450e-004,90.0,'p',1.500e-3,1.700e-001,125,'t',7.00e-002,"
						+ "7.00e-002,4.200e-002,1,'m',0.700,4.000e-005,'p',";
				for (int i = 0; i < solution.numberOfVariables() - 1; i++) {
					input = input
							+ ((Real) solution.getDecisionVariables()[i])
									.getValue() + ",";
				}
				input = input
						+ (int) ((Real) solution.getDecisionVariables()[2])
								.getValue() + ",";

				input = input + "'ai',13500000.0,313.0,548.0,4.166}";

				proxy.eval(input + ";");
				proxy.eval("sea");
				eff = ((double[]) proxy.getVariable("acteff"))[0];
				power = ((double[]) proxy.getVariable("actWpower"))[0];

			} catch (MatlabInvocationException ob) {
				System.out.print("Matlab Invocation Exception occurred");
			}

			if (eff < 0)
				solution.setObjective(0, 1 / 0.00001);
			else {
				double P0 = 900;
				double DP = 0.05 * P0;
				double fitness;
				if (power <= P0 + DP && power >= P0 - DP) {
					// double gw = 0.05 * Math.cos((power-P0)*;
					double gw = Math.exp(-1.
							/ (1 - Math.pow((power - P0) / DP, 2)))
							/ Math.exp(-1);
					fitness = eff * gw;
					solution.setObjective(0, 1 / fitness);
					// exp(-1./(1-((P-P0)/DP).^2))/exp(-1)
				} else
					solution.setObjective(0, 1 / 0.00001);
			}
		}

	} // evaluate
} // FinDesignProblem
