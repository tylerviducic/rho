
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


String dataFile = "myAnalysis.hipo";

TDirectory readDir = new TDirectory();

readDir.readFile(dataFile);
readDir.cd("/Signal");
println "done";

TCanvas c1 = new TCanvas("c1", 1000, 1000);
TCanvas c2 = new TCanvas("c2", 1000, 1000);
TCanvas c3 = new TCanvas("c3", 1000, 1000);

c1.divide(4, 5);
c2.divide(4, 5);
c3.divide(4, 5);

for(int i = 0; i < 20; i++){
    c1.cd(i);
    c1.draw(readDir.getObject("/Signal/hSignal " + (i + 1)));
}
for(int i =20; i < 40; i++){
    c2.cd(i - 20);
    c2.draw(readDir.getObject("/Signal/hSignal " + (i + 1)));
}
for(int i = 40; i < 60; i++){
    c3.cd(i - 40);
    c3.draw(readDir.getObject("/Signal/hSignal " + (i + 1)));
}