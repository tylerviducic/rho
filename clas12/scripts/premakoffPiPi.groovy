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

H1F hEDPyPt = new H1F("hEDPyPt", 100, -0.5, 0.5);
H1F hEDPxPt = new H1F("hEDPxPt", 100, -0.5, 0.5);
H2F hEDPxPyPt = new H2F("hEDPxPyPt", 100, -0.5, 0.5, 100, -0.5, 0.5);
H1F hEDMm2PPipPim = new H1F("hEDMmPPipPim", 100, -0.5, 0.5);
H1F hEDq2 = new H1F("hEDq2", 100, -0.5, 0.5);
H2F hEDImPipPimTheta = new H2F("hImPipPimTheta", 60, 0.5, 1.1, 90, 0, 90);

TDirectory dir = new TDirectory();
dir.mkdir("/ElectronDetected");

//String directory = "/w/hallb-scifs17exp/clas12/rg-a/trains/pass1/v1_4/skim04_inclusive";
String directory = "/lustre19/expphy/cache/clas12/rg-a/production/reconstructed/Fall2018/Torus-1/pass1/v1/005032";

HipoChain reader = new HipoChain();
reader.addDir(directory);
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

    //No Electron detected loop
    if(physicsEvent.getParticleList().count() > 0 &&  physicsEvent.getParticle(0).pid() == -211
            && physicsEvent.countByPid(2212) == 1  && physicsEvent.countByPid(211) == 1
            && physicsEvent.countByCharge(1) == 2 && physicsEvent.countByCharge(-1) == 1){

        noFilterCounter++;

    }

    //Electron detected loop
    if(filter.isValid(physicsEvent) && physicsEvent.getParticleByPid(11, 0).theta() < Math.toRadians(5)) {

        filterCounter++;

        Particle electron = physicsEvent.getParticleByPid(11, 0);
        Particle missingPPipPim = physicsEvent.getParticle("[b] + [t] - [2212] - [211] - [-211]");
        Particle imPipPim = physicsEvent.getParticle("[211] + [-211]");

        double q2 = getQ2(electron, missingPPipPim);
        System.out.println(q2);
        double pyPt = missingPPipPim.py()/missingPPipPim.p();
        double pxPt = missingPPipPim.px()/missingPPipPim.p();
        

        hEDMm2PPipPim.fill(missingPPipPim.mass2());
        hEDPxPt.fill(pxPt);
        hEDPyPt.fill(pyPt);
        hEDPxPyPt.fill(pxPt, pyPt);
        hEDq2.fill(q2);
        hEDImPipPimTheta.fill(imPipPim.mass(), Math.toDegrees(imPipPim.theta()));

    }
}



dir.cd("/ElectronDetected");
dir.addDataSet(hEDPxPyPt, hEDPxPt, hEDPyPt, hEDq2, hEDImPipPimTheta, hEDMm2PPipPim);
dir.writeFile("/w/hallb-scifs17exp/clas12/viducic/premakoff/results/premakoffResults.hipo");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2, 3);
c1.cd(0);
c1.draw(hEDImPipPimTheta);
c1.cd(1);
c1.draw(hEDMm2PPipPim);
c1.cd(2);
c1.draw(hEDPxPt);
c1.cd(3);
c1.draw(hEDPyPt);
c1.cd(4);
c1.draw(hEDPxPyPt);
c1.cd(5);
c1.draw(hEDq2);

System.out.println("++++++++++++++++++++++++++++++++++++");
System.out.println("++++++++++++++++++++++++++++++++++++\n");
System.out.println("Number of p pi+ pi- events with electron: " + filterCounter);
System.out.println("Total number of events = " + eventCounter);
System.out.println("Percentage of events with wanted final state (no e): " + ((double)(filterCounter)/(double)(eventCounter) * 100));
System.out.println("Number of p pi+ pi- events with no electron: " + noFilterCounter);
System.out.println("Percentage of events with wanted final state : " + ((double)(noFilterCounter)/(double)(eventCounter) * 100));

/////////////          Methods              ////////////////

public static double getQ2(Particle particle1, Particle particle2){
    return 4 * particle1.e() * particle2.e() * Math.sin(particle2.theta() /2) * Math.sin(particle2.theta()/2);
}
