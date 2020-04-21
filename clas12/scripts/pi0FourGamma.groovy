import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H2F hgam1gam2 = new H2F("gam1gam2", 30, 0, 0.3, 30, 0, 0.3);
hgam1gam2.setTitleX("gam1gam2");
hgam1gam2.setTitleY("gam3gam4");
H2F hgam1gam3 = new H2F("gam1gam3", 30, 0, 0.3, 30, 0, 0.3);
hgam1gam3.setTitleX("gam1gam3");
hgam1gam3.setTitleY("gam2gam4");
H2F hgam1gam4 = new H2F("gam1gam4", 30, 0, 0.3, 30, 0, 0.3);
hgam1gam4.setTitleX("gam1gam4");
hgam1gam4.setTitleY("gam2gam3");

H2F hpionpion = new H2F("pionpion", 60, 0, 0.3, 60, 0, 0.3);
hpionpion.setTitle("Invariant mass of photons pairs");
hpionpion.setTitleX("IM(#gamma#gamma1)[GeV]");
hpionpion.setTitleY("IM(#gamma#gamma2)[GeV]");

H1F hf0 = new H1F("f0", 100, 0.0, 2.0);
hf0.setTitle("Invariant Mass of #pi^0#pi^0");
hf0.setTitleX("IM(#pi^0#pi^0)[GeV]")

H2F hmp = new H2F("mp", 100, -0.5, 0.5, 100, -0.5, 0.5);
hmp.setTitle("Missing px vs missing py of all particles");

H1F hmm2 = new H1F("mm2", 100, -0.5, 0.5);
hmm2.setTitle("Missing mass2 of all particles");

H1F hmxP = new H1F("mxP", 100, 0.5, 1.5);
hmxP.setTitle("Missing mass of electron and pi0pi0");

H2F hpion1PvsTheta = new H2F("hpion1PvsTheta", 150, 0, 6.0, 40, 0, 40);
hpion1PvsTheta.setTitle("\"Pion1\" momentum vs opening angle of #gamma#gamma");
hpion1PvsTheta.setTitleX("p(#gamma#gamma1)[GeV]");
hpion1PvsTheta.setTitleY("#theta(#gamma#gamma1)[Degrees]");

H2F hpion2PvsTheta = new H2F("hpion2PvsTheta", 100, 0, 4.0, 40, 0, 40);
hpion2PvsTheta.setTitle("\"Pion2\" momentum vs opening angle of #gamma#gamma");
hpion2PvsTheta.setTitleX("p(#gamma#gamma2)[GeV]");
hpion2PvsTheta.setTitleY("#theta(#gamma#gamma2)[Degrees]");

H1F hPtheta = new H1F("hPtheta", 90, 0, 90);
hPtheta.setTitle("theta distribution of proton");

H2F hWvsIMpi0pi0 = new H2F("wvsIMpi0pi0", 100, 0, 2, 250, 0, 5);
hWvsIMpi0pi0.setTitle("W vs IM(#pi^0#pi^0)");
hWvsIMpi0pi0.setTitleX("IM(#pi^0#pi^0) [GeV]");
hWvsIMpi0pi0.setTitleY("W [Gev]");

H2F hQ2vsIMpi0pi0 = new H2F("q2vsIMpi0pi0", 100, 0, 2, 250, 0, 5);
hQ2vsIMpi0pi0.setTitle("Q^2 vs IM(#pi^0#pi^0)");
hQ2vsIMpi0pi0.setTitleX("IM(#pi^0#pi^0) [GeV]");
hQ2vsIMpi0pi0.setTitleY("Q^2 [Gev]");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(3, 2);
c1.getCanvas().initTimer(1000);

c1.cd(0).draw(hpionpion);
c1.cd(1).draw(hf0);
//c1.cd(2).draw(hmm2);
c1.cd(2).draw(hPtheta);
c1.cd(3).draw(hmxP);
c1.cd(4).draw(hpion1PvsTheta);
c1.cd(5).draw(hpion2PvsTheta);

TCanvas c2 = new TCanvas("c2", 1000, 1000);
c2.divide(2,1);
c2.getCanvas().initTimer(1000);
c2.cd(0).draw(hQ2vsIMpi0pi0);
c2.cd(1).draw(hWvsIMpi0pi0);


String dataFile = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/pi0pi0_skim4_inclusive.hipo";
HipoChain reader = new HipoChain();
reader.addFile(dataFile);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank calorimeter = new Bank(reader.getSchemaFactory().getSchema("REC::Calorimeter"));

EventFilter eventFilter = new EventFilter("11:2212:22:22:22:22:Xn:X+:X-");

double gamCut = 0.3;

