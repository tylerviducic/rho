import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.hipo.io.HipoWriter;

String dataFile = "physics_data_0.hipo";
dataFile = args[0]

HipoReader reader = new HipoReader();
reader.open(dataFile);

HipoWriter writer = reader.createWriter();
writer.open("filter_output.hipo");
// Loop over all events
while(reader.hasNext()==true){
    HipoEvent event = reader.readNextEvent();
    if(event.hasGroup("mc::event")==true){
        HipoGroup bank = event.getGroup("mc::event");
        boolean containsPip = false;
        boolean containsPim = false;
        boolean containsP = false;
        boolean containsGam = false;
        int nrows = bank.getNode("pid").getDataSize();
        for(int i = 0; i < nrows; i++){
            int pid = bank.getNode("pid").getShort(i);
            if(pid==211) containsPip = true;
            if(pid==-211) containsPim = true;
            if(pid==2212) containsP = true;
            if(pid==22) containsGam = true;

        }
        if(containsPip==true&&containsP==true&&containsGam==true&&containsPim==true){
            writer.writeEvent(event);
        }
    }
}
writer.close();