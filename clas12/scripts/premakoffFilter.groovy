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


String directory = "/lustre19/expphy/cache/clas12/rg-a/production/reconstructed/Fall2018/Torus-1/pass1/v1/";
List<String> files = FileUtils.getFilesInDirectoryRecursive(directory, "*.hipo");

HipoChain reader = new HipoChain();
reader.addFiles(files);
reader.open();

HipoWriter eWriter = new HipoWriter(reader.getSchemaFactory());
eWriter.open("/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/eDetectedPremakoff.hipo");
HipoWriter noeWriter = new HipoWriter(reader.getSchemaFactory());
noeWriter.open("/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/noeDetectedPremakoff.hipo");


long eventCounter = 0;

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:-211:211:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);
    eventCounter++;

    if(eventCounter % 100000 == 0){
        System.out.println("Event counter = " + eventCounter);
    }

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

////////////////////    No Electron detected loop    /////////////////////////
    if(physicsEvent.getParticleList().count() > 0 &&  physicsEvent.getParticle(0).pid() == -211
            && physicsEvent.countByPid(211) == 1
            && physicsEvent.countByCharge(1) == 1 && physicsEvent.countByCharge(-1) == 1){


        noeWriter.addEvent(event);

    }

////////////////////       Electron detected loop      /////////////////////////
    else if(filter.isValid(physicsEvent) && physicsEvent.getParticleByPid(11, 0).theta() < Math.toRadians(5)) {

        eWriter.addEvent(event);
    }
}

noeWriter.close();
eWriter.close();

println("done");



