import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

String directory = "/lustre19/expphy/cache/clas12/rg-a/production/reconstructed/Fall2018/Torus-1/pass1/v1/005124";

HipoChain reader = new HipoChain();
reader.addDir(directory);

int eventCounter =0;
int noTrigger = 0;
int pimTrigger = 0;
int eTrigger = 0;
int eTagger = 0;

reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

while (reader.hasNext()){
    eventCounter++;
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(physicsEvent.getParticleList().count() > 0) {
        Particle triggerParticle = physicsEvent.getParticle(0);
        int pid = triggerParticle.pid();
        if (pid == 11) {
            eTrigger++;
        } else if (pid == -211) {
            pimTrigger++;
            if (physicsEvent.countByPid(11) >= 1) {
                eTagger++;
            }
        } else if (pid == 0) {
            noTrigger++;
        }
    }
}

System.out.println("Fraction of events with e-trigger: " + eTrigger + "/" + eventCounter);
System.out.println("Fraction of events with pi- trigger: " + pimTrigger + "/" + eventCounter);
System.out.println("Fraction of events with pi- trigger and e in FT: " + eTagger + "/" + eventCounter);
System.out.println("Fraction of events with no trigger: " + noTrigger + "/" + eventCounter);

