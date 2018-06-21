import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoReader;

String dataFile = "physics_data_0.hipo";
HipoReader reader = new HipoReader();
reader.open(dataFile);
// Loop over all events
while(reader.hasNext()==true){
  HipoEvent event = reader.readNextEvent();
  if(event.hasGroup("mc::event")==true){
     HipoGroup bank = event.getGroup("mc::event");
     bank.show();
  }
}
