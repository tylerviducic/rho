import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
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

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(1, 3);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hgam1gam2);
c1.cd(1).draw(hgam1gam3);
c1.cd(2).draw(hgam1gam4);


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

while (reader.hasNext()) {
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle gam1 = physicsEvent.getParticleByPid(22, 0);
    Particle gam2 = physicsEvent.getParticleByPid(22, 1);
    Particle gam3 = physicsEvent.getParticleByPid(22, 2);
    Particle gam4 = physicsEvent.getParticleByPid(22, 3);

    int gam1Index = physicsEvent.getParticleIndex(22, 0);
    int gam2Index = physicsEvent.getParticleIndex(22, 1);
    int gam3Index = physicsEvent.getParticleIndex(22, 2);
    int gam4Index = physicsEvent.getParticleIndex(22, 3);


    Particle gam1gam2 = physicsEvent.getParticle("[22,0] + [22,1]");
    Particle gam3gam4 = physicsEvent.getParticle("[22,2] + [22,3]");

    Particle gam1gam3 = physicsEvent.getParticle("[22,0] + [22,2]");
    Particle gam2gam4 = physicsEvent.getParticle("[22,1] + [22,3]");

    Particle gam1gam4 = physicsEvent.getParticle("[22,0] + [22,3]");
    Particle gam2gam3 = physicsEvent.getParticle("[22,2] + [22,4]");

    hgam1gam2.fill(gam1gam2.mass(), gam3gam4.mass());
    hgam1gam3.fill(gam1gam3.mass(), gam2gam4.mass());
    hgam1gam4.fill(gam1gam4.mass(), gam2gam3.mass());
}

System.out.println("done");
