import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain

String dataFile = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/pi0pi0_skim4.hipo";
HipoChain reader = new HipoChain();
//reader.addFiles(files);
//reader.addDir(directory);
reader.addFile(dataFile);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank calorimeter = new Bank(reader.getSchemaFactory().getSchema("REC::Calorimeter"));

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(calorimeter);

    int rows = calorimeter.getRows();
    for(int i = 0; i < rows; i++){
        System.out.println(calorimeter.getInt("pindex", i));
    }
}
