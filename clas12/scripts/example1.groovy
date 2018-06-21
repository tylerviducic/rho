import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoReader;

String dataFile = "physics_data_0.hipo";
HipoReader reader = new HipoReader();
reader.open(dataFile);
// Loop over events and print them on the screen
while(reader.hasNext()==true){
    HipoEvent event = reader.readNextEvent();
    event.show();
}
