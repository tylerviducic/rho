import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F


// ------------------------------------------  Histograms ------------------------------------------------
H1F hMissingMassEPi0Pi0 = new H1F("MissingMassEPi0Pi0", 100, 0, 2);
hMissingMassEPi0Pi0.setTitle("Missing mass of e'#gamma#gamma");

H1F hIMGamGam = new H1F("IMGamGam", 100, 0, 0.3);
hIMGamGam.setTitle("Invariant mass of #gamma#gamma");

H2F hIMGamGamVSMM = new H2F("IMGamGamVSMM", 100, 0, 0.3, 100, 0, 2);
hIMGamGamVSMM.setTitle("IM(#gamma#gamma) vs MM(e#gamma#gamma)");
hIMGamGamVSMM.setTitleX("IM(#gamma#gamma)");
hIMGamGamVSMM.setTitleY("MM(e#gamma#gamma");

H2F hIMGamGamVSMissingP = new H2F("IMGamGamVSMissingP", 100, 0, 0.3, 100, 0, 1);
hIMGamGamVSMissingP.setTitle("IM(#gamma#gamma) vs Missing Momentum of e' #gamma#gamma");
hIMGamGamVSMissingP.setTitleX("IM(#gamma#gamma)");
hIMGamGamVSMissingP.setTitleY("Missing Momentum of e' #gamma#gamma");

H2F hMMvsMP = new H2F("MMvsMP", 100, 0, 2, 100, 0, 5);
hMMvsMP.setTitle("MM(e'#gamma#gamma) vs MP(e'#gamma#gamma)");
hMMvsMP.setTitleX("MM(e'#gamma#gamma)");
hMMvsMP.setTitleY("MP(e'#gamma#gamma)");


// ------------------------------------------              ------------------------------------------------


TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(3, 2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hIMGamGam);
c1.cd(1).draw(hMissingMassEPi0Pi0);
c1.cd(2).draw(hIMGamGamVSMM);
c1.cd(3).draw(hIMGamGamVSMissingP);
c1.cd(4).draw(hMMvsMP);

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim4.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter eventFilter = new EventFilter("11:22:22:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.614, particle);

    Particle pi0 = getBestPi0(physicsEvent);
    Particle missingEPi0Pi0 = physicsEvent.getParticle("[b] + [t] - [11]");

    missingEPi0Pi0.combine(Particle.copyFrom(pi0), -1);

    hMMvsMP.fill(missingEPi0Pi0.mass(), missingEPi0Pi0.p());

    if(missingEPi0Pi0.p() < 1.0){
        hMissingMassEPi0Pi0.fill(missingEPi0Pi0.mass());
        hIMGamGamVSMM.fill(pi0.mass(), missingEPi0Pi0.mass());
        if(missingEPi0Pi0.mass() > 0.8 && missingEPi0Pi0.mass() < 1.1){
            hIMGamGam.fill(pi0.mass());
            hIMGamGamVSMissingP.fill(pi0.mass(), missingEPi0Pi0.p());
        }
    }
}

System.out.println("done");


public static Particle getBestPi0(PhysicsEvent physicsEvent){
    int numPhotons = physicsEvent.countByPid(22);

    for(int i = 0; i < numPhotons - 1; i++){
        Particle photon1 = physicsEvent.getParticleByPid(22, i);

        for (int j = i + 1; j < numPhotons; j++){
            Particle pi0 = Particle.copyFrom(photon1);
            pi0.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22, j)), 1);
            if (pi0.mass() > 0.12 && pi0.mass() < 0.15){
                return pi0;
            }
        }
    }
    Particle pi0 = Particle.copyFrom(physicsEvent.getParticleByPid(22, 0));
    pi0.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22,1)), 1);
    return pi0;
}