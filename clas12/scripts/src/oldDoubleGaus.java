import org.jlab.groot.data.H1F;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.math.F1D;
import org.jlab.groot.ui.TCanvas;


public class DoubleGaus{
    String name;
    double min;
    double max;
    double peak1min;
    double peak1max;
    double peak2min;
    double peak2max;
    H1F histo;
    F1D f4 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma]) + [amp2]*gaus(x,[mean2],[sigma2]) + ([p1] + [p2]*x + [p3]* x * x)", min, max);

    public DoubleGaus(String name, double min, double max, double peak1min, double peak1max, double peak2min, double peak2max, H1F histo) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.peak1min = peak1min;
        this.peak1max = peak1max;
        this.peak2min = peak2min;
        this.peak2max = peak2max;
        this.histo = histo;
    }

    public void fit(){

        F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])", peak1min, peak1max);
        F1D f2 = new F1D("f2", "[amp]*gaus(x,[mean],[sigma])", peak2min, peak2max);
        F1D f3 = new F1D("f3", "[p1] + [p2]*x + [p3]* x * x", min, max);

        double[] param = new double[f1.getNPars() + f2.getNPars() + f3.getNPars()];

        f1.setParameter(0, histo.getBinContent(histo.getxAxis().getBin((peak1max+peak1min)/2)));
        f1.setParameter(1, (peak1max+peak1min)/2);
        f1.setParameter(2, (peak1max- peak1min)/4);

        f2.setParameter(0, histo.getBinContent(histo.getxAxis().getBin((peak2max+peak2min)/2)));
        f2.setParameter(1, (peak2max+peak2min)/2);
        f2.setParameter(2, (peak2max- peak2min)/4);

        f3.setParameter(0, 1);
        f3.setParameter(1,1);
        f3.setParameter(2, 1);

        DataFitter.fit(f1, histo, "E");
        DataFitter.fit(f2, histo, "E");
        DataFitter.fit(f3, histo, "E");

//        for(int k = 0; k < 10; k++){
            for(int i = 0; i < 9; i++){
                if(i < 3){
                    param[i] = f1.getParameter(i);
                }else if(i >= 3 && i < 6){
                    param[i] = f2.getParameter(i - 3);
                } else{
                    param[i] = f3.getParameter(i -6);
                }
            }
            f4.setParameters(param);
            DataFitter.fit(f4, histo, "Q");

//            f1.setParameter(0, f4.getParameter(0));
//            f1.setParameter(1, f4.getParameter(1));
//            f1.setParameter(2, f4.getParameter(2));
//
//            f2.setParameter(0, f4.getParameter(3));
//            f2.setParameter(1, f4.getParameter(4));
//            f2.setParameter(2, f4.getParameter(5));
//
//            f3.setParameter(0, f4.getParameter(6));
//            f3.setParameter(1, f4.getParameter(7));
//            f3.setParameter(2, f4.getParameter(8));
//
//            DataFitter.fit(f1, histo, "Q");
//            DataFitter.fit(f2, histo, "Q");
//            DataFitter.fit(f3, histo, "Q");
//        }
    }

    public void draw(){
        TCanvas c1 = new TCanvas("c1", 500, 500);
        c1.draw(histo);
        c1.draw(f4, "same");

    }

    public void show(){f4.show();}

    public double getChi2(){return f4.getChiSquare();}

    public int getNDF(){return  f4.getNDF();}

    public double getMin() {return min;}

    public double getMax() {return max;}

    public double getPeak1min() {return peak1min;}

    public double getPeak1max() {return peak1max;}

    public double getPeak2min() {return peak2min;}

    public double getPeak2max() {return peak2max;}

    public void setName(String name) {this.name = name; }

    public void setMin(double min) {this.min = min;}

    public void setMax(double max) {this.max = max;}

    public void setPeak1min(double peak1min) {this.peak1min = peak1min;}

    public void setPeak1max(double peak1max) {this.peak1max = peak1max;}

    public void setPeak2min(double peak2min) {this.peak2min = peak2min;}

    public void setPeak2max(double peak2max) {this.peak2max = peak2max;}

    public void setHisto(H1F histo) {this.histo = histo;}
}
