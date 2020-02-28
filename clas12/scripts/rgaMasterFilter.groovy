import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.hipo4.io.HipoWriterSorted
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager


String directory = "/volatile/clas12/users/clas12/rich/dst/recon/005038/";
//String directory = args[0];
//String file = "/volatile/clas12/users/clas12/rich/dst/recon/005038/rec_clas_005038.evio.00615-00619.hipo"
//need way to only skim files tht have not been skimmed yet. easy way would be to write a file with run rumbers skimmed
//if the run number is in the file, move to the next one

HipoChain reader = new HipoChain();
//reader.addFile(file);
reader.addDir(directory);
reader.open();

HipoWriterSorted writerSorted = new HipoWriterSorted();
writerSorted.getSchemaFactory().copy(reader.getSchemaFactory());
writerSorted.open("/work/clas12/viducic/data/clas12/rga_skimmed.hipo");

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter premakoffRhoFilter = new EventFilter("11:2212:211:-211:Xn");
EventFilter pi0Filter = new EventFilter("11:2212:22:22:Xn");

while(reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);
    event.setEventTag(0);

    // rho -> pi+pi-gamma
    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);
    if(premakoffRhoFilter.isValid(physicsEvent)){
        event.setEventTag(10);
        if(physicsEvent.countByPid(22) >= 1){
            event.setEventTag(11);
        }
    }
    else if (pi0Filter.isValid(physicsEvent)){
        if(physicsEvent.countByPid(22) <= 3){
            event.setEventTag(12);
        }
        else if (physicsEvent.countByPid(22) >= 4){
            event.setEventTag(13);
        }
    }
    if (event.getEventTag() != 0) {
        writerSorted.addEvent(event, event.getEventTag());
    }
}

writerSorted.close();
