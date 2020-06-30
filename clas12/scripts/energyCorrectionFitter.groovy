import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.StatNumber
import org.jlab.groot.ui.TCanvas
import org.jlab.groot.data.H1F

TDirectory dir1 = new TDirectory();
dir1.readFile("/w/hallb-scifs17exp/clas12/viducic/rho/clas12/results/energyCorrections.hipo");

GraphErrors graph = new GraphErrors("MassRatioVsEnergy");
graph.setTitle("Pion Mass Ratio vs Photon Energy");

//ArrayList<Double> mass = new ArrayList<>();
//ArrayList<Double> error = new ArrayList<>();
//
//mass.add(0.13425468758675885);
//mass.add(0.1363027099818894);
//mass.add(0.13654244932898696);
//mass.add(0.13691479190231992);
//mass.add(0.13735092286542408);
//mass.add(0.13686670992767505);
//mass.add(0.13759914635520898);
//mass.add(0.13843131323153757);
//mass.add(0.13813582623651843);
//
//error.add(0.0003392);
//error.add(0.0002641);
//error.add(0.0002759);
//error.add(0.0002701);
//error.add(0.0002572);
//error.add(0.0003295);
//error.add(0.0003959);
//error.add(0.0003564);
//error.add(0.0004967);

for (int i = 0; i < 10; i++){
    Double energy = 1.0 + i * 0.17;
    H1F histo = (H1F) dir1.getObject("/PionsBinned/e(gam)=" + energy.toString());

    F1D func = new F1D("f"+i, "[amp]*gaus(x,[mean],[sigme])+[p0]+[p1]*x+[p2]*x*x", 0.1, 0.2);
    DataFitter.fit(func, histo, "");

    TCanvas c1 = new TCanvas("c1", 1000, 1000);
    c1.draw(graph);
    c1.draw(func, "same");

    StatNumber statNumber = new StatNumber(func.parameter(1).value(), func.parameter(1).error());
    statNumber.divide(new StatNumber(0.135, 0.0000005));

    graph.addPoint(energy, statNumber.number(), 0, statNumber.error());
}

F1D func2 = new F1D("correctionFunction", "[p0]+[p1]/x+[p2]/(x*x)", 0.99, 3);
DataFitter.fit(func2, graph, "");


TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.draw(graph);
c1.draw(func2, "same");

