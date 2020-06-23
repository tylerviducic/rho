import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.StatNumber
import org.jlab.groot.ui.TCanvas
import org.jlab.groot.data.H1F

GraphErrors graph = new GraphErrors("MassRatioVsEnergy");
graph.setTitle("MM(e'#pi^0)/M(p) vs Momemtum(e')");

TDirectory protonDir = new TDirectory();
protonDir.readFile("/w/hallb-scifs17exp/clas12/viducic/rho/clas12/results/energyCorrections.hipo");

//ArrayList<Double> mass = new ArrayList<>();
//ArrayList<Double> error = new ArrayList<>();

TDirectory dir = new TDirectory();
dir.mkdir("/Plot");

//mass.add(1.228);
//mass.add(1.052);
//mass.add(1.025);
//mass.add(1.013);
//mass.add(1.02);
//mass.add(1.011);
//mass.add(1.003);
//mass.add(0.948);
//
//
//error.add(5.35E-3);
//error.add(1.696E-3);
//error.add(1.088E-3);
//error.add(7.068E-4);
//error.add(8.107E-4);
//error.add(6.47E-4);
//error.add(4.779E-4);
//error.add(8.2E-4);

TCanvas c2 = new TCanvas("c2", 1000, 1000);
c2.divide(2, 4);

for(int i = 0; i < 8; i++){
    String pEnergy = Double.toString(4.5 + i * 0.5);
    System.out.println(pEnergy);
    H1F protonHisto = (H1F) protonDir.getObject("/ProtonsBinned/p(e)=" + pEnergy);
    F1D protonMeanFunc = new F1D("f1","[amp]*gaus(x,[mean],[sigme])+[p0]+[p1]*x+[p2]*x*x",0.2,1.6);
    protonMeanFunc.setParameter(0, 1000);
    protonMeanFunc.setParameter(1, 1.0);
    protonMeanFunc.setParameter(2, 0.4);

    DataFitter.fit(protonMeanFunc, protonHisto, "");

    c2.cd(i).draw(protonHisto);
    c2.draw(protonMeanFunc, "same");

    StatNumber dataPoint = new StatNumber(protonMeanFunc.parameter(1).value(), protonMeanFunc.parameter(1).error());
    dataPoint.divide(new StatNumber(0.938272, 0.0000058));
    graph.addPoint(4.5 + (i * 0.5), dataPoint.number(), 0, dataPoint.error());
}

dir.cd("/Plot");
dir.addDataSet(graph);
dir.writeFile("/w/hallb-scifs17exp/clas12/viducic/rho/clas12/results/missingProtonResult.hipo");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
System.out.println("printing graph");
c1.draw(graph);

//F1D func = new F1D("func", "[p0] + [p1]/x + [p2]/(x*x) + [p3]/(x*x*x)", 4.5, 8.0);
//F1D funcW = new F1D("funcW", "[p0] + [p1]/x + [p2]/(x*x) + [p3]/(x*x*x)", 4.5, 8.0);
F1D func3 = new F1D("func3", "[p0]+[p1]*x+[p2]*x*x+[p3]*x*x*x", 4.5, 8.0);

func.setLineColor(2);
funcW.setLineColor(4);
func3.setLineColor(3);

//DataFitter.fit(func, graph, "N");
//DataFitter.fit(funcW, graph, "W");
DataFitter.fit(func3, graph, "");

//c1.draw(func, "same");
//c1.draw(funcW, "same");
c1.draw(func3, "same");
