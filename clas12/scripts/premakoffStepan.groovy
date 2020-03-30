import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.ParticleList
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.utils.file.FileUtils

///////////////////////       e detected histos        ///////////////////////

H1F hEDPyPt = new H1F("hEDPyPt", 100, -0.5, 0.5);
hEDPyPt.setTitle("missing pty/missing p");
H1F hEDPxPt = new H1F("hEDPxPt", 100, -0.5, 0.5);
hEDPxPt.setTitle("missing ptx/missing p");
H2F hEDPxPyPt = new H2F("hEDPxPyPt", 100, -0.2, 0.2, 100, -0.2, 0.2);
hEDPxPyPt.setTitle("ptx/p vs pty/p");
H1F hEDMm2EPipPim = new H1F("hEDMmPPipPim", 150, 0.0, 3);
hEDMm2EPipPim.setTitle("Missing mass2 of e'p'pi+pi-");
H1F hEDq2 = new H1F("hEDq2", 50, 0, 0.1);
hEDq2.setTitle("Q2");
H2F hEDImPipPimTheta = new H2F("hEDImPipPimTheta", 150, 0.0, 3, 50, 0, 50);
hEDImPipPimTheta.setTitle("EDIMpi+pi- vs theta of p(pi+pi-)");
H1F hDiffPT = new H1F("hDiffPT", 100, -0.5, 0.5);
hDiffPT.setTitle("Difference between Missing PT and ePT");

ArrayList<H1F> eDImPipPimHistos = new ArrayList<>();

for(int i = 0; i < 10; i++){
    double theta = 2.5 + (double)(i) * 2.5;
    String name = "imPipPimTheta" + theta;
    eDImPipPimHistos.add(new H1F("ed"+name, 70,0.5, 1.2));

}

TDirectory dir = new TDirectory();
dir.mkdir("/ElectronDetected");
dir.mkdir("/ED-IMPipPim_Theta");

String directory = "/w/hallb-scifs17exp/clas12/rg-a/trains/v2/skim4_inclusive"

HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

long eventCounter = 0;
int filterCounter = 0;
int noFilterCounter = 0;

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:22:22:22:22:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);
    eventCounter++;

    if(eventCounter % 1000000 == 0){
        System.out.println("Event counter = " + eventCounter);
    }

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(filter.isValid(physicsEvent)) {
        filterCounter++;
        //ArrayList<Particle> pions = getBestPi0s(physicsEvent);
        Particle electron = physicsEvent.getParticle("[11]");
        Particle pion1 = physicsEvent.getParticle("[22,0] + [22,1]")
        Particle pion2 = physicsEvent.getParticle("[22,2] + [22,3]")
        Particle missingEpi0Pi0 = physicsEvent.getParticle("[b] + [t] - [11] - [22,0] - [22,1] - [22,2] - [22,3]");

        double q2 = getQ2(physicsEvent.beamParticle(), electron);


    }
}

dir.cd("/ElectronDetected");
dir.addDataSet(hEDq2, hEDImPipPimTheta, hEDMm2EPipPim);
//dir.cd("/NoElectronDetected");
//dir.addDataSet(hPxPyPt, hPxPt, hPyPt, hq2, hImPipPimTheta, hMm2PipPim);
dir.cd("/ED-IMPipPim_Theta");
for(H1F histo: eDImPipPimHistos){
    dir.addDataSet(histo);
}
//dir.cd("/IMPipPim_Theta");
//for(H1F histo: imPipPimHistos){
//    dir.addDataSet(histo);
//}
dir.writeFile("/w/hallb-scifs17exp/clas12/viducic/premakoff/results/premakoffResultsStepan.hipo");



System.out.println("++++++++++++++++++++++++++++++++++++");
System.out.println("++++++++++++++++++++++++++++++++++++\n");

System.out.println("Number of p pi+ pi- events with electron: " + filterCounter);
System.out.println("Total number of events = " + eventCounter);
System.out.println("Number of p pi+ pi- events with no electron: " + noFilterCounter);
System.out.println("Number of events with final state + cuts: " + hEDImPipPimTheta.integral());
/////////////           Methods              ////////////////

static ArrayList<Particle>  getBestPi0s(PhysicsEvent myPhysicsEvent){
    ArrayList<Particle> pions = new ArrayList<>();
    int photonCount = myPhysicsEvent.countByPid(22);
    int index1 = -1;
    int index2 = -1;
    for(int i = 0; i < photonCount-1; i++){
        for(int j = i+1; j<photonCount; j++){
                Particle pi01 = Particle.copyFrom(myPhysicsEvent.getParticleByPid(22, i));
                Particle gam2 = Particle.copyFrom(myPhysicsEvent.getParticleByPid(22, j));

                pi01.combine(gam2, 1);
                if (pi01.mass() < 0.16 && pi01.mass() > 0.1) {
                    pions.add(pi01)
                }
        }
    }
    return pions;
}
