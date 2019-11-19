import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.ParticleList
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.hipo4.io.HipoChain


//String directory = "/w/hallb-scifs17exp/clas12/rg-a/trains/pass1/v1_4/skim04_inclusive";
String directory = "/lustre19/expphy/cache/clas12/rg-a/production/reconstructed/Fall2018/Torus-1/pass1/v1/005032";

HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

int eventCounter = 0;
int filterCounter = 0;
int noFilterCounter = 0;

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:2212:-211:211:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);
    eventCounter++;
    if(eventCounter % 10000 == 0){
        System.out.println("Event counter = " + eventCounter);
    }

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(physicsEvent.getParticleList().count() > 0 &&  physicsEvent.getParticle(0).pid() == -211
            && physicsEvent.countByPid(2212) == 1  && physicsEvent.countByPid(211) == 1
            && physicsEvent.countByCharge(1) == 2 && physicsEvent.countByCharge(-1) == 1){
        noFilterCounter++;
    }
        ParticleList particleList = physicsEvent.getParticleList();
        if(filter.isValid(physicsEvent) && physicsEvent.getParticleByPid(11, 0).theta() < Math.toRadians(5)) {
            filterCounter++;
        }

}

System.out.println("++++++++++++++++++++++++++++++++++++");
System.out.println("++++++++++++++++++++++++++++++++++++\n");
System.out.println("Number of p pi+ pi- events with electron: " + filterCounter);
System.out.println("Total number of events = " + eventCounter);
System.out.println("Percentage of events with wanted final state (no e): " + ((double)(filterCounter)/(double)(eventCounter) * 100));
System.out.println("Number of p pi+ pi- events with no electron: " + noFilterCounter);
System.out.println("Percentage of events with wanted final state : " + ((double)(noFilterCounter)/(double)(eventCounter) * 100));

