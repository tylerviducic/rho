import org.jlab.groot.data.H1FC
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.physics.Particle
import org.jlab.groot.data.H1F

String directory = "/cache/clas12/rg-a/production/recon/fall2018/torus-1/pass1/v0/dst/train/skim14";

H1F hpPion = new H1F("pPion", 150, 0.6, 2.0);
H1F heKaon = new H1F("peKaon", 150, 0.6, 2.0);

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2, 1);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hpPion);
c1.cd(1).draw(heKaon);

HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter eventFilter = new EventFilter("11:2212:321:-211");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);
    if (eventFilter.isValid(physicsEvent)){

        Particle missingAll = physicsEvent.getParticle("[b] + [t] - [2212] - [11] - [321] - [-211]");
        Particle kaon = physicsEvent.getParticleByPid(321, 0);
        Particle protonPion = physicsEvent.getParticle("[2212] + [-211]");
        Particle missingEKaon = physicsEvent.getParticle("[b] + [t] - [11] - [321]");

        if(kaon.p() < 2.0 && missingAll.mass2() < 0.05 && missingAll.p() < 0.1){
            hpPion.fill(protonPion.mass());
            heKaon.fill(missingEKaon.mass());
        }

    }
}

System.out.println("done");
