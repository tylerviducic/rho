import groovy.swing.factory.TDFactory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.math.StatNumber
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

H1F hEGamGam = new H1F("eGamGam", 20, 1, 3.0);
hEGamGam.setTitle("Energy of photons with same energy");
hEGamGam.setTitleX("E(#gamma");

H1F hElectronMomentum = new H1F("ElectronMomentum", 100, 0, 10);
hElectronMomentum.setTitle("Detected Electron Momentum");

H1F hEUncorrectedGamma = new H1F("EUncorrectedGamma", 100, 0, 10);

ArrayList<H1F> pionsBinned = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    double histoBin = 1 + i * 0.17;
    String histoName = "e(gam)=" + Double.toString(histoBin);
    H1F histo = new H1F(histoName, 100, 0, 0.27);
    histo.setTitle("IM(#gamma#gamma) for E_#gamma = " + (1 + i * 0.17));
    pionsBinned.add(histo);
}

//ArrayList<H1F> protonBinned = new ArrayList<>();
//for (int i = 0; i < 8; i++) {
//    double histoBin = 4.5 + i * 0.5;
//    String histoName = "p(e)=" + Double.toString(histoBin);
//    H1F histo = new H1F(histoName, 100, 0.0, 2);
//    histo.setTitle("MM(e'#pi^0) for P(e') = " + (4.5 + i * 0.5));
//    protonBinned.add(histo);
//}
GraphErrors massRatioVsE = new GraphErrors("massRatioVsE");
massRatioVsE.setTitle("IM(#gamma#gamma) vs m(#pi^0)");

//GraphErrors missingMassRatioVsP = new GraphErrors("missingMassRatioVsP");
//missingMassRatioVsP.setTitle("P(e') vs MM(e'#pi^0)/m_p");

// ------------------------------------------              ------------------------------------------------

//TDirectory dir = new TDirectory();
////dir.mkdir("/ProtonsBinned");
//dir.mkdir("/PionsBinned");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.getCanvas().initTimer(10000);
c1.draw(hEUncorrectedGamma);

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim4.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank eCal = new Bank(reader.getSchemaFactory().getSchema("REC::Calorimeter"));

//EventFilter eventFilter = new EventFilter("11:22:22:Xn:X+:X-");

while (reader.hasNext()) {
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.614, particle);

    ArrayList<Integer> photons = getBestPhotons(physicsEvent);
//    Particle photon1 = physicsEvent.getParticleByPid(22, photons.get(0));
//    Particle photon2 = physicsEvent.getParticleByPid(22, photons.get(1));

    Particle photon1 = physicsEvent.getParticle(photons.get(0));
    Particle photon2 = physicsEvent.getParticle(photons.get(1));

    int sector1 = getSector(photons.get(0), eCal);
    int sector2 = getSector(photons.get(1), eCal);


    if (sector1 != sector2) {
        continue;
    }

    double photonTheta = Math.toDegrees(Math.acos(photon1.cosTheta(photon2)));

    Particle pi0 = Particle.copyFrom(photon1);
    pi0.combine(Particle.copyFrom(photon2), 1);

    Particle missingEPi0 = physicsEvent.getParticle("[b] + [t] - [11]");

    missingEPi0.combine(Particle.copyFrom(pi0), -1);

//    hMMvsMP.fill(missingEPi0.mass(), missingEPi0.p());

    if (missingEPi0.p() < 1.0 && pi0.p() > 2 && pi0.p() < 5.5 && photonTheta < 10 && photonTheta > 3 &&
            missingEPi0.mass() > 0.8 && missingEPi0.mass() < 1.1) {
        if (photon1.e() > 1.0 && photon1.e() < 2.6 && (photon2.e() < 1.0 || photon2.e() > 2.6)){
            hEUncorrectedGamma.fill(photon2.e());
        }
        if (photon2.e() > 1.0 && photon2.e() < 2.6 && (photon1.e() < 1.0 || photon1.e() > 2.6)){
            hEUncorrectedGamma.fill(photon1.e());
        }
    }
}

//dir.cd("/PionsBinned");
//for(int i = 0; i < pionsBinned.size(); i++){
//    dir.addDataSet(pionsBinned.get(i));
//}
//
//dir.writeFile("/w/hallb-scifs17exp/clas12/viducic/rho/clas12/results/energyCorrections2.hipo");

System.out.println("done");


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////    METHODS   //////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public static ArrayList<Integer> getBestPhotons(PhysicsEvent physicsEvent) {
    ArrayList<Integer> photons = new ArrayList<>();
    int numPhotons = physicsEvent.countByPid(22);

    for (int i = 0; i < numPhotons - 1; i++) {
        Particle photon1 = Particle.copyFrom(physicsEvent.getParticleByPid(22, i));
        for (int j = i + 1; j < numPhotons; j++) {
            Particle photon2 = Particle.copyFrom(physicsEvent.getParticleByPid(22, j));
            Particle pi0 = Particle.copyFrom(photon1);
            double imGamGam = getPhotonIM(photon1, photon2);
            pi0.combine(photon2, 1);

            if (pi0.mass() > 0.12 && pi0.mass() < 0.15) {
//                System.out.println("equation: " + imGamGam + "  --  object: " + pi0.mass());
//                System.out.println("gagik's theta: " + photon1.cosTheta(photon2) + "  --  my theta: " + myCosTheta(photon1, photon2));

                photons.add(physicsEvent.getParticleIndex(22, i));
                photons.add(physicsEvent.getParticleIndex(22, j));
                return photons;
            }
        }
    }
//    Particle pi0 = Particle.copyFrom(physicsEvent.getParticleByPid(22, 0));
//    pi0.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22,1)), 1);
    photons.add(physicsEvent.getParticleIndex(22, 0));
    photons.add(physicsEvent.getParticleIndex(22, 1));
    return photons;
}

public static double getPhotonIM(Particle photon1, Particle photon2) {
    return Math.sqrt(photon1.e() * photon2.e()) * (1 - photon1.cosTheta(photon2));
}

public static double myCosTheta(Particle part1, Particle part2) {
    return (part1.px() * part2.px() + part1.py() * part2.py() + part1.pz() * part2.pz()) / (part1.p() * part2.p());
}

public static int getSector(int pindex, Bank calorimeter) {
    for (int i = 0; i < calorimeter.getRows(); i++) {
        if (calorimeter.getInt("pindex", i) == pindex) {
            return calorimeter.getInt("sector", i);
        }
    }
    return -1;
}
