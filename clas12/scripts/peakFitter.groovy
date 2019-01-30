
import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo.data.HipoEvent
import org.jlab.jnp.hipo.data.HipoGroup
import org.jlab.jnp.hipo.data.HipoNode
import org.jlab.jnp.hipo.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent


String dataFile = myAnalysis.hipo;

TDirectory dir = new TDirectory();

dir.readFile(dataFile);