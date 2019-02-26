import org.jlab.groot.math.F1D;
import org.jlab.groot.math.Func1D;

import java.util.ArrayList;
import java.util.List;

public class CompositeFunction extends Func1D {

    List<F1D> functions = new ArrayList<F1D>();

    public CompositeFunction addFunction(F1D f){
        functions.add(f);
        return this;
    }

    public String testEval(){
        String exp = "";
        for (F1D func : functions){
            exp += func.getExpression() + "+";
        }
        exp = exp.substring(0, exp.length()-1);
        return exp;
    }

    public double evaluate(double value){
        String exp = "";
        for (F1D func : functions){
            exp += func.getExpression() + "+";
        }
        exp = exp.substring(0, exp.length()-1);
        F1D f1 = new F1D ("f1", exp, getMin(), getMax());
        return f1.evaluate(value);
    }

    public F1D getFunction(int index){
        return functions.get(index);
    }

    public void estimator(){}

    public CompositeFunction(String name, double min, double max) {
        super(name, min, max);
    }

}


//How should this work?
//