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
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Int;
import jmetal.encodings.variable.Real;
import matlabcontrol.*;

/**
 * Class representing problem OneMax. The problem consist of maximizing the
 * number of '1's in a binary string.
 */
public class AddTwoNumber extends Problem {

   	//MatlabProxyFactory factory;
   	//MatlabProxy proxy;
   	
 /**
  * Creates a new OneMax problem instance
  * @param numberOfBits Length of the problem
  */
	 
	
  public AddTwoNumber(String solutionType)  throws ClassNotFoundException,  MatlabConnectionException {
	  numberOfVariables_  = 3;
	    numberOfObjectives_ = 1;
	    numberOfConstraints_= 0;
    
    problemName_        = "AddThreeNumber";
    length_ = new int[3];
    
    //upperLimit_ = new double[2];
    //lowerLimit_ = new double[2];
    
    //lowerLimit_[0]=0;
    //upperLimit_[0]=10;
   
    //lowerLimit_[1]=10.0;
    //upperLimit_[1]=100.0;
    
    for(int i=0;i<numberOfVariables_;i++)
    	length_[i]=3;
    
    
    solutionType_ = new BinarySolutionType(this) ;
    	    
    //variableType_ = new Class[numberOfVariables_] ;
    //length_       = new int[numberOfVariables_];
    //length_      [0] = numberOfBits ;
    
    //VariableType_ 
    
    
    
    
    
    if (solutionType.compareTo("Binary") == 0){
    	solutionType_ = new BinarySolutionType(this) ;
    	
    	//factory = new MatlabProxyFactory();
    	//proxy = factory.getProxy();
    }
    else {
    	System.out.println("OneMax: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }  
    
  } // OneMax
    
 /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
  */      
  public void evaluate(Solution solution) {
    //Int variable1, variable2;
    double result =0.0;
    int    counter  ;
    
    //variable1 = ((Int)solution.getDecisionVariables()[0]) ;
    //variable2 = ((Int)solution.getDecisionVariables()[1]) ;
    
    //int v1=(int) variable1.getValue();
    //int v2=(int) variable2.getValue();
    counter = 0 ;

    for(int i=0;i<numberOfVariables_;i++){
    	String str = solution.getDecisionVariables()[i].toString();
    	int num= Integer.parseInt(str, 2);
    	
    	counter+=num;
    }
   /* for (int i = 0; i < variable.getNumberOfBits() ; i++) 
      if (variable.bits_.get(i) == true)
        counter ++ ;*/
    /*try{
    	//proxy.setVariable("a", v1);
    	//proxy.setVariable("b", v2);
    	//proxy.eval("sum = a+b");
    	//result = ((double[]) proxy.getVariable("sum"))[0];
    }catch(MatlabInvocationException ob){
    	System.out.print("Matlab Invocation Exception occurred");
    }*/
   
    

    // OneMax is a maximization problem: multiply by -1 to minimize
    solution.setObjective(0, 1/counter);            
  } // evaluate
} // OneMax
