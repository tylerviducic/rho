import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.StatNumber
import org.jlab.groot.ui.TCanvas

TDirectory dir1 = new TDirectory();
dir1.readFile("/w/hallb-scifs17exp/clas12/viducic/rho/clas12/results/energyCorrections2.hipo");

GraphErrors graphErrors = new GraphErrors("graph");
graphErrors.setTitle("M(#gamma#gamma)/M(#pi^0) vs E(#gamma)");

for(int i = 0; i < 6; i++){
    Double energy = 0.4 + i * 0.1;
    String energyString = energy.toString();
    H1F histo = (H1F) dir1.getObject("/PionsBinned/e(gam)=" + energyString);

    StatNumber statNumber = getMass2DataPoint(histo);

    graphErrors.addPoint(energy, statNumber.number(), 0, statNumber.error());
}

for(int i = 0; i < 10; i++){
    Double energy = 2.6 + i * 0.19;
    String energyString = energy.toString();
    H1F histo = (H1F) dir1.getObject("/PionsBinned/e(gam)=" + energyString);

    StatNumber statNumber = getMass2DataPoint(histo);

    graphErrors.addPoint(energy, statNumber.number(), 0, statNumber.error());
}

TDirectory dir2 = new TDirectory();
dir2.readFile("/w/hallb-scifs17exp/clas12/viducic/rho/clas12/results/energyCorrections.hipo");

for(int i = 0; i < 8; i++){
    Double energy = 1 + i * 0.17;
    String energyString = energy.toString();
    H1F histo = (H1F) dir2.getObject("/PionsBinned/e(gam)=" + energyString);

    StatNumber statNumber = getMassDataPoint(histo);

    graphErrors.addPoint(energy, statNumber.number(), 0, statNumber.error());
}

F1D func = new F1D("f1", "[p0]+[p1]/x+[p2]/(x*x)", 0.3, 4.2);
DataFitter.fit(func, graphErrors, "");
func.show();

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.draw(graphErrors);
c1.draw(func, "same");

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public static StatNumber getMass2DataPoint(H1F histo){
    F1D func = new F1D("f1", "[amp]*gaus(x,[mean],[sigme])+[p0]+[p1]*x+[p2]*x*x", 0.01, 0.03);
    func.setParameter(0, histo.getMax());
    func.setParameter(1, 0.018);
    func.setParameter(2, 0.01);

    DataFitter.fit(func, histo, "");

//    TCanvas c1 = new TCanvas("c1", 500, 500);
//    c1.draw(histo);
//    c1.draw(func, "same");

    StatNumber statNumber = new StatNumber(func.parameter(1).value(), func.parameter(1).error());
    statNumber.divide(new StatNumber(0.135 * 0.135, 0.0000005 * 0.0000005));

    return statNumber;
}

public static StatNumber getMassDataPoint(H1F histo){
    F1D func = new F1D("f1", "[amp]*gaus(x,[mean],[sigme])+[p0]+[p1]*x+[p2]*x*x", 0.1, 0.2);
    func.setParameter(0, histo.getMax());
    func.setParameter(1, 0.135);
    func.setParameter(2, 0.01);

    DataFitter.fit(func, histo, "");

    StatNumber statNumber = new StatNumber(func.parameter(1).value(), func.parameter(1).error());
    statNumber.divide(new StatNumber(0.135, 0.0000005));

//    TCanvas c1 = new TCanvas("c1", 500, 500);
//    c1.draw(histo);
//    c1.draw(func, "same");

    return statNumber;
}