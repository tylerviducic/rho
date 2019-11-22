import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas

///////////////////////       e detected histos        ///////////////////////

H1F hEDPyPt = new H1F("hEDPyPt", 100, -0.5, 0.5);
hEDPyPt.setTitle("missing pty/missing p");
H1F hEDPxPt = new H1F("hEDPxPt", 100, -0.5, 0.5);
hEDPxPt.setTitle("missing ptx/missing p");
H2F hEDPxPyPt = new H2F("hEDPxPyPt", 100, -0.2, 0.2, 100, -0.2, 0.2);
hEDPxPyPt.setTitle("ptx/p vs pty/p");
H1F hEDMm2EPPipPim = new H1F("hEDMmPPipPim", 100, -0.05, 0.05);
hEDMm2EPPipPim.setTitle("Missing mass2 of e'p'pi+pi-");
H1F hEDq2 = new H1F("hEDq2", 50, 0, 0.1);
hEDq2.setTitle("Q2");
H2F hEDImPipPimTheta = new H2F("hImPipPimTheta", 60, 0.5, 1.1, 50, 0, 50);
hEDImPipPimTheta.setTitle("EDIMpi+pi- vs theta of p(pi+pi-)");
H1F hDiffPT = new H1F("hDiffPT", 100, -0.5, 0.5);
hDiffPT.setTitle("Difference between Missing PT and ePT");

///////////////////////      no e detected histos       ///////////////////////

H1F hPyPt = new H1F("hPyPt", 100, -0.5, 0.5);
hPyPt.setTitle("missing pty/missing p");
H1F hPxPt = new H1F("hPxPt", 100, -0.5, 0.5);
hPxPt.setTitle("missing ptx/missing p");
H2F hPxPyPt = new H2F("hPxPyPt", 100, -0.2, 0.2, 100, -0.2, 0.2);
hPxPyPt.setTitle("ptx/p vs pty/p");
H1F hMm2PPipPim = new H1F("hMmPPipPim", 100, -0.05, 0.05);
hMm2PPipPim.setTitle("Missing mass2 of e'p'pi+pi-");
H1F hq2 = new H1F("hq2", 50, 0, 0.1);
hq2.setTitle("Q2");
H2F hImPipPimTheta = new H2F("hImPipPimTheta", 60, 0.5, 1.1, 50, 0, 50);
hImPipPimTheta.setTitle("IMpi+pi- vs theta of p(pi+pi-)");


TDirectory dir = new TDirectory();
dir.mkdir("/ElectronDetected");
dir.mkdir("NoElectronDetected");

//String directory = "/w/hallb-scifs17exp/clas12/rg-a/trains/pass1/v1_4/skim04_inclusive";
String subDirectory = "/lustre19/expphy/cache/clas12/rg-a/production/reconstructed/Fall2018/Torus-1/pass1/v1/005";
ArrayList<String> runs = new ArrayList<>();
runs.add("169");
runs.add("163");
runs.add("252");
runs.add("258");
runs.add("126");
runs.add("204");
runs.add("038");
runs.add("199");
runs.add("203");
runs.add("117");


HipoChain reader = new HipoChain();
for(String run: runs) {
    String directory = subDirectory + run;
    System.out.println(directory);
    reader.addDir(directory);
}
reader.open();

int eventCounter = 0;
int filterCounter = 0;
int noFilterCounter = 0;

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:2212:-211:211:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);
    eventCounter++;

    if(eventCounter % 100000 == 0){
        System.out.println("Event counter = " + eventCounter);
    }

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

////////////////////    No Electron detected loop    /////////////////////////
    if(physicsEvent.getParticleList().count() > 0 &&  physicsEvent.getParticle(0).pid() == -211
        && physicsEvent.countByPid(2212) == 1  && physicsEvent.countByPid(211) == 1
        && physicsEvent.countByCharge(1) == 2 && physicsEvent.countByCharge(-1) == 1){

        noFilterCounter++;

        Particle missingPPipPim = physicsEvent.getParticle("[b] + [t] - [2212] - [211] - [-211]");
        Particle imPipPim = physicsEvent.getParticle("[211] + [-211]");

        double q2 = getQ2(physicsEvent.beamParticle(), missingPPipPim);
        double pyPt = missingPPipPim.py()/missingPPipPim.p();
        double pxPt = missingPPipPim.px()/missingPPipPim.p();

        hMm2PPipPim.fill(missingPPipPim.mass2());
        if (Math.abs(missingPPipPim.mass2()) < 0.02){
            hPxPt.fill(pxPt);
            hPyPt.fill(pyPt);
            hPxPyPt.fill(pxPt, pyPt);
            hq2.fill(q2);
        }

        if(q2 < 0.02 && Math.abs(pyPt) < 0.2 && Math.abs(pxPt) < 0.2
            && Math.abs(missingPPipPim.mass2()) < 0.02 ) {
            hImPipPimTheta.fill(imPipPim.mass(), Math.toDegrees(imPipPim.theta()));
        }
    }

