import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.Func1D
import org.jlab.groot.ui.TCanvas


String dataFile = "/work/clas12/viducic/data/myAnalysis.hipo";

TDirectory readDir = new TDirectory();

readDir.readFile(dataFile);
readDir.cd("/Signal");

H1F h1 = (H1F) readDir.getObject("/Signal/hMxPPipPim");
//h1.setFunction(null);

DoubleGaus f1 = new DoubleGaus("f1", -0.02, 0.05, -0.01, 0.01, 0.01, 0.03);

f1.setParameter(0, 0, 150);
f1.setParameter(0, 1, 0.0);
f1.setParameter(0, 2, 0.01);

f1.setParameter(1, 0, 400);
f1.setParameter(1, 1, 0.018);
f1.setParameter(1, 2, 0.05);

f1.setParameter(2, 0, 1);
f1.setParameter(2, 1, 1);
f1.setParameter(2, 2, 1);


//TCanvas c1 = new TCanvas("c1", 500, 500);
//c1.draw(h1);
DataFitter.fit(f1, h1, "V");



public class CompositeFunction extends Func1D {

    List<F1D> functions = new ArrayList<F1D>();
    

    public CompositeFunction addFunction(F1D f){
        functions.add(f);
        return this;
    }

    private F1D combineFunctions(){
        String exp = "";
        for(F1D f : functions){
            exp += f.getExpression() + "+";
        }
        exp = exp.substring(0, exp.length()-1);
        return new F1D("f1", exp, getMin(), getMax());
    }

    public double evaluate(double x){
        F1D f1 = combineFunctions();
        f1.setParameters(getParameters());
        //println(f1.evaluate(x));
        return f1.evaluate(x);
    }

//    public double evaluate(double x){
//        double result=0;
//        for(F1D f : functions){
//            result+=f.evaluate(x);
//        }
//        return result;
//    }

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

    private double[] getParameters(){
        List<Double> pars = new ArrayList<Double>();
        for(F1D f : functions){
            //println("For function: " + f.getName() + " - NPars = " + f.getNPars());
            for(int i = 0; i < f.getNPars(); i++){
                pars.add(f.getParameter(i));
            }
        }
        Double[] pars1 = (Double[]) pars.toArray();

        double[] finalPars = new double[pars1.length];
        for(int i = 0; i < finalPars.length; i++){
            finalPars[i] = pars1[i].doubleValue();
        }
        return finalPars;
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
        F1D pol = new F1D("pol", "([p0]x*x+[p1]*x+[p2])", getMin(), getMax());
        addFunction(f1).addFunction(f2).addFunction(pol);
    }


//I can't figure out how to actually fit the function without actually declaring a F1D
//I have a list of functions but DataFitter can't handle that.

}
