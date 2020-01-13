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
H2F hEDImPipPimTheta = new H2F("hEDImPipPimTheta", 60, 0.5, 1.1, 50, 0, 50);
hEDImPipPimTheta.setTitle("EDIMpi+pi- vs theta of p(pi+pi-)");
H1F hDiffPT = new H1F("hDiffPT", 100, -0.5, 0.5);
hDiffPT.setTitle("Difference between Missing PT and ePT");
H1F hEDmmEPPipPim = new H1F("hEDmmEPPipPim", 100, -0.5, 0.5);
hEDmmEPPipPim.setTitle("Missing mass of electron, proton, pip pim");


///////////////////////      no e detected histos       ///////////////////////

H1F hPyPt = new H1F("hPyPt", 100, -0.5, 0.5);
hPyPt.setTitle("missing pty/missing p");
H1F hPxPt = new H1F("hPxPt", 100, -0.5, 0.5);
hPxPt.setTitle("missing ptx/missing p");
H2F hPxPyPt = new H2F("hPxPyPt", 100, -0.2, 0.2, 100, -0.2, 0.2);
hPxPyPt.setTitle("ptx/p vs pty/p");
H1F hMm2PipPim = new H1F("hMmPPipPim", 100, -0.05, 0.05);
hMm2PipPim.setTitle("Missing mass2 of e'p'pi+pi-");
H1F hq2 = new H1F("hq2", 50, 0, 0.1);
hq2.setTitle("Q2");
H2F hImPipPimTheta = new H2F("hImPipPimTheta", 60, 0.5, 1.1, 50, 0, 50);
hImPipPimTheta.setTitle("IMpi+pi- vs theta of p(pi+pi-)");

ArrayList<H1F> imPipPimHistos = new ArrayList<>();
ArrayList<H1F> eDImPipPimHistos = new ArrayList<>();

for(int i = 0; i < 10; i++){
    double theta = 2.5 + (double)(i) * 2.5;
    String name = "imPipPimTheta" + theta;
    imPipPimHistos.add(new H1F(name, 70,0.5, 1.2));

    eDImPipPimHistos.add(new H1F("ed"+name, 70,0.5, 1.2));

}

TDirectory dir = new TDirectory();
dir.mkdir("/ElectronDetected");
dir.mkdir("/NoElectronDetected");
dir.mkdir("/ED-IMPipPim_Theta");
dir.mkdir("/IMPipPim_Theta");

//String directory = "/lustre19/expphy/cache/clas12/rg-a/production/reconstructed/Fall2018/Torus-1/pass1/v1/";
//String directory = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff";
//List<String> files = FileUtils.getFilesInDirectoryRecursive(directory, "eDetectedPremakoff_noP*");

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/eDetectedPremakoff_noP.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

long eventCounter = 0;
int filterCounter = 0;
int noFilterCounter = 0;

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:-211:211:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);
    eventCounter++;

    if(eventCounter % 1000000 == 0){
        System.out.println("Event counter = " + eventCounter);
    }

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

////////////////////    No Electron detected loop    /////////////////////////
//    if(physicsEvent.getParticleList().count() > 0 &&  physicsEvent.getParticle(0).pid() == -211
//        && physicsEvent.countByPid(211) == 1 && physicsEvent.countByCharge(1) == 1
//        && physicsEvent.countByCharge(-1) == 1 && inForward(physicsEvent)){
//
//        noFilterCounter++;
//
//        Particle missingPipPim = physicsEvent.getParticle("[b] + [t] - [211] - [-211]");
//        Particle imPipPim = physicsEvent.getParticle("[211] + [-211]");
//
//        double q2 = getQ2(physicsEvent.beamParticle(), missingPipPim);
//        double pyPt = missingPipPim.py()/missingPipPim.p();
//        double pxPt = missingPipPim.px()/missingPipPim.p();
//
//        hq2.fill(q2);
//        if (q2 < 0.02){
//            hPxPt.fill(pxPt);
//            hPyPt.fill(pyPt);
//            hPxPyPt.fill(pxPt, pyPt);
//            hMm2PipPim.fill(missingPipPim.mass2());
//
//        }
//
//        if(Math.abs(missingPipPim.mass2()) < 0.02 && Math.abs(pyPt) < 0.2 && Math.abs(pxPt) < 0.2
//            && Math.abs(missingPipPim.mass2()) < 0.02 ) {
//            hImPipPimTheta.fill(imPipPim.mass(), Math.toDegrees(imPipPim.theta()));
//
//            if (Math.toDegrees(imPipPim.theta())< 25){
//                imPipPimHistos.get(getBinIndex(imPipPim)).fill(imPipPim.mass());
//            }
//        }
//    }

////////////////////       Electron detected loop      /////////////////////////
    //else
    if(filter.isValid(physicsEvent) && physicsEvent.getParticleByPid(11, 0).theta() < Math.toRadians(5)
            && inForward(physicsEvent)) {

        filterCounter++;

        Particle electron = physicsEvent.getParticle("[11]");
        Particle beamTarget = physicsEvent.getParticle("[b] + [t]");
        Particle ePipPim = physicsEvent.getParticle("[11] + [211] + [-211]");
        Particle missingEPipPim = physicsEvent.getParticle("[b] + [t] - [211] - [-211] - [11]");
        Particle missingPipPim = physicsEvent.getParticle("[b] + [t] - [211] - [-211]");
        Particle imPipPim = physicsEvent.getParticle("[211] + [-211]");

        Particle missingEPPipPim = new Particle();
        missingEPPipPim.copy(beamTarget);
        missingEPPipPim.combine(ePipPim, -1);
        missingEPPipPim.combine(missingEPipPim, -1);

        double q2 = getQ2(physicsEvent.beamParticle(), electron);
        double pyPt = missingPipPim.py()/missingPipPim.p();
        double pxPt = missingPipPim.px()/missingPipPim.p();
        double missingPT = Math.sqrt(missingPipPim.px()*missingPipPim.px() + missingPipPim.py()*missingPipPim.py());
        double ePT = Math.sqrt(electron.px() * electron.px() + electron.py()*electron.py());


        hEDMm2EPipPim.fill(missingEPipPim.mass2());
        if(missingEPipPim.mass2() < 1.4 && missingEPipPim.mass2() > 0.7) {
            hEDPxPt.fill(pxPt);
            hEDPyPt.fill(pyPt);
            hEDPxPyPt.fill(pxPt, pyPt);
            hEDq2.fill(q2);
            hDiffPT.fill(missingPT-ePT);
            hEDmmEPPipPim.fill(missingEPPipPim.mass2());

            if(q2 < 0.02 && Math.abs(pxPt) < 0.2 && Math.abs(pyPt) < 0.2 && Math.abs(missingPT - ePT) < 0.4){
                System.out.println("filling");
                hEDImPipPimTheta.fill(imPipPim.mass(), imPipPim.theta());
                if (Math.toDegrees(imPipPim.theta())< 25){
                    eDImPipPimHistos.get(getBinIndex(imPipPim)).fill(imPipPim.mass());
                }
            }
        }

    }
}

dir.cd("/ElectronDetected");
dir.addDataSet(hEDPxPyPt, hEDPxPt, hEDPyPt, hEDq2, hEDImPipPimTheta, hEDMm2EPipPim, hDiffPT, hEDmmEPPipPim);
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
dir.writeFile("/w/hallb-scifs17exp/clas12/viducic/premakoff/results/premakoffResults_noP.hipo");

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
