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

H2F hMMvsMP = new H2F("MMvsMP", 40, 0.8, 1.2, 100, 0, 5);
hMMvsMP.setTitle("MM(e'#gamma#gamma) vs MP(e'#gamma#gamma)");
hMMvsMP.setTitleX("MM(e'#gamma#gamma)");
hMMvsMP.setTitleY("MP(e'#gamma#gamma)");

H2F hGamGamPvsTheta = new H2F("GamGamPvsTheta", 200, 0, 6, 35, 0, 35);
hGamGamPvsTheta.setTitle("Momentum of #gamma#gamma vs opening angle between them");
hGamGamPvsTheta.setTitleX("P(#gamma#gamma)");
hGamGamPvsTheta.setTitleY("#theta(#gamma#gamma)");

H1F hEGamGam = new H1F("eGamGam", 100, 0, 2.5);
hEGamGam.setTitle("Energy of photons with same energy");
hEGamGam.setTitleX("E(#gamma");


// ------------------------------------------              ------------------------------------------------


TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(3, 2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hIMGamGam);
c1.cd(1).draw(hMissingMassEPi0Pi0);
c1.cd(2).draw(hIMGamGamVSMM);
c1.cd(3).draw(hGamGamPvsTheta);
c1.cd(4).draw(hMMvsMP);
c1.cd(5).draw(hEGamGam);

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim4.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

//EventFilter eventFilter = new EventFilter("11:22:22:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.614, particle);

    ArrayList<Integer> photons = getBestPhotons(physicsEvent);
    Particle photon1 = physicsEvent.getParticleByPid(22, photons.get(0));
    Particle photon2 = physicsEvent.getParticleByPid(22, photons.get(1));

    double photonTheta = Math.toDegrees(Math.acos(photon1.cosTheta(photon2)));
    double imGamGam = getPhotonIM(photon1, photon2);

    Particle pi0 = Particle.copyFrom(photon1);
    pi0.combine(Particle.copyFrom(photon2), 1);

    Particle missingEPi0Pi0 = physicsEvent.getParticle("[b] + [t] - [11]");

    missingEPi0Pi0.combine(Particle.copyFrom(pi0), -1);

    hMMvsMP.fill(missingEPi0Pi0.mass(), missingEPi0Pi0.p());

    if(missingEPi0Pi0.p() < 1.0){
        hMissingMassEPi0Pi0.fill(missingEPi0Pi0.mass());
        hIMGamGamVSMM.fill(pi0.mass(), missingEPi0Pi0.mass());
        if(missingEPi0Pi0.mass() > 0.8 && missingEPi0Pi0.mass() < 1.1){
            //hIMGamGamVSMissingP.fill(pi0.mass(), missingEPi0Pi0.p());
            hGamGamPvsTheta.fill(pi0.p(), photonTheta);
            if(pi0.p() > 2 && pi0.p() < 5.5 && photonTheta < 10 && photonTheta > 3){
                hIMGamGam.fill(imGamGam);
                if(photon1.e()/ photon2.e() < 1.02 && photon1.e()/ photon2.e() > 0.98){
                    hEGamGam.fill((photon1.e() + photon2.e()) / 2);
                }
            }
        }
    }
}

System.out.println("done");


public static ArrayList<Integer> getBestPhotons(PhysicsEvent physicsEvent){
    ArrayList<Integer> photons = new ArrayList<>();
    int numPhotons = physicsEvent.countByPid(22);

    for(int i = 0; i < numPhotons - 1; i++){
        Particle photon1 = Particle.copyFrom(physicsEvent.getParticleByPid(22, i));

        for (int j = i + 1; j < numPhotons; j++){
            Particle photon2 = Particle.copyFrom(physicsEvent.getParticleByPid(22, j));
            double imGamGam = getPhotonIM(photon1, photon2);
            if (imGamGam > 0.12 && imGamGam < 0.15){
                photons.add(i);
                photons.add(j);
                return photons;
            }
        }
    }
//    Particle pi0 = Particle.copyFrom(physicsEvent.getParticleByPid(22, 0));
//    pi0.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22,1)), 1);
    photons.add(0);
    photons.add(1);
    return photons;
}

public static double getPhotonIM(Particle photon1, Particle photon2){
    return (Math.sqrt(photon1.e() * photon2.e()) * (1 - photon1.cosTheta(photon2)));
}