////////////////////       Electron detected loop      /////////////////////////
    else if(filter.isValid(physicsEvent) && physicsEvent.getParticleByPid(11, 0).theta() < Math.toRadians(5)) {

        filterCounter++;

        Particle electron = physicsEvent.getParticle("[11]");
        Particle missingEPPipPim = physicsEvent.getParticle("[b] + [t] - [2212] - [211] - [-211] - [11]");
        Particle missingPPipPim = physicsEvent.getParticle("[b] + [t] - [2212] - [211] - [-211]");
        Particle imPipPim = physicsEvent.getParticle("[211] + [-211]");

        double q2 = getQ2(physicsEvent.beamParticle(), electron);
        double pyPt = missingPPipPim.py()/missingPPipPim.p();
        double pxPt = missingPPipPim.px()/missingPPipPim.p();
        double missingPT = Math.sqrt(missingPPipPim.px()*missingPPipPim.px() + missingPPipPim.py()*missingPPipPim.py());
        double ePT = Math.sqrt(electron.px() * electron.px() + electron.py()*electron.py());

        hEDMm2EPPipPim.fill(missingEPPipPim.mass2());
        if(Math.abs(missingEPPipPim.mass2()) < 0.02) {
            hEDPxPt.fill(pxPt);
            hEDPyPt.fill(pyPt);
            hEDPxPyPt.fill(pxPt, pyPt);
            hEDq2.fill(q2);
            hDiffPT.fill(missingPT-ePT);
        }

        if(q2 < 0.02 && Math.abs(pyPt) < 0.2 && Math.abs(pxPt) < 0.2
            && Math.abs(missingEPPipPim.mass2()) < 0.02 ) {
            hEDImPipPimTheta.fill(imPipPim.mass(), Math.toDegrees(imPipPim.theta()));
        }
    }
}

dir.cd("/ElectronDetected");
dir.addDataSet(hEDPxPyPt, hEDPxPt, hEDPyPt, hEDq2, hEDImPipPimTheta, hEDMm2EPPipPim);
dir.cd("/NoElectronDetected");
dir.addDataSet(hPxPyPt, hPxPt, hPyPt, hq2, hImPipPimTheta, hMm2EPPipPim);
dir.writeFile("/w/hallb-scifs17exp/clas12/viducic/premakoff/results/premakoffResults.hipo");

//TCanvas c1 = new TCanvas("c1", 1000, 1000);
//c1.divide(2, 3);
//c1.cd(0);
//c1.draw(hEDImPipPimTheta);
//c1.cd(1);
//c1.draw(hEDMm2EPPipPim);
//c1.cd(2);
//c1.draw(hEDPxPt);
//c1.cd(3);
//c1.draw(hEDPyPt);
//c1.cd(4);
//c1.draw(hEDPxPyPt);
//c1.cd(5);
//c1.draw(hEDq2);

System.out.println("++++++++++++++++++++++++++++++++++++");
System.out.println("++++++++++++++++++++++++++++++++++++\n");

System.out.println("Number of p pi+ pi- events with electron: " + filterCounter);
System.out.println("Total number of events = " + eventCounter);
System.out.println("Number of p pi+ pi- events with no electron: " + noFilterCounter);
System.out.println("Number of events with final state + cuts: " + hEDImPipPimTheta.integral());
/////////////           Methods              ////////////////

public static double getQ2(Particle particle1, Particle particle2){
    return 4 * particle1.e() * particle2.e() * Math.sin(particle2.theta() /2) * Math.sin(particle2.theta()/2);
}
