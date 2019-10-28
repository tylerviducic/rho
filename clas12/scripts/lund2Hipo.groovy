import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.data.Schema
import org.jlab.jnp.hipo4.io.HipoWriter
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.LundReader
import org.jlab.jnp.utils.benchmark.ProgressPrintout
import org.jlab.jnp.utils.file.FileUtils

String directory = "/w/hallb-scifs17exp/clas12/avakian/mc/mcaugust2019/T-1.00_S-1.0/clasdis/cooked631/";
List<String> fileList = FileUtils.getFileListInDir(directory);

println(fileList.size());

EventFilter filter = new EventFilter("11:2212:211:-211:22");

Schema.SchemaBuilder schemaBuilder = new Schema.SchemaBuilder("mc::event", 22001, 1);
schemaBuilder.addEntry("pid", "I", "");
schemaBuilder.addEntry("px", "F", "");
schemaBuilder.addEntry("py", "F", "");
schemaBuilder.addEntry("pz", "F", "");
schemaBuilder.addEntry("vx", "F", "");
schemaBuilder.addEntry("vy", "F", "");
schemaBuilder.addEntry("vz", "F", "");
schemaBuilder.addEntry("charge", "I", "");
schemaBuilder.addEntry("beta", "F", "");
schemaBuilder.addEntry("chi2", "F", "");
schemaBuilder.addEntry("status", "I", "");
Schema schema = schemaBuilder.build();
Event hipoEvent = new Event();
ProgressPrintout progress = new ProgressPrintout();
HipoWriter writer = new HipoWriter();
writer.getSchemaFactory().addSchema(schema);
writer.setCompressionType(2);
writer.setMaxSize(16777216).setMaxEvents(1000000);
writer.setCompressionType(2);
writer.open("/work/clas12/viducic/data/clas12/rho_mc_higherQ2.hipo");
int counter = 0;
int eventCounter = 0;
Iterator var10 = fileList.iterator();

while(var10.hasNext()) {
    String file = (String)var10.next();
    System.out.println("adding file ----> " + file);
    LundReader reader = new LundReader();
    reader.acceptStatus(1);
    reader.addFile(file);
    reader.open();
    PhysicsEvent event = new PhysicsEvent();
    int eventCounterFile = 0;

    while(reader.nextEvent(event)) {
        if (filter.isValid(event)) {
            progress.updateStatus();
            ++eventCounter;
            ++eventCounterFile;
            Bank node = new Bank(schema, event.count());
            reader.fillNode(node, event);
            hipoEvent.reset();
            hipoEvent.write(node);
            writer.addEvent(hipoEvent);
        }
    }

    ++counter;
    System.out.println(" number of event processed = " + eventCounterFile + "  total = " + eventCounter);
}

writer.close();
System.out.println(progress.getUpdateString());

