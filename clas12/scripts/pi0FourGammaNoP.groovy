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

H1F hmmePi0Pi0 = new H1F("missingMassPi0Pi0", 150, 0.0, 2.0);
hmmePi0Pi0.setTitle("Missing mass of e' pi0 pi0");

H1F hmpePi0Pi0 = new H1F("missingMomentumePi0Pi0", 100, 0.0, 10.0);
hmpePi0Pi0.setTitle("Missing momentum of e', pi0, pi0");

H2F hpion1pion2 = new H2F("pion1pion2", 100, 0, 0.3 , 100, 0, 0.3);
hpion1pion2.setTitle("mass of pion1 vs mass of pion2");
hpion1pion2.setTitleX("mass of pion1");
hpion1pion2.setTitleY("mass of pion2");

H2F hpion1PvsTheta = new H2F("hpion1PvsTheta", 100, 0, 4.0, 40, 0, 40);
hpion1PvsTheta.setTitleX("first pion momentum");
hpion1PvsTheta.setTitleY("first pair photon opening angle theta");

H2F hpion2PvsTheta = new H2F("hpion1PvsTheta", 100, 0, 4.0, 40, 0, 40);
hpion2PvsTheta.setTitleX("second pion momentum");
hpion2PvsTheta.setTitleY("second pair photon opening angle theta");

H2F hf0Theta = new H2F("hfoTheta", 100, 0, 2, 40, 0, 40);
hf0Theta.setTitle("IM(pi0pi0) vs theta");

H1F hf0 = new H1F("f0", 100, 0.0, 2.0);
hf0.setTitle("IM(pi0pi0)");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(3, 2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hmmePi0Pi0);
c1.cd(1).draw(hmpePi0Pi0);
c1.cd(2).draw(hpion1pion2);
c1.cd(3).draw(hpion1PvsTheta);
c1.cd(4).draw(hpion2PvsTheta);
c1.cd(5).draw(hf0);

String dataFile = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/pi0pi0_skim4_inclusive.hipo";
HipoChain reader = new HipoChain();
reader.addFile(dataFile);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank calorimeter = new Bank(reader.getSchemaFactory().getSchema("REC::Calorimeter"));

EventFilter eventFilter = new EventFilter("11:22:22:22:22:X+:X-:Xn");

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

        Particle missingePi0Pi0 = physicsEvent.getParticle("[b] + [t] - [11]");

        if (sector0 == -1 || sector1 == -1 || sector2 == -1 || sector3 == -1
                || (sector0 == sector1 && sector1 == sector2 && sector2 == sector3)) {
            continue;
        }
        if (gam0.e() > gamCut && gam1.e() > gamCut && gam2.e() > gamCut && gam3.e() > gamCut) {
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

            missingePi0Pi0.combine(testPion1, -1);
            missingePi0Pi0.combine(testPion2, -1);

            hmpePi0Pi0.fill(missingePi0Pi0.p());
            if(missingePi0Pi0.p() < 1.0) {

                hpion1pion2.fill(pion1.mass(), pion2.mass());

                hpion1PvsTheta.fill(testPion1.p(), theta1);
                hpion2PvsTheta.fill(testPion2.p(), theta2);

                if (pion1.p() > 1 && pion1.p() < 3.0 && theta1 > 5 && theta1 < 15 && pion2.p() > 1 && pion2.p() < 3.0 && theta2 > 5 && theta2 < 15
                        && pion1.mass() > 0.12 && pion1.mass() < 0.15 && pion2.mass() > 0.12 && pion2.mass() < 0.15) {
                    hmmePi0Pi0.fill(missingePi0Pi0.mass());
                    if (missingePi0Pi0.mass() > 0.8 && missingePi0Pi0.mass() < 1.2){
                        hf0.fill(f0.mass());
                        hf0Theta.fill(f0.mass(), Math.toDegrees(f0.theta()));
                    }
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
