import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.H1F
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.FunctionFactory
import org.jlab.groot.ui.TCanvas

String dataFile = "myAnalysis.hipo";

TDirectory readDir = new TDirectory();

readDir.readFile(dataFile);
readDir.cd("/Cuts");

TCanvas c1 = new TCanvas("c1", 500, 500);

H1F h1 = (H1F) readDir.getObject("/Cuts/hMePPipPimpGam");

c1.draw(h1);

F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])", -0.2, 0.03);
f1.setParameter(0, 400.0);
f1.setParameter(1, 0.01);
f1.setParameter(2, 0.0);

DataFitter.fit(f1, h1, "E");

f1.show();
c1.draw(f1, "same");


//H1F h1 = FunctionFactory.randomGausian(80, 0.1D, 0.8D, 8000, 0.6D, 0.1D);
//H1F h2 = FunctionFactory.randomGausian(80, 0.1D, 0.8D, 20000, 0.3D, 0.05D);
//F1D func = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])", 0.1D, 0.8D);
//func.setParameter(0, 10.0D);
//func.setParameter(1, 0.4D);
//func.setParameter(2, 0.2D);
//DataFitter.fit(func, h1, "E");
//func.show();
//
//TCanvas c1 = new TCanvas("c1", 500, 500);
//
//c1.draw(h1)
//c1.draw(func, "same");