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


//String directory = "/lustre19/expphy/cache/clas12/rg-a/production/reconstructed/Fall2018/Torus-1/pass1/v1/";
//List<String> files = FileUtils.getFilesInDirectoryRecursive(directory, "*.hipo");
String directory = "/cache/clas12/rg-a/production/recon/fall2018/torus-1/pass1/v0/dst/train/skim4";

HipoChain reader = new HipoChain();
//reader.addFiles(files);
reader.addDir(directory);
reader.open();

HipoWriter eWriter = new HipoWriter(reader.getSchemaFactory());
eWriter.open("/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/eDetectedPremakoff_noP.hipo");

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:-211:211:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

////////////////////       Electron detected loop      /////////////////////////
    if(filter.isValid(physicsEvent)) {
        eWriter.addEvent(event);
    }
}

eWriter.close();

println("done");



