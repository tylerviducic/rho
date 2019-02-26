import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.Func1D


String dataFile = "myAnalysis.hipo";

TDirectory readDir = new TDirectory();

readDir.readFile(dataFile);
readDir.cd("/Signal");

H1F h1 = (H1F) readDir.getObject("/Signal/hMxPPipPim");
h1.setFunction(null);

DoubleGaus f1 = new DoubleGaus("f1", -0.02, 0.05, -0.01, 0.01, 0.01, 0.03);
f1.getFunction(0).show();
f1.getFunction(1).show();
f1.getFunction(2).show();

println(f1.testEval());

//TCanvas c1 = new TCanvas("c1", 500, 500);
//c1.draw(h1);
f1.show();











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

    public void setParameter(int funcIndex, int paramIndex, double value){
        functions.get(funcIndex).setParameter(paramIndex, value);
    }



}


public class DoubleGaus extends CompositeFunction{
    double min1;
    double max1;
    double min2;
    double max2;

    public DoubleGaus(String name, double min, double max, double min1, double max1, double min2, double max2) {
        super(name, min, max);
        this.min1 = min1;
        this.max1 = max1;
        this.min2 = min2;
        this.max2 = max2;

        F1D f1 = new F1D("f1", "[amp1]*gaus(x,[mean1],[sigma1])", min1, max1);
        F1D f2 = new F1D("f2", "[amp2]*gaus(x,[mean2],[sigma2])", min2, max2);
        F1D pol = new F1D("pol", "[p0]x * x + [p1]*x + [p2]", getMin(), getMax());
        addFunction(f1).addFunction(f2).addFunction(pol);
    }




}
