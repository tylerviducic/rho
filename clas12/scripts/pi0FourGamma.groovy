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
import org.jlab.jnp.utils.file.FileUtils

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
hpionpion.setTitleX("first pion");
hpionpion.setTitleY("second pion");

H1F hf0 = new H1F("f0", 100, 0.0, 2.0);
hf0.setTitle("IM(pi0pi0)");

H2F hmp = new H2F("mp", 100, -0.5, 0.5, 100, -0.5, 0.5);
hmp.setTitle("Missing px vs missing py of all particles");

H1F hmm2 = new H1F("mm2", 100, -0.5, 0.5);
hmm2.setTitle("Missing mass2 of all particles");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2, 2);
c1.getCanvas().initTimer(1000);
//c1.cd(0).draw(hgam1gam2);
//c1.cd(1).draw(hgam1gam3);
//c1.cd(2).draw(hgam1gam4);
c1.cd(0).draw(hpionpion);
c1.cd(1).draw(hf0);
c1.cd(2).draw(hmm2);
c1.cd(3).draw(hmp);
//String directory = "/cache/clas12/rg-a/production/recon/fall2018/torus-1/pass1/v0/dst/train/skim4";
//String directory = "/work/clas12/viducic/data/clas12/premakoff/skimmedFiles/";
//String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/photons.hipo";
//List<String> files = FileUtils.getFileListInDir(directory);
//System.out.println(files);
String dataFile = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/pi0pi0_skim4.hipo";
HipoChain reader = new HipoChain();
//reader.addFiles(files);
//reader.addDir(directory);
reader.addFile(dataFile);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank calorimeter = new Bank(reader.getSchemaFactory().getSchema("REC::Calorimeter"));

EventFilter eventFilter = new EventFilter("11:2212:22:22:22:22");

while (reader.hasNext()) {
    reader.nextEvent(event);
    event.read(particle);
    event.read(calorimeter);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if (eventFilter.isValid(physicsEvent)) {
        int gam0Index = physicsEvent.getParticleIndex(22, 0);
        int gam1Index = physicsEvent.getParticleIndex(22, 1);
        int gam2Index = physicsEvent.getParticleIndex(22, 2);
        int gam3Index = physicsEvent.getParticleIndex(22, 3);

        int sector0 = getSector(gam0Index, calorimeter);
        int sector1 = getSector(gam1Index, calorimeter);
        int sector2 = getSector(gam2Index, calorimeter);
        int sector3 = getSector(gam3Index, calorimeter);

//        System.out.println(sector0 + "  " + sector1 + "  " + sector2 + "  " + sector3);

        Particle gam0 = physicsEvent.getParticle(gam0Index);
        Particle gam1 = physicsEvent.getParticle(gam1Index);
        Particle gam2 = physicsEvent.getParticle(gam2Index);
        Particle gam3 = physicsEvent.getParticle(gam3Index);

        Particle pion1 = Particle.copyFrom(gam0);
        Particle pion2;

        Particle f0 = physicsEvent.getParticle("[22, 0] + [22, 1] + [22, 2] + [22, 3]");
        Particle missingePPi0Pi0 = physicsEvent.getParticle("[b] + [t] - [2212] - [11] - [22,0] - [22,1] - [22,2] - [22,3]");

        //double missingP = Math.sqrt(missingePPi0Pi0.px()/missingePPi0Pi0.p() * missingePPi0Pi0.px()/missingePPi0Pi0.p() + missingePPi0Pi0.py()/missingePPi0Pi0.p() * missingePPi0Pi0.py()/missingePPi0Pi0.p())

        hmm2.fill(missingePPi0Pi0.mass2());
        hmp.fill(missingePPi0Pi0.px()/missingePPi0Pi0.p(), missingePPi0Pi0.py()/missingePPi0Pi0.p());

        if (sector0 == -1 || sector1 == -1 || sector2 == -1 || sector3 == -1
            || (sector0 == sector1 && sector1 == sector2 && sector2 == sector3)) {
            continue;
        }
        if (gam0.e() > 0.5 && gam1.e() > 0.5 && gam2.e() > 0.5 && gam3.e() > 0.5
                && Math.abs(missingePPi0Pi0.mass2()) < 0.1
                && Math.abs(missingePPi0Pi0.px()/missingePPi0Pi0.p()) < 0.2 && Math.abs(missingePPi0Pi0.py()/missingePPi0Pi0.p()) < 0.2) { // && f0.mass() > 0.8
            if (sector0 == sector1 && sector2 == sector3) {
                pion1.combine(Particle.copyFrom(gam1), 1);
                pion2 = Particle.copyFrom(gam2)
                pion2.combine(Particle.copyFrom(gam3), 1);
            } else if (sector0 == sector2 && sector1 == sector3) {
                pion1.combine(Particle.copyFrom(gam2), 1);
                pion2 = Particle.copyFrom(gam1)
                pion2.combine(Particle.copyFrom(gam3), 1);
            } else if (sector0 == sector3 && sector1 == sector2) {
                pion1.combine(Particle.copyFrom(gam3), 1);
                pion2 = Particle.copyFrom(gam1);
                pion2.combine(Particle.copyFrom(gam2), 1);
            } else {
                continue;
            }
//    Particle gam1gam2 = physicsEvent.getParticle("[22,0] + [22,1]");
//    Particle gam3gam4 = physicsEvent.getParticle("[22,2] + [22,3]");
//
//    Particle gam1gam3 = physicsEvent.getParticle("[22,0] + [22,2]");
//    Particle gam2gam4 = physicsEvent.getParticle("[22,1] + [22,3]");
//
//    Particle gam1gam4 = physicsEvent.getParticle("[22,0] + [22,3]");
//    Particle gam2gam3 = physicsEvent.getParticle("[22,2] + [22,4]");

//    hgam1gam2.fill(gam1gam2.mass(), gam3gam4.mass());
//    hgam1gam3.fill(gam1gam3.mass(), gam2gam4.mass());
//    hgam1gam4.fill(gam1gam4.mass(), gam2gam3.mass());
            hpionpion.fill(pion1.mass(), pion2.mass());
            if (pion1.mass() > 0.1 && pion1.mass() < 0.16 && pion2.mass() > 0.1 && pion2.mass() < 0.16) {
                hf0.fill(f0.mass());
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