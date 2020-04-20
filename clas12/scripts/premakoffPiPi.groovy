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

H1F hEDMm2EPipPim = new H1F("hEDMmPPipPim", 150, 0.0, 1.5);
hEDMm2EPipPim.setTitle("Missing mass2 of e'p'pi+pi-");

H1F hEDq2 = new H1F("hEDq2", 50, 0, 0.1);
hEDq2.setTitle("Q2");

H2F hEDImPipPimTheta = new H2F("hEDImPipPimTheta", 150, 0.0, 1.5, 50, 0, 50);
hEDImPipPimTheta.setTitle("EDIMpi+pi- vs theta of p(pi+pi-)");

H1F hIMPiPi = new H1F("imPiPi", 100, 0.5, 1.5);
hIMPiPi.setTitle("Invariant mass of #pi^+#pi^-");

H1F hDiffPT = new H1F("hDiffPT", 100, -0.5, 0.5);
hDiffPT.setTitle("Difference between Missing PT and ePT");

H1F hMP = new H1F("mp", 100, 0, 5);
hMP.setTitle("Missing momentum of e #pi^+#pi^-");

ArrayList<H1F> imPipPimHistos = new ArrayList<>();
ArrayList<H1F> eDImPipPimHistos = new ArrayList<>();

for(int i = 0; i < 10; i++){
    double theta = 2.5 + (double)(i) * 2.5;
    String name = "imPipPimTheta" + theta;
    imPipPimHistos.add(new H1F(name, 70,0.5, 1.2));

    eDImPipPimHistos.add(new H1F("ed"+name, 70,0.5, 1.2));

}

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2,2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hEDMm2EPipPim);
c1.cd(1).draw(hIMPiPi);
c1.cd(2).draw(hMP);
c1.cd(3).draw(hEDImPipPimTheta);

TDirectory dir = new TDirectory();
dir.mkdir("/ElectronDetected");
dir.mkdir("/NoElectronDetected");
dir.mkdir("/ED-IMPipPim_Theta");
dir.mkdir("/IMPipPim_Theta");

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/eDetectedPremakoff_noP.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();


Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:-211:211:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

////////////////////       Electron detected loop      /////////////////////////
    if(filter.isValid(physicsEvent)) {

        Particle electron = physicsEvent.getParticle("[11]");
        Particle missingEPipPim = physicsEvent.getParticle("[b] + [t] - [211] - [-211] - [11]");
        Particle imPipPim = physicsEvent.getParticle("[211] + [-211]");

        double q2 = getQ2(physicsEvent.beamParticle(), electron);
        hMP.fill(missingEPipPim.p());
        if(missingEPipPim.p() < 1.0) {
            hEDMm2EPipPim.fill(missingEPipPim.mass());

            if (missingEPipPim.mass2() < 1.0 && missingEPipPim.mass2() > 0.8) {
//            hEDPxPt.fill(pxPt);
//            hEDPyPt.fill(pyPt);
//            hEDPxPyPt.fill(pxPt, pyPt);
                hEDq2.fill(q2);
//            hDiffPT.fill(missingPT-ePT);

                hEDImPipPimTheta.fill(imPipPim.mass(), Math.toDegrees(imPipPim.theta()));
                hIMPiPi.fill(imPipPim.mass());
                if (Math.toDegrees(imPipPim.theta()) < 25) {
                    eDImPipPimHistos.get(getBinIndex(imPipPim)).fill(imPipPim.mass());
                }
            }
        }
    }
}

dir.cd("/ElectronDetected");
dir.addDataSet(hEDq2, hEDImPipPimTheta, hEDMm2EPipPim);
dir.cd("/ED-IMPipPim_Theta");
for(H1F histo: eDImPipPimHistos){
    dir.addDataSet(histo);
}
dir.writeFile("/w/hallb-scifs17exp/clas12/viducic/premakoff/results/premakoffResults_noP.hipo");

/////////////           Methods              ////////////////

public static double getQ2(Particle particle1, Particle particle2){
    return 4 * particle1.e() * particle2.e() * Math.sin(particle2.theta() /2) * Math.sin(particle2.theta()/2);
}

public static int getBinIndex(Particle particle){
    return (int)(Math.toDegrees(particle.theta())/2.5);
}

public static boolean inForward(PhysicsEvent physicsEvent){
    ParticleList particleList = physicsEvent.getParticleList();
    for(int i = 0; i < particleList.count(); i++){
        if (Math.toDegrees(particleList.get(i).theta()) > 35 && particleList.get(i).pid() != 2212){
            return false;
        }
    }
    return true;
}



