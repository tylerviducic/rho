import org.jlab.clas.fastmc.Clas12FastMC
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.LundReader

String dataFile = "/u/group/clas12/mcdata/generated/lund/ppippim/clasdispr.00.e11.000.emn0.75tmn.09.xs65.61nb.113.0001.dat ";

//Clas12FastMC fmc = new Clas12FastMC(-1, -1);
LundReader reader = new LundReader(dataFile);

PhysicsEvent event = new PhysicsEvent();

while(reader.nextEvent(event)){
    println(event.toLundString());
}
