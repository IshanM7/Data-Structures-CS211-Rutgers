
package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/    	
    	StringTokenizer tokenizer = new StringTokenizer(expr, ",()]-+/*1234567890 ");
    	String st = "";    	
    	while(tokenizer.hasMoreTokens()) {
    		st+=tokenizer.nextToken() + " ";
    	}
    	//System.out.println(st);

    	String str = "";
    	int i = 0;
    	boolean contains = false;
    	while(i < st.length()) {
    		
    		for(int x = 0; x < arrays.size(); x++) {
    			if(str.equals(arrays.get(x).name)) {
    				contains = true;
    			}
    		}
    		for(int x = 0; x < vars.size(); x++) {
    			if(str.equals(vars.get(x).name)) {
    				contains = true;
    			}
    		}
    		
    		
    		
    		if(st.charAt(i) == '[') {
    			if(str == "") {
    				i++;
    			} else if (!contains) {
	    			arrays.add(new Array(str));
	    			str = "";
	    			i++;
    			} else {
    				i++; 
    				str = "";
    			}
    			
    		} else if (st.charAt(i) == ' ') {
    			if(str == "") {
					i++;
				} else if(!contains) {					
					vars.add(new Variable(str));
					str = "";
					i++;
				} else {
					i++;
					str = "";
				}
				
			}
    		else {
    			str += st.charAt(i);
    			i++;
    		}
    		contains = false;
    		
       	}
    	
  
    	//System.out.println(arrays);
    	//System.out.println(vars);
    	
    	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    private static boolean isConstant(String token) {
    	try {
    		Float f = Float.parseFloat(token);
    		return true;
    	} catch (Exception e) {
    	
    		return false;
    	}
    }
    
    private static Float getConstant(String token) {
    	Float f = null;
    	
    	try {
    		f = Float.parseFloat(token);
    	} catch (Exception e) { }
    	
    	return f;
    }
    
    private static boolean isOperator(String token) {
    	
		if(token.charAt(0) == '+' || token.charAt(0) == '-' ||token.charAt(0) == '*' ||token.charAt(0) == '/')
			return true;
        	
    	return false;
    }
    
    private static boolean isLowerPrecedence(String current, String previous) {
    	if(current.equals("*") || current.equals("/")) {
    		if(previous.equals("/") || previous.equals("*"))
    			return true;
    		if(previous.equals("+") || previous.equals("-"))
    			return false;
    	} else if(current.equals("+") || current.equals("-")) {
    		if(previous.equals("*") || previous.equals("/") || previous.equals("+") || previous.equals("-"))
    			return true;
    	}
    	
		return false;	
    }
    
    private static Float solve(Float num1, Float num2, String operation) {
    	switch(operation) {
    	case "+":
    		return num1 + num2;
    	case "-":
    		return num1 - num2;
    	case "*":
    		return num1 * num2;
    	case "/":
    		return num1 / num2;
    	}
    	
    	return 0f;
    }
    
    private static boolean isVariable(String str, ArrayList<Variable> vars) {
    	for(int i = 0; i < vars.size(); i++) {
    		if(str.equals(vars.get(i).name)) 
    			return true;
    	}
    	return false;
    }
    
    private static Float getVariable(String str, ArrayList<Variable> vars) {
    	Float f = 0f;
    	for(int i = 0; i < vars.size(); i++) {
    		if(str.equals(vars.get(i).name)) 
    			f =  (float)vars.get(i).value;
    	}
    	return f;
    }
    
    private static boolean isArray(String str, ArrayList<Array> arrays) {
    	for(int i = 0; i < arrays.size(); i++) {
    		if(str.equals(arrays.get(i).name)) 
    			return true;
    	}
    	return false;
    }
    
    private static Array getArray(String str, ArrayList<Array> arrays) {
    	Array arr = null;
    	for(int i = 0; i < arrays.size(); i++) {
    		if(str.equals(arrays.get(i).name)) 
    			return arrays.get(i);
    	}
    	return arr;
    }
    
    
    
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	Stack<Float> numbers = new Stack<Float>();
    	Stack<String> operations = new Stack<String>();
    	expr = expr.replaceAll(" ", "");
    	StringTokenizer st = new StringTokenizer(expr, " \t*+-/()[]", true);
    	boolean parenth = false;
    	boolean arr = false;
    	int pcount = 0;
    	int bcount = 0;
    	String inParenth = "";
    	String inBracket = "";
    	Array array = null;
    	
    	while(st.hasMoreTokens()) {
    		String token = st.nextToken();
    		    		
    		if(parenth) {
    			if(token.equals("("))
    				pcount++;
    			else if(token.equals(")") && pcount > 1)
    				pcount--;
    			else if(token.equals(")") && pcount == 1) {
    				numbers.push(evaluate(inParenth, vars, arrays));
    				pcount--;
    				parenth = false;
    				inParenth = "";
    				continue;
    			}
    			inParenth += token;
    		} else if(arr) {
    			if(token.equals("[") && bcount == 0) {
					bcount++;
					continue;
				} else if(token.equals("["))
					bcount++;
				else if(token.equals("]") && bcount > 1)
					bcount--;
				else if(token.equals("]") && bcount == 1) {
					int index = (int) evaluate(inBracket, vars, arrays);
					numbers.push((float) array.values[index]);
					arr = false;
					array = null;
					bcount--;
					inBracket = "";
					continue;
				}
				inBracket += token;
    		} else {
        		if(isArray(token, arrays)) {
        			arr = true;
        			array = getArray(token, arrays);
        			continue;
        		}
    			if(token.equals("(")) {
    				parenth = true;
    				pcount++;
    				continue;
    			}
    			if(isConstant(token))
    				numbers.push(getConstant(token));
    			if(isVariable(token, vars))
    				numbers.push(getVariable(token, vars));
    			if(isOperator(token)) {
    				if(operations.isEmpty()) {
    					operations.push(token);
    					continue;
	    			}
					boolean x = false;
					while(!operations.isEmpty()) {
						String prevOper = operations.peek();
						if(isLowerPrecedence(token, prevOper)) {
							Float int2 = numbers.pop();
							Float int1 = numbers.pop();
							String operation = operations.pop();
							numbers.push(solve(int1, int2, operation));		
						} else {
							operations.push(token);
							x = true;
							break;
						}
					}
					if(!x) operations.push(token);
	    		}
    		}
    	}
    	
    	while(!operations.isEmpty()) {
    		Float int2 = numbers.pop();
    		Float int1 = numbers.pop();
    		String oper = operations.pop();
    		numbers.push(solve(int1, int2, oper));
    	}
    		
    	return numbers.pop().floatValue();
    }
    
}
    









