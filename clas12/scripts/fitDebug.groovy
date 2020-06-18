import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.ui.TCanvas

TDirectory dir = new TDirectory();
dir.readFile("/w/hallb-scifs17exp/clas12/viducic/rho/clas12/results/missingProtonResult.hipo");
dir.cd();

GraphErrors graph = (GraphErrors) dir.getObject("/Plot/MassRatioVsEnergy");

F1D func = new F1D("func", "[p0]+[p1]*x+[p2]*x*x+[p3]*x*x*x", 4.5, 8.0);
DataFitter.fit(func, graph, "");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.draw(graph);
c1.draw(func, "same");
