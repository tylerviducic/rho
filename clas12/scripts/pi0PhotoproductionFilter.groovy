import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.io.HipoWriter
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.utils.file.FileUtils


//String directory = "/volatile/clas12/users/clas12/release/6b.5.1/calib/recon/005038/";
String directory = "/lustre19/expphy/volatile/clas12/rg-a/production/recon/fall2018/torus+1/pass1/v0/dst/train/skim3";

HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

HipoWriter writer = new HipoWriter(reader.getSchemaFactory());
writer.open("/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim3.hipo");


Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:22:22:2212:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(filter.isValid(physicsEvent)){
        writer.addEvent(event);
    }
}

writer.close();

println("done");

