import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.hipo4.io.HipoWriter
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

String directory = "/cache/clas12/rg-a/production/recon/fall2018/torus-1/pass1/v0/dst/train/skim4";

HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

HipoWriter writer = new HipoWriter(reader.getSchemaFactory());
writer.open("/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/pi0pi0_skim4_inclusive.hipo");

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter eventFilter = new EventFilter("11:2212:22:22:22:22:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(eventFilter.isValid(physicsEvent)){
        writer.addEvent(event);
    }
}

writer.close();