while (reader.hasNext()) {
    reader.nextEvent(event);
    event.read(particle);
    event.read(calorimeter);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.614, particle);

    if (eventFilter.isValid(physicsEvent)) {

        ArrayList<Integer> photons = new ArrayList<>();
        for(int i = 0; i < physicsEvent.count(); i++){
            Particle currentParticle = Particle.copyFrom(physicsEvent.getParticle(i));
            if(currentParticle.pid() == 22 && currentParticle.e() > gamCut){
                photons.add(i);
            }
        }

        if(photons.size() != 4){
            continue;
        }

        int gam0Index = photons.get(0);
        int gam1Index = photons.get(1);
        int gam2Index = photons.get(2);
        int gam3Index = photons.get(3);

        int sector0 = getSector(gam0Index, calorimeter);
        int sector1 = getSector(gam1Index, calorimeter);
        int sector2 = getSector(gam2Index, calorimeter);
        int sector3 = getSector(gam3Index, calorimeter);

        if(sector0 == -1 && physicsEvent.getParticle(gam0Index).status < 2000){
            sector0 = -2;
        }
        if(sector1 == -1 && physicsEvent.getParticle(gam1Index).status < 2000){
            sector1 = -2;
        }
        if(sector2 == -1 && physicsEvent.getParticle(gam2Index).status < 2000){
            sector2 = -2;
        }
        if(sector3 == -1 && physicsEvent.getParticle(gam3Index).status < 2000){
            sector3 = -2;
        }

//        System.out.println(sector0 + "  " + sector1 + "  " + sector2 + "  " + sector3);

        Particle gam0 = physicsEvent.getParticle(gam0Index);
        Particle gam1 = physicsEvent.getParticle(gam1Index);
        Particle gam2 = physicsEvent.getParticle(gam2Index);
        Particle gam3 = physicsEvent.getParticle(gam3Index);

        Particle pion1 = Particle.copyFrom(gam0);
        Particle pion2;
        Particle testPion1 = new Particle();
        Particle testPion2 = new Particle();

        double theta1;
        double theta2;
        double pTheta = Math.toDegrees(physicsEvent.getParticleByPid(2212, 0).theta());

        //Particle f0 = physicsEvent.getParticle("[22, 0] + [22, 1] + [22, 2] + [22, 3]");
        Particle missingePPi0Pi0 = physicsEvent.getParticle("[b] + [t] - [2212] - [11]");
        missingePPi0Pi0.combine(Particle.copyFrom(gam0), -1);
        missingePPi0Pi0.combine(Particle.copyFrom(gam1), -1);
        missingePPi0Pi0.combine(Particle.copyFrom(gam2), -1);
        missingePPi0Pi0.combine(Particle.copyFrom(gam3), -1);

        Particle missingePi0Pi0 = physicsEvent.getParticle("[b] + [t] - [11]");
        Particle w = physicsEvent.getParticle("[b] - [11]");

        hmp.fill(missingePPi0Pi0.px()/missingePPi0Pi0.p(), missingePPi0Pi0.py()/missingePPi0Pi0.p());
        hmm2.fill(missingePPi0Pi0.mass2());

        if (sector0 == -1 || sector1 == -1 || sector2 == -1 || sector3 == -1
            || (sector0 == sector1 && sector1 == sector2 && sector2 == sector3)) {
            continue;
        }
        if (gam0.e() > gamCut && gam1.e() > gamCut && gam2.e() > gamCut && gam3.e() > gamCut
                && Math.abs(missingePPi0Pi0.mass2()) < 0.05) { // && f0.mass() > 0.8 && && Math.toDegrees(physicsEvent.getParticleByPid(2212, 0).theta()) < 35
//        if(Math.abs(missingePPi0Pi0.mass2()) < 0.05){
            if (sector0 == sector1 && sector2 == sector3) {
                pion1.combine(Particle.copyFrom(gam1), 1);
                pion2 = Particle.copyFrom(gam2)
                pion2.combine(Particle.copyFrom(gam3), 1);

                testPion1.initParticleWithMass(0.135, gam0.px() + gam1.px(), gam0.py() + gam1.py(), gam0.pz() + gam1.pz(),
                        (gam0.vx() + gam1.py())/2, (gam0.vy() + gam1.vy())/2, (gam0.vz() + gam1.vz())/2);
                testPion2.initParticleWithMass(0.135, gam2.px() + gam3.px(), gam2.py() + gam3.py(), gam2.pz() + gam3.pz(),
                        (gam2.vx() + gam3.py())/2, (gam2.vy() + gam3.vy())/2, (gam2.vz() + gam3.vz())/2);

                theta1 = Math.toDegrees(Math.acos(gam0.cosTheta(gam1)));
                theta2 = Math.toDegrees(Math.acos(gam2.cosTheta(gam3)));
            } else if (sector0 == sector2 && sector1 == sector3) {
                pion1.combine(Particle.copyFrom(gam2), 1);
                pion2 = Particle.copyFrom(gam1)
                pion2.combine(Particle.copyFrom(gam3), 1);

                testPion1.initParticleWithMass(0.135, gam0.px() + gam2.px(), gam0.py() + gam2.py(), gam0.pz() + gam2.pz(),
                        (gam0.vx() + gam2.py())/2, (gam0.vy() + gam2.vy())/2, (gam0.vz() + gam2.vz())/2);
                testPion2.initParticleWithMass(0.135, gam1.px() + gam3.px(), gam1.py() + gam3.py(), gam1.pz() + gam3.pz(),
                        (gam1.vx() + gam3.py())/2, (gam2.vy() + gam3.vy())/2, (gam1.vz() + gam3.vz())/2);

                theta1 = Math.toDegrees(Math.acos(gam0.cosTheta(gam2)));
                theta2 = Math.toDegrees(Math.acos(gam1.cosTheta(gam3)));
            } else if (sector0 == sector3 && sector1 == sector2) {
                pion1.combine(Particle.copyFrom(gam3), 1);
                pion2 = Particle.copyFrom(gam1);
                pion2.combine(Particle.copyFrom(gam2), 1);

                testPion1.initParticleWithMass(0.135, gam0.px() + gam3.px(), gam0.py() + gam3.py(), gam0.pz() + gam3.pz(),
                        (gam0.vx() + gam3.py())/2, (gam0.vy() + gam3.vy())/2, (gam0.vz() + gam3.vz())/2);
                testPion2.initParticleWithMass(0.135, gam2.px() + gam1.px(), gam2.py() + gam1.py(), gam2.pz() + gam1.pz(),
                        (gam2.vx() + gam1.py())/2, (gam2.vy() + gam1.vy())/2, (gam2.vz() + gam1.vz())/2);

                theta1 = Math.toDegrees(Math.acos(gam0.cosTheta(gam3)));
                theta2 = Math.toDegrees(Math.acos(gam1.cosTheta(gam2)));
            } else {
                continue;
            }

            Particle f0 = Particle.copyFrom(testPion1);
            f0.combine(testPion2, 1);

//            missingePPi0Pi0.combine(f0, -1);

            missingePi0Pi0.combine(testPion1, -1);
            missingePi0Pi0.combine(testPion2, -1);

//            for(int i = 1; i < physicsEvent.count(); i ++){
//                if (i != gam0Index && i != gam2Index && i != gam3Index && i != gam1Index &&  physicsEvent.getParticle(i).pid() != 2212){
//                    missingePPi0Pi0.combine(Particle.copyFrom(physicsEvent.getParticle(i)), -1);
//                }
//            }

            hpionpion.fill(pion1.mass(), pion2.mass());
            hpion1PvsTheta.fill(pion1.p(), theta1);
            hpion2PvsTheta.fill(pion2.p(), theta2);

            if (pion1.mass() > 0.12 && pion1.mass() < 0.15 && pion2.mass() > 0.12 && pion2.mass() < 0.15
                    && pion1.p() > 1.5 && pion1.p() < 5.0 && pion2.p() < 2.5 && pion2.p() > 1 && theta1 < 10 && theta2 < 14
                    && theta1 > 4 && theta2 > 6 && missingePPi0Pi0.p() < 1.0) {
                hf0.fill(f0.mass());
                hmxP.fill(missingePi0Pi0.mass());
                double q2 = getQ2(Particle.copyFrom(physicsEvent.beamParticle()), Particle.copyFrom(physicsEvent.getParticleByPid(11, 0)));

                hWvsIMpi0pi0.fill(f0.mass(), Math.abs(w.mass()));
                hQ2vsIMpi0pi0.fill(f0.mass(), q2);

                if(f0.mass() < 1.0){
                    hPtheta.fill(Math.toDegrees(physicsEvent.getParticleByPid(2212, 0).theta()));
                }
            }
        }
    }
}
System.out.println("done");

//   methods

public static int getSector(int pindex, Bank calorimeter){
    for(int i = 0; i < calorimeter.getRows(); i++){
        if(calorimeter.getInt("pindex", i) == pindex){
            return calorimeter.getInt("sector", i);
        }
    }
    return -1;
}

public static double getQ2(Particle particle1, Particle particle2){
    return 4 * particle1.e() * particle2.e() * Math.sin(particle2.theta() /2) * Math.sin(particle2.theta()/2);
}