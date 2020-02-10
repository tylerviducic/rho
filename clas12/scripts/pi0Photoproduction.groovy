import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H1F hPi0 = new H1F("pi0", 50, 0, 0.25);
hPi0.setTitle("Invariant mass gamma gamma");
H1F hMissingPE = new H1F("missingPE", 100, 0, 0.25);
hMissingPE.setTitle("missing mass of proton-electron");
H1F hMissingPEGamGam = new H1F("hMissingPEGamGam", 100, -0.1, 0.1);
hMissingPEGamGam.setTitle("Missing mass^2 of proton, electron, and first two photons");
H1F hMissingEnergy = new H1F("missingEnergy", 80, 0, 0.4);
hMissingEnergy.setTitle("missing momentum of proton, electron, and first two photons");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2, 2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hPi0);
c1.cd(1).draw(hMissingPE);
c1.cd(2).draw(hMissingPEGamGam);
c1.cd(3).draw(hMissingEnergy);

String directory = "/work/clas12/viducic/data/clas12/pion/forward/";

HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);
    //Particle pi0 = physicsEvent.getParticle("[22,0] + [22,1]");
    Particle pi0 = getBestPi0(physicsEvent);
    Particle missingPE = physicsEvent.getParticle("[b] + [t] - [2212] - [11]");
    Particle missingPEGamGam = physicsEvent.getParticle("[b] + [t] - [2212] - [11] - [22,0] - [22,1]");


    hMissingPEGamGam.fill(missingPEGamGam.mass2());
    hPi0.fill(pi0.mass());
    hMissingEnergy.fill(missingPEGamGam.p());
    if(pi0.mass() > 0.1 && pi0.mass() < 0.16 && Math.abs(missingPEGamGam.mass2()) < 0.02 && missingPEGamGam.p() < 0.1){
        hMissingPE.fill(missingPE.mass());
    }
}


System.out.println("done");


/////// Methods /////////

static Particle getBestPi0(PhysicsEvent physicsEvent){
    int photonCount = physicsEvent.countByPid(22);
    for(int i = 0; i < photonCount-1; i++){
        for(int j = i+1; j<photonCount; j++){
            Particle pi0 = physicsEvent.getParticleByPid(22, i);
            Particle gam2 = physicsEvent.getParticleByPid(2, j);
             pi0.combine(gam2, 1);

            if(pi0.mass() < 0.16 && pi0.mass() > 0.1){
                return pi0
            }
        }
    }
    return physicsEvent.getParticle("[22,0]+[22,1]");
}
