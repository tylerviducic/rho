import org.jlab.groot.math.F1D;

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
        F1D pol = new F1D("pol", "[p0]x*x+[p1]*x+[p2]", getMin(), getMax());
        addFunction(f1);
        addFunction(f2);
        addFunction(pol);
    }


}
