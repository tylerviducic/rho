import org.jlab.groot.data.GraphErrors
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.StatNumber
import org.jlab.groot.ui.TCanvas

GraphErrors graph = new GraphErrors("MassRatioVsEnergy");
graph.setTitle("Pion Mass Ratio vs Photon Energy");

ArrayList<Double> mass = new ArrayList<>();
ArrayList<Double> error = new ArrayList<>();

mass.add(0.13425468758675885);
mass.add(0.1363027099818894);
mass.add(0.13654244932898696);
mass.add(0.13691479190231992);
mass.add(0.13735092286542408);
mass.add(0.13686670992767505);
mass.add(0.13759914635520898);
mass.add(0.13843131323153757);
mass.add(0.13813582623651843);

error.add(0.0003392);
error.add(0.0002641);
error.add(0.0002759);
error.add(0.0002701);
error.add(0.0002572);
error.add(0.0003295);
error.add(0.0003959);
error.add(0.0003959);
error.add(0.0003564);
error.add(0.0004967);

for(int i = 0; i < mass.size(); i++){
    StatNumber dataPoint = new StatNumber(mass.get(i), error.get(i));
    dataPoint.divide(new StatNumber(0.135, 0.0000005));
    graph.addPoint(1 + (i * 0.17), dataPoint.number(), 0, dataPoint.error());
}

F1D func = new F1D("func", "[p0] + [p1]/x + [p2]/(x*x) + [p3]/(x*x*x)", 0, 3);
func.setParameter(0, 1);
func.setParameter(1, 1);
func.setParameter(2, 1);
func.setParameter(3, 1);

DataFitter.fit(func, graph, "N");


F1D funcW = new F1D("func", "[p0] + [p1]/x + [p2]/(x*x) + [p3]/(x*x*x)", 0, 3);
funcW.setParameter(0, 1);
funcW.setParameter(1, 1);
funcW.setParameter(2, 1);
funcW.setParameter(3, 1);

DataFitter.fit(funcW, graph, "W");

func.setLineColor(2);
func.setLineColor(4);

TCanvas c1 = new TCanvas("c1", 1000, 1000);
System.out.println("printing graph");
c1.draw(graph);
//c1.draw(func, "same");
//System.out.println("printing fit 1");
//c1.draw(funcW, "same");
//System.out.println("printing fit 2");

//Fit for E(#gamma) = 1.00
//Mean mass = 0.13425468758675885 --- Error = 3.392943496316817E-4
//[fit-benchmark] Time = 0.019 , Iterrations = 477
//Fit for E(#gamma) = 1.17
//Mean mass = 0.1363027099818894 --- Error = 2.641006240861557E-4
//[fit-benchmark] Time = 0.011 , Iterrations = 492
//Fit for E(#gamma) = 1.34
//Mean mass = 0.13654244932898696 --- Error = 2.759337080867641E-4
//[fit-benchmark] Time = 0.011 , Iterrations = 500
//Fit for E(#gamma) = 1.51
//Mean mass = 0.13691479190231992 --- Error = 2.7009250848417815E-4
//[fit-benchmark] Time = 0.011 , Iterrations = 497
//Fit for E(#gamma) = 1.68
//Mean mass = 0.13735092286542408 --- Error = 2.572143850395676E-4
//[fit-benchmark] Time = 0.009 , Iterrations = 499
//Fit for E(#gamma) = 1.85
//Mean mass = 0.13686670992767505 --- Error = 3.2948972804102864E-4
//[fit-benchmark] Time = 0.011 , Iterrations = 546
//Fit for E(#gamma) = 2.02
//Mean mass = 0.13759914635520898 --- Error = 3.9588158298374597E-4
//[fit-benchmark] Time = 0.060 , Iterrations = 588
//Fit for E(#gamma) = 2.19
//Mean mass = 0.13843131323153757 --- Error = 3.563828837928601E-4
//[fit-benchmark] Time = 0.009 , Iterrations = 714
//Fit for E(#gamma) = 2.36
//Mean mass = 0.13813582623651843 --- Error = 4.966868550319091E-4
//[fit-benchmark] Time = 0.008 , Iterrations = 745
//Fit for E(#gamma) = 2.53
//Mean mass = 0.14167637098214056 --- Error = 4.047111634769342E-4