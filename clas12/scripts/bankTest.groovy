import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

String dataFile = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/pi0pi0_skim4.hipo";
HipoChain reader = new HipoChain();
//reader.addFiles(files);
//reader.addDir(directory);
reader.addFile(dataFile);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank calorimeter = new Bank(reader.getSchemaFactory().getSchema("REC::Calorimeter"));

int eventCounter = 0;

while (reader.hasNext() && eventCounter < 11){
    System.out.println("------------------")
    eventCounter++;
    reader.nextEvent(event);
    event.read(calorimeter);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);
    ArrayList<Particle> photons = new ArrayList<>();

    int rows = calorimeter.getRows();
    for(int i = 0; i < rows; i++){
        int pIndex = calorimeter.getInt("pindex", i);
        int sector = calorimeter.getInt("sector", i);

    }
}


