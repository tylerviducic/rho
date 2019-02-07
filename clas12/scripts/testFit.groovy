import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.H1F
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.FunctionFactory
import org.jlab.groot.ui.TCanvas

String dataFile = "myAnalysis.hipo";

TDirectory readDir = new TDirectory();

readDir.readFile(dataFile);
readDir.cd("/Signal");

TCanvas c1 = new TCanvas("c1", 500, 500);

H1F h1 = (H1F) readDir.getObject("/Signal/hMxPPipPim");

c1.draw(h1);

//F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma]) + [amp2]*gaus(x,[mean2],[sigma2]) + [p0]*([p1] + [p2]*x + [p3]* x * x)", -0.03, 0.05);
F1D f1 = new F1D("f1","[amp]*gaus(x,[mean],[sigma])",-0.01,0.01);
f1.setParameter(0,120.0);
f1.setParameter(1, 0.0001);
f1.setParameter(2, 0.004);

F1D f2 = new F1D("f2","[amp]*gaus(x,[mean],[sigma])",0.01,0.05);
f2.setParameter(0, 325);
f2.setParameter(1, 0.018);
f2.setParameter(2, 0.005);



//f1.setParameter(0, 150.0);
//f1.setParameter(1, 0.0);
//f1.setParameter(2, 0.005);
//f1.setParameter(3, 340.0);
//f1.setParameter(4, 0.018);
//f1.setParameter(5, 0.001);


DataFitter.fit(f1, h1, "Q");
DataFitter.fit(f2, h1, "Q");


f1.show();
f2.show();
c1.draw(f1, "same");
c1.draw(f2,"same");

